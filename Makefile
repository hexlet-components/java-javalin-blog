setup:
	make -C app setup

clean:
	make -C app clean

build:
	make -C app build

start:
	make -C app start

install:
	make -C app install

start-dist:
	make -C app start-dist

generate-migrations:
	make -C app generate-migrations

lint:
	make -C app lint

test:
	make -C app test

report:
	make -C app report

check-updates:
	make -C app check-updates

image-build:
	docker build -t hexletcomponents/java-javalin-blog:latest .

image-push:
	docker push hexletcomponents/java-javalin-blog:latest

.PHONY: build
