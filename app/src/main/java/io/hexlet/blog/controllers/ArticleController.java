package io.hexlet.blog.controllers;

import io.javalin.http.Handler;
import io.javalin.http.NotFoundResponse;
import io.ebean.PagedList;
import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Collectors;

import io.hexlet.blog.domain.query.QArticle;
import io.hexlet.blog.domain.Article;

public final class ArticleController {

    public static Handler listArticles = ctx -> {
        String term = ctx.queryParamAsClass("term", String.class).getOrDefault("");
        int page = ctx.queryParamAsClass("page", Integer.class).getOrDefault(1) - 1;
        int rowsPerPage = 10;

        PagedList<Article> pagedArticles = new QArticle()
            .name.icontains(term)
            .setFirstRow(page * rowsPerPage)
            .setMaxRows(rowsPerPage)
            .orderBy()
                .id.asc()
            .findPagedList();

        List<Article> articles = pagedArticles.getList();

        int lastPage = pagedArticles.getTotalPageCount() + 1;
        int currentPage = pagedArticles.getPageIndex() + 1;
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

    public static Handler newArticle = ctx -> {
        Article article = new Article();

        ctx.attribute("article", article);
        ctx.render("articles/new.html");
    };

    public static Handler createArticle = ctx -> {
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

        article.save();

        ctx.sessionAttribute("flash", "Статья успешно создана");
        ctx.sessionAttribute("flash-type", "success");
        ctx.redirect("/articles");
    };

    public static Handler showArticle = ctx -> {
        int id = ctx.pathParamAsClass("id", Integer.class).getOrDefault(null);

        Article article = new QArticle()
            .id.equalTo(id)
            .findOne();

        if (article == null) {
            throw new NotFoundResponse();
        }

        ctx.attribute("article", article);
        ctx.render("articles/show.html");
    };
}
