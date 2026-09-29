package org.example.hexlet.util;

public class NamedRoutes {
    public static String rootPath() {
        return "/";
    }

    public static String aboutPath() {
        return "/about";
    }

    public static String articlesPath() {
        return "/articles";
    }

    public static String buildArticlePath() {
        return "/articles/build";
    }

    // Это нужно, чтобы не преобразовывать типы снаружи
    public static String articlePath(Long id) {
        return articlePath(String.valueOf(id));
    }

    public static String articlePath(String id) {
        return "/articles/" + id;
    }
}
