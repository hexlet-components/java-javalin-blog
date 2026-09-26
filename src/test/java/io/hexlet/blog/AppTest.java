package io.hexlet.blog;

import static org.assertj.core.api.Assertions.assertThat;

import io.hexlet.blog.domain.Article;
import io.hexlet.blog.repository.ArticleRepository;
import io.hexlet.blog.repository.BaseRepository;
import io.javalin.Javalin;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.stream.Collectors;
import kong.unirest.core.HttpResponse;
import kong.unirest.core.Unirest;
import kong.unirest.core.UnirestInstance;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class AppTest {

    // Свой клиент, чтобы настройка редиректов не меняла глобальный Unirest
    private static final UnirestInstance http = Unirest.spawnInstance();

    private static Javalin app;
    private static String baseUrl;

    @Test
    void testInit() {
        assertThat(true).isEqualTo(true);
    }

    @BeforeAll
    public static void beforeAll() throws IOException, SQLException {
        // Unirest 4 по умолчанию идёт по редиректу и после POST, а тестам нужен сам ответ 302.
        http.config().followRedirects(false);
        app = App.getApp();
        app.start(0);
        int port = app.port();
        baseUrl = "http://localhost:" + port;
    }

    @AfterAll
    public static void afterAll() {
        // Если beforeAll упал в App.getApp(), app остаётся null,
        // а клиент нужно закрыть в любом случае
        try {
            if (app != null) {
                app.stop();
            }
        } finally {
            http.close();
        }
    }

    // Тесты не зависят друг от друга
    // Но хорошей практикой будет возвращать базу данных между тестами в исходное состояние
    @BeforeEach
    void beforeEach() throws IOException, SQLException {
        runScript("truncate.sql");
        runScript("seed-test-db.sql");
    }

    private static void runScript(String fileName) throws IOException, SQLException {
        var inputStream = AppTest.class.getClassLoader().getResourceAsStream(fileName);
        String sql;
        try (var reader =
                new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
            sql = reader.lines().collect(Collectors.joining("\n"));
        }
        try (var connection = BaseRepository.dataSource.getConnection();
                var statement = connection.createStatement()) {
            statement.execute(sql);
        }
    }

    @Nested
    class RootTest {

        @Test
        void testIndex() {
            HttpResponse<String> response = http.get(baseUrl).asString();
            assertThat(response.getStatus()).isEqualTo(200);
            assertThat(response.getBody()).contains("Привет от Хекслета!");
        }

        @Test
        void testAbout() {
            HttpResponse<String> response = http.get(baseUrl + "/about").asString();
            assertThat(response.getStatus()).isEqualTo(200);
            assertThat(response.getBody()).contains("Эксперименты с Javalin на Хекслете");
        }
    }

    @Nested
    class UrlTest {

        @Test
        void testIndex() {
            HttpResponse<String> response = http.get(baseUrl + "/articles").asString();
            String body = response.getBody();

            assertThat(response.getStatus()).isEqualTo(200);
            assertThat(body).contains("The Man Within");
            assertThat(body).contains("Consider the Lilies");
        }

        // Дату в списке форматирует #temporals. С Thymeleaf 3.1 он входит в ядро,
        // поэтому отдельный модуль thymeleaf-extras-java8time убран, и тест
        // страхует, что форматирование работает без него.
        @Test
        void testIndexCreatedAtFormat() {
            HttpResponse<String> response = http.get(baseUrl + "/articles").asString();

            assertThat(response.getStatus()).isEqualTo(200);
            assertThat(response.getBody()).contains("01/01/2022 13:57");
        }

        @Test
        void testShow() {
            HttpResponse<String> response = http.get(baseUrl + "/articles/1").asString();
            String body = response.getBody();

            assertThat(response.getStatus()).isEqualTo(200);
            assertThat(body).contains("The Man Within");
            assertThat(body).contains("Every flight begins with a fall");
        }

        @Test
        void testNew() {
            HttpResponse<String> response = http.get(baseUrl + "/articles/new").asString();
            String body = response.getBody();

            assertThat(response.getStatus()).isEqualTo(200);
        }

        @Test
        void testCreate() throws SQLException {
            String inputName = "new name";
            String inputDescription = "new description";
            HttpResponse responsePost =
                    http.post(baseUrl + "/articles")
                            .field("name", inputName)
                            .field("description", inputDescription)
                            .asEmpty();

            assertThat(responsePost.getStatus()).isEqualTo(302);
            assertThat(responsePost.getHeaders().getFirst("Location")).isEqualTo("/articles");

            HttpResponse<String> response = http.get(baseUrl + "/articles").asString();
            String body = response.getBody();

            assertThat(response.getStatus()).isEqualTo(200);
            assertThat(body).contains(inputName);
            assertThat(body).contains("Статья успешно создана");

            Article actualArticle = ArticleRepository.findByName(inputName).orElse(null);

            assertThat(actualArticle).isNotNull();
            assertThat(actualArticle.getName()).isEqualTo(inputName);
            assertThat(actualArticle.getDescription()).isEqualTo(inputDescription);
        }

        @Test
        void testSearch() {
            var queryString = "?term=man";
            HttpResponse<String> response =
                    http.get(baseUrl + "/articles" + queryString).asString();
            String body = response.getBody();

            assertThat(response.getStatus()).isEqualTo(200);
            assertThat(body).contains("The Man Within");
            assertThat(body).doesNotContain("Consider the Lilies");
        }

        // Страницу отдаёт LIMIT с OFFSET, а число страниц считает отдельный COUNT.
        // Ошибка в любом из двух запросов снаружи выглядит одинаково — «статьи
        // пропали», — поэтому проверяются обе страницы сразу.
        @Test
        void testPagination() throws SQLException {
            for (int i = 1; i <= 11; i++) {
                ArticleRepository.save(new Article("Article " + i, "description " + i));
            }

            HttpResponse<String> firstPage = http.get(baseUrl + "/articles").asString();
            assertThat(firstPage.getStatus()).isEqualTo(200);
            assertThat(firstPage.getBody()).contains("The Man Within");
            assertThat(firstPage.getBody()).doesNotContain("Article 10");

            HttpResponse<String> secondPage = http.get(baseUrl + "/articles?page=2").asString();
            assertThat(secondPage.getStatus()).isEqualTo(200);
            assertThat(secondPage.getBody()).contains("Article 10");
            assertThat(secondPage.getBody()).doesNotContain("The Man Within");
        }

        // Номер страницы приходит из адреса, и ноль с минусом приходят тоже.
        // Без нижней границы OFFSET уходил в минус и база отвечала ошибкой.
        @Test
        void testPaginationBelowFirstPage() {
            for (String page : new String[] {"0", "-1"}) {
                HttpResponse<String> response =
                        http.get(baseUrl + "/articles?page=" + page).asString();

                assertThat(response.getStatus()).isEqualTo(200);
                assertThat(response.getBody()).contains("The Man Within");
            }
        }

        // Нечисловой параметр Javalin проверяет сам, но её обработчик сериализует
        // ошибки в json и без jackson-databind падает, превращая 400 в 500.
        // Поэтому номер страницы и id разбираются своим кодом.
        @Test
        void testPaginationInvalidPage() {
            HttpResponse<String> response = http.get(baseUrl + "/articles?page=abc").asString();

            assertThat(response.getStatus()).isEqualTo(200);
            assertThat(response.getBody()).contains("The Man Within");
        }

        @Test
        void testShowInvalidId() {
            HttpResponse<String> response = http.get(baseUrl + "/articles/abc").asString();

            assertThat(response.getStatus()).isEqualTo(404);
        }

        @Test
        void testShowNotFound() {
            HttpResponse<String> response = http.get(baseUrl + "/articles/999").asString();

            assertThat(response.getStatus()).isEqualTo(404);
        }

        // Пустое поле до базы доезжать не должно: контроллер возвращает форму,
        // и статья не создаётся.
        @Test
        void testCreateWithEmptyName() throws SQLException {
            HttpResponse<String> response =
                    http.post(baseUrl + "/articles")
                            .field("name", "")
                            .field("description", "new description")
                            .asString();

            assertThat(response.getStatus()).isEqualTo(200);
            assertThat(response.getBody()).contains("Не удалось создать статью");
            assertThat(ArticleRepository.findByName("")).isEmpty();
        }
    }
}
