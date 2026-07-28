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

RUN gradle installDist

CMD ["./build/install/java-javalin-blog/bin/java-javalin-blog"]
