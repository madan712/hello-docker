## Docker
A simple docker example using springboot and database as mysql/mariadb


Create local image
```
cd hello-docker
docker build --tag hello-docker .
```

Check generated image
```
docker images
```

Create local tag
```
docker tag hello-docker madan712/hello-docker:v1.0  
```

### Run individual container(s) in a docker network
Since springboot application need to connect to database, both the containers should be present in same docker network
Note - If user doesn't provide network explicitly docker will run container in repective default network
```
docker network create mynetwork
```

Check the created network
```
docker network ls
```

Run database
```
docker run -d --name mysqldb -p 3306:3306  -e MYSQL_ROOT_PASSWORD=pass123 -e MYSQL_DATABASE=mydb --network mynetwork mariadb
```

Get into database container shell
```
docker exec -it mysqldb /bin/sh
mysql -u root -p
```

Run springboot application
```
docker run -d --name hello-docker -p 8080:8080 \
-e SPRING_DATASOURCE_URL="jdbc:mysql://mysqldb:3306/mydb?useSSL=false" \
-e SPRING_DATASOURCE_USERNAME=root \
-e SPRING_DATASOURCE_PASSWORD=pass123 \
--network mynetwork \
madan712/hello-docker:v1.0
```

Check logs
```
docker logs -f hello-docker
```

Check all running containers
```
docker ps -a
```

### For multi-container docker application, better option is to use docker compose
Run all the containers (defualt file  -f docker-compose.yaml)
```
docker-compose up -d
```

Check logs
```
docker logs -f hello-docker
```

Get into container shell
```
docker exec -it hello-docker /bin/sh
```

Stop the container
```
docker-compose down
```

Optional - Push local image to docker hub
```
docker push madan712/hello-docker:v1.0 
```

## kubernetes
