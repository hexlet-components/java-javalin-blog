# Мигрируем на Javalin 7

## Обновление зависимостей

- Обновите версию Javalin на актуальную. Модуль `javalin-rendering` в 7 версии разделён по движкам шаблонизации — вместо него подключайте `javalin-rendering-<engine>`

```groovy
dependencies {
  implementation 'io.javalin:javalin:7.2.2'
  implementation 'io.javalin:javalin-rendering-thymeleaf:7.2.2'
  implementation 'org.slf4j:slf4j-simple:2.0.18'
}
```

- Обновите остальные зависимости до актуальных версий

## Изменения в коде

В Javalin 7 конфигурация стала «upfront» — все роуты, плагины и рендереры теперь регистрируются только внутри блока `Javalin.create(config -> ...)`. Экземпляр `Javalin`, возвращаемый из `create()`, больше не имеет методов `get`/`post`/`before` — только методы жизненного цикла (`start`, `stop`, `port`).

- Вместо `app.get(...)`, `app.before(...)` и `app.routes(...)` после создания приложения используйте `config.routes`

```java
// Было (Javalin 5/6)
Javalin app = Javalin.create(config -> { ... });
app.get("/", RootController.welcome);
app.before(ctx -> ctx.attribute("ctx", ctx));

// Теперь
Javalin app = Javalin.create(config -> {
    config.routes.get("/", RootController.welcome);
    config.routes.before(ctx -> ctx.attribute("ctx", ctx));
});
```

- Это касается и ApiBuilder DSL — в Javalin 6 он подключался через `config.router.apiBuilder`, в 7 версии переехал на `config.routes.apiBuilder` (`router` и `routes` — теперь разные подконфигурации: `router` отвечает только за общие настройки путей, `routes` — за сами обработчики)

```java
config.routes.apiBuilder(() -> {
    path("articles", () -> {
        get(ArticleController.listArticles);
    });
});
```

- Подключение логгирования (`enableDevLogging`) не изменилось, но само переехало из `config.plugins` (Javalin 5) в `config.bundledPlugins` ещё в 6 версии

```java
config.bundledPlugins.enableDevLogging();
```

- Регистрация файлового рендерера (например, Thymeleaf) тоже стала настройкой конфигурации вместо статического метода

```java
// Было
JavalinThymeleaf.init(getTemplateEngine());

// Теперь
config.fileRenderer(new JavalinThymeleaf(getTemplateEngine()));
```

## Частая ошибка: `Validator.getOrDefault(null)`

В Javalin 7 параметр `default` у `Validator.getOrDefault()` стал строго не-null на уровне рантайма — вызов с `null` кидает `NullPointerException` вместо того, чтобы просто не сработать. Если раньше вы писали что-то вроде `ctx.pathParamAsClass("id", Integer.class).getOrDefault(null)` для обязательного path-параметра, замените на `.get()`:

```java
// Было — падает с NPE в Javalin 7
int id = ctx.pathParamAsClass("id", Integer.class).getOrDefault(null);

// Теперь
int id = ctx.pathParamAsClass("id", Integer.class).get();
```
