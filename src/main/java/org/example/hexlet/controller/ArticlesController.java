package org.example.hexlet.controller;

import io.javalin.http.Context;
import io.javalin.http.NotFoundResponse;
import java.sql.SQLException;
import java.util.Map;
import java.util.stream.IntStream;
import org.example.hexlet.dto.articles.ArticlePage;
import org.example.hexlet.dto.articles.ArticlesPage;
import org.example.hexlet.dto.articles.BuildArticlePage;
import org.example.hexlet.model.Article;
import org.example.hexlet.repository.ArticleRepository;
import org.example.hexlet.util.NamedRoutes;

public final class ArticlesController {

    private static final int ROWS_PER_PAGE = 10;

    // Номер страницы и id приходят из адреса строками, и разбор у них свой.
    // Встроенная проверка Javalin на непрошедшем значении бросает
    // ValidationException, а её штатный обработчик сериализует ошибки в json и
    // без jackson-databind падает сам: вместо 400 приходит 500.
    private static int parsePage(String raw) {
        if (raw == null) {
            return 1;
        }
        try {
            return Integer.parseInt(raw);
        } catch (NumberFormatException e) {
            return 1;
        }
    }

    private static long parseId(String raw) {
        try {
            return Long.parseLong(raw);
        } catch (NumberFormatException e) {
            throw new NotFoundResponse();
        }
    }

    public static void index(Context ctx) throws SQLException {
        var term = ctx.queryParamAsClass("term", String.class).getOrDefault("");
        // Ноль и минус приходят оттуда же. Без нижней границы OFFSET
        // уходит в минус, и база отвечает ошибкой.
        var currentPage = Math.max(1, parsePage(ctx.queryParam("page")));
        var offset = (currentPage - 1) * ROWS_PER_PAGE;

        var articles = ArticleRepository.search(term, offset, ROWS_PER_PAGE);

        var total = ArticleRepository.countByTerm(term);
        var lastPage = (int) Math.ceil((double) total / ROWS_PER_PAGE);
        var pages = IntStream.rangeClosed(1, lastPage).boxed().toList();

        var page = new ArticlesPage(articles, term, pages, currentPage);
        page.setFlash(ctx.consumeSessionAttribute("flash"));
        page.setFlashType(ctx.consumeSessionAttribute("flash-type"));
        ctx.render("articles/index.jte", Map.of("page", page));
    }

    public static void build(Context ctx) {
        var page = new BuildArticlePage();
        ctx.render("articles/build.jte", Map.of("page", page));
    }

    public static void create(Context ctx) throws SQLException {
        var name = ctx.formParam("name");
        var description = ctx.formParam("description");

        if (name.isEmpty() || description.isEmpty()) {
            var page = new BuildArticlePage(name, description);
            page.setFlash("Не удалось создать статью");
            page.setFlashType("danger");
            ctx.render("articles/build.jte", Map.of("page", page));
            return;
        }

        var article = new Article(name, description);
        ArticleRepository.save(article);

        ctx.sessionAttribute("flash", "Статья успешно создана");
        ctx.sessionAttribute("flash-type", "success");
        ctx.redirect(NamedRoutes.articlesPath());
    }

    public static void show(Context ctx) throws SQLException {
        var id = parseId(ctx.pathParam("id"));
        var article = ArticleRepository.find(id).orElseThrow(() -> new NotFoundResponse());

        var page = new ArticlePage(article);
        ctx.render("articles/show.jte", Map.of("page", page));
    }
}
