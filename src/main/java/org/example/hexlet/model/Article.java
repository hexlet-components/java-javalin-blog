package org.example.hexlet.model;

import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public final class Article {
    private long id;

    @ToString.Include private String name;
    private String description;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Article(String name, String description) {
        this.name = name;
        this.description = description;
    }
}
