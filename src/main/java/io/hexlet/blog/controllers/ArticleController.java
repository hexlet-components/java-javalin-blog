package io.hexlet.blog.controllers;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.sql.SQLException;

import io.javalin.http.NotFoundResponse;
import io.javalin.http.Context;
import io.hexlet.blog.model.Article;

import io.hexlet.blog.repository.ArticlesRepository;

public final class ArticleController {

    public static void listArticles(Context ctx) throws SQLException {
        String term = ctx.queryParamAsClass("term", String.class).getOrDefault("");
        int page = ctx.queryParamAsClass("page", Integer.class).getOrDefault(1) - 1;
        int rowsPerPage = 10;



        // PagedList<Article> pagedArticles = new QArticle()
        //     .name.icontains(term)
        //     .setFirstRow(page * rowsPerPage)
        //     .setMaxRows(rowsPerPage)
        //     .orderBy()
        //         .id.asc()
        //     .findPagedList();

        List<Article> articles = ArticlesRepository.getEntities(page, rowsPerPage, term);

        int lastPage = 1; //pagedArticles.getTotalPageCount() + 1;
        int currentPage = 1; //pagedArticles.getPageIndex() + 1;
        List<Integer> pages = IntStream
            .range(1, lastPage)
            .boxed()
            .collect(Collectors.toList());

        ctx.attribute("articles", articles);
        ctx.attribute("term", term);
        ctx.attribute("pages", pages);
        ctx.attribute("currentPage", currentPage);
        ctx.render("articles/index.html");
    };

    public static void newArticle(Context ctx) throws SQLException {
        var name = ctx.formParamAsClass("name", String.class)
            .check(value -> value.length() >= 2, "Название не должно быть короче двух символов")
            .get();
        var description = ctx.formParamAsClass("description", String.class)
            .get();

        Article article = new Article(name, description);

        ctx.attribute("article", article);
        ctx.render("articles/new.html");
    };

    public static void createArticle(Context ctx) throws SQLException {
        String name = ctx.formParam("name");
        String description = ctx.formParam("description");

        Article article = new Article(name, description);

        if (name.isEmpty() || description.isEmpty()) {
            ctx.sessionAttribute("flash", "Не удалось создать статью");
            ctx.sessionAttribute("flash-type", "danger");
            ctx.attribute("article", article);
            ctx.render("articles/new.html");
            return;
        }

        ArticlesRepository.save(article);

        ctx.sessionAttribute("flash", "Статья успешно создана");
        ctx.sessionAttribute("flash-type", "success");
        ctx.redirect("/articles");
    };

    public static void showArticle(Context ctx) throws SQLException {
        var id = ctx.pathParamAsClass("id", Long.class).get();

        var article = ArticlesRepository.findById(id)
            .orElseThrow(() -> new NotFoundResponse("Article with id = " + id + " not found"));

        ctx.attribute("article", article);
        ctx.render("articles/show.html");
    };
}
