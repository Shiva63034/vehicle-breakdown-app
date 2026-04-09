FROM eclipse-temurin:21-jdk AS build
WORKDIR /app

COPY . .
RUN chmod +x mvnw
RUN ./mvnw clean package -DskipTests

FROM eclipse-temurin:21-jre
WORKDIR /app

COPY --from=build /app/target/breakdown-assistance-0.0.1-SNAPSHOT.war /app/app.war

EXPOSE 8080

CMD ["java", "-jar", "/app/app.war"]