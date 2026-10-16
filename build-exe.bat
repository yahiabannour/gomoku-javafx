@echo off
echo ================================
echo    Gomoku .EXE Build Script
echo ================================
echo.

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

REM Check if executable JAR exists
if not exist "target\gomoku-1.0.0-executable.jar" (
    echo [ERROR] Executable JAR not found!
    echo Expected: target\gomoku-1.0.0-executable.jar
    pause
    exit /b 1
)

REM Copy executable JAR to releases
echo [2/3] Copying executable JAR to releases...
copy "target\gomoku-1.0.0-executable.jar" "releases\Gomoku-1.0.0.jar"

REM Check if Launch4j is installed
where launch4j >nul 2>nul
if %errorlevel% neq 0 (
    echo [WARNING] Launch4j ist nicht installiert oder nicht im PATH.
    echo.
    echo ALTERNATIVE: Executable JAR wurde erstellt:
    echo releases\Gomoku-1.0.0.jar
    echo.
    echo Um das JAR zu starten:
    echo java -jar releases\Gomoku-1.0.0.jar
    echo.
    echo Für EXE-Erstellung installieren Sie Launch4j von:
    echo https://launch4j.sourceforge.net/
    echo.
    echo Und fügen Sie den Installationsordner zum PATH hinzu.
    echo Dann führen Sie dieses Script erneut aus.
    pause
    exit /b 0
)

REM Convert PNG to ICO (if needed)
echo [3/4] Checking icon file...
if not exist "src\main\resources\icon.ico" (
    echo [INFO] Converting PNG to ICO...
    REM You can use online converters or ImageMagick for this
    echo [WARNING] icon.ico not found. Using default icon.
    echo Please convert src\main\resources\icon.png to icon.ico manually.
)

REM Create EXE with Launch4j
echo [4/4] Creating EXE with Launch4j...
launch4j build-tools\launch4j-config.xml
if %errorlevel% neq 0 (
    echo [ERROR] Launch4j failed to create EXE!
    echo.
    echo FALLBACK: Executable JAR ist verfügbar:
    echo releases\Gomoku-1.0.0.jar
    pause
    exit /b 1
)

echo.
echo ================================
echo     BUILD SUCCESSFUL!
echo ================================
echo.
echo Files created:
echo - releases\Gomoku-1.0.0.jar (Executable JAR)
echo - releases\Gomoku-1.0.0.exe (Windows EXE)
echo.
echo To run the JAR:
echo java -jar releases\Gomoku-1.0.0.jar
echo.
echo To run the EXE, make sure you have:
echo - Java 17 or higher installed
echo - JavaFX SDK in %%JAVAFX_HOME%%
echo.
pause 