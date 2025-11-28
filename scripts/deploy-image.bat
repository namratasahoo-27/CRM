@echo off
setlocal enabledelayedexpansion

echo === AWS EKS Deployment Script for CRM Application ===

REM Get EKS cluster information
set /p AWS_REGION="Enter AWS region (default: us-east-1): "
if "!AWS_REGION!"=="" set AWS_REGION=us-east-1

set /p CLUSTER_NAME="Enter EKS cluster name: "
if "!CLUSTER_NAME!"=="" (
    echo Error: EKS cluster name is required
    exit /b 1
)

REM Get Docker image URI
set /p IMAGE_URI="Enter Docker image URI (registry/repo:tag): "
if "!IMAGE_URI!"=="" (
    echo Error: Docker image URI is required
    exit /b 1
)

echo.
echo === Environment Configuration ===
set /p DB_HOST="Enter database host (optional, press Enter to skip): "
set /p DB_PORT="Enter database port (default: 3306): "
if "!DB_PORT!"=="" set DB_PORT=3306
set /p DB_NAME="Enter database name (default: crm): "
if "!DB_NAME!"=="" set DB_NAME=crm
set /p DB_USERNAME="Enter database username (default: root): "
if "!DB_USERNAME!"=="" set DB_USERNAME=root
set /p DB_PASSWORD="Enter database password (optional): "

REM Configure kubectl
echo.
echo Configuring kubectl for EKS cluster: !CLUSTER_NAME!
aws eks update-kubeconfig --region !AWS_REGION! --name !CLUSTER_NAME!
if !ERRORLEVEL! neq 0 (
    echo Error: Failed to configure kubectl
    exit /b 1
)

REM Verify cluster connectivity
echo Verifying cluster connectivity...
kubectl cluster-info
if !ERRORLEVEL! neq 0 (
    echo Error: Unable to connect to EKS cluster
    exit /b 1
)

REM Create temporary directory and copy manifests
set TEMP_DIR=%TEMP%\k8s-deploy-%RANDOM%
mkdir !TEMP_DIR!
xcopy kubernetes\*.* !TEMP_DIR!\ /Y

REM Replace placeholders in manifests
echo Updating Kubernetes manifests with deployment values...
powershell -Command "(Get-Content !TEMP_DIR!\deployment.yaml) -replace '{{IMAGE_URI}}', '!IMAGE_URI!' | Set-Content !TEMP_DIR!\deployment.yaml"

if "!DB_HOST!"=="" (
    set DB_HOST=mysql-service
)
powershell -Command "(Get-Content !TEMP_DIR!\deployment.yaml) -replace '{{DB_HOST}}', '!DB_HOST!' | Set-Content !TEMP_DIR!\deployment.yaml"
powershell -Command "(Get-Content !TEMP_DIR!\deployment.yaml) -replace '{{DB_PORT}}', '!DB_PORT!' | Set-Content !TEMP_DIR!\deployment.yaml"
powershell -Command "(Get-Content !TEMP_DIR!\deployment.yaml) -replace '{{DB_NAME}}', '!DB_NAME!' | Set-Content !TEMP_DIR!\deployment.yaml"
powershell -Command "(Get-Content !TEMP_DIR!\deployment.yaml) -replace '{{DB_USERNAME}}', '!DB_USERNAME!' | Set-Content !TEMP_DIR!\deployment.yaml"

if "!DB_PASSWORD!"=="" (
    set DB_PASSWORD=changeme
)
powershell -Command "(Get-Content !TEMP_DIR!\deployment.yaml) -replace '{{DB_PASSWORD}}', '!DB_PASSWORD!' | Set-Content !TEMP_DIR!\deployment.yaml"

REM Deploy to EKS
echo.
echo Deploying to EKS cluster...

echo Creating namespace...
kubectl apply -f !TEMP_DIR!\namespace.yaml
if !ERRORLEVEL! neq 0 (
    echo Error: Failed to create namespace
    goto cleanup
)

echo Deploying application...
kubectl apply -f !TEMP_DIR!\deployment.yaml
if !ERRORLEVEL! neq 0 (
    echo Error: Failed to deploy application
    goto cleanup
)

echo Creating service...
kubectl apply -f !TEMP_DIR!\service.yaml
if !ERRORLEVEL! neq 0 (
    echo Error: Failed to create service
    goto cleanup
)

echo Creating ingress...
kubectl apply -f !TEMP_DIR!\ingress.yaml
if !ERRORLEVEL! neq 0 (
    echo Error: Failed to create ingress
    goto cleanup
)

REM Wait for deployment to be ready
echo.
echo Waiting for deployment to be ready...
kubectl rollout status deployment/crm-app -n crm --timeout=300s
if !ERRORLEVEL! neq 0 (
    echo Warning: Deployment rollout may not have completed successfully
)

REM Verify deployment
echo.
echo Verifying deployment...
kubectl get pods,svc,ingress -n crm

REM Display completion message
echo.
echo === Deployment Complete ===
echo Application deployed successfully to EKS cluster: !CLUSTER_NAME!
echo Namespace: crm
echo Image: !IMAGE_URI!
echo.
echo To access the application:
echo 1. Get the ingress URL: kubectl get ingress -n crm
echo 2. Access via: http://crm-app.example.com (configure DNS accordingly)
echo 3. Port-forward for testing: kubectl port-forward svc/crm-app-service 8080:80 -n crm
echo.
echo Monitoring commands:
echo - View logs: kubectl logs -f deployment/crm-app -n crm
echo - Scale app: kubectl scale deployment crm-app --replicas=3 -n crm
echo - Get status: kubectl get all -n crm

:cleanup
REM Cleanup temporary directory
if exist !TEMP_DIR! rmdir /s /q !TEMP_DIR!

echo.
echo Deployment script completed!
exit /b 0