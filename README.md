## Docker
A simple docker example using springboot and database as mysql/mariadb. It also includes steps to deploy it in AWS


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
Since springboot application need to connect to database, both the containers should be present in same docker network. Note - If user doesn't provide network explicitly docker will run container in repective default network
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

Check all running containers
```
docker ps -a
```

Check logs
```
docker logs -f hello-docker
```

Stop running process
```
docker stop hello-docker
```

Remove the stopped process
```
docker rm hello-docker
```

Delete an image
```
docker rmi hello-docker
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

### AWS steps

Create build for ECR
```
docker buildx build --platform=linux/arm64 -t 695663959248.dkr.ecr.us-east-1.amazonaws.com/hello-docker .
```

Create tag for ECR
```
docker tag 695663959248.dkr.ecr.us-east-1.amazonaws.com/hello-docker 695663959248.dkr.ecr.us-east-1.amazonaws.com/hello-docker:v1.0
```

Login to AWS (Dont forget to update aws_access_key_id and aws_secret_access_key in ~/.aws/credentials)
```
aws ecr get-login-password --region us-east-1 | docker login --username AWS --password-stdin 695663959248.dkr.ecr.us-east-1.amazonaws.com
```

Create private ECR repository
```
aws ecr create-repository --repository-name hello-docker
```

Push image to the ECR repository
```
docker push 695663959248.dkr.ecr.us-east-1.amazonaws.com/hello-docker:v1.0
```

List images in ECR repository
```
aws ecr list-images --repository-name hello-docker
```

Delete image from ECR
```
aws ecr batch-delete-image --repository-name hello-docker --image-ids imageTag=v2.0
```

Delete ECR repository
```
aws ecr delete-repository --repository-name test --force
```

**Docker ECS** context is required to run docker compose in AWS
```
docker context create ecs myecscontext
```

List all docker context
```
docker context ls
docker context use myecscontext
```

Run docker compose in AWS (Note - docker compose as two different words unlike docker-compose)
```
docker compose -f aws-docker-compose.yaml up
```

## kubernetes

### Run [minikube](https://minikube.sigs.k8s.io/docs/start/) locally

To start a local Kubernetes cluster
```
minikube start
```

Access the Kubernetes dashboard running within the minikube cluster
```
minikube dashboard
```

Connect to LoadBalancer services
```
minikube tunnel
```

Apply Kubernetes configuration using [kubectl](https://kubernetes.io/docs/tasks/tools/)
```
kubectl apply -f hello-kube.yaml 
```

Check running pods
```
kubectl get pods
```

Check running services
```
kubectl get services
```

To run ingress locally you would need to install a controller like - [NGINX Ingress Controller](https://kubernetes.github.io/ingress-nginx/deploy/)
```
helm upgrade --install ingress-nginx ingress-nginx \
  --repo https://kubernetes.github.io/ingress-nginx \
  --namespace ingress-nginx --create-namespace
```

Check ingress
```
kubectl get ingress
NAME         CLASS    HOSTS        ADDRESS     PORTS   AGE
my-ingress   <none>   myhost.com   127.0.0.1   80      66s
```

Update `/etc/hosts` file with
```
127.0.0.1   myhost.com
```

Delete all resources by file names
```
kubectl delete -f hello-kube.yaml

```

## Helm
Helm is a package manager for Kubernetes applications - https://helm.sh/

Create a new helm chart with a given name
```
helm create hello-helm
```

Test a chart for possible issues
```
helm lint ./hello-helm
```

View helm template 
```
helm template ./hello-helm 
```

Install the chart into Kubernetes cluster
```
helm install hello-helm ./hello-helm
```

List all installed helm charts
```
helm list -a
```

Uninstall the chart
```
helm uninstall hello-helm
```
