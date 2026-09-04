package io.hexlet.blog.controllers;

import io.hexlet.blog.domain.Article;
import io.hexlet.blog.repository.ArticleRepository;
import io.javalin.http.Handler;
import io.javalin.http.NotFoundResponse;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public final class ArticleController {

    private static final int ROWS_PER_PAGE = 10;

    public static Handler listArticles =
            ctx -> {
                String term = ctx.queryParamAsClass("term", String.class).getOrDefault("");
                int currentPage = ctx.queryParamAsClass("page", Integer.class).getOrDefault(1);
                int offset = (currentPage - 1) * ROWS_PER_PAGE;

                List<Article> articles = ArticleRepository.search(term, offset, ROWS_PER_PAGE);

                int total = ArticleRepository.countByTerm(term);
                int lastPage = (int) Math.ceil((double) total / ROWS_PER_PAGE);
                List<Integer> pages =
                        IntStream.rangeClosed(1, lastPage).boxed().collect(Collectors.toList());

                ctx.attribute("articles", articles);
                ctx.attribute("term", term);
                ctx.attribute("pages", pages);
                ctx.attribute("currentPage", currentPage);
                ctx.render("articles/index.html");
            };

    public static Handler newArticle =
            ctx -> {
                Article article = new Article();

                ctx.attribute("article", article);
                ctx.render("articles/new.html");
            };

    public static Handler createArticle =
            ctx -> {
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

                ArticleRepository.save(article);

                ctx.sessionAttribute("flash", "Статья успешно создана");
                ctx.sessionAttribute("flash-type", "success");
                ctx.redirect("/articles");
            };

    public static Handler showArticle =
            ctx -> {
                long id = ctx.pathParamAsClass("id", Long.class).get();

                Article article =
                        ArticleRepository.find(id).orElseThrow(() -> new NotFoundResponse());

                ctx.attribute("article", article);
                ctx.render("articles/show.html");
            };
}
