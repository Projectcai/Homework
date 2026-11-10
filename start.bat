@echo off
setlocal
title Flood Prevention System - Launcher
echo ================================================
echo    Flood Prevention Resource Dispatch System
echo ================================================
echo.

rem ===== Project directories (same folder as this script) =====
set "PROJECT_DIR=%~dp0"
set "BACKEND_DIR=%PROJECT_DIR%backend"
set "FRONTEND_DIR=%PROJECT_DIR%frontend"
set "SQL_FILE=%PROJECT_DIR%database\flood_prevention.sql"

rem ===== MySQL config (change root123 if your password differs) =====
set "MYSQL_HOME=D:\dev\mysql-8.0.28-winx64"
set "DB_PASSWORD=root123"

rem ===== Dev environment paths =====
set "JAVA_HOME=D:\dev\jdk-17.0.20.1+1"
set "PATH=%JAVA_HOME%\bin;D:\dev\apache-maven-3.9.9\bin;%MYSQL_HOME%\bin;%PATH%"

echo [1/5] Starting MySQL ...
tasklist /fi "imagename eq mysqld.exe" 2>nul | findstr /i "mysqld.exe" >nul
if not errorlevel 1 goto mysql_running
start "MySQL Server" /min "%MYSQL_HOME%\bin\mysqld.exe" --defaults-file="%MYSQL_HOME%\my.ini" --console
echo        MySQL launching, waiting 6 seconds ...
timeout /t 6 /nobreak >nul
goto mysql_done
:mysql_running
echo        MySQL already running
:mysql_done
echo.

echo [2/5] Starting Redis ...
sc query Redis | findstr /i "RUNNING" >nul
if not errorlevel 1 goto redis_ok
net start Redis >nul 2>&1
if not errorlevel 1 goto redis_ok
echo        [WARN] Redis start failed, try running as administrator.
goto redis_done
:redis_ok
echo        Redis is running
:redis_done
echo.

echo [3/5] Checking database ...
mysql -uroot -p%DB_PASSWORD% --default-character-set=utf8mb4 -e "USE flood_prevention" >nul 2>&1
if not errorlevel 1 goto db_ok
echo        Database not found, initializing (first run only) ...
mysql -uroot -p%DB_PASSWORD% --default-character-set=utf8mb4 -e "CREATE DATABASE IF NOT EXISTS flood_prevention DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;" >nul 2>&1
mysql -uroot -p%DB_PASSWORD% --default-character-set=utf8mb4 flood_prevention < "%SQL_FILE%" >nul 2>&1
if not errorlevel 1 (echo        Database initialized OK) else (echo        [WARN] Init failed, check password is %DB_PASSWORD%)
goto db_done
:db_ok
echo        Database exists, skip init
:db_done
echo.

echo [3.5/5] Clearing stale processes ...
for /f "tokens=5" %%a in ('netstat -ano ^| findstr ":8080" ^| findstr "LISTENING"') do taskkill /f /pid %%a >nul 2>&1
for /f "tokens=5" %%a in ('netstat -ano ^| findstr ":5173" ^| findstr "LISTENING"') do taskkill /f /pid %%a >nul 2>&1
echo        Stale backend/frontend processes cleared.
echo.

echo [4/5] Starting backend (port 8080) ...
start "Flood-Backend" cmd /k "cd /d %BACKEND_DIR% && mvn spring-boot:run"
echo        Backend window opened. First run downloads dependencies, please wait.
echo.

echo [5/5] Starting frontend (port 5173) ...
if exist "%FRONTEND_DIR%\node_modules" goto fe_ok
echo        Frontend dependencies missing, installing (takes a few minutes) ...
cd /d "%FRONTEND_DIR%"
call npm install
:fe_ok
start "Flood-Frontend" cmd /k "cd /d %FRONTEND_DIR% && npm run dev"
echo        Frontend window opened
echo.

echo ================================================
echo    DONE!
echo    Frontend : http://localhost:5173
echo    Backend  : http://localhost:8080
echo    Account  : admin / admin123
echo    MySQL    : mysqld started on port 3306
echo ================================================
echo.
echo Keep the two black windows open. Run stop.bat to shut down.
echo.
pause