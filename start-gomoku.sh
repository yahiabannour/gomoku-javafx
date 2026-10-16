#!/bin/bash

echo "================================================"
echo "  Gomoku - Strategisches Brettspiel"
echo "  OIL SS2025 - Hochschule Hannover"
echo "================================================"
echo

# Prüfe ob Java verfügbar ist
if ! command -v java &> /dev/null; then
    echo "FEHLER: Java ist nicht installiert oder nicht im PATH!"
    echo "Bitte installiere Java 17 oder höher."
    exit 1
fi

jar_name="gomoku-1.0.0-executable.jar"

# Prüfe ob JAR-Datei existiert
if [ ! -f "$jar_name" ]; then
    echo "FEHLER: $jar_name nicht gefunden!"
    echo "Bitte führe zuerst 'mvn clean package' aus."
    exit 1
fi

echo "Starte Gomoku..."
echo

# Starte das Spiel
if [ $# -eq 0 ]; then
    echo "GUI-Modus wird gestartet..."
    java --add-opens javafx.graphics/com.sun.javafx.application=ALL-UNNAMED -jar $jar_name
else
    echo "Konsolen-Modus mit Parametern: $@"
    java --add-opens javafx.graphics/com.sun.javafx.application=ALL-UNNAMED -jar $jar_name "$@"
fi

echo
echo "Spiel beendet." 