# Task Management System service

Небольшой REST API «Система Управления Задачами» с использованием Spring MVC.

Работа со списком задач и комментариев к ним. Авторизация пользователей OAuth 2.0.<br>
Фильтрация запросов с использованием спецификаций.<br>
Маппинг сущностей с использованием MapStruct.<br>
Работа с базой данных с использованием Spring Boot Data JPA и Spring Boot Data Redis.<br>

## How To Use

```
mvn clean install
docker build -t tms .
docker compose up
```

## Доступные запросы (endpoints)

## Swagger-ui

http://localhost:8088/swagger-ui/index.html

### Авторизация

* [POST] http://localhost:8088/api/auth/signin <br>
  Авторизация пользователя в системе.<br>
* [POST] http://localhost:8081/api/auth/register <br>
  Регистрация нового пользователя в системе.<br>
* [POST] http://localhost:8081/api/auth/refresh-token <br>
  Обновление токена.<br>
* [POST] http://localhost:8081/api/auth/logout <br>
  Выход из системы. <br>

### Задачи (tasks)

Пользователь с правами ROLE_USER может только получать задачу по id и менять ее статус.<br>
Остальные доступно только пользователю с правами ROLE_ADMIN.

* [GET] http://localhost:8081/api/task/filter - фильтрация задач по параметрам.<br>
* [GET] http://localhost:8081/api/task
* [GET] http://localhost:8081/api/task/{id}
* [POST] http://localhost:8081/api/task/
* [POST] http://localhost:8081/api/task/{id}/{performerId}
* [PUT] http://localhost:8081/api/task/{id}
* [PUT] http://localhost:8081/api/task/{id}/status
* [PUT] http://localhost:8081/api/task/{id}/priority
* [DELETE] http://localhost:8081/api/task/{id}

### Комментарии (comments)

Редактирование и удаление комментария к задаче разрешается только пользователям ROLE_ADMIN.

* [GET] http://localhost:8081/api/comment
* [GET] http://localhost:8081/api/comment/{id}
* [POST] http://localhost:8081/api/comment/
* [PUT] http://localhost:8081/api/comment/{id}
* [DELETE] http://localhost:8081/api/comment/{id}

### Пользователи (users)

Редактирование пользователя разрешается только тому пользователю,
который его создал и пользователям с ROLE_ADMIN.
Получение списка пользователей и удаление пользователя по id доступно только пользователям с ролью ROLE_ADMIN.

* [GET] http://localhost:8081/api/user
* [GET] http://localhost:8081/api/user/{id}
* [PUT] http://localhost:8081/api/user/{id}
* [DELETE] http://localhost:8081/api/user/{id}

## Структура БД

| table_name | primary | foreign   | foreign      | column   | column        | column         | column         |
|------------|---------|-----------|--------------|----------|---------------|----------------|----------------|
| comments   | id      | task_id   | author_id    | content  | creation_time | updated_time   |                |
| news       | id      | author_id |              | title    | description   | current_status | current_priority |
| users      | id      |           |              | username | password      | email          |
| user_roles |         | user_id   | roles        |
| user_task  |         | task_id   | performer_id |

## Значения по умолчанию

### application.yml

spring.datasource.url:<br> по умолчанию: jdbc:postgresql://localhost:5432/tms_service_db.

spring.datasource.username:<br> по умолчанию: user.

spring.datasource.password:<br> по умолчанию: pass.

spring.data.redis.port:<br> по умолчанию: 6379.

spring.data.redis.host:<br> по умолчанию: localhost.

app.redis.enabled::<br> по умолчанию: false.

app.jwt.secretKey:<br> по умолчанию: 635266556A586E32743777217A25432A462D4A614E72357538782F413F442847

app.security.jwt.expiration:<br> по умолчанию:  86400000

app.security.refresh-token.expiration:<br> по умолчанию:  604800000

server.port:<br> по умолчанию: 8088.

app.service.[user, task, comment].defaultPageSize:<br> по умолчанию: 10.

app.service.[user, task, comment].defaultPageNumber:<br> по умолчанию: 0.

### Docker variables

SERVER_URL - url для подключения к Базе данных.<br>
По умолчанию: jdbc:postgresql://db:5432/tms_service_db

SERVER_USERNAME - Имя пользователя в Базе данных.<br>
По умолчанию: user

SERVER_PASS - Пароль пользователя в Базе данных.<br>
По умолчанию: pass

REDIS_ENABLED - Хост подключения redis.<br>
По умолчанию: true

REDIS_HOST - Хост подключения redis.<br>
По умолчанию: redis

REDIS_PORT - Порт подключения redis.<br>
По умолчанию: 6379
