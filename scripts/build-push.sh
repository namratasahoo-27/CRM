#!/bin/bash
set -e

echo "🚀 CRM Application - Docker Build and Push Script"
echo "================================================="

# Get project name and sanitize it
PROJECT_NAME="crm-app"
IMAGE_NAME=$(echo "$PROJECT_NAME" | tr '[:upper:]' '[:lower:]' | tr -cs 'a-z0-9' '-' | sed 's/^-*//;s/-*$//')

echo "📦 Project: $PROJECT_NAME"
echo "🏷️  Image Name: $IMAGE_NAME"
echo

# Prompt for image tag
read -p "Enter image tag (default: latest): " IMAGE_TAG
IMAGE_TAG=${IMAGE_TAG:-latest}
IMAGE_TAG=$(echo "$IMAGE_TAG" | tr '[:upper:]' '[:lower:]' | tr -cs 'a-z0-9.-' '-' | sed 's/^-*//;s/-*$//')

echo "🏷️  Using tag: $IMAGE_TAG"
echo

# Registry selection
echo "Select Docker registry:"
echo "1. AWS ECR"
echo "2. Docker Hub"
read -p "Choose registry (1-2): " REGISTRY_CHOICE

case $REGISTRY_CHOICE in
    1)
        echo "🔧 Configuring AWS ECR..."
        read -p "Enter AWS region (e.g., us-east-1): " AWS_REGION
        read -p "Enter ECR repository name (default: $IMAGE_NAME): " ECR_REPO
        ECR_REPO=${ECR_REPO:-$IMAGE_NAME}
        
        # Get AWS account ID
        AWS_ACCOUNT_ID=$(aws sts get-caller-identity --query Account --output text)
        REGISTRY_URL="$AWS_ACCOUNT_ID.dkr.ecr.$AWS_REGION.amazonaws.com"
        FULL_IMAGE_NAME="$REGISTRY_URL/$ECR_REPO:$IMAGE_TAG"
        
        echo "🔐 Logging into AWS ECR..."
        aws ecr get-login-password --region $AWS_REGION | docker login --username AWS --password-stdin $REGISTRY_URL
        
        echo "📋 Creating ECR repository if it doesn't exist..."
        aws ecr describe-repositories --repository-names $ECR_REPO --region $AWS_REGION >/dev/null 2>&1 || \
        aws ecr create-repository --repository-name $ECR_REPO --region $AWS_REGION
        ;;
    2)
        echo "🔧 Configuring Docker Hub..."
        read -p "Enter Docker Hub username: " DOCKER_USERNAME
        read -s -p "Enter Docker Hub password: " DOCKER_PASSWORD
        echo
        
        FULL_IMAGE_NAME="$DOCKER_USERNAME/$IMAGE_NAME:$IMAGE_TAG"
        
        echo "🔐 Logging into Docker Hub..."
        echo $DOCKER_PASSWORD | docker login --username $DOCKER_USERNAME --password-stdin
        ;;
    *)
        echo "❌ Invalid choice. Exiting."
        exit 1
        ;;
esac

echo "🏗️  Building Docker image: $FULL_IMAGE_NAME"
docker build -t $FULL_IMAGE_NAME .

echo "📤 Pushing Docker image: $FULL_IMAGE_NAME"
docker push $FULL_IMAGE_NAME

echo "✅ Build and push completed successfully!"
echo "🎯 Image: $FULL_IMAGE_NAME"
echo "💡 Use this image URI in your deployment scripts."
