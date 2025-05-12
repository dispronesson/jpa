# Orders Service
Простое Spring Boot приложение, позволяющее создавать пользователей и заказы. Приложение использует PostgreSQL в качестве БД. Фронтенд реализован с помощью библиотек React & Ant Design, которые позволяют сделать
удобный, привлекательный и отзывчивый интерфейс.

## Технологии

- Java
- Spring Boot
- Maven
- Hibernate
- PostgreSQL
- React & Ant Design
- Docker
- SwaggerUI
- Mockito & JUnit

## Установка и запуск

Для запуска приложения можно использовать как свою локальную машину, так и Docker контейнеры.

Для запуска на своей машине пропишите приведенные ниже команды.

```bash
$ git clone https://github.com/dispronesson/orders-service.git
$ cd orders-service
$ mvn spring-boot:run
```

Для запуска в Docker пропишите приведенные ниже команды.

```bash
$ git clone https://github.com/dispronesson/orders-service.git
$ cd orders-service
$ docker-compose up --build
```

Следующие сервисы буду запущены:

| URL                                  | Сервис                |
|--------------------------------------|-----------------------|
| localhost:8080                       | OrdersService Web App |
| localhost:8080/swagger-ui/index.html | OrdersService Web Api |

## Тестирование

В проекте используется юнит-тестирование. В директории src/tests находятся юнит-тесы для бизнес-логики проекта.
Для их запуска в корне проекта необходимо прописать ```$ mvn clean test```.

## SonarCloud

[SonarCloud](https://sonarcloud.io/project/overview?id=dispronesson_orders-service)
