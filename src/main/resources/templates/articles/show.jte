@import org.example.hexlet.dto.articles.ArticlePage
@param ArticlePage page

@template.layout.page(
  page = page,
  header = "Просмотр статьи",
  content = @`
    <div class="overflow-hidden rounded-lg border border-gray-200 bg-white shadow-sm">
      <div class="border-b border-gray-200 bg-gray-800 px-6 py-4 text-white">
        <h2 class="text-xl font-semibold">${page.getArticle().getName()}</h2>
      </div>
      <div class="px-6 py-4">
        <p class="text-lg">${page.getArticle().getDescription()}</p>
      </div>
    </div>
  `
)
