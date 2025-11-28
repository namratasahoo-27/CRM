# CRM Application Deployment Guide

## Table of Contents
1. [Prerequisites](#prerequisites)
2. [Local Development Setup](#local-development-setup)
3. [Docker Deployment](#docker-deployment)
4. [AWS EKS Deployment](#aws-eks-deployment)
5. [Configuration Management](#configuration-management)
6. [Troubleshooting](#troubleshooting)
7. [Scaling and Management](#scaling-and-management)
8. [Security Considerations](#security-considerations)

## Prerequisites

### System Requirements
- Java 8 or higher
- Docker 20.10 or higher
- Docker Compose 2.0 or higher
- kubectl 1.20 or higher
- AWS CLI 2.0 or higher
- Git

### AWS Requirements
- AWS Account with appropriate permissions
- EKS cluster (or ability to create one)
- ECR registry access (optional, for AWS ECR)
- IAM user/role with following permissions:
  - EKS cluster access
  - ECR repository management
  - CloudFormation (if creating EKS cluster)

## Local Development Setup

### 1. Clone Repository
```bash
git clone <repository-url>
cd crm-application
```

### 2. Environment Configuration
Create a `.env` file with your local settings:
```env
DB_HOST=localhost
DB_PORT=3306
DB_NAME=crm
DB_USERNAME=root
DB_PASSWORD=yourpassword
SPRING_PROFILES_ACTIVE=dev
```

### 3. Database Setup
The application supports both MySQL and H2 database:

**For H2 (Development):**
- No setup required, H2 runs in-memory
- Access H2 console at: http://localhost:8080/h2-console

**For MySQL:**
```sql
CREATE DATABASE crm;
CREATE USER 'crmuser'@'%' IDENTIFIED BY 'password';
GRANT ALL PRIVILEGES ON crm.* TO 'crmuser'@'%';
FLUSH PRIVILEGES;
```

### 4. Local Run with Maven
```bash
# Build the application
mvn clean compile

# Run tests
mvn test

# Run the application
mvn spring-boot:run
```

The application will start on http://localhost:8080

### 5. Application Endpoints
- Main application: http://localhost:8080
- Actuator health: http://localhost:8080/appinfo/health
- Actuator info: http://localhost:8080/appinfo/info

## Docker Deployment

### 1. Build Docker Image
```bash
# Build image locally
docker build -t crm:latest .

# Or use the build script
chmod +x scripts/build-push.sh
./scripts/build-push.sh
```

### 2. Run with Docker Compose
```bash
# Start the application
docker-compose up -d

# View logs
docker-compose logs -f

# Stop the application
docker-compose down
```

### 3. Docker Environment Variables
The docker-compose.yml includes the following environment variables:
- `SPRING_PROFILES_ACTIVE=docker`
- `DB_HOST` - Database host
- `DB_PORT` - Database port
- `DB_NAME` - Database name
- `DB_USERNAME` - Database username
- `DB_PASSWORD` - Database password
- `JAVA_OPTS` - JVM options

## AWS EKS Deployment

### 1. EKS Cluster Setup

#### Option A: Using eksctl (Recommended)
```bash
# Install eksctl
curl --silent --location "https://github.com/weaveworks/eksctl/releases/latest/download/eksctl_$(uname -s)_amd64.tar.gz" | tar xz -C /tmp
sudo mv /tmp/eksctl /usr/local/bin

# Create EKS cluster
eksctl create cluster \
  --name crm-cluster \
  --region us-east-1 \
  --nodegroup-name workers \
  --nodes 2 \
  --nodes-min 1 \
  --nodes-max 3 \
  --node-type t3.medium \
  --managed
```

#### Option B: Using AWS Console
1. Navigate to EKS in AWS Console
2. Create cluster with desired configuration
3. Create node group
4. Configure kubectl access

### 2. Configure kubectl
```bash
# Update kubeconfig
aws eks update-kubeconfig --region us-east-1 --name crm-cluster

# Verify connection
kubectl cluster-info
```

### 3. Install AWS Load Balancer Controller (Required for Ingress)
```bash
# Create IAM OIDC provider
eksctl utils associate-iam-oidc-provider --region us-east-1 --cluster crm-cluster --approve

# Create IAM service account
eksctl create iamserviceaccount \
  --cluster=crm-cluster \
  --namespace=kube-system \
  --name=aws-load-balancer-controller \
  --role-name="AmazonEKSLoadBalancerControllerRole" \
  --attach-policy-arn=arn:aws:iam::aws:policy/ElasticLoadBalancingFullAccess \
  --approve

# Install the controller
helm repo add eks https://aws.github.io/eks-charts
helm repo update

helm install aws-load-balancer-controller eks/aws-load-balancer-controller \
  -n kube-system \
  --set clusterName=crm-cluster \
  --set serviceAccount.create=false \
  --set serviceAccount.name=aws-load-balancer-controller
```

### 4. Build and Push Image
```bash
# Using the build script
chmod +x scripts/build-push.sh
./scripts/build-push.sh

# Follow prompts to:
# 1. Select AWS ECR or Docker Hub
# 2. Provide registry credentials
# 3. Build and push image
```

### 5. Deploy to EKS
```bash
# Run deployment script
chmod +x scripts/deploy-image.sh
./scripts/deploy-image.sh

# Follow prompts to provide:
# - EKS cluster name and region
# - Docker image URI
# - Database configuration
```

### 6. Verify Deployment
```bash
# Check deployment status
kubectl get all -n crm

# View application logs
kubectl logs -f deployment/crm-app -n crm

# Check ingress
kubectl get ingress -n crm
```

## Configuration Management

### Environment Variables
The application uses the following environment variables:

| Variable | Description | Default | Required |
|----------|-------------|---------|----------|
| `DB_HOST` | Database host | localhost | No |
| `DB_PORT` | Database port | 3306 | No |
| `DB_NAME` | Database name | crm | No |
| `DB_USERNAME` | Database username | root | No |
| `DB_PASSWORD` | Database password | - | Yes (for MySQL) |
| `SPRING_PROFILES_ACTIVE` | Spring profile | - | No |
| `JAVA_OPTS` | JVM options | - | No |
| `TZ` | Timezone | UTC | No |

### Kubernetes Secrets
For production deployments, use Kubernetes secrets for sensitive data:

```bash
# Create database secret
kubectl create secret generic db-credentials \
  --from-literal=username=crmuser \
  --from-literal=password=securepassword \
  -n crm

# Update deployment to use secret
kubectl patch deployment crm-app -n crm -p '{
  "spec": {
    "template": {
      "spec": {
        "containers": [{
          "name": "crm-app",
          "env": [
            {
              "name": "DB_PASSWORD",
              "valueFrom": {
                "secretKeyRef": {
                  "name": "db-credentials",
                  "key": "password"
                }
              }
            }
          ]
        }]
      }
    }
  }
}'
```

### ConfigMaps
```bash
# Create application configuration
kubectl create configmap app-config \
  --from-literal=spring.profiles.active=kubernetes \
  --from-literal=logging.level.crm=INFO \
  -n crm
```

## Troubleshooting

### Common Issues

#### 1. Application Not Starting
**Symptoms:** Pod in CrashLoopBackOff
**Solutions:**
```bash
# Check pod events
kubectl describe pod <pod-name> -n crm

# View application logs
kubectl logs <pod-name> -n crm

# Common causes:
# - Database connection issues
# - Missing environment variables
# - Resource limits too low
```

#### 2. Database Connection Issues
**Symptoms:** Connection timeout or authentication errors
**Solutions:**
- Verify database host and port
- Check credentials
- Ensure database is accessible from cluster
- Verify security groups/network policies

#### 3. Health Check Failures
**Symptoms:** Pod restarts frequently
**Solutions:**
```bash
# Adjust health check timing in deployment.yaml
livenessProbe:
  initialDelaySeconds: 180  # Increase for slow startup
  periodSeconds: 30
  timeoutSeconds: 10
```

#### 4. Ingress Not Working
**Symptoms:** Cannot access application externally
**Solutions:**
- Verify AWS Load Balancer Controller is installed
- Check security groups allow traffic
- Verify DNS configuration
- Check ingress annotations

#### 5. Out of Memory Errors
**Symptoms:** Java heap space errors
**Solutions:**
```bash
# Update resource limits in deployment.yaml
resources:
  limits:
    memory: "2Gi"  # Increase memory
    cpu: "1000m"
  requests:
    memory: "1Gi"
    cpu: "500m"

# Adjust JAVA_OPTS
env:
- name: JAVA_OPTS
  value: "-Xmx1024m -Xms512m"
```

### Debug Commands
```bash
# Get cluster info
kubectl cluster-info

# Describe deployment
kubectl describe deployment crm-app -n crm

# Get events
kubectl get events -n crm --sort-by='.lastTimestamp'

# Port forward for testing
kubectl port-forward svc/crm-app-service 8080:80 -n crm

# Execute shell in pod
kubectl exec -it deployment/crm-app -n crm -- /bin/sh

# View resource usage
kubectl top pods -n crm
```

## Scaling and Management

### Manual Scaling
```bash
# Scale deployment
kubectl scale deployment crm-app --replicas=5 -n crm

# Check scaling status
kubectl get deployment crm-app -n crm
```

### Horizontal Pod Autoscaler (HPA)
```yaml
apiVersion: autoscaling/v2
kind: HorizontalPodAutoscaler
metadata:
  name: crm-app-hpa
  namespace: crm
spec:
  scaleTargetRef:
    apiVersion: apps/v1
    kind: Deployment
    name: crm-app
  minReplicas: 2
  maxReplicas: 10
  metrics:
  - type: Resource
    resource:
      name: cpu
      target:
        type: Utilization
        averageUtilization: 70
  - type: Resource
    resource:
      name: memory
      target:
        type: Utilization
        averageUtilization: 80
```

### Rolling Updates
```bash
# Update image
kubectl set image deployment/crm-app crm-app=new-image:tag -n crm

# Watch rollout
kubectl rollout status deployment/crm-app -n crm

# Rollback if needed
kubectl rollout undo deployment/crm-app -n crm
```

### Blue-Green Deployment
For zero-downtime deployments, consider blue-green deployment strategy using tools like Argo Rollouts or Flagger.

## Security Considerations

### 1. Container Security
- Application runs as non-root user (UID 1001)
- Minimal base image (Alpine Linux)
- No unnecessary packages installed
- Regular security updates

### 2. Kubernetes Security
```yaml
# Security context in deployment
securityContext:
  runAsNonRoot: true
  runAsUser: 1001
  fsGroup: 1001
  capabilities:
    drop:
    - ALL
  readOnlyRootFilesystem: true
```

### 3. Network Security
- Use Network Policies to restrict pod communication
- Configure security groups for EKS nodes
- Use private subnets for worker nodes
- Enable encryption in transit and at rest

### 4. Secrets Management
- Use Kubernetes Secrets for sensitive data
- Consider AWS Secrets Manager integration
- Rotate credentials regularly
- Use service accounts with minimal permissions

### 5. Image Security
```bash
# Scan image for vulnerabilities
docker run --rm -v /var/run/docker.sock:/var/run/docker.sock \
  -v $HOME/.cache:/tmp/.cache \
  aquasec/trivy image crm:latest
```

### 6. RBAC Configuration
```yaml
apiVersion: rbac.authorization.k8s.io/v1
kind: Role
metadata:
  namespace: crm
  name: crm-app-role
rules:
- apiGroups: [""]
  resources: ["pods", "services"]
  verbs: ["get", "list"]
---
apiVersion: rbac.authorization.k8s.io/v1
kind: RoleBinding
metadata:
  name: crm-app-rolebinding
  namespace: crm
subjects:
- kind: ServiceAccount
  name: crm-app-sa
  namespace: crm
roleRef:
  kind: Role
  name: crm-app-role
  apiGroup: rbac.authorization.k8s.io
```

## Monitoring and Logging

### Application Metrics
The application includes Spring Boot Actuator for monitoring:
- Health: `/appinfo/health`
- Metrics: `/appinfo/metrics`
- Info: `/appinfo/info`

### Kubernetes Monitoring
```bash
# Install metrics server
kubectl apply -f https://github.com/kubernetes-sigs/metrics-server/releases/latest/download/components.yaml

# View resource usage
kubectl top nodes
kubectl top pods -n crm
```

### Centralized Logging
Consider implementing centralized logging with:
- Fluent Bit for log collection
- Amazon CloudWatch for log storage
- Grafana for log visualization

## Backup and Disaster Recovery

### Database Backup
```bash
# Create database backup job
kubectl create job --from=cronjob/db-backup db-backup-$(date +%s) -n crm
```

### Application State
- Configuration stored in ConfigMaps and Secrets
- Stateless application design enables easy recovery
- Use persistent volumes for any file storage needs

## Performance Tuning

### JVM Tuning
```yaml
env:
- name: JAVA_OPTS
  value: "-Xmx1024m -Xms512m -XX:+UseG1GC -XX:MaxGCPauseMillis=200"
```

### Database Optimization
- Configure connection pooling
- Optimize database queries
- Use database indexing
- Consider read replicas for scaling

### Kubernetes Resource Optimization
```yaml
resources:
  requests:
    cpu: "250m"
    memory: "512Mi"
  limits:
    cpu: "500m"
    memory: "1Gi"
```

## Support and Maintenance

### Regular Maintenance Tasks
1. Update base images regularly
2. Apply security patches
3. Monitor resource usage
4. Review and rotate secrets
5. Update dependencies

### Support Contacts
- Development Team: dev-team@company.com
- DevOps Team: devops@company.com
- On-call Support: +1-XXX-XXX-XXXX

---

**Note:** Replace placeholder values (like domain names, credentials, and contact information) with your actual configuration before deploying to production.