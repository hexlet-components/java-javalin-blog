@import gg.jte.Content
@import io.hexlet.blog.util.NamedRoutes
@import io.hexlet.blog.dto.BasePage
@import io.hexlet.blog.util.NamedRoutes
@param Content content
@param Content header
@param BasePage page = null

<!DOCTYPE html>
<html>

  <head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Javalin Blog</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.2.3/dist/css/bootstrap.min.css" rel="stylesheet" integrity="sha384-rbsA2VBKQhggwzxH7pPCaAqO46MgnOM80zW1RWuH61DGLwZJEdK2Kadq2F9CUG65" crossorigin="anonymous">
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.2.3/dist/js/bootstrap.bundle.min.js" integrity="sha384-kenU1KFdBIe4zVF0s0G1M5b4hcpxyD9F7jL+jjXkk+Q2h455rYXK/7HAuoJl+0I4" crossorigin="anonymous"></script>
  </head>

  <body class="d-flex flex-column min-vh-100">
    <nav class="navbar navbar-expand-lg navbar-light bg-light">
      <div class="container">
        <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarNav" aria-controls="navbarNav" aria-expanded="false" aria-label="Toggle navigation">
          <span class="navbar-toggler-icon"></span>
        </button>
        <div class="collapse navbar-collapse" id="navbarNav">
          <div class="navbar-nav">
            <a class="nav-link" href="${NamedRoutes.rootPath()}">Главная</a>
            <a class="nav-link" href="${NamedRoutes.aboutPath()}">О блоге</a>
            <a class="nav-link" href="${NamedRoutes.articlesPath()}">Статьи</a>
          </div>
        </div>
      </div>
    </nav>

    <main class="flex-grow-1">
      @if(page != null && page.getFlash() != null)
        <div class="alert alert-${page.getFlashType()}" role="alert">
          ${page.getFlash()}
        </div>
      @endif

      <div class="container mt-4">
        <h1 class="my-4">
          <section>
            ${header}
          </section>
        </h1>
        <section>
          ${content}
        </section>
      </div>
    </main>

    <footer class="footer border-top py-3 mt-5 bg-light">
      <div class="container-xl">
        <div class="text-center">
          created by
          <a href="https://ru.hexlet.io" target="_blank">Hexlet</a>
        </div>
      </div>
    </footer>
   </body>

</html>
