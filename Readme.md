**Run RabbitMQ docker container**
```bash
docker run -d
--name rabbit-server
-p 5672:5672
-p 15672:15672
-e RABBITMQ_DEFAULT_PASS=secret
-e RABBITMQ_DEFAULT_USER=myuser
rabbitmq:3.13-management
```