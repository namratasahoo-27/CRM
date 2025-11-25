@echo off
setlocal enabledelayedexpansion

echo ========================================
echo Docker Build and Push Script
echo ========================================
echo.

REM Project configuration
set PROJECT_NAME=crm
set IMAGE_NAME=crm

echo Project: !PROJECT_NAME!
echo Image name: !IMAGE_NAME!
echo.

REM Prompt for image tag
set /p IMAGE_TAG="Enter image tag (default: latest): "
if "!IMAGE_TAG!"=="" set IMAGE_TAG=latest

echo Using tag: !IMAGE_TAG!
echo.

REM Registry selection
echo Select container registry:
echo 1. AWS ECR (Elastic Container Registry)
echo 2. Docker Hub
set /p REGISTRY_CHOICE="Enter choice (1 or 2): "

if "!REGISTRY_CHOICE!"=="1" (
    echo.
    echo === AWS ECR Configuration ===
    
    set /p AWS_REGION="Enter AWS Region (e.g., us-east-1): "
    set /p ECR_REPO="Enter ECR Repository Name (default: !IMAGE_NAME!): "
    if "!ECR_REPO!"=="" set ECR_REPO=!IMAGE_NAME!
    
    echo Retrieving AWS Account ID...
    for /f "tokens=*" %%i in ('aws sts get-caller-identity --query Account --output text') do set AWS_ACCOUNT_ID=%%i
    
    if "!AWS_ACCOUNT_ID!"=="" (
        echo Error: Failed to retrieve AWS Account ID
        exit /b 1
    )
    
    set REGISTRY_URL=!AWS_ACCOUNT_ID!.dkr.ecr.!AWS_REGION!.amazonaws.com
    set FULL_IMAGE_NAME=!REGISTRY_URL!/!ECR_REPO!:!IMAGE_TAG!
    
    echo Registry URL: !REGISTRY_URL!
    echo Full image name: !FULL_IMAGE_NAME!
    echo.
    
    echo Logging in to AWS ECR...
    aws ecr get-login-password --region !AWS_REGION! | docker login --username AWS --password-stdin !REGISTRY_URL!
    
    if !ERRORLEVEL! neq 0 (
        echo Error: ECR login failed
        exit /b 1
    )
    
    echo Checking if ECR repository exists...
    aws ecr describe-repositories --repository-names !ECR_REPO! --region !AWS_REGION! >nul 2>&1
    if !ERRORLEVEL! neq 0 (
        echo Creating ECR repository: !ECR_REPO!
        aws ecr create-repository --repository-name !ECR_REPO! --region !AWS_REGION!
    )
    
    echo ECR repository ready: !ECR_REPO!
    echo.
    
) else if "!REGISTRY_CHOICE!"=="2" (
    echo.
    echo === Docker Hub Configuration ===
    
    set /p DOCKER_USERNAME="Enter Docker Hub username: "
    set /p DOCKER_PASSWORD="Enter Docker Hub password or access token: "
    
    set FULL_IMAGE_NAME=!DOCKER_USERNAME!/!IMAGE_NAME!:!IMAGE_TAG!
    
    echo Full image name: !FULL_IMAGE_NAME!
    echo.
    
    echo Logging in to Docker Hub...
    echo !DOCKER_PASSWORD! | docker login --username !DOCKER_USERNAME! --password-stdin
    
    if !ERRORLEVEL! neq 0 (
        echo Error: Docker Hub login failed
        exit /b 1
    )
    
) else (
    echo Invalid choice. Exiting.
    exit /b 1
)

REM Build Docker image
echo ========================================
echo Building Docker image...
echo ========================================
docker build -t !FULL_IMAGE_NAME! .

if !ERRORLEVEL! neq 0 (
    echo Error: Docker build failed
    exit /b 1
)

echo.
echo Docker image built successfully: !FULL_IMAGE_NAME!
echo.

REM Push Docker image
echo ========================================
echo Pushing Docker image to registry...
echo ========================================
docker push !FULL_IMAGE_NAME!

if !ERRORLEVEL! neq 0 (
    echo Error: Docker push failed
    exit /b 1
)

echo.
echo ========================================
echo Success!
echo ========================================
echo Image pushed: !FULL_IMAGE_NAME!
echo.
echo Use this image URI for deployment:
echo !FULL_IMAGE_NAME!
echo.

endlocal