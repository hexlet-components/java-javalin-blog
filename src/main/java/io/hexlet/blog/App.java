package io.hexlet.blog;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.stream.Collectors;

import io.javalin.Javalin;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import io.hexlet.blog.controllers.RootController;
import io.hexlet.blog.controllers.ArticlesController;
import io.hexlet.blog.repository.BaseRepository;
import io.hexlet.blog.util.NamedRoutes;

public final class App {

    private static String getDatabaseUrl() {
        return System.getenv().getOrDefault("JDBC_DATABASE_URL", "jdbc:h2:mem:project");
    }

    private static int getPort() {
        String port = System.getenv().getOrDefault("PORT", "3000");
        return Integer.valueOf(port);
    }

    private static String getMode() {
        return System.getenv().getOrDefault("APP_ENV", "development");
    }

    private static boolean isProduction() {
        return getMode().equals("production");
    }

    public static String readResourceFile(String name) throws IOException {
        ClassLoader classLoader = App.class.getClassLoader();
        InputStream is = classLoader.getResourceAsStream(name);
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
            return reader.lines().collect(Collectors.joining("\n"));
        }
    }

    private static void addRoutes(Javalin app) {
        app.get(NamedRoutes.rootPath(), RootController::welcome);
        app.get(NamedRoutes.aboutPath(), RootController::about);

        app.get(NamedRoutes.articlesPath(), ArticlesController::listArticles);
        app.post(NamedRoutes.articlesPath(), ArticlesController::createArticle);

        app.get(NamedRoutes.articleBuildPath(), ArticlesController::buildArticle);
        app.get(NamedRoutes.articlePath("{id}"), ArticlesController::showArticle);
    }

    public static Javalin getApp() throws IOException, SQLException {
        var hikariConfig = new HikariConfig();
        hikariConfig.setJdbcUrl(getDatabaseUrl());

        var dataSource = new HikariDataSource(hikariConfig);

        String schemaSql = readResourceFile("schema.sql");
        String seedSql = readResourceFile("seed.sql");

        try (var connection = dataSource.getConnection();
             var statement = connection.createStatement()) {
            statement.execute(schemaSql);
            statement.execute(seedSql);
        }

        BaseRepository.dataSource = dataSource;

        Javalin app = Javalin.create(config -> {
            if (!isProduction()) {
                config.plugins.enableDevLogging();
            }
        });

        addRoutes(app);

        app.before(ctx -> {
            ctx.attribute("ctx", ctx);
        });

        return app;
    }

    public static void main(String[] args) throws SQLException, IOException {
        Javalin app = getApp();
        app.start(getPort());
    }

}
