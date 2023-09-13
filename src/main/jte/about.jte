@import io.hexlet.blog.dto.BasePage
@param BasePage page

@template.layout.page(
  page = page,
  header = @`
    <section>
      О блоге
    </section>
  `,
  content = @`
    <section>
      <p class="lead">Эксперименты с Javalin на Хекслете</p>
    </section>
  `
)
