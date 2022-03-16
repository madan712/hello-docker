# hello-docker
A simple docker example using springboot and mysql

Some usefull docker commands

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

Run the container
```
docker-compose up -d
```

Check all running containers
```
docker ps -a
```

Check logs
```
docker logs -f hello-docker
```

Look inside container shell
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
