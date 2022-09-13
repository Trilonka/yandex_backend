# Yandex backend (Yandex Disk analog) on Java

[Описание задания](./enrollment/Task.md) и [openAPI specification](./enrollment/openapi.yaml).

Для начала используйте git clone, чтобы скачать проект на свой компьютер

```
git clone https://github.com/Trilonka/yandex_backend.git
```

## Docker Compose

### Запуск приложения

Соберите jar файл:

```
./mvnw clean install
```

Если Вы хотите пропустить тесты при сборке, используйте:

```
./mvnw clean install -DskipTests=true
```

При установленном docker-compose все можно запустить одной командой:

```
docker-compose up
```

Все необходимые образы будут загружены автоматически.

Для запуска в фоновом режиме можно использовать:

```
docker-compose up -d
```

### Остановка приложения

Остановить все запущенные контейнеры также можно одной командой:

```
docker-compose down
```

Если вам нужно остановить и удалить все контейнеры и все образы, используемые какой-либо службой в файле docker-compose.yml, используйте команду
(все данные, ранее сохраненные в таблице, будут удалены):

```
docker-compose down --rmi all
```

### Автоматический запуск приложения после перезагрузки системы

Если приложение необходимо автоматически запускать при перезагрузке системы, то создайте следующий файл (путь указан первой строкой):

```properties
# /etc/systemd/system/yandex-backend-app.service

[Unit]
Description=Yandex Backend Application Service
Requires=docker.service
After=docker.service

[Service]
Type=oneshot
RemainAfterExit=yes
WorkingDirectory=/srv/yandex_backend
ExecStart=/usr/local/bin/docker-compose up
ExecStop=/usr/local/bin/docker-compose down
TimeoutStartSec=0

[Install]
WantedBy=multi-user.target
```

Измените WorkingDirectory параметр на путь к проекту и включите службу для автоматического запуска:

```bash
systemctl enable yandex-backend-app
```

## Самостоятельная инициализация и запуск

### Инициализация

Создайте или используйте имеющуюся базу данных postgresql и измените файл (путь указан первой строкой):

```properties
#src/main/resources/application.properties

spring.datasource.url=jdbc:postgreqsl://localhost:5432/database_name
spring.datasource.username=user_name
spring.datasource.password=user_password

spring.jpa.database-platform=org.hibernate.dialect.PostgreSQLDialect

spring.jpa.properties.hibernate.jdbc.batch_size=4
spring.jpa.properties.hibernate.order_inserts=true
```

Заменив spring.datasource.url, spring.datasource.username и spring.datasource.password на Ваши данные для подключения к базе данных.

В своей базе данных создайте необходимые таблицы. SQL для их создания находится в [этом файле](./init.sql).

### Запуск

Запустить приложение можно просто, используя:

```
./mvnw spring-boot:run
```

Или при собранном jar:

```
java -jar .\target\yandexBackend-0.0.1-SNAPSHOT.jar
```
