package io.hexlet.blog.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import java.sql.Timestamp;

@Getter
@Setter
@ToString
public final class Article {
    private long id;

    private String name;

    private String description;

    private Timestamp createdAt;

    private Timestamp updatedAt;

    public Article(String name, String description) {
        this.name = name;
        this.description = description;
    }
}
