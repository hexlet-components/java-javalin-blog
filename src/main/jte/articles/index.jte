@import io.hexlet.blog.dto.articles.ArticlesPage
@import java.time.format.DateTimeFormatter
@import io.hexlet.blog.util.NamedRoutes
@param ArticlesPage page

!{var formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");}
!{var pages = page.getPages();}
!{var currentPage = page.getCurrentPage();}
!{var term = page.getTerm();}

@template.layout.page(
  page = page,
  header = @`
    <section>
      Статьи
    </section>
  `,
  content = @`
    <section>
      <a class="btn btn-primary mb-5" href="${NamedRoutes.articleBuildPath()}">Создать статью</a>
      <form action="${NamedRoutes.articlesPath()}" method="get">
        <div class="input-group">
          <input type="search" class="form-control rounded" name="term" value="${term}" placeholder="Search by article name" />
          <button type="submit" class="btn btn-outline-primary">Search</button>
        </div>
      </form>

      <table class="table table-striped mt-5">
        <thead>
          <tr>
            <th class="col-1">ID</th>
            <th class="col-3">Название</th>
            <th>Описание</th>
            <th class="col-2">Дата создания</th>
          </tr>
        </thead>
        <tbody>
          @for(var article : page.getArticles())
            <tr>
              <td>${article.getId()}</td>
              <td>
                <a href="${NamedRoutes.articlePath(article.getId())}">${article.getName()}</a>
              </td>
              <td>${article.getDescription()}</td>
              <td>${article.getCreatedAt().toLocalDateTime().format(formatter)}</td>
            </tr>
          @endfor
        </tbody>
      </table>

      @if(pages.size() > 0)
        <nav aria-label="Page navigation">
          <ul class="pagination justify-content-center mt-5">
            <li class='${"page-item" + (currentPage <= 1 ? " disabled" : "")}'>
              <a class="page-link" href="${NamedRoutes.articlesPath()}?page=${currentPage - 1}&term=${term}">Previous</a>
            </li>
            @for(var pageNumber : pages)
              <li class='${"page-item" + (currentPage == pageNumber ? " active" : "")}'>
                <a class="page-link" href="${NamedRoutes.articlesPath()}?page=${pageNumber}&term=${term}">${pageNumber}</a>
              </li>
            @endfor
            <li class='${"page-item" + (currentPage >= pages.size() ? " disabled" : "")}'>
              <a class="page-link" href="${NamedRoutes.articlesPath()}?page=${currentPage + 1}&term=${term}">Next</a>
            </li>
          </ul>
        </nav>
      @endif
    </section>
  `
)
