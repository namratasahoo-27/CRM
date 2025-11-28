@echo off
setlocal enabledelayedexpansion

echo 🚀 CRM Application - Docker Build and Push Script
echo =================================================

REM Get project name and sanitize it
set PROJECT_NAME=crm-app
set IMAGE_NAME=crm-app

echo 📦 Project: !PROJECT_NAME!
echo 🏷️  Image Name: !IMAGE_NAME!
echo.

REM Prompt for image tag
set /p IMAGE_TAG="Enter image tag (default: latest): "
if "!IMAGE_TAG!"=="" set IMAGE_TAG=latest

echo 🏷️  Using tag: !IMAGE_TAG!
echo.

REM Registry selection
echo Select Docker registry:
echo 1. AWS ECR
echo 2. Docker Hub
set /p REGISTRY_CHOICE="Choose registry (1-2): "

if "!REGISTRY_CHOICE!"=="1" (
    echo 🔧 Configuring AWS ECR...
    set /p AWS_REGION="Enter AWS region (e.g., us-east-1): "
    set /p ECR_REPO="Enter ECR repository name (default: !IMAGE_NAME!): "
    if "!ECR_REPO!"=="" set ECR_REPO=!IMAGE_NAME!
    
    REM Get AWS account ID
    for /f "tokens=*" %%i in ('aws sts get-caller-identity --query Account --output text') do set AWS_ACCOUNT_ID=%%i
    set REGISTRY_URL=!AWS_ACCOUNT_ID!.dkr.ecr.!AWS_REGION!.amazonaws.com
    set FULL_IMAGE_NAME=!REGISTRY_URL!/!ECR_REPO!:!IMAGE_TAG!
    
    echo 🔐 Logging into AWS ECR...
    aws ecr get-login-password --region !AWS_REGION! | docker login --username AWS --password-stdin !REGISTRY_URL!
    if !ERRORLEVEL! neq 0 (
        echo ❌ ECR login failed
        exit /b 1
    )
    
    echo 📋 Creating ECR repository if it doesn't exist...
    aws ecr describe-repositories --repository-names !ECR_REPO! --region !AWS_REGION! >nul 2>&1
    if !ERRORLEVEL! neq 0 (
        echo Creating ECR repository...
        aws ecr create-repository --repository-name !ECR_REPO! --region !AWS_REGION!
    )
) else if "!REGISTRY_CHOICE!"=="2" (
    echo 🔧 Configuring Docker Hub...
    set /p DOCKER_USERNAME="Enter Docker Hub username: "
    set /p DOCKER_PASSWORD="Enter Docker Hub password: "
    
    set FULL_IMAGE_NAME=!DOCKER_USERNAME!/!IMAGE_NAME!:!IMAGE_TAG!
    
    echo 🔐 Logging into Docker Hub...
    echo !DOCKER_PASSWORD! | docker login --username !DOCKER_USERNAME! --password-stdin
    if !ERRORLEVEL! neq 0 (
        echo ❌ Docker Hub login failed
        exit /b 1
    )
) else (
    echo ❌ Invalid choice. Exiting.
    exit /b 1
)

echo 🏗️  Building Docker image: !FULL_IMAGE_NAME!
docker build -t !FULL_IMAGE_NAME! .
if !ERRORLEVEL! neq 0 (
    echo ❌ Docker build failed
    exit /b 1
)

echo 📤 Pushing Docker image: !FULL_IMAGE_NAME!
docker push !FULL_IMAGE_NAME!
if !ERRORLEVEL! neq 0 (
    echo ❌ Docker push failed
    exit /b 1
)

echo ✅ Build and push completed successfully!
echo 🎯 Image: !FULL_IMAGE_NAME!
echo 💡 Use this image URI in your deployment scripts.

pause
