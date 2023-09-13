package io.hexlet.blog.dto.articles;

import io.hexlet.blog.model.Article;
import lombok.AllArgsConstructor;
import lombok.Getter;
import io.hexlet.blog.dto.BasePage;

import java.util.List;

@AllArgsConstructor
@Getter
public class ArticlesPage extends BasePage {
    private List<Article> articles;
    private String term;
    private int currentPage;
    private List<Integer> pages;
}
