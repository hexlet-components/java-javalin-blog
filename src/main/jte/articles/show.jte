@import io.hexlet.blog.dto.articles.ArticlePage
@param ArticlePage page

@template.layout.page(
  page = page,
  header = @`
    <section>
      Просмотр статьи
    </section>
  `,
  content = @`
    !{var article = page.getArticle();}
    <section>
      <div class="card">
        <div class="card-header bg-secondary text-white">
          <h2>${article.getName()}</h2>
        </div>
        <div class="card-body bg-light">
          <p class="lead">${article.getDescription()}</p>
        </div>
      </div>
    </section>
  `
)
