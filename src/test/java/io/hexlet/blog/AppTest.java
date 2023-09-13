package io.hexlet.blog;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.sql.SQLException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import io.javalin.Javalin;
import io.javalin.testtools.JavalinTest;

public class AppTest {

    Javalin app;

    @BeforeEach
    public final void setUp() throws IOException, SQLException {
        app = App.getApp();
    }

    @Test
    public void testMainPage() {
        JavalinTest.test(app, (server, client) -> {
            var response = client.get("/");
            assertThat(response.code()).isEqualTo(200);
            assertThat(response.body().string()).contains("Привет от Хекслета!");
        });
    }

    @Test
    public void testAbout() {
        JavalinTest.test(app, (server, client) -> {
            var response = client.get("/about");
            assertThat(response.code()).isEqualTo(200);
            assertThat(response.body().string()).contains("Эксперименты с Javalin на Хекслете");
        });
    }

    @Test
    public void testBuildArticlePage() {
        JavalinTest.test(app, (server, client) -> {
            var response = client.get("/articles/new");
            assertThat(response.code()).isEqualTo(200);
        });
    }

    @Test
    public void testArticlesPage() {
        JavalinTest.test(app, (server, client) -> {
            var response = client.get("/articles");
            assertThat(response.code()).isEqualTo(200);
        });
    }

    @Test
    public void testCreateArticle() {
        JavalinTest.test(app, (server, client) -> {
            var requestBody = "name=articlename&description=articledescription";
            var response = client.post("/articles", requestBody);
            assertThat(response.code()).isEqualTo(200);

            var lastPageResponse = client.get("/articles?page=2");
            assertThat(lastPageResponse.body().string()).contains("articlename");
        });
    }

    @Test
    void testCarNotFound() throws Exception {
        JavalinTest.test(app, (server, client) -> {
            var response = client.get("/articles/999999");
            assertThat(response.code()).isEqualTo(404);
        });
    }

    @Test
    public void testSearchArticle() {
        JavalinTest.test(app, (server, client) -> {
            var queryString = "?term=man";
            var response = client.get("/articles" + queryString);
            var body = response.body().string();
            assertThat(response.code()).isEqualTo(200);
            assertThat(body).contains("The Man Within");
            assertThat(body).doesNotContain("Consider the Lilies");
        });
    }
}
