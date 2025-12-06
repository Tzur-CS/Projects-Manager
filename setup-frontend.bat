@echo off
echo ========================================
echo React Frontend Setup Script
echo ========================================
echo.

cd /d C:\Users\Surense\Desktop

echo Step 1: Creating React App...
call npx create-react-app project-frontend

cd project-frontend

echo.
echo Step 2: Installing Dependencies...
call npm install axios react-router-dom react-toastify

echo.
echo Step 3: Installing AWS Amplify (for Cognito)...
call npm install aws-amplify @aws-amplify/ui-react

echo.
echo ========================================
echo Setup Complete!
echo ========================================
echo.
echo Next Steps:
echo 1. Copy files from C:\Users\Surense\Desktop\project\frontend-code\
echo    to C:\Users\Surense\Desktop\project-frontend\src\
echo.
echo 2. Start development server:
echo    cd C:\Users\Surense\Desktop\project-frontend
echo    npm start
echo.
echo 3. Make sure backend is running on http://localhost:8080
echo.
echo ========================================

pause

