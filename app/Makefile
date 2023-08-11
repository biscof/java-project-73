#Makefile

prepare:
	./gradlew clean installDist

build:
	./gradlew clean build test checkstyleMain checkstyleTest

make api-doc:
	./gradlew clean generateOpenApiDocs

report:
	./gradlew jacocoTestReport

start:
	./gradlew bootRun --args='--spring.profiles.active=dev'

start-prod:
	./gradlew bootRun --args='--spring.profiles.active=prod'

.PHONY: build
