# AutopartsStore Analytics Service

Микросервис для сбора и хранения аналитических данных о пользовательских событиях.

## Стек технологий

- Java 25
- Spring Boot 4.1.1
- Apache Kafka (Consumer, batch-режим)
- ClickHouse (OLAP база данных)
- Flyway
- Docker + Docker Compose

## Роль в архитектуре

Этот сервис является частью событийно-ориентированной архитектуры проекта **AutopartsStore**. 
Он независим от основного монолита и общается с ним исключительно через Kafka (Event-Driven Architecture).

**Поток данных:**
1. Основной монолит генерирует события (клики, заказы) и отправляет их в Kafka (через паттерн Outbox).
2. Analytics Service читает эти события из топика `client-events-topic`.
3. Данные батчами записываются в ClickHouse для быстрой аналитики.
4. Grafana визуализирует данные из ClickHouse.

## Как запустить

### Через Docker Compose (рекомендуется)

Для локальной разработки сервис изолирован и поднимает все свои зависимости (Kafka, ClickHouse):

```bash
# Запустить сервис вместе с инфраструктурой
docker compose up -d --build
```

Сервис будет доступен на http://localhost:8000 (эндпоинты Actuator).

## Локально
```
# Собрать JAR
mvn clean package -DskipTests

# Запустить
java -jar target/*.jar
```

Для локального запуска убедитесь, что Kafka и ClickHouse доступны на `localhost:29092` и `localhost:8123` соответственно.

## Конфигурация

| Переменная |	Описание |	По умолчанию |
|---|---|---|
| `KAFKA_BOOTSTRAP_SERVERS` |	Адрес брокера Kafka |	kafka:9092 (в Docker) |
| `SPRING_DATASOURCE_URL` |	JDBC URL ClickHouse	| jdbc:clickhouse://clickhouse:8123/default |
## Схема данных (ClickHouse)
Таблица event создается автоматически через Flyway:

```sql
CREATE TABLE IF NOT EXISTS event (
    event_id UInt64,
    event_type String,
    user_id Nullable(UInt64),
    event_date DateTime,
    session_id UInt32
) ENGINE = ReplacingMergeTree()
ORDER BY event_id;
```

Движок ReplacingMergeTree обеспечивает идемпотентность: при повторном чтении сообщения из Kafka (at-least-once) дубликаты будут удалены при слиянии.

## Безопасность

- Сервис является внутренним (не публикует публичных API).
- Для локальной разработки креденшелы ClickHouse задаются через переменные окружения в `docker-compose.yml`. В продакшене они должны передаваться через секреты (например, K8s Secrets или Vault).

## Дальнейшее развитие

- Подключение Grafana к ClickHouse для визуализации аналитики.
