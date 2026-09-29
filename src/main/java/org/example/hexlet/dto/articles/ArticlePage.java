package org.example.hexlet.dto.articles;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.example.hexlet.dto.BasePage;
import org.example.hexlet.model.Article;

@AllArgsConstructor
@Getter
public class ArticlePage extends BasePage {
    private Article article;
}
