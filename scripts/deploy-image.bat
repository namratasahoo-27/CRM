@echo off
setlocal enabledelayedexpansion

echo ========================================
echo AWS EKS Deployment Script
echo ========================================
echo.

REM Prompt for AWS configuration
set /p AWS_REGION="Enter AWS Region (e.g., us-east-1): "
set /p CLUSTER_NAME="Enter EKS Cluster Name: "

if "!AWS_REGION!"=="" (
    echo Error: AWS Region is required
    exit /b 1
)

if "!CLUSTER_NAME!"=="" (
    echo Error: EKS Cluster Name is required
    exit /b 1
)

echo.
echo AWS Region: !AWS_REGION!
echo EKS Cluster: !CLUSTER_NAME!
echo.

REM Prompt for Docker image URI
set /p IMAGE_URI="Enter Docker Image URI (e.g., 123456789012.dkr.ecr.us-east-1.amazonaws.com/crm:latest): "

if "!IMAGE_URI!"=="" (
    echo Error: Docker Image URI is required
    exit /b 1
)

echo Image URI: !IMAGE_URI!
echo.

REM Prompt for database configuration
echo === Database Configuration ===
set /p DB_HOST="Enter Database Host (default: localhost): "
if "!DB_HOST!"=="" set DB_HOST=localhost

set /p DB_PORT="Enter Database Port (default: 3306): "
if "!DB_PORT!"=="" set DB_PORT=3306

set /p DB_NAME="Enter Database Name (default: crm): "
if "!DB_NAME!"=="" set DB_NAME=crm

set /p DB_USERNAME="Enter Database Username (default: root): "
if "!DB_USERNAME!"=="" set DB_USERNAME=root

set /p HIBERNATE_DDL_AUTO="Enter Hibernate DDL Auto (default: update): "
if "!HIBERNATE_DDL_AUTO!"=="" set HIBERNATE_DDL_AUTO=update

set /p SQL_INIT_MODE="Enter SQL Init Mode (default: never): "
if "!SQL_INIT_MODE!"=="" set SQL_INIT_MODE=never

echo.
echo Database Host: !DB_HOST!
echo Database Port: !DB_PORT!
echo Database Name: !DB_NAME!
echo Database Username: !DB_USERNAME!
echo Hibernate DDL Auto: !HIBERNATE_DDL_AUTO!
echo SQL Init Mode: !SQL_INIT_MODE!
echo.

REM Configure kubectl
echo ========================================
echo Configuring kubectl for EKS cluster...
echo ========================================
aws eks update-kubeconfig --region !AWS_REGION! --name !CLUSTER_NAME!

if !ERRORLEVEL! neq 0 (
    echo Error: Failed to configure kubectl
    exit /b 1
)

echo.
echo Verifying cluster connectivity...
kubectl cluster-info

if !ERRORLEVEL! neq 0 (
    echo Error: Cannot connect to Kubernetes cluster
    exit /b 1
)

echo.
echo ========================================
echo Updating Kubernetes manifests...
echo ========================================

REM Update deployment.yaml with actual values using PowerShell
powershell -Command "(Get-Content kubernetes\deployment.yaml) -replace '{{IMAGE_URI}}', '!IMAGE_URI!' | Set-Content kubernetes\deployment.yaml"
powershell -Command "(Get-Content kubernetes\deployment.yaml) -replace '{{DB_HOST}}', '!DB_HOST!' | Set-Content kubernetes\deployment.yaml"
powershell -Command "(Get-Content kubernetes\deployment.yaml) -replace '{{DB_PORT}}', '!DB_PORT!' | Set-Content kubernetes\deployment.yaml"
powershell -Command "(Get-Content kubernetes\deployment.yaml) -replace '{{DB_NAME}}', '!DB_NAME!' | Set-Content kubernetes\deployment.yaml"
powershell -Command "(Get-Content kubernetes\deployment.yaml) -replace '{{DB_USERNAME}}', '!DB_USERNAME!' | Set-Content kubernetes\deployment.yaml"
powershell -Command "(Get-Content kubernetes\deployment.yaml) -replace '{{HIBERNATE_DDL_AUTO}}', '!HIBERNATE_DDL_AUTO!' | Set-Content kubernetes\deployment.yaml"
powershell -Command "(Get-Content kubernetes\deployment.yaml) -replace '{{SQL_INIT_MODE}}', '!SQL_INIT_MODE!' | Set-Content kubernetes\deployment.yaml"

echo Manifests updated successfully
echo.

REM Apply Kubernetes manifests
echo ========================================
echo Deploying to Kubernetes...
echo ========================================

echo Creating namespace...
kubectl apply -f kubernetes\namespace.yaml

echo Deploying application...
kubectl apply -f kubernetes\deployment.yaml

echo Creating service...
kubectl apply -f kubernetes\service.yaml

echo Creating ingress...
kubectl apply -f kubernetes\ingress.yaml

echo.
echo ========================================
echo Waiting for deployment to complete...
echo ========================================
kubectl rollout status deployment/crm -n crm --timeout=5m

if !ERRORLEVEL! neq 0 (
    echo Warning: Deployment rollout did not complete in time
    echo Check status with: kubectl get pods -n crm
)

echo.
echo ========================================
echo Deployment Summary
echo ========================================
kubectl get pods,svc,ingress -n crm

echo.
echo ========================================
echo Deployment Complete!
echo ========================================
echo.
echo To check application logs:
echo   kubectl logs -f deployment/crm -n crm
echo.
echo To check pod status:
echo   kubectl get pods -n crm
echo.
echo To get ingress URL:
echo   kubectl get ingress crm-ingress -n crm
echo.
echo To rollback deployment:
echo   kubectl rollout undo deployment/crm -n crm
echo.

endlocal