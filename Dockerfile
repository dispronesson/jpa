FROM eclipse-temurin:23-jdk

WORKDIR /docker

COPY target/app-0.0.1.jar .

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app-0.0.1.jar"]