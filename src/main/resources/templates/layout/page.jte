@import org.example.hexlet.dto.BasePage
@import org.example.hexlet.util.NamedRoutes
@import gg.jte.Content
@param String header
@param Content content
@param BasePage page = null

<!DOCTYPE html>
<html lang="ru">
  <head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Javalin Blog</title>
    <link href="/css/main.css" rel="stylesheet">
  </head>

  <body class="flex min-h-screen flex-col bg-gray-50 text-gray-900">
    <nav class="border-b border-gray-200 bg-white">
      <div class="mx-auto flex max-w-5xl flex-wrap items-center gap-6 px-4 py-3">
        <a class="text-sm text-gray-600 hover:text-gray-900" href="${NamedRoutes.rootPath()}">Главная</a>
        <a class="text-sm text-gray-600 hover:text-gray-900" href="${NamedRoutes.aboutPath()}">О блоге</a>
        <a class="text-sm text-gray-600 hover:text-gray-900" href="${NamedRoutes.articlesPath()}">Статьи</a>
      </div>
    </nav>

    <main class="flex-1">
      @if(page != null && page.getFlash() != null)
        !{var flashClass = "success".equals(page.getFlashType())
            ? "border-green-200 bg-green-50 text-green-800"
            : "danger".equals(page.getFlashType())
                ? "border-red-200 bg-red-50 text-red-800"
                : "border-sky-200 bg-sky-50 text-sky-800";}
        <div class="border-b px-4 py-3 ${flashClass}" role="alert">
          <p class="mx-auto max-w-5xl">${page.getFlash()}</p>
        </div>
      @endif
      <div class="mx-auto w-full max-w-5xl px-4 pb-10">
        <h1 class="mt-6 mb-4 text-3xl font-bold">${header}</h1>
        ${content}
      </div>
    </main>

    <footer class="border-t border-gray-200 bg-white py-4">
      <div class="mx-auto max-w-5xl px-4 text-center text-sm text-gray-600">
        created by
        <a class="text-blue-600 hover:underline" href="https://ru.hexlet.io" target="_blank">Hexlet</a>
      </div>
    </footer>
  </body>
</html>
