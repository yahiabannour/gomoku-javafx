@echo off
echo ================================
echo    Gomoku .EXE Build Script
echo ================================
echo.

REM Check if Launch4j is installed
where launch4j >nul 2>nul
if %errorlevel% neq 0 (
    echo [ERROR] Launch4j ist nicht installiert oder nicht im PATH.
    echo.
    echo Bitte laden Sie Launch4j herunter von:
    echo https://launch4j.sourceforge.net/
    echo.
    echo Und fügen Sie den Installationsordner zum PATH hinzu.
    pause
    exit /b 1
)

REM Create releases directory
if not exist "releases" mkdir releases

REM Build JAR first
echo [1/3] Building JAR with Maven...
call mvn clean package -q
if %errorlevel% neq 0 (
    echo [ERROR] Maven build failed!
    pause
    exit /b 1
)

REM Convert PNG to ICO (if needed)
echo [2/3] Checking icon file...
if not exist "src\main\resources\icon.ico" (
    echo [INFO] Converting PNG to ICO...
    REM You can use online converters or ImageMagick for this
    echo [WARNING] icon.ico not found. Using default icon.
    echo Please convert src\main\resources\icon.png to icon.ico manually.
)

REM Create EXE with Launch4j
echo [3/3] Creating EXE with Launch4j...
launch4j build-tools\launch4j-config.xml
if %errorlevel% neq 0 (
    echo [ERROR] Launch4j failed to create EXE!
    pause
    exit /b 1
)

echo.
echo ================================
echo     BUILD SUCCESSFUL!
echo ================================
echo.
echo EXE created: releases\Gomoku-1.0.0.exe
echo.
echo To run the EXE, make sure you have:
echo - Java 17 or higher installed
echo - JavaFX SDK in %%JAVAFX_HOME%%
echo.
pause 