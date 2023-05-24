# Мигрируем на Javalin 5

## Обновление зависимостей

- Обновите версию Javalin на актуальную

```groovy
implementation 'io.javalin:javalin:5.5.0'

testImplementation 'io.ebean:ebean-test:13.15.0'
```

- Обновите остальные зависимости до актуальных версий

## Изменения в коде

В Javalin 5 пакет `core` был удален, что упростило структуру пакета Javalin. Некоторые другие вещи также были перемещены

```java
// Вместо
import io.javalin.plugin.rendering.template.JavalinThymeleaf;

// Теперь
import io.javalin.rendering.template.JavalinThymeleaf;
```

В Javalin 5 по сравнению с версией 4 была значительно изменена конфигурация. Большинство старых параметров конфигурации были перемещены в подконфигурации. Обзор можно посмотреть в [документации](https://javalin.io/documentation#configuration)

- Подключение логгирования теперь перемещено в подконфигурацию `plugins`

```java
 Javalin app = Javalin.create(config -> {
    config.plugins.enableDevLogging();
});
```

- Javalin 5 все также имеет встроенную поддержку популярных шаблонизаторов, в том числе Thymeleaf. Но интерфейс подключения шаблонизатора изменился:

```java
JavalinThymeleaf.init(getTemplateEngine());
```
