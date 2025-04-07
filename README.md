# JPA
Java Persistence API (JPA) — спецификация API Jakarta EE, которая предоставляет возможность сохранять в удобном виде Java-объекты в базе данных. В данном проекте реализована связь сущностей OneToMany, которые хранятся в БД. Для реализации JPA использован Hibernate. Запрашиваемые данные помещаются в in-memory кэш для оптимизации работы БД. Присутствуют пагинация и гет-запросы с параметрами для фильтрации данных из БД, а также получение лог-файла за опредленную дату из общего лог-файла. Все ошибки обрабатываются через глобальный обработчик исключений (ControllerAdvice).

## Технологии

- Java
- Spring Boot
- Maven
- Hibernate
- PostgreSQL
- Lombok
- SpringDoc

## Требования

- Java 23+
- Spring Boot 3.4.3+
- Maven 3.9.9+
- Hibernate 6.6.8+
- PostgreSQL 42.7.5+
- Lombok 1.18.36+
- SpringDoc 2.8+

## Установка и запуск

```bash
git clone https://github.com/dispronesson/jpa.git
cd jpa
mvn clean install
mvn spring-boot:run
```
В resources/application.properties на место ${DB_URL}, ${DB_USER}, ${DB_PASS} необходимо указать соответственно url, где находится ваша БД, юзернейм и пароль пользователя БД.

Документация по всем HTTP-методам в проекте по умолчанию доступна по адресу: http://localhost:8080/swagger-ui/index.html

## SonarCloud

[SonarCloud](https://sonarcloud.io/project/overview?id=dispronesson_jpa)
