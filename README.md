# hello-docker
A simple docker example using springboot and mysql

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
docker-compose -f docker-compose.yaml up -d
```

Check logs
```
docker logs -f hello-docker
```

Optional - Push image to docker hub to share with the world
```
docker push madan712/hello-docker:v1.0 
```
