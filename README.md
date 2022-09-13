# Yandex backend (Yandex Disk analog) on Java

Для начала используйте git clone, чтобы скачать проект на свой компьютер

```bash
git clone https://github.com/Trilonka/yandex_backend.git
```

## Docker Compose

### Запуск приложения

При установленном docker-compose все можно запустить одной командой:

```bash
docker-compose up
```

Все необходимые образы будут загружены автоматически.

Для запуска в фоновом режиме можно использовать:

```bash
docker-compose up -d
```

### Остановка приложения

Остановить все запущенные контейнеры также можно одной командой:

```bash
docker-compose down
```

Если вам нужно остановить и удалить все контейнеры и все образы, используемые какой-либо службой в файле docker-compose.yml, используйте команду
(все данные, ранее сохраненные в таблице, будут удалены):

```bash
docker-compose down --rmi all
```

### Автоматический запуск приложения после перезагрузки системы

Если приложение необходимо автоматически запускать при перезагрузке системы, то создайте следующий файл (путь указан первой строкой):

```bash
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

### Запуск приложения

