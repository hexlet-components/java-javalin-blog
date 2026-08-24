package io.hexlet.blog;

import static io.javalin.apibuilder.ApiBuilder.get;
import static io.javalin.apibuilder.ApiBuilder.path;
import static io.javalin.apibuilder.ApiBuilder.post;

import io.hexlet.blog.controllers.ArticleController;
import io.hexlet.blog.controllers.RootController;
import io.javalin.Javalin;
import io.javalin.config.RoutesConfig;
import io.javalin.http.staticfiles.Location;
import io.javalin.rendering.template.JavalinThymeleaf;
import nz.net.ultraq.thymeleaf.layoutdialect.LayoutDialect;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.extras.java8time.dialect.Java8TimeDialect;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

public final class App {

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

    private static TemplateEngine getTemplateEngine() {
        TemplateEngine templateEngine = new TemplateEngine();

        ClassLoaderTemplateResolver templateResolver = new ClassLoaderTemplateResolver();
        templateResolver.setPrefix("/templates/");
        templateResolver.setCharacterEncoding("UTF-8");

        templateEngine.addTemplateResolver(templateResolver);
        templateEngine.addDialect(new LayoutDialect());
        templateEngine.addDialect(new Java8TimeDialect());

        return templateEngine;
    }

    private static void addRoutes(RoutesConfig routes) {
        routes.get("/", RootController.welcome);
        routes.get("/about", RootController.about);
    }

    public static Javalin getApp() {
        return Javalin.create(
                config -> {
                    if (!isProduction()) {
                        config.bundledPlugins.enableDevLogging();
                    }

                    config.fileRenderer(new JavalinThymeleaf(getTemplateEngine()));

                    // Собранный css лежит в ресурсах: его пишет tailwind из
                    // assets/css/source.css.
                    config.staticFiles.add("/static", Location.CLASSPATH);

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

    public static void main(String[] args) {
        Javalin app = getApp();
        app.start(getPort());
    }
}
