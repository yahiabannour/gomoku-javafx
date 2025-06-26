#!/bin/bash

echo "================================================"
echo "  Gomoku - Strategisches Brettspiel"
echo "  OIL SS2025 - Hochschule Anhalt"
echo "================================================"
echo

# Prüfe ob Java verfügbar ist
if ! command -v java &> /dev/null; then
    echo "FEHLER: Java ist nicht installiert oder nicht im PATH!"
    echo "Bitte installiere Java 17 oder höher."
    exit 1
fi

# Prüfe ob JAR-Datei existiert
if [ ! -f "gomoku-1.0.0-executable.jar" ]; then
    echo "FEHLER: gomoku-1.0.0-executable.jar nicht gefunden!"
    echo "Bitte führe zuerst 'mvn clean package' aus."
    exit 1
fi

echo "Starte Gomoku..."
echo

# Starte das Spiel
if [ $# -eq 0 ]; then
    echo "GUI-Modus wird gestartet..."
    java --add-opens javafx.graphics/com.sun.javafx.application=ALL-UNNAMED -jar gomoku-1.0.0-executable.jar
else
    echo "Konsolen-Modus mit Parametern: $@"
    java --add-opens javafx.graphics/com.sun.javafx.application=ALL-UNNAMED -jar gomoku-1.0.0-executable.jar "$@"
fi

echo
echo "Spiel beendet." 