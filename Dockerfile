# Стили собирает tailwind, а он живёт в node. Отдельный слой вместо node внутри
# java-образа: в рантайме node не нужен.
FROM node:26-slim AS css

WORKDIR /app

# Node 26 не несёт corepack: из дистрибутива его убрали. Ставим пакетом,
# чтобы версия pnpm по-прежнему бралась из packageManager в package.json.
RUN npm install -g corepack && corepack enable

COPY package.json pnpm-lock.yaml pnpm-workspace.yaml ./
RUN pnpm install --frozen-lockfile

COPY assets ./assets
COPY src/main/resources/templates ./src/main/resources/templates
RUN pnpm run build:css

FROM eclipse-temurin:25-jdk

ARG GRADLE_VERSION=9.6.1

RUN apt-get update && apt-get install -yq unzip wget

RUN wget -q https://services.gradle.org/distributions/gradle-${GRADLE_VERSION}-bin.zip \
    && unzip gradle-${GRADLE_VERSION}-bin.zip \
    && rm gradle-${GRADLE_VERSION}-bin.zip

ENV GRADLE_HOME=/opt/gradle

RUN mv gradle-${GRADLE_VERSION} ${GRADLE_HOME}

ENV PATH=$PATH:$GRADLE_HOME/bin

WORKDIR /app

COPY . .

# Собранный css в гит не едет, поэтому он приезжает из слоя выше. Без этой
# строки образ собрался бы без стилей, и приложение выглядело бы сломанным
# только на проде.
COPY --from=css /app/src/main/resources/static/css/main.css src/main/resources/static/css/main.css

RUN gradle installDist

CMD ["./build/install/java-javalin-blog/bin/java-javalin-blog"]
