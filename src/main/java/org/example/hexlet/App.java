package org.example.hexlet;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import gg.jte.ContentType;
import gg.jte.TemplateEngine;
import gg.jte.resolve.DirectoryCodeResolver;
import gg.jte.resolve.ResourceCodeResolver;
import io.javalin.Javalin;
import io.javalin.http.staticfiles.Location;
import io.javalin.rendering.template.JavalinJte;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.SQLException;
import java.util.stream.Collectors;
import org.example.hexlet.controller.ArticlesController;
import org.example.hexlet.controller.RootController;
import org.example.hexlet.repository.BaseRepository;
import org.example.hexlet.util.NamedRoutes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class App {

    private static int getPort() {
        String port = System.getenv().getOrDefault("PORT", "3000");
        return Integer.valueOf(port);
    }

    private static final Logger LOG = LoggerFactory.getLogger(App.class);

    private static final Path TEMPLATES_PATH = Path.of("src", "main", "resources", "templates");
    private static final Path STATIC_PATH = Path.of("src", "main", "resources", "static");
    private static final Path JTE_CLASSES_PATH = Path.of("jte-classes");

    private static String getMode() {
        return System.getenv().getOrDefault("APP_ENV", "production");
    }

    // Режим разработки включает переменная APP_ENV, её ставит цель start.
    // Вторая половина условия смотрит, лежат ли исходники рядом с процессом:
    // запущенное из другого каталога приложение возьмёт шаблоны с classpath
    // вместо непонятного отказа.
    private static boolean isDevelopment() {
        return getMode().equals("development") && Files.isDirectory(TEMPLATES_PATH);
    }

    private static boolean isProduction() {
        return getMode().equals("production");
    }

    private static String getDatabaseUrl() {
        return System.getenv().getOrDefault("JDBC_DATABASE_URL", "jdbc:h2:mem:blog");
    }

    private static String readResourceFile(String fileName) throws IOException {
        var inputStream = App.class.getClassLoader().getResourceAsStream(fileName);
        try (var reader =
                new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
            return reader.lines().collect(Collectors.joining("\n"));
        }
    }

    private static TemplateEngine createTemplateEngine() {
        // В разработке шаблоны читаются из каталога исходников и перекомпилируются
        // на лету, поэтому правка разметки видна без перезапуска приложения.
        if (isDevelopment()) {
            var codeResolver = new DirectoryCodeResolver(TEMPLATES_PATH);
            return TemplateEngine.create(codeResolver, JTE_CLASSES_PATH, ContentType.Html);
        }
        var classLoader = App.class.getClassLoader();
        var codeResolver = new ResourceCodeResolver("templates", classLoader);
        return TemplateEngine.create(codeResolver, ContentType.Html);
    }

    public static Javalin getApp() throws IOException, SQLException {
        var hikariConfig = new HikariConfig();
        hikariConfig.setJdbcUrl(getDatabaseUrl());

        var dataSource = new HikariDataSource(hikariConfig);

        // Схему и демонстрационные статьи кладёт сам код при старте: у примера нет
        // отдельного шага миграций, а пустой список статей выглядит как поломка.
        try (var connection = dataSource.getConnection();
                var statement = connection.createStatement()) {
            statement.execute(readResourceFile("schema.sql"));
            statement.execute(readResourceFile("seed.sql"));
        }
        BaseRepository.dataSource = dataSource;

        // Молча выбранный режим неотличим от «правка разметки не подхватилась».
        LOG.info(
                "Mode: {}",
                isDevelopment()
                        ? "development, templates and static are read from src"
                        : "production, templates and static are read from classpath");

        return Javalin.create(
                config -> {
                    if (!isProduction()) {
                        config.bundledPlugins.enableDevLogging();
                    }

                    config.fileRenderer(new JavalinJte(createTemplateEngine()));

                    // Собранный css лежит в ресурсах, его пишет tailwind из
                    // assets/css/source.css. В разработке он отдаётся прямо из
                    // исходников, чтобы вотчер попадал в работающее приложение.
                    if (isDevelopment()) {
                        config.staticFiles.add(STATIC_PATH.toString(), Location.EXTERNAL);
                    } else {
                        config.staticFiles.add("/static", Location.CLASSPATH);
                    }

                    config.routes.get(NamedRoutes.rootPath(), RootController::index);
                    config.routes.get(NamedRoutes.aboutPath(), RootController::about);

                    config.routes.get(NamedRoutes.articlesPath(), ArticlesController::index);
                    config.routes.get(NamedRoutes.buildArticlePath(), ArticlesController::build);
                    config.routes.get(NamedRoutes.articlePath("{id}"), ArticlesController::show);
                    config.routes.post(NamedRoutes.articlesPath(), ArticlesController::create);
                });
    }

    public static void main(String[] args) throws IOException, SQLException {
        Javalin app = getApp();
        app.start(getPort());
    }
}
