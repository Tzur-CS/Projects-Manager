@echo off
echo ========================================
echo Starting Application in DEV MODE
echo With AWS Cognito Authentication
echo ========================================
echo.

cd /d C:\Users\Surense\Desktop\project

echo Stopping any running Java processes...
taskkill /F /IM java.exe 2>nul

echo.
echo IMPORTANT: Before starting, make sure you have updated:
echo   application-dev.properties
echo.
echo Replace:
echo   - spring.security.oauth2.resourceserver.jwt.issuer-uri
echo   - spring.security.oauth2.resourceserver.jwt.audiences
echo.
echo With YOUR Cognito User Pool ID and App Client ID
echo.
pause

echo.
echo Starting Spring Boot application...
echo Profile: dev
echo.
echo Watch for: "Started DemoApplication"
echo Then:
echo   1. Open: http://localhost:8080/swagger-ui.html
echo   2. Click "Authorize" button
echo   3. Enter: Bearer YOUR_JWT_TOKEN
echo   4. Test your APIs!
echo.
echo ========================================

mvn spring-boot:run -D"spring-boot.run.profiles=dev"

pause

