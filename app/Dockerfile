FROM eclipse-temurin:20-jdk

ARG GRADLE_VERSION=8.2
ENV GRADLE_HOME=/opt/gradle

WORKDIR /app

COPY . .

RUN apt-get update && apt-get install -yq make unzip \
    && wget -q https://services.gradle.org/distributions/gradle-${GRADLE_VERSION}-bin.zip \
    && unzip gradle-${GRADLE_VERSION}-bin.zip \
    && rm gradle-${GRADLE_VERSION}-bin.zip \
    && mv gradle-${GRADLE_VERSION} ${GRADLE_HOME}

ENV PATH=$PATH:$GRADLE_HOME/bin

RUN ./gradlew installDist

CMD ./build/install/app/bin/app