# Gomoku – JavaFX board game with Minimax AI

A complete implementation of the board game **Gomoku** (five in a row on a 15×15 board) in Java 17 and JavaFX,
playable in a graphical interface or on the console, against a friend or against one of three computer opponents.

Built as the final project for the course *Objektorientierte Implementierungssprache (OIL)* at
Hochschule Hannover, summer semester 2025.

<!-- Add a screenshot here: docs/screenshot.png -->

## Features

- **Three game modes**: Player vs Player, Player vs Computer, Computer vs Computer
- **Three AI strategies**, selectable per player:
  - **S1** – random moves, limited to the area around existing stones
  - **S2** – rule-based: completes its own lines and blocks the opponent's threats
  - **S3** – **Minimax with alpha-beta pruning** (search depth 4) and a heuristic board evaluation
- **JavaFX GUI** with a welcome screen, game settings and save/load dialogs
- **Console mode** for playing entirely in the terminal
- **Save and load games as XML** (JAXB), including the move history and win/draw statistics
- **Command-line parameters** validated with regular expressions, in any order
- **Debug output** to the console (`D`) or to a log file (`DF`)
- Windows `.exe` build via Launch4j

## Architecture

The project follows **Model-View-Controller**, with the **Strategy pattern** for the AI opponents and a
**Factory** that creates a strategy from its parameter (`S1`–`S3`).

```
de.hsh.oil.gomoku/
├── Main.java, GomokuApplication.java   Entry points, parameter handling
├── model/        Board, players, moves, configuration, save games, statistics
├── controller/   SpielController – game flow, turn order, win detection
├── view/         GuiView, WelcomeView (JavaFX), KonsoleView (terminal)
├── strategy/     ZugStrategie interface + Random, Rule-based and Minimax strategies
└── util/         ParameterValidator (regex), StrategieFactory, XmlSaveManager, DebugLogger
```

Because the views only talk to the controller, the same game logic drives both the GUI and the console.

## Getting started

Requirements: **Java 17+**. Maven does not need to be installed; the Maven wrapper downloads it.
JavaFX is pulled in as a Maven dependency.

```bash
./mvnw package                     # Windows: mvnw.cmd package
java -jar target/gomoku-1.0.0-executable.jar
```

### Command-line parameters

Without parameters the game opens the welcome screen. Parameters can be combined in any order:

| Parameter | Meaning |
|-----------|---------|
| `PP` / `PC` / `CC` | Player vs Player / Player vs Computer / Computer vs Computer |
| `S1` / `S2` / `S3` | AI strategy: random / rule-based / Minimax |
| `D` / `DF` | Debug output to the console / to a file |
| `Go` | Start the given configuration in the GUI instead of the console |

```bash
java -jar target/gomoku-1.0.0-executable.jar PC S3 D        # console, against Minimax, with debug output
java -jar target/gomoku-1.0.0-executable.jar Go CC S1 S3    # GUI, random AI vs Minimax
```

### Windows executable

`build-exe.bat` builds the jar and wraps it into `releases/Gomoku-1.0.0.exe` with Launch4j.
See [BUILD-README.md](BUILD-README.md) for details.

## Tech stack

Java 17 · JavaFX 20 · Maven · JAXB (XML) · Launch4j
