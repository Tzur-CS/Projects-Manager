@echo off
echo ========================================
echo Setting Up Users: Tzur and Eden
echo ========================================
echo.

echo User 1: Tzur
echo   - Email: tzur@example.com
echo   - Cognito Sub: 3265e404-d051-70fe-f26e-13b4afd5d058
echo.

echo User 2: Eden
echo   - Email: eden@example.com
echo   - Cognito Sub: e24574f4-a0f1-70e4-fcbd-11a0911f8d67
echo.

echo ========================================
echo Running SQL script...
echo ========================================

cd /d C:\Users\Surense\Desktop\project

REM Try common MySQL installation paths
set MYSQL_PATH="C:\Program Files\MySQL\MySQL Server 8.0\bin\mysql.exe"
if not exist %MYSQL_PATH% set MYSQL_PATH="C:\xampp\mysql\bin\mysql.exe"
if not exist %MYSQL_PATH% set MYSQL_PATH="C:\wamp64\bin\mysql\mysql8.0.21\bin\mysql.exe"
if not exist %MYSQL_PATH% set MYSQL_PATH=mysql

echo Using MySQL at: %MYSQL_PATH%
echo.

%MYSQL_PATH% -u root -p12324 moveo-db < setup_users_tzur_eden.sql

if %ERRORLEVEL% EQU 0 (
    echo.
    echo ========================================
    echo SUCCESS! Users and data created!
    echo ========================================
    echo.
    echo Database now contains:
    echo   - 2 Users (Tzur and Eden)
    echo   - 6 Projects (3 for each user)
    echo   - 6 Tasks (3 for each user)
    echo.
    echo Next steps:
    echo 1. Create these users in AWS Cognito
    echo 2. Make sure their Cognito sub IDs match exactly
    echo 3. Start backend: mvn spring-boot:run -D"spring-boot.run.profiles=dev"
    echo 4. Test with JWT tokens!
    echo.
) else (
    echo.
    echo ========================================
    echo ERROR! Failed to run SQL script
    echo ========================================
    echo.
    echo Possible reasons:
    echo 1. MySQL is not running
    echo 2. Wrong password (currently using: 12324)
    echo 3. Database 'moveo-db' doesn't exist
    echo.
    echo You can run the SQL manually:
    echo 1. Open MySQL Workbench
    echo 2. Open file: setup_users_tzur_eden.sql
    echo 3. Execute the script
    echo.
)

pause

