# JPA
Java Persistence API (JPA) — спецификация API Jakarta EE, которая предоставляет возможность сохранять в удобном виде Java-объекты в базе данных.
В данном проекте реализована связь сущностей OneToMany, которые хранятся в БД. Для реализации JPA использован Hibernate.

## Технологии

- Java
- Spring Boot
- Maven
- Hibernate
- PostgreSQL
- Lombok

## Требования

- Java 23+
- Spring Boot 3.4.3+
- Maven 3.9.9+
- Hibernate 6.6.8+
- PostgreSQL 42.7.5+
- Lombok 1.18.36+

## Установка и запуск

```bash
git clone https://github.com/dispronesson/jpa.git
cd jpa
mvn clean install
mvn spring-boot:run
```
В resources/application.properties на место ${DB_URL}, ${DB_USER}, ${DB_PASS} надо указать соответственно url, где находится ваша БД, юзернейм и пароль пользователя БД.

## SonarCloud

[SonarCloud](https://sonarcloud.io/project/overview?id=dispronesson_jpa)
