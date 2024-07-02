# Build Stage
FROM amazoncorretto:21-alpine as build
ENV APP_HOME=/app
WORKDIR $APP_HOME
COPY build.gradle.kts settings.gradle.kts gradlew ./
COPY gradle ./gradle
RUN chmod +x gradlew
RUN apk add --no-cache dos2unix
RUN dos2unix ./gradlew
RUN ./gradlew build || return 0
COPY src ./src
RUN ./gradlew clean bootJar

# Production Stage
FROM amazoncorretto:21-alpine
ENV APP_HOME=/app
WORKDIR $APP_HOME
ARG JAR_FILE=build/libs/*.jar
COPY --from=build $APP_HOME/$JAR_FILE ./app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]