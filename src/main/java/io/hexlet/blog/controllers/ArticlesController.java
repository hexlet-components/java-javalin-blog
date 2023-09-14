package io.hexlet.blog.controllers;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.sql.SQLException;
import java.util.Collections;
import io.javalin.http.NotFoundResponse;
import io.javalin.http.Context;

import io.hexlet.blog.model.Article;
import io.hexlet.blog.dto.articles.ArticlesPage;
import io.hexlet.blog.dto.articles.ArticlePage;
import io.hexlet.blog.repository.ArticlesRepository;
import io.hexlet.blog.util.NamedRoutes;
import io.hexlet.blog.dto.articles.ArticlesData;

public final class ArticlesController {

    public static void listArticles(Context ctx) throws SQLException {
        String term = ctx.queryParamAsClass("term", String.class).getOrDefault("");
        int currentPage = ctx.queryParamAsClass("page", Integer.class).getOrDefault(1);
        int rowsPerPage = 10;

        ArticlesData articlesData = ArticlesRepository.findEntities(currentPage - 1, rowsPerPage, term);

        List<Article> articles = articlesData.getArticles();
        int count = articlesData.getTotalCount();

        var lastPage = (int) Math.ceil((float) count / rowsPerPage);
        List<Integer> pages = IntStream
            .range(1, lastPage + 1)
            .boxed()
            .collect(Collectors.toList());

        var page = new ArticlesPage(articles, term, currentPage, pages);

        page.setFlash(ctx.consumeSessionAttribute("flash"));
        page.setFlashType(ctx.consumeSessionAttribute("flash-type"));

        ctx.render("articles/index.jte", Collections.singletonMap("page", page));
    };

    public static void buildArticle(Context ctx) throws SQLException {
        var name = ctx.formParamAsClass("name", String.class)
            .getOrDefault("");
        var description = ctx.formParamAsClass("description", String.class)
            .getOrDefault("");

        Article article = new Article(name, description);

        var page = new ArticlePage(article);
        ctx.render("articles/build.jte", Collections.singletonMap("page", page));
    };

    public static void createArticle(Context ctx) throws SQLException {
        String name = ctx.formParam("name");
        String description = ctx.formParam("description");

        Article article = new Article(name, description);

        if (name.isEmpty() || description.isEmpty()) {
            ctx.sessionAttribute("flash", "Не удалось создать статью");
            ctx.sessionAttribute("flash-type", "danger");
            ctx.attribute("article", article);
            ctx.render("articles/new.jte");
            return;
        }

        ArticlesRepository.save(article);

        ctx.sessionAttribute("flash", "Статья успешно создана");
        ctx.sessionAttribute("flash-type", "success");
        ctx.redirect(NamedRoutes.articlesPath());
    };

    public static void showArticle(Context ctx) throws SQLException {
        var id = ctx.pathParamAsClass("id", Long.class).get();

        var article = ArticlesRepository.findById(id)
            .orElseThrow(() -> new NotFoundResponse("Article with id = " + id + " not found"));

        var page = new ArticlePage(article);

        ctx.render("articles/show.jte", Collections.singletonMap("page", page));
    };
}
