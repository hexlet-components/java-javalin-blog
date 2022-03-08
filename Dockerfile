FROM gradle:7.4.0-jdk17

WORKDIR /app

COPY . .

RUN ./gradlew installDist

CMD build/install/java-javalin-blog/bin/java-javalin-blog
