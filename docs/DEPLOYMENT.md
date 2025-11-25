# CRM Application - AWS EKS Deployment Guide

## Table of Contents
1. [Prerequisites](#prerequisites)
2. [Local Development Setup](#local-development-setup)
3. [Building Docker Image](#building-docker-image)
4. [AWS EKS Prerequisites](#aws-eks-prerequisites)
5. [EKS Cluster Setup](#eks-cluster-setup)
6. [Deploying to EKS](#deploying-to-eks)
7. [Configuration Management](#configuration-management)
8. [Monitoring and Troubleshooting](#monitoring-and-troubleshooting)
9. [Scaling and Updates](#scaling-and-updates)
10. [Security Considerations](#security-considerations)

---

## Prerequisites

### System Requirements
- **Docker**: Version 20.10 or higher
- **AWS CLI**: Version 2.x or higher
- **kubectl**: Version 1.23 or higher
- **eksctl**: Version 0.100 or higher (optional, for cluster creation)
- **Java**: JDK 8 (for local development)
- **Maven**: 3.6 or higher (for local development)

### AWS Account Requirements
- Active AWS account with appropriate permissions
- IAM permissions for:
  - EKS cluster management
  - ECR repository access
  - VPC and networking resources
  - IAM role creation

---

## Local Development Setup

### 1. Clone the Repository
```bash
git clone <repository-url>
cd crm
```

### 2. Configure Application Properties
Create a local `application-dev.properties` file in `src/main/resources/`:

```properties
spring.jpa.hibernate.ddl-auto=create-drop
spring.sql.init.mode=always
spring.datasource.url=jdbc:mysql://localhost:3306/crm?useSSL=false
spring.datasource.username=root
spring.datasource.password=yourpassword

management.security.enabled=false
management.context-path=/appinfo

spring.thymeleaf.mode=LEGACYHTML5
spring.thymeleaf.cache=false
```

### 3. Run Locally with Maven
```bash
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

### 4. Run with Docker Compose
```bash
# Set environment variables
export DB_HOST=mysql-host
export DB_PASSWORD=your-db-password

# Start application
docker-compose up -d

# View logs
docker-compose logs -f crm-app

# Stop application
docker-compose down
```

Access the application at: http://localhost:8080

---

## Building Docker Image

### Using Build Script (Recommended)

#### Linux/macOS:
```bash
cd scripts
chmod +x build-push.sh
./build-push.sh
```

#### Windows:
```cmd
cd scripts
build-push.bat
```

The script will prompt you for:
1. Registry type (AWS ECR or Docker Hub)
2. Registry credentials and details
3. Image tag (default: latest)

### Manual Build

#### Build locally:
```bash
docker build -t crm:latest .
```

#### Tag for ECR:
```bash
REGION=us-east-1
ACCOUNT_ID=$(aws sts get-caller-identity --query Account --output text)
REPO_NAME=crm

docker tag crm:latest $ACCOUNT_ID.dkr.ecr.$REGION.amazonaws.com/$REPO_NAME:latest
```

#### Push to ECR:
```bash
aws ecr get-login-password --region $REGION | docker login --username AWS --password-stdin $ACCOUNT_ID.dkr.ecr.$REGION.amazonaws.com

aws ecr create-repository --repository-name $REPO_NAME --region $REGION 2>/dev/null || true

docker push $ACCOUNT_ID.dkr.ecr.$REGION.amazonaws.com/$REPO_NAME:latest
```

---

## AWS EKS Prerequisites

### 1. Install AWS CLI
```bash
# Linux/macOS
curl "https://awscli.amazonaws.com/awscli-exe-linux-x86_64.zip" -o "awscliv2.zip"
unzip awscliv2.zip
sudo ./aws/install

# Verify installation
aws --version
```

### 2. Configure AWS CLI
```bash
aws configure
```

Provide:
- AWS Access Key ID
- AWS Secret Access Key
- Default region (e.g., us-east-1)
- Default output format (json)

### 3. Install kubectl
```bash
# Linux
curl -LO "https://dl.k8s.io/release/$(curl -L -s https://dl.k8s.io/release/stable.txt)/bin/linux/amd64/kubectl"
sudo install -o root -g root -m 0755 kubectl /usr/local/bin/kubectl

# Verify installation
kubectl version --client
```

### 4. Install eksctl (Optional)
```bash
curl --silent --location "https://github.com/weaveworks/eksctl/releases/latest/download/eksctl_$(uname -s)_amd64.tar.gz" | tar xz -C /tmp
sudo mv /tmp/eksctl /usr/local/bin

# Verify installation
eksctl version
```

---

## EKS Cluster Setup

### Option 1: Create Cluster with eksctl (Recommended)

```bash
export CLUSTER_NAME=crm-cluster
export REGION=us-east-1

eksctl create cluster \
  --name $CLUSTER_NAME \
  --region $REGION \
  --version 1.27 \
  --nodegroup-name crm-nodes \
  --node-type t3.medium \
  --nodes 2 \
  --nodes-min 2 \
  --nodes-max 4 \
  --managed
```

This will:
- Create a new VPC with public and private subnets
- Create an EKS cluster
- Create a managed node group with 2-4 t3.medium instances
- Configure kubectl context automatically

### Option 2: Use Existing Cluster

```bash
export CLUSTER_NAME=your-existing-cluster
export REGION=us-east-1

# Update kubeconfig
aws eks update-kubeconfig --region $REGION --name $CLUSTER_NAME

# Verify connectivity
kubectl cluster-info
kubectl get nodes
```

### Install AWS Load Balancer Controller (Required for Ingress)

```bash
# Create IAM policy
curl -o iam_policy.json https://raw.githubusercontent.com/kubernetes-sigs/aws-load-balancer-controller/v2.5.4/docs/install/iam_policy.json

aws iam create-policy \
  --policy-name AWSLoadBalancerControllerIAMPolicy \
  --policy-document file://iam_policy.json

# Create service account
eksctl create iamserviceaccount \
  --cluster=$CLUSTER_NAME \
  --namespace=kube-system \
  --name=aws-load-balancer-controller \
  --attach-policy-arn=arn:aws:iam::$(aws sts get-caller-identity --query Account --output text):policy/AWSLoadBalancerControllerIAMPolicy \
  --override-existing-serviceaccounts \
  --approve

# Install controller with Helm
helm repo add eks https://aws.github.io/eks-charts
helm repo update

helm install aws-load-balancer-controller eks/aws-load-balancer-controller \
  -n kube-system \
  --set clusterName=$CLUSTER_NAME \
  --set serviceAccount.create=false \
  --set serviceAccount.name=aws-load-balancer-controller

# Verify installation
kubectl get deployment -n kube-system aws-load-balancer-controller
```

---

## Deploying to EKS

### Using Deployment Script (Recommended)

#### Linux/macOS:
```bash
cd scripts
chmod +x deploy-image.sh
./deploy-image.sh
```

#### Windows:
```cmd
cd scripts
deploy-image.bat
```

The script will prompt you for:
1. AWS Region
2. EKS Cluster Name
3. Docker Image URI
4. Database configuration
5. Application environment variables

### Manual Deployment

#### 1. Create Database Secret (Important)
```bash
kubectl create namespace crm

kubectl create secret generic crm-db-secret \
  --from-literal=password=your-db-password \
  -n crm
```

#### 2. Update Kubernetes Manifests

Edit `kubernetes/deployment.yaml` and replace placeholders:
- `{{IMAGE_URI}}` → Your ECR image URI
- `{{DB_HOST}}` → Your database host
- `{{DB_PORT}}` → Your database port (default: 3306)
- `{{DB_NAME}}` → Your database name (default: crm)
- `{{DB_USERNAME}}` → Your database username
- `{{HIBERNATE_DDL_AUTO}}` → Hibernate DDL mode (update/validate/none)
- `{{SQL_INIT_MODE}}` → SQL init mode (never/always)

#### 3. Apply Kubernetes Manifests
```bash
# Create namespace
kubectl apply -f kubernetes/namespace.yaml

# Deploy application
kubectl apply -f kubernetes/deployment.yaml

# Create service
kubectl apply -f kubernetes/service.yaml

# Create ingress
kubectl apply -f kubernetes/ingress.yaml

# Wait for rollout
kubectl rollout status deployment/crm -n crm
```

#### 4. Verify Deployment
```bash
# Check pods
kubectl get pods -n crm

# Check services
kubectl get svc -n crm

# Check ingress
kubectl get ingress -n crm

# View logs
kubectl logs -f deployment/crm -n crm
```

#### 5. Get Application URL
```bash
kubectl get ingress crm-ingress -n crm -o jsonpath='{.status.loadBalancer.ingress[0].hostname}'
```

The application will be available at the ALB hostname after DNS propagation (2-5 minutes).

---

## Configuration Management

### Environment Variables

The application uses the following environment variables:

| Variable | Description | Default |
|----------|-------------|--------|
| `SPRING_PROFILES_ACTIVE` | Active Spring profile | production |
| `DB_HOST` | Database host | localhost |
| `DB_PORT` | Database port | 3306 |
| `DB_NAME` | Database name | crm |
| `DB_USERNAME` | Database username | root |
| `DB_PASSWORD` | Database password | (from secret) |
| `HIBERNATE_DDL_AUTO` | Hibernate DDL mode | update |
| `SQL_INIT_MODE` | SQL initialization | never |
| `JAVA_OPTS` | JVM options | -Xmx512m -Xms256m |

### ConfigMaps (Optional)

For non-sensitive configuration:

```bash
kubectl create configmap crm-config \
  --from-literal=HIBERNATE_DDL_AUTO=update \
  --from-literal=SQL_INIT_MODE=never \
  -n crm
```

Update deployment to use ConfigMap:
```yaml
envFrom:
- configMapRef:
    name: crm-config
```

### Secrets Management

For sensitive data, always use Kubernetes Secrets:

```bash
# Create from literal
kubectl create secret generic crm-db-secret \
  --from-literal=password=your-password \
  -n crm

# Create from file
echo -n 'your-password' > db-password.txt
kubectl create secret generic crm-db-secret \
  --from-file=password=db-password.txt \
  -n crm
```

---

## Monitoring and Troubleshooting

### Check Application Health

```bash
# Get pod status
kubectl get pods -n crm

# Check pod details
kubectl describe pod <pod-name> -n crm

# View logs
kubectl logs -f deployment/crm -n crm

# View previous logs (if pod crashed)
kubectl logs <pod-name> -n crm --previous

# Execute commands in pod
kubectl exec -it <pod-name> -n crm -- /bin/sh
```

### Health Check Endpoints

```bash
# Port forward to access health endpoints
kubectl port-forward -n crm deployment/crm 8080:8080

# Check health (in another terminal)
curl http://localhost:8080/appinfo/health
curl http://localhost:8080/appinfo/info
```

### Common Issues

#### 1. Image Pull Errors

**Error**: `ErrImagePull` or `ImagePullBackOff`

**Solution**:
```bash
# Check if ECR permissions are correct
aws ecr describe-repositories --repository-names crm

# Verify image exists
aws ecr describe-images --repository-name crm --region us-east-1

# Check if node IAM role has ECR permissions
kubectl describe pod <pod-name> -n crm
```

#### 2. CrashLoopBackOff

**Error**: Pod keeps restarting

**Solution**:
```bash
# Check logs
kubectl logs <pod-name> -n crm --previous

# Common causes:
# - Database connection issues
# - Missing environment variables
# - Application errors

# Verify database connectivity
kubectl exec -it <pod-name> -n crm -- nc -zv $DB_HOST $DB_PORT
```

#### 3. Database Connection Issues

**Error**: Cannot connect to database

**Solution**:
```bash
# Check secret exists
kubectl get secret crm-db-secret -n crm

# Verify environment variables
kubectl exec <pod-name> -n crm -- env | grep DB_

# Check database security groups allow EKS cluster
# Update RDS security group to allow traffic from EKS worker nodes
```

#### 4. Ingress Not Working

**Error**: Cannot access application via ingress

**Solution**:
```bash
# Check ingress status
kubectl describe ingress crm-ingress -n crm

# Verify AWS Load Balancer Controller is running
kubectl get deployment -n kube-system aws-load-balancer-controller

# Check ALB logs in AWS Console
# Check ALB target group health
```

### Performance Monitoring

```bash
# View resource usage
kubectl top pods -n crm
kubectl top nodes

# Describe deployment for resource limits
kubectl describe deployment crm -n crm
```

---

## Scaling and Updates

### Manual Scaling

```bash
# Scale up
kubectl scale deployment crm --replicas=4 -n crm

# Scale down
kubectl scale deployment crm --replicas=2 -n crm

# Verify
kubectl get pods -n crm
```

### Horizontal Pod Autoscaler (HPA)

```bash
# Create HPA
kubectl autoscale deployment crm \
  --cpu-percent=70 \
  --min=2 \
  --max=10 \
  -n crm

# Check HPA status
kubectl get hpa -n crm
kubectl describe hpa crm -n crm
```

### Rolling Updates

```bash
# Update image
kubectl set image deployment/crm \
  crm=123456789012.dkr.ecr.us-east-1.amazonaws.com/crm:v2.0.0 \
  -n crm

# Check rollout status
kubectl rollout status deployment/crm -n crm

# View rollout history
kubectl rollout history deployment/crm -n crm
```

### Rollback

```bash
# Rollback to previous version
kubectl rollout undo deployment/crm -n crm

# Rollback to specific revision
kubectl rollout undo deployment/crm --to-revision=2 -n crm

# Check rollout status
kubectl rollout status deployment/crm -n crm
```

---

## Security Considerations

### 1. Image Security

- Use specific image tags (not `latest`)
- Scan images for vulnerabilities:
  ```bash
  aws ecr start-image-scan \
    --repository-name crm \
    --image-id imageTag=v1.0.0 \
    --region us-east-1
  
  aws ecr describe-image-scan-findings \
    --repository-name crm \
    --image-id imageTag=v1.0.0 \
    --region us-east-1
  ```

### 2. Network Security

- Use Network Policies to restrict pod-to-pod communication
- Configure security groups for database access
- Use private subnets for pods when possible

### 3. Secrets Management

- Never commit secrets to version control
- Use Kubernetes Secrets for sensitive data
- Consider AWS Secrets Manager integration:
  ```bash
  # Install External Secrets Operator
  helm repo add external-secrets https://charts.external-secrets.io
  helm install external-secrets \
    external-secrets/external-secrets \
    -n external-secrets-system \
    --create-namespace
  ```

### 4. RBAC Configuration

```bash
# Create read-only role for developers
kubectl create role crm-reader \
  --verb=get,list,watch \
  --resource=pods,services,deployments \
  -n crm

# Bind role to user
kubectl create rolebinding crm-reader-binding \
  --role=crm-reader \
  --user=developer@example.com \
  -n crm
```

### 5. Pod Security

- Run as non-root user (already configured)
- Use read-only root filesystem where possible
- Set resource limits to prevent resource exhaustion
- Enable security context:
  ```yaml
  securityContext:
    runAsNonRoot: true
    runAsUser: 1000
    fsGroup: 1000
    capabilities:
      drop:
      - ALL
  ```

---

## Spring Boot Specific Notes

### 1. Actuator Endpoints

Health checks use Spring Boot Actuator at `/appinfo/health`

Available endpoints:
- `/appinfo/health` - Application health status
- `/appinfo/info` - Application information
- `/appinfo/metrics` - Application metrics

### 2. JVM Configuration

The application uses the following JVM settings:
```
-Xmx512m -Xms256m -XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0
```

Adjust in `kubernetes/deployment.yaml` if needed:
```yaml
env:
- name: JAVA_OPTS
  value: "-Xmx1g -Xms512m -XX:+UseContainerSupport"
```

### 3. Spring Profiles

The deployment uses `production` profile by default. Create additional profiles:

```properties
# application-production.properties
spring.jpa.hibernate.ddl-auto=validate
spring.sql.init.mode=never
logging.level.root=INFO
```

### 4. Database Initialization

- **Development**: Use `create-drop` or `create` with `spring.sql.init.mode=always`
- **Production**: Use `validate` or `none` with `spring.sql.init.mode=never`
- Use Flyway or Liquibase for production database migrations

---

## Additional Resources

- [AWS EKS Documentation](https://docs.aws.amazon.com/eks/)
- [Kubernetes Documentation](https://kubernetes.io/docs/)
- [Spring Boot on Kubernetes](https://spring.io/guides/gs/spring-boot-kubernetes/)
- [AWS Load Balancer Controller](https://kubernetes-sigs.github.io/aws-load-balancer-controller/)

---

## Support and Maintenance

### Backup and Disaster Recovery

```bash
# Backup Kubernetes resources
kubectl get all -n crm -o yaml > crm-backup.yaml

# Backup secrets (encrypted)
kubectl get secrets -n crm -o yaml > crm-secrets-backup.yaml
```

### Clean Up

```bash
# Delete application
kubectl delete namespace crm

# Delete cluster (if needed)
eksctl delete cluster --name $CLUSTER_NAME --region $REGION
```

---

For questions or issues, please contact the DevOps team or create an issue in the project repository.