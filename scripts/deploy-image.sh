#!/bin/bash
set -e
set -o pipefail

echo "🚀 CRM Application - AWS EKS Deployment Script"
echo "============================================="

# Prompt for AWS EKS configuration
read -p "Enter AWS region (e.g., us-east-1): " AWS_REGION
read -p "Enter EKS cluster name: " CLUSTER_NAME
read -p "Enter Docker image URI (with tag): " IMAGE_URI

echo "📋 Configuration:"
echo "   AWS Region: $AWS_REGION"
echo "   EKS Cluster: $CLUSTER_NAME"
echo "   Image URI: $IMAGE_URI"
echo

# Prompt for application environment variables
echo "🔧 Application Configuration (press Enter to skip optional variables):"
read -p "Enter DB_HOST (database host): " DB_HOST
read -p "Enter DB_PORT (default: 3306): " DB_PORT
DB_PORT=${DB_PORT:-3306}
read -p "Enter DB_NAME (default: crm): " DB_NAME
DB_NAME=${DB_NAME:-crm}
read -p "Enter DB_USER (default: root): " DB_USER
DB_USER=${DB_USER:-root}
read -s -p "Enter DB_PASSWORD: " DB_PASSWORD
echo
read -p "Enter SSL Certificate ARN (for HTTPS): " SSL_CERT_ARN

echo
echo "🔧 Configuring kubectl for EKS cluster..."
aws eks update-kubeconfig --region $AWS_REGION --name $CLUSTER_NAME

echo "🔍 Verifying cluster connectivity..."
kubectl cluster-info || {
    echo "❌ Failed to connect to EKS cluster"
    exit 1
}

echo "📝 Updating Kubernetes manifests..."
# Create temporary directory for processed manifests
mkdir -p /tmp/k8s-manifests
cp -r kubernetes/* /tmp/k8s-manifests/

# Replace placeholders in manifests
sed -i 's|{{IMAGE_URI}}|'$IMAGE_URI'|g' /tmp/k8s-manifests/*.yaml
sed -i 's|{{DB_HOST}}|'$DB_HOST'|g' /tmp/k8s-manifests/*.yaml
sed -i 's|{{DB_PORT}}|'$DB_PORT'|g' /tmp/k8s-manifests/*.yaml
sed -i 's|{{DB_NAME}}|'$DB_NAME'|g' /tmp/k8s-manifests/*.yaml
sed -i 's|{{DB_USER}}|'$DB_USER'|g' /tmp/k8s-manifests/*.yaml
sed -i 's|{{SSL_CERT_ARN}}|'$SSL_CERT_ARN'|g' /tmp/k8s-manifests/*.yaml

echo "🔐 Creating database password secret..."
kubectl create secret generic crm-app-secrets \
  --from-literal=db-password="$DB_PASSWORD" \
  --namespace=crm-app \
  --dry-run=client -o yaml | kubectl apply -f -

echo "🚀 Deploying to Kubernetes..."
echo "   📦 Creating namespace..."
kubectl apply -f /tmp/k8s-manifests/namespace.yaml

echo "   📦 Deploying application..."
kubectl apply -f /tmp/k8s-manifests/deployment.yaml

echo "   📦 Creating service..."
kubectl apply -f /tmp/k8s-manifests/service.yaml

echo "   📦 Creating ingress..."
kubectl apply -f /tmp/k8s-manifests/ingress.yaml

echo "⏳ Waiting for deployment rollout..."
kubectl rollout status deployment/crm-app -n crm-app --timeout=300s

echo "🔍 Verifying deployment..."
kubectl get pods,svc,ingress -n crm-app

echo
echo "✅ Deployment completed successfully!"
echo
echo "📋 Application Information:"
echo "   Namespace: crm-app"
echo "   Service: crm-app-service"
echo "   Image: $IMAGE_URI"
echo
echo "🌐 Access your application:"
echo "   Internal: http://crm-app-service.crm-app.svc.cluster.local"
echo "   External: Check ingress for external URL"
echo
echo "📊 Monitoring commands:"
echo "   kubectl get pods -n crm-app"
echo "   kubectl logs -f deployment/crm-app -n crm-app"
echo "   kubectl describe deployment crm-app -n crm-app"

# Cleanup
rm -rf /tmp/k8s-manifests

echo
echo "🎉 Deployment script completed!"
