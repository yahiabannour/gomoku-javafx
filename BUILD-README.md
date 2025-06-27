# 🏗️ Gomoku Build Guide

## 📋 Prerequisites

### 1. Java Development Kit (JDK 17+)
- Download from [Adoptium](https://adoptium.net/)
- Set `JAVA_HOME` environment variable

### 2. JavaFX SDK
- Download from [OpenJFX](https://openjfx.io/)
- Extract to a folder (e.g., `C:\Program Files\JavaFX\javafx-sdk-23.0.1`)
- Set `JAVAFX_HOME` environment variable

### 3. Maven
- Download from [Apache Maven](https://maven.apache.org/)
- Add to PATH

### 4. Launch4j (for .exe creation)
- Download from [Launch4j](https://launch4j.sourceforge.net/)
- Install and add to PATH

## 🚀 Build Instructions

### Option 1: JAR Only
```bash
# Build executable JAR
mvn clean package

# Run JAR
java --module-path "%JAVAFX_HOME%\lib" --add-modules javafx.controls,javafx.fxml,javafx.swing --add-opens javafx.graphics/com.sun.javafx.application=ALL-UNNAMED -jar target/gomoku-1.0.0.jar
```

### Option 2: Windows .EXE
```bash
# Run the build script
build-exe.bat
```

This will:
1. Build the JAR with Maven
2. Create Windows executable with Launch4j
3. Output: `releases/Gomoku-1.0.0.exe`

## 📁 Project Structure
```
bannour-gomoku/
├── src/main/java/          # Source code
├── src/main/resources/     # Resources (icons, etc.)
├── build-tools/            # Build configuration
│   └── launch4j-config.xml # Launch4j configuration
├── releases/               # Generated executables
├── target/                 # Maven build output
├── build-exe.bat          # Windows build script
└── pom.xml                # Maven configuration
```

## 🎯 Running the Application

### From JAR:
```bash
java --module-path "path/to/javafx/lib" --add-modules javafx.controls,javafx.fxml,javafx.swing --add-opens javafx.graphics/com.sun.javafx.application=ALL-UNNAMED -jar gomoku-1.0.0.jar
```

### From EXE:
- Double-click `Gomoku-1.0.0.exe`
- Or run from command line: `Gomoku-1.0.0.exe`

### Command Line Parameters:
```bash
# Examples
Gomoku-1.0.0.exe                    # Welcome screen
Gomoku-1.0.0.exe PC D S3            # Player vs Computer, Debug, Minimax
Gomoku-1.0.0.exe Go PP              # GUI mode, Player vs Player
```

## 🔧 Troubleshooting

### "Java not found" Error
- Ensure Java 17+ is installed
- Check `JAVA_HOME` environment variable

### "JavaFX modules not found" Error
- Install JavaFX SDK
- Set `JAVAFX_HOME` environment variable
- Or use the bundled JAR version

### Launch4j Errors
- Ensure Launch4j is installed and in PATH
- Check that all paths in `launch4j-config.xml` are correct
- Verify icon.ico exists (convert from icon.png if needed)

## 📦 Distribution

The generated .exe file can be distributed and will:
- Check for Java 17+ installation
- Provide helpful error messages if requirements are missing
- Include application metadata and icon
- Support command-line parameters

## 🎨 FontAwesome Icons

The application now uses FontAwesome icons instead of emojis for a more professional look. Icons are rendered using Unicode characters and fallback to system fonts if FontAwesome is not available.

## 💡 Development Tips

- Use `mvn compile` for quick compilation during development
- Use `mvn clean package` for full build with dependencies
- Test both GUI and console modes before release
- Verify all game modes (PP, PC, CC) and strategies (S1, S2, S3) work correctly 