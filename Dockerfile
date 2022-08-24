FROM gradle:7.4.0-jdk17

WORKDIR /app

COPY . .

RUN gradle installDist

CMD APP_ENV=production ./build/install/java-javalin-blog/bin/java-javalin-blog
