package io.hexlet.blog;

import java.io.IOException;
import java.sql.SQLException;
import java.util.stream.Collectors;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.io.BufferedReader;

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

    private static String readResourceFile(String fileName) throws IOException {
        var inputStream = App.class.getClassLoader().getResourceAsStream(fileName);
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
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

        var schemaSql = readResourceFile("schema.sql");
        var seedSql = readResourceFile("seed.sql");

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
