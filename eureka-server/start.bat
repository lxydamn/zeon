@echo off
REM ================================
REM Windows Script: Build Eureka Docker Image and Run Container
REM ================================

docker build -t eureka-server:1.0 .

if %errorlevel% neq 0 (
    echo Error Docker Image Build Failed!
    pause
    exit /b 1
)

echo Docker Image Build Success!

docker run -d -p 8761:8761 --name eureka-server eureka-server:1.0

if %errorlevel% neq 0 (
    echo Error Docker Container Build Failed!
    pause
    exit /b 1
)

echo Docker Container Build Success!

pause
