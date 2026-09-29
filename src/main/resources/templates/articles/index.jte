@import java.net.URLEncoder
@import java.nio.charset.StandardCharsets
@import java.time.format.DateTimeFormatter
@import org.example.hexlet.dto.articles.ArticlesPage
@import org.example.hexlet.util.NamedRoutes
@param ArticlesPage page

!{var term = URLEncoder.encode(page.getTerm(), StandardCharsets.UTF_8);}
!{var dateFormat = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");}
!{var lastPage = page.getPages().size();}

@template.layout.page(
  page = page,
  header = "Статьи",
  content = @`
    <a class="mb-6 inline-block rounded bg-blue-600 px-4 py-2 text-white hover:bg-blue-700" href="${NamedRoutes.buildArticlePath()}">Создать статью</a>
    <form action="${NamedRoutes.articlesPath()}" method="get">
      <div class="flex flex-wrap gap-2">
        <input type="search" class="flex-1 rounded" name="term" value="${page.getTerm()}" placeholder="Search by article name" />
        <button type="submit" class="cursor-pointer rounded border border-blue-600 px-4 py-2 text-blue-600 hover:bg-blue-50">Search</button>
      </div>
    </form>

    <div class="mt-8 overflow-x-auto">
      <table class="w-full border-collapse bg-white text-left">
        <thead>
          <tr class="border-b border-gray-200">
            <th class="px-4 py-2 font-semibold">ID</th>
            <th class="px-4 py-2 font-semibold">Название</th>
            <th class="px-4 py-2 font-semibold">Описание</th>
            <th class="px-4 py-2 font-semibold">Дата создания</th>
          </tr>
        </thead>
        <tbody>
          @for(var article : page.getArticles())
            <tr class="border-b border-gray-100 odd:bg-gray-50">
              <td class="px-4 py-2">${article.getId()}</td>
              <td class="max-w-xs truncate px-4 py-2">
                <a class="text-blue-600 hover:underline" href="${NamedRoutes.articlePath(article.getId())}">${article.getName()}</a>
              </td>
              <td class="max-w-md truncate px-4 py-2">${article.getDescription()}</td>
              <td class="px-4 py-2">${article.getCreatedAt().format(dateFormat)}</td>
            </tr>
          @endfor
        </tbody>
      </table>
    </div>

    @if(lastPage > 0)
      <nav aria-label="Page navigation">
        <ul class="mt-8 flex flex-wrap justify-center gap-1">
          <li>
            <a class="block rounded border px-3 py-1 ${page.getCurrentPage() <= 1 ? "pointer-events-none border-gray-200 text-gray-400" : "border-gray-300 text-blue-600 hover:bg-blue-50"}"
               href="${NamedRoutes.articlesPath()}?page=${page.getCurrentPage() - 1}&term=${term}">Previous</a>
          </li>
          @for(var number : page.getPages())
            <li>
              <a class="block rounded border px-3 py-1 ${page.getCurrentPage() == number ? "border-blue-600 bg-blue-600 text-white" : "border-gray-300 text-blue-600 hover:bg-blue-50"}"
                 href="${NamedRoutes.articlesPath()}?page=${number}&term=${term}">${number}</a>
            </li>
          @endfor
          <li>
            <a class="block rounded border px-3 py-1 ${page.getCurrentPage() >= lastPage ? "pointer-events-none border-gray-200 text-gray-400" : "border-gray-300 text-blue-600 hover:bg-blue-50"}"
               href="${NamedRoutes.articlesPath()}?page=${page.getCurrentPage() + 1}&term=${term}">Next</a>
          </li>
        </ul>
      </nav>
    @endif
  `
)
