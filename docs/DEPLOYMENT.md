# CRM Application Deployment Guide

This guide provides comprehensive instructions for deploying the Java Spring Boot CRM application using Docker and AWS EKS (Elastic Kubernetes Service).

## Table of Contents

- [Prerequisites](#prerequisites)
- [Application Overview](#application-overview)
- [Local Development Setup](#local-development-setup)
- [Docker Deployment](#docker-deployment)
- [AWS EKS Deployment](#aws-eks-deployment)
- [Configuration Management](#configuration-management)
- [Monitoring and Troubleshooting](#monitoring-and-troubleshooting)
- [Security Considerations](#security-considerations)
- [Scaling and Maintenance](#scaling-and-maintenance)

## Prerequisites

### System Requirements

- **Docker**: Version 20.10+
- **Docker Compose**: Version 2.0+
- **AWS CLI**: Version 2.0+
- **kubectl**: Version 1.21+
- **Java**: 8+ (for local development)
- **Maven**: 3.6+ (for local development)

### AWS Requirements

- AWS Account with appropriate permissions
- EKS cluster created and configured
- ECR repository (optional, for AWS registry)
- IAM roles and policies for EKS
- AWS Load Balancer Controller installed in EKS cluster

### Required AWS IAM Permissions

```json
{
    "Version": "2012-10-17",
    "Statement": [
        {
            "Effect": "Allow",
            "Action": [
                "eks:DescribeCluster",
                "eks:ListClusters",
                "ecr:GetAuthorizationToken",
                "ecr:BatchCheckLayerAvailability",
                "ecr:GetDownloadUrlForLayer",
                "ecr:BatchGetImage",
                "ecr:DescribeRepositories",
                "ecr:CreateRepository"
            ],
            "Resource": "*"
        }
    ]
}
```

## Application Overview

### Technology Stack

- **Framework**: Spring Boot 1.5.10.RELEASE
- **Java Version**: 8
- **Build Tool**: Maven
- **Database**: MySQL (production) / H2 (development)
- **Security**: Spring Security
- **Template Engine**: Thymeleaf
- **Additional Features**: PDF generation, CSV export, Excel export

### Application Architecture

The CRM application follows a typical Spring Boot MVC architecture:

- **Controllers**: Handle HTTP requests and responses
- **Services**: Business logic layer
- **Repositories**: Data access layer (Spring Data JPA)
- **Entities**: JPA entities representing database tables
- **Views**: Thymeleaf templates for web interface

### Key Features

- Customer management
- Contract management
- User authentication and authorization
- Data export (PDF, CSV, Excel)
- Search and filtering capabilities
- Admin panel

## Local Development Setup

### 1. Clone and Setup

```bash
# Navigate to project directory
cd /path/to/crm-application

# Verify Maven installation
mvn --version

# Install dependencies
mvn clean install
```

### 2. Database Configuration

For local development, the application uses H2 in-memory database by default. For MySQL:

```properties
# application-dev.properties
spring.datasource.url=jdbc:mysql://localhost:3306/crm
spring.datasource.username=root
spring.datasource.password=password
spring.jpa.hibernate.ddl-auto=update
```

### 3. Run Locally

```bash
# Run with development profile
mvn spring-boot:run -Dspring-boot.run.profiles=dev

# Or run the JAR
mvn clean package
java -jar target/crm-0.0.1-SNAPSHOT.jar --spring.profiles.active=dev
```

Access the application at: `http://localhost:8080`

### 4. Local Docker Development

```bash
# Build and run with Docker Compose
docker-compose up --build

# Run in background
docker-compose up -d

# View logs
docker-compose logs -f crm-app

# Stop and cleanup
docker-compose down -v
```

## Docker Deployment

### 1. Build Docker Image

```bash
# Make scripts executable
chmod +x scripts/build-push.sh

# Run build script
./scripts/build-push.sh
```

The script will:
- Prompt for registry choice (ECR or Docker Hub)
- Build the Docker image using multi-stage build
- Push to the selected registry

### 2. Manual Docker Build

```bash
# Build image
docker build -t crm-app:latest .

# Run container
docker run -d \
  --name crm-app \
  -p 8080:8080 \
  -e DB_HOST=your-db-host \
  -e DB_PASSWORD=your-password \
  crm-app:latest
```

### 3. Docker Compose Deployment

Create a `.env` file:

```env
DB_HOST=mysql-server
DB_PORT=3306
DB_NAME=crm
DB_USER=root
DB_PASSWORD=your-secure-password
```

Deploy:

```bash
docker-compose up -d
```

## AWS EKS Deployment

### 1. EKS Cluster Setup

If you don't have an EKS cluster, create one:

```bash
# Install eksctl
curl --silent --location "https://github.com/weaveworks/eksctl/releases/latest/download/eksctl_$(uname -s)_amd64.tar.gz" | tar xz -C /tmp
sudo mv /tmp/eksctl /usr/local/bin

# Create EKS cluster
eksctl create cluster \
  --name crm-cluster \
  --region us-east-1 \
  --nodegroup-name crm-nodes \
  --node-type t3.medium \
  --nodes 2 \
  --nodes-min 1 \
  --nodes-max 4

# Install AWS Load Balancer Controller
kubectl apply -k "github.com/aws/eks-charts/stable/aws-load-balancer-controller/crds"
helm repo add eks https://aws.github.io/eks-charts
helm install aws-load-balancer-controller eks/aws-load-balancer-controller \
  -n kube-system \
  --set clusterName=crm-cluster
```

### 2. Deploy Application

```bash
# Make script executable
chmod +x scripts/deploy-image.sh

# Run deployment script
./scripts/deploy-image.sh
```

The deployment script will:
1. Configure kubectl for your EKS cluster
2. Prompt for configuration values
3. Create Kubernetes secrets
4. Deploy all Kubernetes manifests
5. Wait for rollout completion
6. Verify deployment

### 3. Manual Kubernetes Deployment

```bash
# Configure kubectl
aws eks update-kubeconfig --region us-east-1 --name crm-cluster

# Create namespace
kubectl apply -f kubernetes/namespace.yaml

# Create secrets
kubectl create secret generic crm-app-secrets \
  --from-literal=db-password="your-password" \
  --namespace=crm-app

# Update deployment with your image
sed -i 's|{{IMAGE_URI}}|your-registry/crm-app:latest|g' kubernetes/deployment.yaml

# Deploy manifests
kubectl apply -f kubernetes/deployment.yaml
kubectl apply -f kubernetes/service.yaml
kubectl apply -f kubernetes/ingress.yaml

# Check status
kubectl get all -n crm-app
```

### 4. Access Application

```bash
# Get ingress URL
kubectl get ingress crm-app-ingress -n crm-app

# Port forward for testing
kubectl port-forward svc/crm-app-service 8080:80 -n crm-app
```

## Configuration Management

### Environment Variables

The application supports the following environment variables:

| Variable | Description | Default | Required |
|----------|-------------|---------|----------|
| `DB_HOST` | Database host | localhost | Yes |
| `DB_PORT` | Database port | 3306 | No |
| `DB_NAME` | Database name | crm | No |
| `DB_USER` | Database username | root | No |
| `DB_PASSWORD` | Database password | - | Yes |
| `SPRING_PROFILES_ACTIVE` | Spring profile | - | No |
| `JAVA_OPTS` | JVM options | - | No |

### Spring Profiles

- `default`: Uses H2 in-memory database
- `docker`: Docker-specific configuration
- `kubernetes`: Kubernetes-specific configuration
- `prod`: Production configuration

### Database Configuration

For production MySQL setup:

```sql
-- Create database
CREATE DATABASE crm CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- Create user
CREATE USER 'crmuser'@'%' IDENTIFIED BY 'secure-password';
GRANT ALL PRIVILEGES ON crm.* TO 'crmuser'@'%';
FLUSH PRIVILEGES;
```

## Monitoring and Troubleshooting

### Health Checks

The application provides health endpoints:

- Health: `http://localhost:8080/appinfo/health`
- Info: `http://localhost:8080/appinfo/info`
- Metrics: `http://localhost:8080/appinfo/metrics`

### Kubernetes Troubleshooting

```bash
# Check pod status
kubectl get pods -n crm-app

# View pod logs
kubectl logs -f deployment/crm-app -n crm-app

# Describe deployment
kubectl describe deployment crm-app -n crm-app

# Check events
kubectl get events -n crm-app --sort-by=.metadata.creationTimestamp

# Execute into pod
kubectl exec -it deployment/crm-app -n crm-app -- /bin/bash
```

### Common Issues

1. **Pod CrashLoopBackOff**
   - Check application logs
   - Verify environment variables
   - Ensure database connectivity

2. **Database Connection Issues**
   - Verify database credentials
   - Check network connectivity
   - Ensure database is running

3. **Ingress Not Working**
   - Verify AWS Load Balancer Controller is installed
   - Check security groups
   - Verify SSL certificate ARN

### Performance Monitoring

```bash
# Resource usage
kubectl top pods -n crm-app
kubectl top nodes

# Horizontal Pod Autoscaler
kubectl autoscale deployment crm-app --cpu-percent=70 --min=2 --max=10 -n crm-app
```

## Security Considerations

### Container Security

- Application runs as non-root user
- Minimal base image (OpenJDK 8)
- No unnecessary packages installed
- Security updates applied

### Kubernetes Security

- Separate namespace for isolation
- Resource limits and requests defined
- Secrets used for sensitive data
- Network policies (recommended)

### Database Security

- Use dedicated database user with minimal privileges
- Enable SSL/TLS for database connections
- Regular security updates
- Backup and encryption

### Best Practices

1. **Use Kubernetes Secrets** for sensitive data
2. **Enable RBAC** in EKS cluster
3. **Use Network Policies** to restrict pod communication
4. **Regular Updates** of base images and dependencies
5. **Monitor Security** with tools like Falco or Twistlock

## Scaling and Maintenance

### Horizontal Pod Autoscaling

```yaml
apiVersion: autoscaling/v2
kind: HorizontalPodAutoscaler
metadata:
  name: crm-app-hpa
  namespace: crm-app
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
# Update deployment with new image
kubectl set image deployment/crm-app crm-app=new-image:tag -n crm-app

# Check rollout status
kubectl rollout status deployment/crm-app -n crm-app

# Rollback if needed
kubectl rollout undo deployment/crm-app -n crm-app
```

### Backup and Recovery

1. **Database Backups**: Regular automated backups
2. **Application State**: Stateless application design
3. **Configuration**: Version control all configuration
4. **Disaster Recovery**: Multi-AZ deployment

### Maintenance Tasks

- **Regular Security Updates**: Update base images and dependencies
- **Performance Monitoring**: Monitor application metrics
- **Log Rotation**: Implement log retention policies
- **Resource Optimization**: Review and adjust resource limits

## Java-Specific Configuration

### JVM Tuning

For production workloads, consider these JVM options:

```bash
JAVA_OPTS="-Xmx1g -Xms512m -XX:+UseG1GC -XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0 -Djava.security.egd=file:/dev/./urandom -XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=/app/logs/"
```

### Spring Boot Configuration

```properties
# Production configuration
server.port=8080
management.endpoints.web.exposure.include=health,info,metrics
management.endpoint.health.show-details=when_authorized
logging.level.org.springframework.security=INFO
logging.file.name=/app/logs/crm-app.log
```

### Memory Management

Monitor and adjust:

- Heap size based on application requirements
- Container memory limits
- Garbage collection settings
- Connection pool sizes

## Support and Troubleshooting

For support and additional troubleshooting:

1. **Check Application Logs**: Look for Spring Boot startup messages and errors
2. **Database Connectivity**: Verify database connection and permissions
3. **Resource Constraints**: Ensure adequate CPU and memory
4. **Network Issues**: Check service discovery and ingress configuration
5. **Security Policies**: Verify RBAC and network policies

---

**Note**: This deployment guide is specific to the Java Spring Boot CRM application. Adjust configurations based on your specific environment and requirements.
