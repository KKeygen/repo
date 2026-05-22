# Dismai deployment plan (contiguous port block)

## 1) Current server status (observed)

- Listening ports include: 22, 80, 3307, 3309, 6380, 6082-6087, 8026, 8082, 8088, 8091, 8092, 8099, 8848, 9090, 9092, 9200, 9300, 9848-9849, 10082, 11434, 3389.
- Running containers (excerpt): dismai-* services, mysql, redis, nacos, kafka, elasticsearch, sentinel, plus other unrelated stacks.

This means the default ports in docker-compose.yml are already occupied on this server.

## 2) Proposed contiguous port block

Pick a small, uncommon, contiguous host port block: 22100-22117 (18 ports).

Mapping (host -> container):

| Host | Service | Container Port |
|------|---------|----------------|
| 22100 | mysql | 3306 |
| 22101 | redis | 6379 |
| 22102 | nacos | 8848 |
| 22103 | nacos | 9848 |
| 22104 | nacos | 9849 |
| 22105 | kafka | 9092 |
| 22106 | elasticsearch | 9200 |
| 22107 | elasticsearch | 9300 |
| 22108 | sentinel | 8858 |
| 22109 | gateway | 6085 |
| 22110 | user | 6082 |
| 22111 | program | 6086 |
| 22112 | order | 8081 |
| 22113 | pay | 6087 |
| 22114 | base-data | 6083 |
| 22115 | customize | 6084 |
| 22116 | admin | 10082 |
| 22117 | frontend (nginx) | 80 |

Note: this server deployment currently starts only the backend stack. Port 22117 is reserved for a future frontend deployment and is not occupied now.

## 3) Create a ports override file

Create docker-compose.ports.yml next to docker-compose.yml with only port overrides:

```yaml
services:
  mysql:
    ports: ["22100:3306"]
  redis:
    ports: ["22101:6379"]
  nacos:
    ports:
      - "22102:8848"
      - "22103:9848"
      - "22104:9849"
  kafka:
    ports: ["22105:9092"]
  elasticsearch:
    ports:
      - "22106:9200"
      - "22107:9300"
  sentinel:
    ports: ["22108:8858"]
  gateway:
    ports: ["22109:6085"]
  user:
    ports: ["22110:6082"]
  program:
    ports: ["22111:6086"]
  order:
    ports: ["22112:8081"]
  pay:
    ports: ["22113:6087"]
  base-data:
    ports: ["22114:6083"]
  customize:
    ports: ["22115:6084"]
  admin:
    ports: ["22116:10082"]
  frontend:
    ports: ["22117:80"]
```

## 4) Deploy steps

1) Verify the block is free:

```bash
ss -tulpn | grep 221
```

2) Stop the existing stack if it is running (no volume deletion):

```bash
cd /home/user/winstar/repo
docker compose -f docker-compose.yml down
```

3) Start with the override file:

```bash
docker compose -f docker-compose.yml -f docker-compose.ports.yml up -d
```

## 5) Access points after deploy

- Frontend: http://<server-ip>:22117
- Gateway: http://<server-ip>:22109
- Nacos: http://<server-ip>:22102/nacos
- Sentinel: http://<server-ip>:22108

Current status on this server: backend only, no frontend container is running.

## 6) Firewall (if enabled)

Open the contiguous range 22100-22117.

- UFW example:

```bash
sudo ufw allow 22100:22117/tcp
```

- firewalld example:

```bash
sudo firewall-cmd --add-port=22100-22117/tcp --permanent
sudo firewall-cmd --reload
```

## 7) Quick verification

```bash
ss -tulpn | grep -E ':(2210[0-9]|2211[0-7])'
```

```bash
curl -I http://127.0.0.1:22117
```
