package io.hexlet.blog.controllers;

import io.javalin.http.Handler;

public final class RootController {

    public static Handler welcome = ctx -> {
        ctx.render("index.html");
    };

    public static Handler about = ctx -> {
        ctx.render("about.html");
    };
}
