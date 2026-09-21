
FROM eclipse-temurin:21-jre

WORKDIR /app

COPY target/food-delivery-service-*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
