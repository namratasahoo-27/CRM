#!/bin/bash
set -e
set -o pipefail

echo "========================================"
echo "AWS EKS Deployment Script"
echo "========================================"
echo ""

# Prompt for AWS configuration
read -p "Enter AWS Region (e.g., us-east-1): " AWS_REGION
read -p "Enter EKS Cluster Name: " CLUSTER_NAME

if [ -z "$AWS_REGION" ] || [ -z "$CLUSTER_NAME" ]; then
    echo "Error: AWS Region and EKS Cluster Name are required"
    exit 1
fi

echo ""
echo "AWS Region: $AWS_REGION"
echo "EKS Cluster: $CLUSTER_NAME"
echo ""

# Prompt for Docker image URI
read -p "Enter Docker Image URI (e.g., 123456789012.dkr.ecr.us-east-1.amazonaws.com/crm:latest): " IMAGE_URI

if [ -z "$IMAGE_URI" ]; then
    echo "Error: Docker Image URI is required"
    exit 1
fi

echo "Image URI: $IMAGE_URI"
echo ""

# Prompt for database configuration
echo "=== Database Configuration ==="
read -p "Enter Database Host (default: localhost): " DB_HOST
DB_HOST=${DB_HOST:-localhost}

read -p "Enter Database Port (default: 3306): " DB_PORT
DB_PORT=${DB_PORT:-3306}

read -p "Enter Database Name (default: crm): " DB_NAME
DB_NAME=${DB_NAME:-crm}

read -p "Enter Database Username (default: root): " DB_USERNAME
DB_USERNAME=${DB_USERNAME:-root}

read -p "Enter Hibernate DDL Auto (default: update): " HIBERNATE_DDL_AUTO
HIBERNATE_DDL_AUTO=${HIBERNATE_DDL_AUTO:-update}

read -p "Enter SQL Init Mode (default: never): " SQL_INIT_MODE
SQL_INIT_MODE=${SQL_INIT_MODE:-never}

echo ""
echo "Database Host: $DB_HOST"
echo "Database Port: $DB_PORT"
echo "Database Name: $DB_NAME"
echo "Database Username: $DB_USERNAME"
echo "Hibernate DDL Auto: $HIBERNATE_DDL_AUTO"
echo "SQL Init Mode: $SQL_INIT_MODE"
echo ""

# Configure kubectl
echo "========================================"
echo "Configuring kubectl for EKS cluster..."
echo "========================================"
aws eks update-kubeconfig --region "$AWS_REGION" --name "$CLUSTER_NAME"

if [ $? -ne 0 ]; then
    echo "Error: Failed to configure kubectl"
    exit 1
fi

echo ""
echo "Verifying cluster connectivity..."
kubectl cluster-info

if [ $? -ne 0 ]; then
    echo "Error: Cannot connect to Kubernetes cluster"
    exit 1
fi

echo ""
echo "========================================"
echo "Updating Kubernetes manifests..."
echo "========================================"

# Update deployment.yaml with actual values
sed -i "s|{{IMAGE_URI}}|$IMAGE_URI|g" kubernetes/deployment.yaml
sed -i "s|{{DB_HOST}}|$DB_HOST|g" kubernetes/deployment.yaml
sed -i "s|{{DB_PORT}}|$DB_PORT|g" kubernetes/deployment.yaml
sed -i "s|{{DB_NAME}}|$DB_NAME|g" kubernetes/deployment.yaml
sed -i "s|{{DB_USERNAME}}|$DB_USERNAME|g" kubernetes/deployment.yaml
sed -i "s|{{HIBERNATE_DDL_AUTO}}|$HIBERNATE_DDL_AUTO|g" kubernetes/deployment.yaml
sed -i "s|{{SQL_INIT_MODE}}|$SQL_INIT_MODE|g" kubernetes/deployment.yaml

echo "Manifests updated successfully"
echo ""

# Apply Kubernetes manifests
echo "========================================"
echo "Deploying to Kubernetes..."
echo "========================================"

echo "Creating namespace..."
kubectl apply -f kubernetes/namespace.yaml

echo "Deploying application..."
kubectl apply -f kubernetes/deployment.yaml

echo "Creating service..."
kubectl apply -f kubernetes/service.yaml

echo "Creating ingress..."
kubectl apply -f kubernetes/ingress.yaml

echo ""
echo "========================================"
echo "Waiting for deployment to complete..."
echo "========================================"
kubectl rollout status deployment/crm -n crm --timeout=5m

if [ $? -ne 0 ]; then
    echo "Warning: Deployment rollout did not complete in time"
    echo "Check status with: kubectl get pods -n crm"
fi

echo ""
echo "========================================"
echo "Deployment Summary"
echo "========================================"
kubectl get pods,svc,ingress -n crm

echo ""
echo "========================================"
echo "Deployment Complete!"
echo "========================================"
echo ""
echo "To check application logs:"
echo "  kubectl logs -f deployment/crm -n crm"
echo ""
echo "To check pod status:"
echo "  kubectl get pods -n crm"
echo ""
echo "To get ingress URL:"
echo "  kubectl get ingress crm-ingress -n crm"
echo ""
echo "To rollback deployment:"
echo "  kubectl rollout undo deployment/crm -n crm"
echo ""