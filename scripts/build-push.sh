#!/bin/bash
set -e

echo "=== Docker Build and Push Script for CRM Application ==="

# Sanitize project name for Docker tag
PROJECT_NAME="crm"
IMAGE_NAME=$(echo "$PROJECT_NAME" | tr '[:upper:]' '[:lower:]' | tr -cs 'a-z0-9' '-' | sed 's/^-*//;s/-*$//')

echo "Project: $PROJECT_NAME"
echo "Image name: $IMAGE_NAME"

# Get image tag
echo
read -p "Enter image tag (default: latest): " IMAGE_TAG
IMAGE_TAG=${IMAGE_TAG:-latest}

# Sanitize tag
IMAGE_TAG=$(echo "$IMAGE_TAG" | tr '[:upper:]' '[:lower:]' | tr -cs 'a-z0-9.-' '-' | sed 's/^-*//;s/-*$//')
echo "Using tag: $IMAGE_TAG"

echo
echo "Select registry type:"
echo "1. AWS ECR"
echo "2. Docker Hub"
read -p "Enter choice (1-2): " REGISTRY_CHOICE

case $REGISTRY_CHOICE in
    1)
        echo "=== AWS ECR Configuration ==="
        read -p "Enter AWS region (default: us-east-1): " AWS_REGION
        AWS_REGION=${AWS_REGION:-us-east-1}
        
        read -p "Enter AWS Account ID: " AWS_ACCOUNT_ID
        if [ -z "$AWS_ACCOUNT_ID" ]; then
            echo "Error: AWS Account ID is required"
            exit 1
        fi
        
        ECR_REPO=$IMAGE_NAME
        REGISTRY_URL="$AWS_ACCOUNT_ID.dkr.ecr.$AWS_REGION.amazonaws.com"
        FULL_IMAGE_NAME="$REGISTRY_URL/$ECR_REPO:$IMAGE_TAG"
        
        echo "Full image name: $FULL_IMAGE_NAME"
        
        echo "Logging in to ECR..."
        aws ecr get-login-password --region $AWS_REGION | docker login --username AWS --password-stdin $REGISTRY_URL
        
        echo "Checking if ECR repository exists..."
        aws ecr describe-repositories --repository-names $ECR_REPO --region $AWS_REGION >/dev/null 2>&1 || {
            echo "Creating ECR repository: $ECR_REPO"
            aws ecr create-repository --repository-name $ECR_REPO --region $AWS_REGION
        }
        ;;
    2)
        echo "=== Docker Hub Configuration ==="
        read -p "Enter Docker Hub username: " DOCKER_USERNAME
        if [ -z "$DOCKER_USERNAME" ]; then
            echo "Error: Docker Hub username is required"
            exit 1
        fi
        
        read -s -p "Enter Docker Hub password/token: " DOCKER_PASSWORD
        echo
        
        FULL_IMAGE_NAME="$DOCKER_USERNAME/$IMAGE_NAME:$IMAGE_TAG"
        
        echo "Full image name: $FULL_IMAGE_NAME"
        
        echo "Logging in to Docker Hub..."
        echo $DOCKER_PASSWORD | docker login --username $DOCKER_USERNAME --password-stdin
        ;;
    *)
        echo "Invalid choice"
        exit 1
        ;;
esac

echo
echo "Building Docker image..."
docker build -t $FULL_IMAGE_NAME .

echo "Pushing Docker image..."
docker push $FULL_IMAGE_NAME

echo
echo "=== Build and Push Complete ==="
echo "Image: $FULL_IMAGE_NAME"
echo "Ready for deployment to AWS EKS"
echo
echo "Next steps:"
echo "1. Run deploy-image.sh to deploy to EKS"
echo "2. Use image URI: $FULL_IMAGE_NAME"