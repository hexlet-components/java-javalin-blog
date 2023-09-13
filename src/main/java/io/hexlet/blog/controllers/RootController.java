package io.hexlet.blog.controllers;

import io.javalin.http.Context;
import java.util.Collections;

import io.hexlet.blog.dto.BasePage;

public final class RootController {

    public static void welcome(Context ctx) {
        var page = new BasePage();
        ctx.render("index.jte", Collections.singletonMap("page", page));
    };

    public static void about(Context ctx) {
        var page = new BasePage();
        ctx.render("about.jte", Collections.singletonMap("page", page));
    };
}
