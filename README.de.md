[English](README.md) | **Deutsch**

# 🎮 Gomoku – Fünf in einer Reihe (JavaFX) ⚫⚪

Eine JavaFX-Umsetzung von **Gomoku** (Fünf in einer Reihe) mit drei KI-Gegnern – von Zufallszügen bis zur
**Minimax-Suche mit Alpha-Beta-Pruning** – spielbar in einer modernen Oberfläche oder direkt in der Konsole.
Gebaut mit **Java 17**, **JavaFX** und **Maven**.

| 🏠 Startbildschirm | ♟️ Laufendes Spiel (Computer gegen Computer) |
|---|---|
| ![Startbildschirm](docs/startbildschirm.png) | ![Spiel](docs/spiel.png) |

## 📋 Voraussetzungen

- Java 17+
- Keine Maven-Installation nötig – der Maven-Wrapper ist enthalten

## 🚀 Bauen & Starten

```bash
# Ausführbares JAR bauen (Windows: mvnw.cmd package)
./mvnw package

# JAR starten
java -jar target/gomoku-1.0.0-executable.jar
```

## 💻 Kommandozeilenargumente

Der Startbildschirm lässt sich überspringen, um direkt ein Spiel zu starten. Die Argumente sind in beliebiger
Reihenfolge erlaubt und werden mit **regulären Ausdrücken** geprüft.

| Argument | Beschreibung |
|----------|--------------|
| *(keins)* | Öffnet den Startbildschirm |
| `PP` | Spieler gegen Spieler |
| `PC` | Spieler gegen Computer |
| `CC` | Computer gegen Computer |
| `S1` / `S2` / `S3` | KI-Strategie (eine pro Computer-Spieler) |
| `D` | Debug-Ausgabe in der Konsole |
| `DF` | Debug-Ausgabe in eine Datei |
| `Go` | Die gewählte Konfiguration in der Oberfläche statt in der Konsole spielen |

**🤖 KI-Strategien:**

| Code | Name | Nutzt Tiefe | Beschreibung |
|------|------|-------------|--------------|
| `S1` | 🎲 Zufall | Nein | Zufallszüge, beschränkt auf die Umgebung vorhandener Steine |
| `S2` | 🛡️ Regelbasiert | Nein | Vervollständigt eigene Reihen und blockt Drohungen des Gegners |
| `S3` | 🧠 Minimax | Ja | Minimax-Suche mit Alpha-Beta-Pruning und heuristischer Stellungsbewertung |

Die Suchtiefe (1–6, Standard 4) wird auf dem Startbildschirm gewählt und gilt nur für S3.

**📝 Beispiele:**

```bash
java -jar target/gomoku-1.0.0-executable.jar PP                # Spieler gegen Spieler in der Konsole
java -jar target/gomoku-1.0.0-executable.jar PC S3 D           # gegen Minimax, mit Debug-Ausgabe
java -jar target/gomoku-1.0.0-executable.jar Go CC S2 S3       # Oberfläche: regelbasierte KI gegen Minimax
```

## 📁 Projektstruktur

```
de.hsh.oil.gomoku/
├── Main.java, GomokuApplication.java   Einstiegspunkte, Parameterverarbeitung
├── model/        Spielbrett, Spieler, Züge, Konfiguration, Spielstände, Statistik
├── controller/   SpielController – Spielablauf, Zugreihenfolge, Gewinnerkennung
├── view/         GuiView, WelcomeView (JavaFX), KonsoleView (Terminal)
├── strategy/     Interface ZugStrategie + Zufalls-, Regel- und Minimax-Strategie
└── util/         ParameterValidator (Regex), StrategieFactory, XmlSaveManager, DebugLogger
```

## 🧩 Entwurfsmuster

| Muster | Wo |
|--------|----|
| **MVC** | `model` / `view` / `controller` sind vollständig getrennt – dieselbe Spiellogik steuert Oberfläche und Konsole |
| **Strategy** | `ZugStrategie` – KI-Algorithmen austauschbar, ohne den Controller zu ändern |
| **Factory** | `StrategieFactory` ordnet die Codes `S1`–`S3` den Strategien zu |

## ✨ Spielfunktionen

- 📐 Einstellbare Brettgröße von 10×10 bis 20×20 (Standard 15×15)
- ⚫ Klassische Gomoku-Regeln – fünf in einer Reihe gewinnen, waagerecht, senkrecht oder diagonal
- 🎮 Drei Spielmodi: Spieler gegen Spieler, Spieler gegen Computer, Computer gegen Computer
- 🧠 Einstellbare Minimax-Suchtiefe (1–6)
- 📜 **Zugprotokoll** mit der Position jedes Zugs – und für jeden KI-Zug Rechenzeit und Anzahl der bewerteten
  Stellungen (im Test über **10 Millionen** für einen einzigen Zug)
- 💾 Spielstände als XML speichern und laden (JAXB), inklusive Zugverlauf und Sieg-/Remis-Statistik
- 🐛 Debug-Modus – Ausgabe in der Konsole (`D`) oder in einer Logdatei (`DF`)
- 🖥️ Konsolenmodus zum Spielen direkt im Terminal
- 🪟 Windows-`.exe` über Launch4j (`build-exe.bat`, siehe [BUILD-README.md](BUILD-README.md))
