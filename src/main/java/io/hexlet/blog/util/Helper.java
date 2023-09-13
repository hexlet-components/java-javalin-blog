package io.hexlet.blog.util;

import java.io.File;
import java.nio.file.Files;
import java.util.stream.Collectors;
import java.io.IOException;

import io.hexlet.blog.App;

public class Helper {
    public static String getSql(String sourceFileName) throws IOException {
        var url = App.class.getClassLoader().getResource(sourceFileName);
        var file = new File(url.getFile());
        var sql = Files.lines(file.toPath())
                .collect(Collectors.joining("\n"));
        return sql;
    }
}
