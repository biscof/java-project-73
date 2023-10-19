#Makefile

clean:
	./gradlew clean

prepare:
	./gradlew clean installDist

build-and-check:
	./gradlew clean build test checkstyleMain checkstyleTest

docker-db-start:
	docker-compose -f docker/docker-compose.yml up -d -V --remove-orphans

make api-doc:
	./gradlew generateOpenApiDocs

report:
	./gradlew jacocoTestReport

run-dev:
	./gradlew bootRun --args='--spring.profiles.active=dev'

run-dev-with-docker-db: docker-db-start run-dev

run-prod:
	./gradlew bootRun --args='--spring.profiles.active=prod'

test:
	./gradlew test

.PHONY: build
