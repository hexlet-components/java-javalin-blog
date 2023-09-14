package io.hexlet.blog.dto.articles;

import lombok.Getter;
import lombok.Setter;
import lombok.AllArgsConstructor;
import java.util.List;

import io.hexlet.blog.model.Article;

@Getter
@Setter
@AllArgsConstructor
public final class ArticlesData {
    private List<Article> articles;

    private int totalCount;
}
