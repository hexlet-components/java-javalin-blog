package io.hexlet.blog.util;

public class NamedRoutes {
    public static String rootPath() {
        return "/";
    }

    public static String articlePath(Long id) {
        return articlePath(String.valueOf(id));
    }

    public static String articlePath(String id) {
        return "/articles/" + id;
    }

    public static String articlesPath() {
        return "/articles";
    }

    public static String aboutPath() {
        return "/about";
    }
}
