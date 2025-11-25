#!/bin/bash
set -e
set -o pipefail

echo "========================================"
echo "Docker Build and Push Script"
echo "========================================"
echo ""

# Project configuration
PROJECT_NAME="crm"
IMAGE_NAME=$(echo "$PROJECT_NAME" | tr '[:upper:]' '[:lower:]' | tr -cs 'a-z0-9' '-' | sed 's/^-*//;s/-*$//')

echo "Project: $PROJECT_NAME"
echo "Image name: $IMAGE_NAME"
echo ""

# Prompt for image tag
echo "Enter image tag (default: latest):"
read -r IMAGE_TAG
IMAGE_TAG=${IMAGE_TAG:-latest}
IMAGE_TAG=$(echo "$IMAGE_TAG" | tr '[:upper:]' '[:lower:]' | tr -cs 'a-z0-9.-' '-' | sed 's/^-*//;s/-*$//')

echo "Using tag: $IMAGE_TAG"
echo ""

# Registry selection
echo "Select container registry:"
echo "1. AWS ECR (Elastic Container Registry)"
echo "2. Docker Hub"
read -p "Enter choice (1 or 2): " REGISTRY_CHOICE

if [ "$REGISTRY_CHOICE" = "1" ]; then
    echo ""
    echo "=== AWS ECR Configuration ==="
    
    read -p "Enter AWS Region (e.g., us-east-1): " AWS_REGION
    read -p "Enter ECR Repository Name (default: $IMAGE_NAME): " ECR_REPO
    ECR_REPO=${ECR_REPO:-$IMAGE_NAME}
    
    # Get AWS Account ID
    echo "Retrieving AWS Account ID..."
    AWS_ACCOUNT_ID=$(aws sts get-caller-identity --query Account --output text)
    
    if [ -z "$AWS_ACCOUNT_ID" ]; then
        echo "Error: Failed to retrieve AWS Account ID. Please check AWS CLI configuration."
        exit 1
    fi
    
    REGISTRY_URL="$AWS_ACCOUNT_ID.dkr.ecr.$AWS_REGION.amazonaws.com"
    FULL_IMAGE_NAME="$REGISTRY_URL/$ECR_REPO:$IMAGE_TAG"
    
    echo "Registry URL: $REGISTRY_URL"
    echo "Full image name: $FULL_IMAGE_NAME"
    echo ""
    
    # ECR Login
    echo "Logging in to AWS ECR..."
    aws ecr get-login-password --region "$AWS_REGION" | docker login --username AWS --password-stdin "$REGISTRY_URL"
    
    if [ $? -ne 0 ]; then
        echo "Error: ECR login failed"
        exit 1
    fi
    
    # Create ECR repository if it doesn't exist
    echo "Checking if ECR repository exists..."
    aws ecr describe-repositories --repository-names "$ECR_REPO" --region "$AWS_REGION" >/dev/null 2>&1 || \
        (echo "Creating ECR repository: $ECR_REPO" && \
         aws ecr create-repository --repository-name "$ECR_REPO" --region "$AWS_REGION")
    
    echo "ECR repository ready: $ECR_REPO"
    echo ""
    
elif [ "$REGISTRY_CHOICE" = "2" ]; then
    echo ""
    echo "=== Docker Hub Configuration ==="
    
    read -p "Enter Docker Hub username: " DOCKER_USERNAME
    read -sp "Enter Docker Hub password or access token: " DOCKER_PASSWORD
    echo ""
    
    FULL_IMAGE_NAME="$DOCKER_USERNAME/$IMAGE_NAME:$IMAGE_TAG"
    
    echo "Full image name: $FULL_IMAGE_NAME"
    echo ""
    
    # Docker Hub Login
    echo "Logging in to Docker Hub..."
    echo "$DOCKER_PASSWORD" | docker login --username "$DOCKER_USERNAME" --password-stdin
    
    if [ $? -ne 0 ]; then
        echo "Error: Docker Hub login failed"
        exit 1
    fi
    
else
    echo "Invalid choice. Exiting."
    exit 1
fi

# Build Docker image
echo "========================================"
echo "Building Docker image..."
echo "========================================"
docker build -t "$FULL_IMAGE_NAME" .

if [ $? -ne 0 ]; then
    echo "Error: Docker build failed"
    exit 1
fi

echo ""
echo "Docker image built successfully: $FULL_IMAGE_NAME"
echo ""

# Push Docker image
echo "========================================"
echo "Pushing Docker image to registry..."
echo "========================================"
docker push "$FULL_IMAGE_NAME"

if [ $? -ne 0 ]; then
    echo "Error: Docker push failed"
    exit 1
fi

echo ""
echo "========================================"
echo "Success!"
echo "========================================"
echo "Image pushed: $FULL_IMAGE_NAME"
echo ""
echo "Use this image URI for deployment:"
echo "$FULL_IMAGE_NAME"
echo ""