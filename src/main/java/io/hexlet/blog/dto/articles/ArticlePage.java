package io.hexlet.blog.dto.articles;

import io.hexlet.blog.model.Article;
import lombok.AllArgsConstructor;
import lombok.Getter;
import io.hexlet.blog.dto.BasePage;

@AllArgsConstructor
@Getter
public class ArticlePage extends BasePage {
    private Article article;
}
