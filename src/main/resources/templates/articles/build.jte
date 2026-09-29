@import org.example.hexlet.dto.articles.BuildArticlePage
@import org.example.hexlet.util.NamedRoutes
@param BuildArticlePage page

@template.layout.page(
  page = page,
  header = "Создание статьи",
  content = @`
    <form class="max-w-xl rounded-lg border border-gray-200 bg-white p-6 shadow-sm" action="${NamedRoutes.articlesPath()}" method="post">
      <div class="mb-4">
        <label for="name" class="mb-1 block text-sm font-medium">Наименование</label>
        <input type="text" class="w-full rounded" id="name" name="name" value="${page.getName()}">
      </div>
      <div class="mb-4">
        <label for="description" class="mb-1 block text-sm font-medium">Описание</label>
        <textarea rows="10" class="w-full rounded" id="description" name="description">${page.getDescription()}</textarea>
      </div>
      <button type="submit" class="cursor-pointer rounded bg-blue-600 px-4 py-2 text-white hover:bg-blue-700">Создать</button>
    </form>
  `
)
