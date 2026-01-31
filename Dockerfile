FROM eclipse-temurin:17-alpine as build
WORKDIR /app
COPY gradlew build.gradle.kts settings.gradle.kts ./
COPY .gradle .gradle
COPY gradle gradle
COPY src src

RUN ./gradlew clean build

FROM eclipse-temurin:17-alpine
ARG JAR_FILE_DIR=/app/build/libs
COPY --from=build $JAR_FILE_DIR /app/jar
ENTRYPOINT ["java","-jar","app/jar/ShortLink-0.0.1-SNAPSHOT.jar"]