setup:
	gradle wrapper --gradle-version 7.4

clean:
	./gradlew clean

build:
	./gradlew clean build

start:
	APP_ENV=development ./gradlew run

install:
	./gradlew install

start-dist:
	APP_ENV=production ./build/install/java-javalin-blog/bin/java-javalin-blog

generate-migrations:
	./gradlew generateMigrations

lint:
	./gradlew checkstyleMain checkstyleTest

test:
	./gradlew test

report:
	./gradlew jacocoTestReport

check-updates:
	./gradlew dependencyUpdates

image-build:
	docker build -t hexletcomponents/java-javalin-blog:latest .

image-push:
	docker push hexletcomponents/java-javalin-blog:latest

.PHONY: build
