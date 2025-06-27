# Gomoku Brettspiel - OIL SS2025

Ein strategisches Gomoku-Brettspiel implementiert in Java mit JavaFX, entwickelt als Abschlussaufgabe für die Lehrveranstaltung "Objektorientierte Implementierungssprache (OIL)" SS2025.

## Über das Spiel

Gomoku ist ein strategisches Brettspiel für zwei Spieler auf einem 15x15 Gitter. Das Ziel ist es, fünf eigene Steine in einer Reihe zu platzieren – horizontal, vertikal oder diagonal.

## Features

### Kernfunktionalitäten (Pflicht)
- ✅ **Spielmodi**: Player vs Player (PP), Player vs Computer (PC), Computer vs Computer (CC)
- ✅ **KI-Strategien**: 
  - S1: Zufällige Züge (eingeschränkt)
  - S2: Einfache Logik (Blocken)
  - S3: Minimax-Algorithmus
- ✅ **Parameterverarbeitung**: Reguläre Ausdrücke mit Logikvalidierung
- ✅ **Debug-Modi**: Konsole (D) und Datei (DF) Ausgabe
- ✅ **GUI**: JavaFX-basierte Benutzeroberfläche
- ✅ **Konsole**: Interaktive Kommandozeilen-Bedienung

### Zusatzfunktionalitäten
- ✅ **Design Patterns**: MVC-Architektur + Strategy Pattern
- ✅ **XML-Support**: Speichern und Laden von Spielständen
- ✅ **Alpha-Beta-Pruning**: Optimierter Minimax-Algorithmus
- ✅ **Statistiken**: Spielerstatistiken und Gewinnraten
- ✅ **Animationen**: GUI-Animationen für Züge und Siege

## Architektur

Das Projekt folgt dem **Model-View-Controller (MVC)** Pattern kombiniert mit dem **Strategy Design Pattern** für KI-Strategien:

```
de.hsh.oil.gomoku/
├── Main.java                 # Einstiegspunkt mit Parameterverarbeitung
├── model/                    # Model-Schicht
│   ├── SpielKonfiguration.java
│   ├── SpielModus.java
│   ├── DebugModus.java
│   ├── KiStrategie.java
│   ├── Spieler.java
│   ├── Zug.java
│   └── Spielbrett.java
├── controller/               # Controller-Schicht
│   └── SpielController.java
├── view/                     # View-Schicht
│   ├── KonsoleView.java
│   └── GuiView.java
├── strategy/                 # Strategy Pattern für KI
│   ├── ZugStrategie.java
│   ├── ZufallsStrategie.java
│   ├── EinfacheLogikStrategie.java
│   └── MinimaxStrategie.java
└── util/                     # Utility-Klassen
    ├── ParameterValidator.java
    ├── XmlManager.java
    └── DebugLogger.java
```

## Verwendung

### Kommandozeile

```bash
# GUI-Modus (ohne Parameter)
java -jar gomoku.jar

# Spieler vs Computer mit Minimax-Strategie und Debug
java -jar gomoku.jar PC S3 D

# Computer vs Computer mit verschiedenen Strategien
java -jar gomoku.jar CC S1 S3 DF

# Spieler vs Spieler
java -jar gomoku.jar PP
```

### Parameter

| Parameter | Beschreibung |
|-----------|-------------|
| **PP** | Player vs Player (Spieler gegen Spieler) |
| **PC** | Player vs Computer (Spieler gegen Computer) |
| **CC** | Computer vs Computer |
| **D** | Debug-Ausgabe auf der Konsole |
| **DF** | Debug-Ausgabe in ASCII-Datei |
| **S1** | Zufällige Züge (eingeschränkt) |
| **S2** | Einfache Logik (Blocken) |
| **S3** | Minimax-Algorithmus |

### Beispiele

```bash
# Verschiedene Kombinationen (Reihenfolge beliebig)
java -jar gomoku.jar PC D S3
java -jar gomoku.jar S2 PC DF
java -jar gomoku.jar CC S1 S3 D
```

## Kompilierung und Ausführung

### Voraussetzungen
- Java 17 oder höher
- Maven 3.6 oder höher
- JavaFX 20.0.1 (wird automatisch über Maven geladen)

### Kompilierung
```bash
mvn clean compile # mvn compile
mvn package # mvn compile jar:jar assembly:single
```

### Ausführung
```bash
java --add-exports javafx.graphics/com.sun.javafx.application=ALL-UNNAMED -jar target/gomoku-1.0.0.jar

# Mit Maven
mvn javafx:run

# Als JAR (nach mvn package)
java -jar target/gomoku-1.0.0.jar [PARAMETER]
```

### Tests
```bash
mvn test
```

## Design Patterns

### Model-View-Controller (MVC)
- **Model**: Spiellogik, Datenstrukturen (SpielKonfiguration, Spielbrett, Spieler)
- **View**: Benutzeroberflächen (KonsoleView, GuiView)
- **Controller**: Spielsteuerung und Koordination (SpielController)

### Strategy Pattern
- **Interface**: `ZugStrategie`
- **Implementierungen**: 
  - `ZufallsStrategie` (S1)
  - `EinfacheLogikStrategie` (S2)
  - `MinimaxStrategie` (S3 mit Alpha-Beta-Pruning)

## Technische Details

### Parametervalidierung
- Reguläre Ausdrücke für syntaktische Korrektheit
- Logikprüfung für sinnvolle Parameterkombinationen
- Beliebige Parameterreihenfolge unterstützt

### KI-Strategien
1. **S1 - Zufallsstrategie**: Beschränkte zufällige Züge in der Nähe bestehender Steine
2. **S2 - Einfache Logik**: Blockt Gegnerzüge und sucht eigene Gewinnmöglichkeiten
3. **S3 - Minimax**: Rekursiver Algorithmus mit Alpha-Beta-Pruning

### Debug-Modi
- **Konsole (D)**: Echtzeitausgabe aller Züge
- **Datei (DF)**: Persistente Protokollierung in ASCII-Format

### XML-Speicherung
```xml
<spiel>
  <modus>PC</modus>
  <strategie>S3</strategie>
  <brett groesse="15">...</brett>
  <zuege>...</zuege>
  <gewinner>P1</gewinner>
</spiel>
```

## Autoren

- OIL SS2025 - Hochschule Hannover    
- Entwickelt für die Abschlussaufgabe "Objektorientierte Implementierungssprache"

## Lizenz

Dieses Projekt wurde für Bildungszwecke entwickelt. 