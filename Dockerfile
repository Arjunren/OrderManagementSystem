FROM eclipse-temurin:25-jdk AS build
WORKDIR /workspace
COPY .mvn .mvn
COPY mvnw pom.xml ./
RUN chmod +x mvnw && ./mvnw -B -DskipTests dependency:go-offline
COPY src src
RUN ./mvnw -B -DskipTests package

FROM eclipse-temurin:25-jre
RUN useradd --system --create-home spring
WORKDIR /app
COPY --from=build /workspace/target/*.jar app.jar
USER spring
EXPOSE 8080
ENTRYPOINT ["java","-jar","/app/app.jar"]

