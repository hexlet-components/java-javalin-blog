package io.hexlet.blog.domain;

import io.ebean.Model;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import java.time.Instant;
import io.ebean.annotation.WhenCreated;
import io.ebean.annotation.WhenModified;

@Entity
public final class Article extends Model {

    @Id
    private long id;

    private String name;

    @Lob
    private String description;

    @WhenCreated
    private Instant createdAt;

    @WhenModified
    private Instant updatedAt;

    public Article() {
    }

    public Article(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public long getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public String getDescription() {
        return this.description;
    }

    public Instant getCreatedAt() {
        return this.createdAt;
    }

    public Instant getUpdatedAt() {
        return this.updatedAt;
    }
}
