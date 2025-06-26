@echo off
echo ================================================
echo   Gomoku - Strategisches Brettspiel
echo   OIL SS2025 - Hochschule Anhalt  
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

REM Prüfe ob JAR-Datei existiert
if not exist "gomoku-1.0.0-executable.jar" (
    echo FEHLER: gomoku-1.0.0-executable.jar nicht gefunden!
    echo Bitte führe zuerst 'mvn clean package' aus.
    pause
    exit /b 1
)

echo Starte Gomoku...
echo.

REM Starte das Spiel
if "%1"=="" (
    echo GUI-Modus wird gestartet...
    java --add-opens javafx.graphics/com.sun.javafx.application=ALL-UNNAMED -jar gomoku-1.0.0-executable.jar
) else (
    echo Konsolen-Modus mit Parametern: %*
    java --add-opens javafx.graphics/com.sun.javafx.application=ALL-UNNAMED -jar gomoku-1.0.0-executable.jar %*
)

echo.
echo Spiel beendet.
pause 