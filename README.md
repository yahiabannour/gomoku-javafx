**Deutsch** | [English](README.en.md)

# 🎮 Gomoku – Fünf in einer Reihe (JavaFX) ⚫⚪

![Java](https://img.shields.io/badge/java-17-%23ED8B00.svg?style=for-the-badge&logo=openjdk&logoColor=white)
![JavaFX](https://img.shields.io/badge/javafx-20-%23007396.svg?style=for-the-badge&logo=openjdk&logoColor=white)
![Maven](https://img.shields.io/badge/maven-%23C71A36.svg?style=for-the-badge&logo=apachemaven&logoColor=white)

Eine vollständige Umsetzung des Brettspiels **Gomoku** (fünf in einer Reihe auf einem 15×15-Feld) in Java 17 und
JavaFX – spielbar in einer grafischen Oberfläche oder in der Konsole, gegen eine andere Person oder gegen eine von
drei Computer-Strategien.

Abschlussprojekt im Fach *Objektorientierte Implementierungssprache (OIL)* an der Hochschule Hannover,
Sommersemester 2025.

| 🏠 Startbildschirm | ♟️ Laufendes Spiel (Computer gegen Computer) |
|---|---|
| ![Startbildschirm](docs/startbildschirm.png) | ![Spiel](docs/spiel.png) |

---

## ✨ Funktionen

### 🕹️ Spielmodi
* **Spieler gegen Spieler**, **Spieler gegen Computer** und **Computer gegen Computer**
* **JavaFX-Oberfläche** mit Startbildschirm und Spieleinstellungen, dazu ein **Konsolenmodus** für das Terminal

### 🤖 KI-Strategien (pro Spieler wählbar)
* **S1 – Zufall**: Zufallszüge, beschränkt auf die Umgebung vorhandener Steine
* **S2 – Regelbasiert**: vervollständigt eigene Reihen und blockt Drohungen des Gegners
* **S3 – Minimax mit Alpha-Beta-Pruning**: heuristische Stellungsbewertung, Suchtiefe von 1 bis 6 einstellbar
  (Standard: 4)
* **📋 Zugprotokoll**: für jeden KI-Zug Rechenzeit und Anzahl der bewerteten Stellungen – im Test über
  **10 Millionen Stellungen** für einen einzigen Zug

### 💾 Speichern & Werkzeuge
* **Spielstände als XML speichern und laden** (JAXB), inklusive Zugverlauf und Sieg-/Remis-Statistik
* **Kommandozeilenparameter**, geprüft mit **regulären Ausdrücken**, in beliebiger Reihenfolge
* **Debug-Ausgabe** in der Konsole (`D`) oder in eine Datei (`DF`)
* **Windows-`.exe`** über Launch4j

---

## 🏗️ Architektur

**Model-View-Controller** mit dem **Strategy-Pattern** für die KI-Gegner und einer **Factory**, die die Strategie
aus ihrem Parameter (`S1`–`S3`) erzeugt.

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

---

## 🛠️ Starten

**Voraussetzung:** Java 17+. Maven muss nicht installiert sein – der Maven-Wrapper lädt es herunter, und JavaFX ist
als Abhängigkeit eingebunden.

```bash
./mvnw package                     # Windows: mvnw.cmd package
java -jar target/gomoku-1.0.0-executable.jar
```

### ⌨️ Kommandozeilenparameter

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

### 🪟 Windows-Programm

`build-exe.bat` baut das JAR und verpackt es mit Launch4j zu `releases/Gomoku-1.0.0.exe` – Details in
[BUILD-README.md](BUILD-README.md).

---

## 🧰 Technologien

Java 17 · JavaFX 20 · Maven · JAXB (XML) · Launch4j
