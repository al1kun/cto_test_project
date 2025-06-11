# Проект «STO Test Project»

> **Микросервис заявок в СТО**, реализованный на Spring Boot с PostgreSQL, Kafka и Liquibase. Включает конфигурацию Docker Compose.

---

## Содержание

1. [Требования](#требования)
2. [Быстрый старт](#быстрый-старт)
    1. [Клонирование и сборка](#клонирование-и-сборка)
    2. [Запуск через Docker Compose](#запуск-через-docker-compose)
    3. [Запуск «локально» без Docker](#запуск-локально-без-docker)
4. [API Проверка](#api-проверка)
5. [Запуск тестов](#запуск-тестов)
6. [Остановка и очистка](#остановка-и-очистка)
7. [Устранение неполадок](#устранение-неполадок)

---

## Требования

- **Java 21** (JDK)
- **Maven 3.8+**
- **Docker** & **Docker Compose** (v2)
- **Postman**
- (Опционально) IDE (IntelliJ IDEA, VS Code и т.п.)

---

---

## Быстрый старт

### Клонирование и сборка

```bash
git clone https://github.com/al1kun/cto_test_project.git
cd cto_test_project

# Сборка и установка в локальный репозиторий Maven
mvn clean install -DskipTests
```

> Важно: Модуль core зависит от артефакта notification, поэтому сначала установите оба.

### Запуск через Docker Compose

```bash
docker-compose up -d --build
```

Контейнеры:
- PostgreSQL ➔ порт 5432
- Zookeeper ➔ порт 2181
- Kafka ➔ порт 9092
- Приложение ➔ порт 8080

Логи приложения:

```bash
docker-compose logs -f app
```

### Запуск «локально» без Docker

1. Убедитесь, что локально запущены PostgreSQL, Zookeeper и Kafka.
2. В ```bash core/src/main/resources/application.yml ``` или через переменные окружения задайте:
```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/sto_db
    username: username
    password: password
  kafka:
    bootstrap-servers: localhost:9092
```
3. Запустите:
```bash
cd core
mvn spring-boot:run
```

## API-проверка
Воспользуйтесь набором запросов к базовым REST-эндпоинтам. Пример в Postman:

### 1. Создать новую заявку
- **Метод:** POST
- **URL:** ``` http://localhost:8080/api/requests/create```
- **Body(JSON):**
```json
{
  "clientId": 3,
  "description": "Replace oil filter"
}
```
- **Ожидаемый ответ:**
  - HTTP 201 Created
  - Location: /api/requests/{id}
  - Body:
```json
{
  "id": 1,
  "clientId": 3,
  "description": "Replace oil filter",
  "status": "NEW",
  "createdAt": "2025-06-10T12:34:56.789"
}
```

### 2. Получить заявки по клиенту

- **Метод:** GET
- **URL:** ``` http://localhost:8080/api/requests/by-client?clientId=3```
- **Ожидаемый ответ:**
  - HTTP 200 OK
  - Body: массив DTO, например:
```json
[
  {
    "id": 1,
    "clientId": 3,
    "description": "Replace oil filter",
    "status": "NEW",
    "createdAt": "2025-06-10T12:34:56.789"
  }
]
```

### 3. Получить заявки по статусу

- **Метод:** GET
- - **URL:** ``` http://localhost:8080/api/requests/by-status?NEW```
- **Ожидаемый ответ:**
  - HTTP 200 OK
  - Body: массив DTO со статусом NEW.

### 4. Изменить статус заявки

- **Метод:** PUT
- **URL:** ``` http://localhost:8080/api/requests/{id}/status```
- **Body(JSON):**
```json
{
  "newStatus": "IN_PROGRESS",
  "changedBy": "operator1",
  "reason": "Started repair"
}
```
- **Ожидаемый ответ:**
  - HTTP 200 OK
  - Body: обновлённый RequestDto.

## Запуск тестов

```bash
{
  cd core
  mvn test
}
```

> **Unit и Integration тесты** поднимают контейнер PostgreSQL через Testcontainers.

## Остановка и очистка

```bash
{
  docker-compose down --volumes
}
```

## Устранение неполадок

- **Postman 404/500:**
    Проверьте, что сервис запущен на ```localhost:8080```.
- **Liquibase ошибки:**
  Убедитесь, что ```${spring.datasource.*}``` в ```application.yml``` прописаны без плейсхолдеров.
