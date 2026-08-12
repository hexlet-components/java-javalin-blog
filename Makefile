setup:
	gradle wrapper --gradle-version 9.7.0

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
	./gradlew spotlessCheck

format:
	./gradlew spotlessApply

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
