package io.hexlet.blog.controllers;

import io.javalin.http.Context;

public final class RootController {

    public static void welcome(Context ctx) {
        ctx.render("index.html");
    };

    public static void about(Context ctx) {
        ctx.render("about.html");
    };
}
