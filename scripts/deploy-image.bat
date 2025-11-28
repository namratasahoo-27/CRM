@echo off
setlocal enabledelayedexpansion

echo 🚀 CRM Application - AWS EKS Deployment Script
echo =============================================

REM Prompt for AWS EKS configuration
set /p AWS_REGION="Enter AWS region (e.g., us-east-1): "
set /p CLUSTER_NAME="Enter EKS cluster name: "
set /p IMAGE_URI="Enter Docker image URI (with tag): "

echo 📋 Configuration:
echo    AWS Region: !AWS_REGION!
echo    EKS Cluster: !CLUSTER_NAME!
echo    Image URI: !IMAGE_URI!
echo.

REM Prompt for application environment variables
echo 🔧 Application Configuration (press Enter to skip optional variables):
set /p DB_HOST="Enter DB_HOST (database host): "
set /p DB_PORT="Enter DB_PORT (default: 3306): "
if "!DB_PORT!"=="" set DB_PORT=3306
set /p DB_NAME="Enter DB_NAME (default: crm): "
if "!DB_NAME!"=="" set DB_NAME=crm
set /p DB_USER="Enter DB_USER (default: root): "
if "!DB_USER!"=="" set DB_USER=root
set /p DB_PASSWORD="Enter DB_PASSWORD: "
set /p SSL_CERT_ARN="Enter SSL Certificate ARN (for HTTPS): "

echo.
echo 🔧 Configuring kubectl for EKS cluster...
aws eks update-kubeconfig --region !AWS_REGION! --name !CLUSTER_NAME!
if !ERRORLEVEL! neq 0 (
    echo ❌ Failed to configure kubectl
    exit /b 1
)

echo 🔍 Verifying cluster connectivity...
kubectl cluster-info
if !ERRORLEVEL! neq 0 (
    echo ❌ Failed to connect to EKS cluster
    exit /b 1
)

echo 📝 Updating Kubernetes manifests...
REM Create temporary directory for processed manifests
if not exist "temp-k8s-manifests" mkdir temp-k8s-manifests
xcopy kubernetes\*.yaml temp-k8s-manifests\ /Y

REM Replace placeholders in manifests using PowerShell
powershell -Command "(Get-Content temp-k8s-manifests\deployment.yaml) -replace '{{IMAGE_URI}}', '!IMAGE_URI!' | Set-Content temp-k8s-manifests\deployment.yaml"
powershell -Command "(Get-Content temp-k8s-manifests\deployment.yaml) -replace '{{DB_HOST}}', '!DB_HOST!' | Set-Content temp-k8s-manifests\deployment.yaml"
powershell -Command "(Get-Content temp-k8s-manifests\deployment.yaml) -replace '{{DB_PORT}}', '!DB_PORT!' | Set-Content temp-k8s-manifests\deployment.yaml"
powershell -Command "(Get-Content temp-k8s-manifests\deployment.yaml) -replace '{{DB_NAME}}', '!DB_NAME!' | Set-Content temp-k8s-manifests\deployment.yaml"
powershell -Command "(Get-Content temp-k8s-manifests\deployment.yaml) -replace '{{DB_USER}}', '!DB_USER!' | Set-Content temp-k8s-manifests\deployment.yaml"
powershell -Command "(Get-Content temp-k8s-manifests\ingress.yaml) -replace '{{SSL_CERT_ARN}}', '!SSL_CERT_ARN!' | Set-Content temp-k8s-manifests\ingress.yaml"

echo 🔐 Creating database password secret...
kubectl create secret generic crm-app-secrets --from-literal=db-password="!DB_PASSWORD!" --namespace=crm-app --dry-run=client -o yaml | kubectl apply -f -

echo 🚀 Deploying to Kubernetes...
echo    📦 Creating namespace...
kubectl apply -f temp-k8s-manifests\namespace.yaml
if !ERRORLEVEL! neq 0 (
    echo ❌ Failed to create namespace
    exit /b 1
)

echo    📦 Deploying application...
kubectl apply -f temp-k8s-manifests\deployment.yaml
if !ERRORLEVEL! neq 0 (
    echo ❌ Failed to deploy application
    exit /b 1
)

echo    📦 Creating service...
kubectl apply -f temp-k8s-manifests\service.yaml
if !ERRORLEVEL! neq 0 (
    echo ❌ Failed to create service
    exit /b 1
)

echo    📦 Creating ingress...
kubectl apply -f temp-k8s-manifests\ingress.yaml
if !ERRORLEVEL! neq 0 (
    echo ❌ Failed to create ingress
    exit /b 1
)

echo ⏳ Waiting for deployment rollout...
kubectl rollout status deployment/crm-app -n crm-app --timeout=300s
if !ERRORLEVEL! neq 0 (
    echo ❌ Deployment rollout failed
    exit /b 1
)

echo 🔍 Verifying deployment...
kubectl get pods,svc,ingress -n crm-app

echo.
echo ✅ Deployment completed successfully!
echo.
echo 📋 Application Information:
echo    Namespace: crm-app
echo    Service: crm-app-service
echo    Image: !IMAGE_URI!
echo.
echo 🌐 Access your application:
echo    Internal: http://crm-app-service.crm-app.svc.cluster.local
echo    External: Check ingress for external URL
echo.
echo 📊 Monitoring commands:
echo    kubectl get pods -n crm-app
echo    kubectl logs -f deployment/crm-app -n crm-app
echo    kubectl describe deployment crm-app -n crm-app

REM Cleanup
rmdir /s /q temp-k8s-manifests

echo.
echo 🎉 Deployment script completed!

pause
