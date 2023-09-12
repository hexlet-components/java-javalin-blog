package io.hexlet.blog;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import io.javalin.Javalin;
import java.util.stream.Collectors;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.nio.file.Files;

import io.hexlet.blog.model.Article;

class AppTest {

    @Test
    void testInit() {
        assertThat(true).isEqualTo(true);
    }

    private static Javalin app;
    private static String baseUrl;
    private static Article existingArticle;
    private HikariDataSource dataSource;

    private static String getDatabaseUrl() {
        return System.getenv().getOrDefault("JDBC_DATABASE_URL", "jdbc:h2:mem:project");
    }

    @BeforeAll
    public static void beforeAll() throws IOException, SQLException {
        app = App.getApp();
        app.start(0);
        int port = app.port();
        baseUrl = "http://localhost:" + port;
    }

    @AfterAll
    public static void afterAll() {
        app.stop();
    }

    // Тесты не зависят друг от друга
    // Но хорошей практикой будет возвращать базу данных между тестами в исходное состояние
    @BeforeEach
    void beforeEach() throws IOException, SQLException {
        var hikariConfig = new HikariConfig();
        hikariConfig.setJdbcUrl(getDatabaseUrl());

        dataSource = new HikariDataSource(hikariConfig);

        var schema = AppTest.class.getClassLoader().getResource("truncate.sql");
        var file = new File(schema.getFile());
        var sql = Files.lines(file.toPath())
                .collect(Collectors.joining("\n"));

        try (var connection = dataSource.getConnection();
             var statement = connection.createStatement()) {
            statement.execute(sql);
        }
    }

    @Nested
    class RootTest {

        @Test
        void testIndex() {
            HttpResponse<String> response = Unirest.get(baseUrl).asString();
            assertThat(response.getStatus()).isEqualTo(200);
            assertThat(response.getBody()).contains("Привет от Хекслета!");
        }

        @Test
        void testAbout() {
            HttpResponse<String> response = Unirest.get(baseUrl + "/about").asString();
            assertThat(response.getStatus()).isEqualTo(200);
            assertThat(response.getBody()).contains("Эксперименты с Javalin на Хекслете");
        }
    }

    @Nested
    class UrlTest {

        @Test
        void testIndex() {
            HttpResponse<String> response = Unirest
                .get(baseUrl + "/articles")
                .asString();
            String body = response.getBody();

            assertThat(response.getStatus()).isEqualTo(200);
            assertThat(body).contains("The Man Within");
            assertThat(body).contains("Consider the Lilies");
        }

        @Test
        void testShow() {
            HttpResponse<String> response = Unirest
                .get(baseUrl + "/articles/1")
                .asString();
            String body = response.getBody();

            assertThat(response.getStatus()).isEqualTo(200);
            assertThat(body).contains("The Man Within");
            assertThat(body).contains("Every flight begins with a fall");
        }

        @Test
        void testNew() {
            HttpResponse<String> response = Unirest
                .get(baseUrl + "/articles/new")
                .asString();
            String body = response.getBody();

            assertThat(response.getStatus()).isEqualTo(200);
        }

        @Test
        void testCreate() {
            String inputName = "new name";
            String inputDescription = "new description";
            HttpResponse responsePost = Unirest
                .post(baseUrl + "/articles")
                .field("name", inputName)
                .field("description", inputDescription)
                .asEmpty();

            assertThat(responsePost.getStatus()).isEqualTo(302);
            assertThat(responsePost.getHeaders().getFirst("Location")).isEqualTo("/articles");

            HttpResponse<String> response = Unirest
                .get(baseUrl + "/articles")
                .asString();
            String body = response.getBody();

            assertThat(response.getStatus()).isEqualTo(200);
            assertThat(body).contains(inputName);
            assertThat(body).contains("Статья успешно создана");

            Article actualArticle = new Article(inputName, inputDescription);

            assertThat(actualArticle).isNotNull();
            assertThat(actualArticle.getName()).isEqualTo(inputName);
            assertThat(actualArticle.getDescription()).isEqualTo(inputDescription);
        }

        @Test
        void testSearch() {
            var queryString = "?term=man";
            HttpResponse<String> response = Unirest
                .get(baseUrl + "/articles" + queryString)
                .asString();
            String body = response.getBody();
            System.out.println(body);
            assertThat(response.getStatus()).isEqualTo(200);
            assertThat(body).contains("The Man Within");
            assertThat(body).doesNotContain("Consider the Lilies");
        }
    }
}
