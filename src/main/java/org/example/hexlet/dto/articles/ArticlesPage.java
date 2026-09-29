package org.example.hexlet.dto.articles;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.example.hexlet.dto.BasePage;
import org.example.hexlet.model.Article;

@AllArgsConstructor
@Getter
public class ArticlesPage extends BasePage {
    private List<Article> articles;
    private String term;
    private List<Integer> pages;
    private int currentPage;
}
