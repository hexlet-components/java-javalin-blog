@import io.hexlet.blog.dto.BasePage
@param BasePage page

@template.layout.page(
  page = page,
  header = @`
    <section>
      Приветствие
    </section>
  `,
  content = @`
    <section>
      <div class="card">
        <div class="card-body p-5 bg-light">
          <div class="display-4">Привет от Хекслета!</div>
          <p class="lead">Практические курсы по программированию</p>
          <hr>
          <a class="btn btn-primary" href="https://ru.hexlet.io" target="_blank">Узнать Больше</a>
        </div>
      </div>
    </section>
  `
)
