package io.hexlet.blog.repository;

import io.hexlet.blog.model.Article;

import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


public class ArticlesRepository extends BaseRepository {
    public static void save(Article article) throws SQLException {
        var sql = "INSERT INTO articles (name, description, created_at, updated_at) VALUES (?, ?, ?, ?)";
        var datetime = new Timestamp(System.currentTimeMillis());
        try (var conn = dataSource.getConnection();
             var preparedStatement = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setString(1, article.getName());
            preparedStatement.setString(2, article.getDescription());
            preparedStatement.setTimestamp(3, datetime);
            preparedStatement.setTimestamp(4, datetime);
            preparedStatement.executeUpdate();
            var generatedKeys = preparedStatement.getGeneratedKeys();
            if (generatedKeys.next()) {
                article.setId(generatedKeys.getLong(1));
                article.setCreatedAt(datetime);
                article.setUpdatedAt(datetime);
            } else {
                throw new SQLException("DB have not returned an id after saving an entity");
            }
        }
    }

    public static Optional<Article> find(Long id) throws SQLException {
        var sql = "SELECT * FROM articles WHERE id = ?";
        try (var conn = dataSource.getConnection();
             var stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);
            var resultSet = stmt.executeQuery();
            if (resultSet.next()) {
                var name = resultSet.getString("name");
                var description = resultSet.getString("description");
                var createdAt = resultSet.getTimestamp("created_at");
                var updatedAt = resultSet.getTimestamp("updated_at");
                var article = new Article(name, description);
                article.setId(id);
                article.setCreatedAt(createdAt);
                article.setUpdatedAt(updatedAt);

                return Optional.of(article);
            }
            return Optional.empty();
        }
    }

    public static Optional<Article> findById(Long id) throws SQLException {
        var sql = "SELECT * FROM articles WHERE id = ?";
        try (var conn = dataSource.getConnection();
             var stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);
            var resultSet = stmt.executeQuery();
            if (resultSet.next()) {
                var name = resultSet.getString("name");
                var description = resultSet.getString("description");
                var createdAt = resultSet.getTimestamp("created_at");
                var updatedAt = resultSet.getTimestamp("updated_at");
                var articleId = resultSet.getLong("id");
                var article = new Article(name, description);
                article.setId(id);
                article.setCreatedAt(createdAt);
                article.setUpdatedAt(updatedAt);

                return Optional.of(article);
            }
            return Optional.empty();
        }
    }

    public static List<Article> getEntities(int page, int rowsPerPage, String term) throws SQLException {
        var offset = page * rowsPerPage;
        var sql = "SELECT * FROM articles WHERE LOWER(name) LIKE LOWER('%"
            + term + "%') ORDER BY id LIMIT " + rowsPerPage + " OFFSET " + offset + ";";
        try (var conn = dataSource.getConnection();
             var stmt = conn.prepareStatement(sql)) {
            var resultSet = stmt.executeQuery();
            var result = new ArrayList<Article>();
            while (resultSet.next()) {
                var id = resultSet.getLong("id");
                var name = resultSet.getString("name");
                var description = resultSet.getString("description");
                var createdAt = resultSet.getTimestamp("created_at");
                var updatedAt = resultSet.getTimestamp("updated_at");
                var article = new Article(name, description);
                article.setId(id);
                article.setCreatedAt(createdAt);
                article.setUpdatedAt(updatedAt);
                result.add(article);
            }
            return result;
        }
    }

    public static int getEntitiesCount(String term) throws SQLException {
        var sql = "SELECT COUNT(*) AS count FROM articles WHERE name LIKE '%" + term + "%';";
        var result = 0;
        try (var conn = dataSource.getConnection();
             var stmt = conn.prepareStatement(sql)) {
            var resultSet = stmt.executeQuery();
            if (resultSet.next()) {
                result = resultSet.getInt("count");
            }
            return result;
        }
    }
}
