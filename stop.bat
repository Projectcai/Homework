@echo off
setlocal
title Flood Prevention System - Shutdown
echo ================================================
echo    Shutting down Flood Prevention System ...
echo ================================================
echo.

echo Stopping frontend (port 5173) ...
for /f "tokens=5" %%a in ('netstat -ano ^| findstr ":5173" ^| findstr "LISTENING"') do (
    taskkill /f /pid %%a >nul 2>&1
)

echo Stopping backend (port 8080) ...
for /f "tokens=5" %%a in ('netstat -ano ^| findstr ":8080" ^| findstr "LISTENING"') do (
    taskkill /f /pid %%a >nul 2>&1
)

echo.
echo System stopped. MySQL and Redis services are left running.
echo.
pause