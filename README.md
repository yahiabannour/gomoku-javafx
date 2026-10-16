# Gomoku – Brettspiel in JavaFX mit Minimax-KI

Eine vollständige Umsetzung des Brettspiels **Gomoku** (fünf in einer Reihe auf einem 15×15-Feld) in Java 17 und
JavaFX. Gespielt wird in einer grafischen Oberfläche oder in der Konsole, gegen eine andere Person oder gegen eine
von drei Computer-Strategien.

Entstanden als Abschlussprojekt im Fach *Objektorientierte Implementierungssprache (OIL)* an der
Hochschule Hannover, Sommersemester 2025.

| Startbildschirm | Laufendes Spiel (Computer gegen Computer) |
|---|---|
| ![Startbildschirm](docs/startbildschirm.png) | ![Spiel](docs/spiel.png) |

## Funktionen

- **Drei Spielmodi**: Spieler gegen Spieler, Spieler gegen Computer, Computer gegen Computer
- **Drei KI-Strategien**, pro Spieler wählbar:
  - **S1** – Zufallszüge, beschränkt auf die Umgebung vorhandener Steine
  - **S2** – regelbasiert: vervollständigt eigene Reihen und blockt Drohungen des Gegners
  - **S3** – **Minimax mit Alpha-Beta-Pruning** und heuristischer Stellungsbewertung; die Suchtiefe ist von 1 bis 6
    einstellbar (Standard: 4)
- **JavaFX-Oberfläche** mit Startbildschirm, Spieleinstellungen und einem Zugprotokoll, das für jeden KI-Zug die
  Rechenzeit und die Anzahl der bewerteten Stellungen anzeigt
- **Konsolenmodus** zum Spielen direkt im Terminal
- **Spielstände als XML speichern und laden** (JAXB), inklusive Zugverlauf und Sieg-/Remis-Statistik
- **Kommandozeilenparameter**, geprüft mit regulären Ausdrücken, in beliebiger Reihenfolge
- **Debug-Ausgabe** in der Konsole (`D`) oder in eine Datei (`DF`)
- Windows-`.exe` über Launch4j

## Architektur

Das Projekt folgt dem **Model-View-Controller**-Muster. Die KI-Gegner sind mit dem **Strategy-Pattern**
umgesetzt, eine **Factory** erzeugt die Strategie aus ihrem Parameter (`S1`–`S3`).

```
de.hsh.oil.gomoku/
├── Main.java, GomokuApplication.java   Einstiegspunkte, Parameterverarbeitung
├── model/        Spielbrett, Spieler, Züge, Konfiguration, Spielstände, Statistik
├── controller/   SpielController – Spielablauf, Zugreihenfolge, Gewinnerkennung
├── view/         GuiView, WelcomeView (JavaFX), KonsoleView (Terminal)
├── strategy/     Interface ZugStrategie + Zufalls-, Regel- und Minimax-Strategie
└── util/         ParameterValidator (Regex), StrategieFactory, XmlSaveManager, DebugLogger
```

Da die Views nur mit dem Controller sprechen, steuert dieselbe Spiellogik sowohl die Oberfläche als auch die Konsole.

## Starten

Voraussetzung: **Java 17+**. Maven muss nicht installiert sein, der Maven-Wrapper lädt es herunter.
JavaFX wird als Maven-Abhängigkeit eingebunden.

```bash
./mvnw package                     # Windows: mvnw.cmd package
java -jar target/gomoku-1.0.0-executable.jar
```

### Kommandozeilenparameter

Ohne Parameter öffnet sich der Startbildschirm. Die Parameter lassen sich beliebig kombinieren:

| Parameter | Bedeutung |
|-----------|-----------|
| `PP` / `PC` / `CC` | Spieler gegen Spieler / Spieler gegen Computer / Computer gegen Computer |
| `S1` / `S2` / `S3` | KI-Strategie: Zufall / regelbasiert / Minimax |
| `D` / `DF` | Debug-Ausgabe in der Konsole / in einer Datei |
| `Go` | Die gewählte Konfiguration in der Oberfläche statt in der Konsole starten |

```bash
java -jar target/gomoku-1.0.0-executable.jar PC S3 D        # Konsole, gegen Minimax, mit Debug-Ausgabe
java -jar target/gomoku-1.0.0-executable.jar Go CC S2 S3    # Oberfläche, regelbasierte KI gegen Minimax
```

### Windows-Programm

`build-exe.bat` baut das JAR und verpackt es mit Launch4j zu `releases/Gomoku-1.0.0.exe`.
Details stehen in [BUILD-README.md](BUILD-README.md).

## Technologien

Java 17 · JavaFX 20 · Maven · JAXB (XML) · Launch4j
