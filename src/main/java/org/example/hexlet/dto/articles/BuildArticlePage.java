package org.example.hexlet.dto.articles;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.hexlet.dto.BasePage;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class BuildArticlePage extends BasePage {
    private String name;
    private String description;
}
