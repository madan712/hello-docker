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

### Deploy docker application to AWS Elastic container service

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

Get into container (pod)
```
kubectl exec --stdin --tty [pod-name] -- /bin/sh
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

Install the chart into Kubernetes cluster, gets default values from values.yaml
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

## Deploy helm chart to AWS Elastic kubernetes service

Create EKS cluster
```
eksctl create cluster --name mycluster --node-type t2.large --nodes 3 --nodes-min 3 --nodes-max 5 --region us-east-1 --zones us-east-1a,us-east-1b,us-east-1c,us-east-1d,us-east-1f
```

### Install [AWS Load Balancer Controller](https://artifacthub.io/packages/helm/aws/aws-load-balancer-controller)
Create IAM OIDC provider
```
eksctl utils associate-iam-oidc-provider \
    --region <aws-region> \
    --cluster <your-cluster-name> \
    --approve
```

Create an IAM policy called AWSLoadBalancerControllerIAMPolicy
```
curl -o iam-policy.json https://raw.githubusercontent.com/kubernetes-sigs/aws-load-balancer-controller/main/docs/install/iam_policy.json
aws iam create-policy \
    --policy-name AWSLoadBalancerControllerIAMPolicy \
    --policy-document file://iam-policy.json
```

Create a IAM role and ServiceAccount for the Load Balancer controller, use the ARN from the step above
```
eksctl create iamserviceaccount \
--cluster=<cluster-name> \
--namespace=kube-system \
--name=aws-load-balancer-controller \
--attach-policy-arn=arn:aws:iam::<AWS_ACCOUNT_ID>:policy/AWSLoadBalancerControllerIAMPolicy \
--approve
```

Add the EKS repository to Helm
```
helm repo add eks https://aws.github.io/eks-charts
```

Install the TargetGroupBinding CRDs
```
kubectl apply -k "github.com/aws/eks-charts/stable/aws-load-balancer-controller//crds?ref=master"
```

Install the AWS Load Balancer controller
```
helm upgrade -i aws-load-balancer-controller eks/aws-load-balancer-controller -n kube-system --set clusterName=<k8s-cluster-name> --set serviceAccount.create=false --set serviceAccount.name=aws-load-balancer-controller
```

Install helm chat with aws specific values
```
helm install hello-helm -f aws-values.yaml ./hello-helm
```

NOTE - remow host and change below annotations in ingress.yaml to run the helm chat in AWS EKS
```
annotations:
  kubernetes.io/ingress.class: alb
  alb.ingress.kubernetes.io/scheme: internet-facing
  alb.ingress.kubernetes.io/target-type: ip
```
