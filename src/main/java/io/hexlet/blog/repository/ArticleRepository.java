package io.hexlet.blog.repository;

import io.hexlet.blog.domain.Article;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ArticleRepository extends BaseRepository {

    public static void save(Article article) throws SQLException {
        var sql =
                "INSERT INTO article (name, description, created_at, updated_at) VALUES (?, ?, ?, ?)";
        var now = LocalDateTime.now();
        try (var conn = dataSource.getConnection();
                var stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, article.getName());
            stmt.setString(2, article.getDescription());
            stmt.setTimestamp(3, Timestamp.valueOf(now));
            stmt.setTimestamp(4, Timestamp.valueOf(now));
            stmt.executeUpdate();
            var generatedKeys = stmt.getGeneratedKeys();
            if (generatedKeys.next()) {
                article.setId(generatedKeys.getLong(1));
                article.setCreatedAt(now);
                article.setUpdatedAt(now);
            } else {
                throw new SQLException("DB have not returned an id after saving an entity");
            }
        }
    }

    public static Optional<Article> find(Long id) throws SQLException {
        var sql = "SELECT * FROM article WHERE id = ?";
        try (var conn = dataSource.getConnection();
                var stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);
            var resultSet = stmt.executeQuery();
            if (resultSet.next()) {
                return Optional.of(buildArticle(resultSet));
            }
            return Optional.empty();
        }
    }

    public static Optional<Article> findByName(String name) throws SQLException {
        var sql = "SELECT * FROM article WHERE name = ?";
        try (var conn = dataSource.getConnection();
                var stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, name);
            var resultSet = stmt.executeQuery();
            if (resultSet.next()) {
                return Optional.of(buildArticle(resultSet));
            }
            return Optional.empty();
        }
    }

    // Страницу выбирает сама база: LIMIT с OFFSET вместо чтения всех строк в память.
    public static List<Article> search(String term, int offset, int limit) throws SQLException {
        var sql =
                "SELECT * FROM article WHERE LOWER(name) LIKE LOWER(?) ORDER BY id LIMIT ? OFFSET ?";
        try (var conn = dataSource.getConnection();
                var stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, "%" + term + "%");
            stmt.setInt(2, limit);
            stmt.setInt(3, offset);
            var resultSet = stmt.executeQuery();
            var result = new ArrayList<Article>();
            while (resultSet.next()) {
                result.add(buildArticle(resultSet));
            }
            return result;
        }
    }

    // Число страниц считается отдельным запросом: LIMIT в выборке выше уже отрезал
    // хвост, и по её результату общее количество не восстановить.
    public static int countByTerm(String term) throws SQLException {
        var sql = "SELECT COUNT(*) FROM article WHERE LOWER(name) LIKE LOWER(?)";
        try (var conn = dataSource.getConnection();
                var stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, "%" + term + "%");
            var resultSet = stmt.executeQuery();
            resultSet.next();
            return resultSet.getInt(1);
        }
    }

    private static Article buildArticle(ResultSet resultSet) throws SQLException {
        var article = new Article(resultSet.getString("name"), resultSet.getString("description"));
        article.setId(resultSet.getLong("id"));
        article.setCreatedAt(resultSet.getTimestamp("created_at").toLocalDateTime());
        article.setUpdatedAt(resultSet.getTimestamp("updated_at").toLocalDateTime());
        return article;
    }
}
