package io.hexlet.blog;

import static io.javalin.apibuilder.ApiBuilder.get;
import static io.javalin.apibuilder.ApiBuilder.path;
import static io.javalin.apibuilder.ApiBuilder.post;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import io.hexlet.blog.controllers.ArticleController;
import io.hexlet.blog.controllers.RootController;
import io.hexlet.blog.repository.BaseRepository;
import io.javalin.Javalin;
import io.javalin.config.RoutesConfig;
import io.javalin.http.staticfiles.Location;
import io.javalin.rendering.template.JavalinThymeleaf;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.SQLException;
import java.util.stream.Collectors;
import nz.net.ultraq.thymeleaf.layoutdialect.LayoutDialect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.extras.java8time.dialect.Java8TimeDialect;
import org.thymeleaf.templateresolver.AbstractConfigurableTemplateResolver;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;
import org.thymeleaf.templateresolver.FileTemplateResolver;

public final class App {

    private static int getPort() {
        String port = System.getenv().getOrDefault("PORT", "3000");
        return Integer.valueOf(port);
    }

    private static final Logger LOG = LoggerFactory.getLogger(App.class);

    private static final Path TEMPLATES_PATH = Path.of("src", "main", "resources", "templates");
    private static final Path STATIC_PATH = Path.of("src", "main", "resources", "static");

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

    private static TemplateEngine getTemplateEngine() {
        TemplateEngine templateEngine = new TemplateEngine();

        templateEngine.addTemplateResolver(createTemplateResolver());
        templateEngine.addDialect(new LayoutDialect());
        templateEngine.addDialect(new Java8TimeDialect());

        return templateEngine;
    }

    // В разработке шаблоны читаются из каталога исходников и не кешируются,
    // поэтому правка разметки видна без перезапуска приложения.
    private static AbstractConfigurableTemplateResolver createTemplateResolver() {
        if (isDevelopment()) {
            var fileResolver = new FileTemplateResolver();
            fileResolver.setPrefix(TEMPLATES_PATH + "/");
            fileResolver.setCharacterEncoding("UTF-8");
            fileResolver.setCacheable(false);
            return fileResolver;
        }
        var classLoaderResolver = new ClassLoaderTemplateResolver();
        classLoaderResolver.setPrefix("/templates/");
        classLoaderResolver.setCharacterEncoding("UTF-8");
        return classLoaderResolver;
    }

    private static void addRoutes(RoutesConfig routes) {
        routes.get("/", RootController.welcome);
        routes.get("/about", RootController.about);
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

                    config.fileRenderer(new JavalinThymeleaf(getTemplateEngine()));

                    // Собранный css лежит в ресурсах, его пишет tailwind из
                    // assets/css/source.css. В разработке он отдаётся прямо из
                    // исходников, чтобы вотчер попадал в работающее приложение.
                    if (isDevelopment()) {
                        config.staticFiles.add(STATIC_PATH.toString(), Location.EXTERNAL);
                    } else {
                        config.staticFiles.add("/static", Location.CLASSPATH);
                    }

                    addRoutes(config.routes);

                    config.routes.apiBuilder(
                            () -> {
                                path(
                                        "articles",
                                        () -> {
                                            get(ArticleController.listArticles);
                                            post(ArticleController.createArticle);
                                            get("new", ArticleController.newArticle);
                                            path(
                                                    "{id}",
                                                    () -> {
                                                        get(ArticleController.showArticle);
                                                    });
                                        });
                            });

                    config.routes.before(
                            ctx -> {
                                ctx.attribute("ctx", ctx);
                            });
                });
    }

    public static void main(String[] args) throws IOException, SQLException {
        Javalin app = getApp();
        app.start(getPort());
    }
}
