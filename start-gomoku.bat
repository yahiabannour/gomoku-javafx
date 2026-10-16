@echo off
echo ================================================
echo   Gomoku - Strategisches Brettspiel
echo   OIL SS2025 - Hochschule Hannover  
echo ================================================
echo.

REM Prüfe ob Java verfügbar ist
java -version >nul 2>&1
if %errorlevel% neq 0 (
    echo FEHLER: Java ist nicht installiert oder nicht im PATH!
    echo Bitte installiere Java 17 oder höher.
    pause
    exit /b 1
)

set jar_name=gomoku-1.0.0-executable.jar

REM Prüfe ob JAR-Datei existiert
if not exist "%jar_name%" (
    echo FEHLER: %jar_name% nicht gefunden!
    echo Bitte führe zuerst 'mvn clean package' aus.
    pause
    exit /b 1
)

echo Starte Gomoku...
echo.

REM Starte das Spiel
if "%1"=="" (
    echo GUI-Modus wird gestartet...
    java --add-opens javafx.graphics/com.sun.javafx.application=ALL-UNNAMED -jar %jar_name%
) else (
    echo Konsolen-Modus mit Parametern: %*
    java --add-opens javafx.graphics/com.sun.javafx.application=ALL-UNNAMED -jar %jar_name% %*
)

echo.
echo Spiel beendet.
pause 