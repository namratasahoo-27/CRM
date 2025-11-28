#!/bin/bash
set -e
set -o pipefail

echo "=== AWS EKS Deployment Script for CRM Application ==="

# Get EKS cluster information
read -p "Enter AWS region (default: us-east-1): " AWS_REGION
AWS_REGION=${AWS_REGION:-us-east-1}

read -p "Enter EKS cluster name: " CLUSTER_NAME
if [ -z "$CLUSTER_NAME" ]; then
    echo "Error: EKS cluster name is required"
    exit 1
fi

# Get Docker image URI
read -p "Enter Docker image URI (registry/repo:tag): " IMAGE_URI
if [ -z "$IMAGE_URI" ]; then
    echo "Error: Docker image URI is required"
    exit 1
fi

echo
echo "=== Environment Configuration ==="
read -p "Enter database host (optional, press Enter to skip): " DB_HOST
read -p "Enter database port (default: 3306): " DB_PORT
DB_PORT=${DB_PORT:-3306}
read -p "Enter database name (default: crm): " DB_NAME
DB_NAME=${DB_NAME:-crm}
read -p "Enter database username (default: root): " DB_USERNAME
DB_USERNAME=${DB_USERNAME:-root}
read -s -p "Enter database password (optional): " DB_PASSWORD
echo

# Configure kubectl
echo
echo "Configuring kubectl for EKS cluster: $CLUSTER_NAME"
aws eks update-kubeconfig --region $AWS_REGION --name $CLUSTER_NAME

# Verify cluster connectivity
echo "Verifying cluster connectivity..."
kubectl cluster-info || {
    echo "Error: Unable to connect to EKS cluster"
    exit 1
}

# Create temporary directory for manifests
TEMP_DIR=$(mktemp -d)
cp -r kubernetes/* $TEMP_DIR/

# Replace placeholders in manifests
echo "Updating Kubernetes manifests with deployment values..."
sed -i 's|{{IMAGE_URI}}|'$IMAGE_URI'|g' $TEMP_DIR/deployment.yaml

if [ -n "$DB_HOST" ]; then
    sed -i 's|{{DB_HOST}}|'$DB_HOST'|g' $TEMP_DIR/deployment.yaml
else
    sed -i 's|{{DB_HOST}}|mysql-service|g' $TEMP_DIR/deployment.yaml
fi

sed -i 's|{{DB_PORT}}|'$DB_PORT'|g' $TEMP_DIR/deployment.yaml
sed -i 's|{{DB_NAME}}|'$DB_NAME'|g' $TEMP_DIR/deployment.yaml
sed -i 's|{{DB_USERNAME}}|'$DB_USERNAME'|g' $TEMP_DIR/deployment.yaml

if [ -n "$DB_PASSWORD" ]; then
    sed -i 's|{{DB_PASSWORD}}|'$DB_PASSWORD'|g' $TEMP_DIR/deployment.yaml
else
    sed -i 's|{{DB_PASSWORD}}|changeme|g' $TEMP_DIR/deployment.yaml
fi

# Deploy to EKS
echo
echo "Deploying to EKS cluster..."

echo "Creating namespace..."
kubectl apply -f $TEMP_DIR/namespace.yaml

echo "Deploying application..."
kubectl apply -f $TEMP_DIR/deployment.yaml

echo "Creating service..."
kubectl apply -f $TEMP_DIR/service.yaml

echo "Creating ingress..."
kubectl apply -f $TEMP_DIR/ingress.yaml

# Wait for deployment to be ready
echo
echo "Waiting for deployment to be ready..."
kubectl rollout status deployment/crm-app -n crm --timeout=300s

# Verify deployment
echo
echo "Verifying deployment..."
kubectl get pods,svc,ingress -n crm

# Get application URL
echo
echo "=== Deployment Complete ==="
echo "Application deployed successfully to EKS cluster: $CLUSTER_NAME"
echo "Namespace: crm"
echo "Image: $IMAGE_URI"
echo
echo "To access the application:"
echo "1. Get the ingress URL: kubectl get ingress -n crm"
echo "2. Access via: http://crm-app.example.com (configure DNS accordingly)"
echo "3. Port-forward for testing: kubectl port-forward svc/crm-app-service 8080:80 -n crm"
echo
echo "Monitoring commands:"
echo "- View logs: kubectl logs -f deployment/crm-app -n crm"
echo "- Scale app: kubectl scale deployment crm-app --replicas=3 -n crm"
echo "- Get status: kubectl get all -n crm"

# Cleanup
rm -rf $TEMP_DIR

echo
echo "Deployment script completed successfully!"