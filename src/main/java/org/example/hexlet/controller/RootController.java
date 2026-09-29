package org.example.hexlet.controller;

import io.javalin.http.Context;

public final class RootController {
    public static void index(Context ctx) {
        ctx.render("index.jte");
    }

    public static void about(Context ctx) {
        ctx.render("about.jte");
    }
}
