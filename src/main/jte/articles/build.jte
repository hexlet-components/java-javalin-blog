@import io.hexlet.blog.dto.articles.ArticlePage
@import io.hexlet.blog.util.NamedRoutes
@param ArticlePage page

@template.layout.page(
  page = page,
  header = @`
    <section>
      Создание статьи
    </section>
  `,
  content = @`
    !{var article = page.getArticle();}
    <section>
      <form action="${NamedRoutes.articlesPath()}" method="post">
        <div class="mb-3">
          <label for="name" class="form-label">Наименование</label>
          <input type="text" class="form-control" id="name" name="name" value="${article.getName()}">
        </div>
        <div class="mb-3">
          <label for="description" class="form-label">Описание</label>
          <textarea rows="10" class="form-control" id="description", name="description">${article.getDescription()}</textarea>
        </div>
        <button type="submit" class="btn btn-primary">Создать</button>
      </form>
    </section>
  `
)
