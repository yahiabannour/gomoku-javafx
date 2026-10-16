[Deutsch](README.md) | **English**

# 🎮 Gomoku – Five in a Row (JavaFX) ⚫⚪

![Java](https://img.shields.io/badge/java-17-%23ED8B00.svg?style=for-the-badge&logo=openjdk&logoColor=white)
![JavaFX](https://img.shields.io/badge/javafx-20-%23007396.svg?style=for-the-badge&logo=openjdk&logoColor=white)
![Maven](https://img.shields.io/badge/maven-%23C71A36.svg?style=for-the-badge&logo=apachemaven&logoColor=white)

A complete implementation of the board game **Gomoku** (five in a row on a 15×15 board) in Java 17 and JavaFX –
playable in a graphical interface or on the console, against another person or against one of three computer
strategies.

Final project for the course *Objektorientierte Implementierungssprache (OIL)* at Hochschule Hannover, summer
semester 2025. The interface is in German.

| 🏠 Start screen | ♟️ Game in progress (computer vs computer) |
|---|---|
| ![Start screen](docs/startbildschirm.png) | ![Game](docs/spiel.png) |

---

## ✨ Features

### 🕹️ Game modes
* **Player vs player**, **player vs computer** and **computer vs computer**
* **JavaFX interface** with start screen and game settings, plus a **console mode** for the terminal

### 🤖 AI strategies (selectable per player)
* **S1 – Random**: random moves, limited to the area around existing stones
* **S2 – Rule-based**: completes its own lines and blocks the opponent's threats
* **S3 – Minimax with alpha-beta pruning**: heuristic board evaluation, search depth adjustable from 1 to 6
  (default: 4)
* **📋 Move log**: for every AI move the computing time and the number of evaluated positions – in my test over
  **10 million positions** for a single move

### 💾 Saving & tools
* **Save and load games as XML** (JAXB), including the move history and win/draw statistics
* **Command-line parameters** validated with **regular expressions**, in any order
* **Debug output** to the console (`D`) or to a file (`DF`)
* **Windows `.exe`** via Launch4j

---

## 🏗️ Architecture

**Model-View-Controller** with the **Strategy pattern** for the AI opponents and a **Factory** that creates a
strategy from its parameter (`S1`–`S3`).

```
de.hsh.oil.gomoku/
├── Main.java, GomokuApplication.java   Entry points, parameter handling
├── model/        Board, players, moves, configuration, save games, statistics
├── controller/   SpielController – game flow, turn order, win detection
├── view/         GuiView, WelcomeView (JavaFX), KonsoleView (terminal)
├── strategy/     ZugStrategie interface + random, rule-based and Minimax strategies
└── util/         ParameterValidator (regex), StrategieFactory, XmlSaveManager, DebugLogger
```

Because the views only talk to the controller, the same game logic drives both the GUI and the console.

---

## 🛠️ Getting Started

**Requirement:** Java 17+. Maven does not need to be installed – the Maven wrapper downloads it, and JavaFX is
included as a dependency.

```bash
./mvnw package                     # Windows: mvnw.cmd package
java -jar target/gomoku-1.0.0-executable.jar
```

### ⌨️ Command-line parameters

Without parameters the start screen opens. Parameters can be combined in any order:

| Parameter | Meaning |
|-----------|---------|
| `PP` / `PC` / `CC` | Player vs player / player vs computer / computer vs computer |
| `S1` / `S2` / `S3` | AI strategy: random / rule-based / Minimax |
| `D` / `DF` | Debug output to the console / to a file |
| `Go` | Start the chosen configuration in the GUI instead of the console |

```bash
java -jar target/gomoku-1.0.0-executable.jar PC S3 D        # console, against Minimax, with debug output
java -jar target/gomoku-1.0.0-executable.jar Go CC S2 S3    # GUI, rule-based AI vs Minimax
```

### 🪟 Windows executable

`build-exe.bat` builds the jar and wraps it into `releases/Gomoku-1.0.0.exe` with Launch4j – see
[BUILD-README.md](BUILD-README.md).

---

## 🧰 Tech Stack

Java 17 · JavaFX 20 · Maven · JAXB (XML) · Launch4j
