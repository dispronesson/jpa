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

Для запуска на своей машине запустите приведенные ниже команды.

```bash
$ git clone https://github.com/dispronesson/orders-service.git
$ cd orders-service
$ mvn spring-boot:run
```

Для запуска в Docker запустите приведенные ниже команды.

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
| localhost:5432                       | PostgreSQL            |

## Тестирование

Внутри проекта присутствует 2 юнит-теста:

- OrderServiceTest
- UserServiceTest

Для их запуска в корне каталога необходимо прописать ```$ mvn clean test```.

## SonarCloud

[SonarCloud](https://sonarcloud.io/project/overview?id=dispronesson_orders-service)
