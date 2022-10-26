FROM gradle:7.4.0-jdk17

WORKDIR /app

COPY /app .

RUN gradle installDist

CMD ./build/install/java-javalin-blog/bin/java-javalin-blog
