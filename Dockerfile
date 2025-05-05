FROM eclipse-temurin:23-jdk

WORKDIR /app

COPY target/myapp-0.0.1.jar .

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "myapp-0.0.1.jar"]