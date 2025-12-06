@echo off
echo ========================================
echo Starting Application in LOCAL MODE
echo ========================================
echo.

cd /d C:\Users\Surense\Desktop\project

echo Stopping any running Java processes...
taskkill /F /IM java.exe 2>nul

echo.
echo Starting Spring Boot application...
echo Profile: local
echo.
echo Watch for this message: "Started DemoApplication"
echo Then open: http://localhost:8080/swagger-ui.html
echo.
echo ========================================

mvn spring-boot:run -Dspring-boot.run.profiles=local

pause

