[Deutsch](README.md) | **English**

# Gomoku – JavaFX board game with a Minimax AI

A complete implementation of the board game **Gomoku** (five in a row on a 15×15 board) in Java 17 and JavaFX.
It can be played in a graphical interface or on the console, against another person or against one of three
computer strategies.

Built as the final project for the course *Objektorientierte Implementierungssprache (OIL)* at
Hochschule Hannover, summer semester 2025. The interface is in German.

| Start screen | Game in progress (computer vs computer) |
|---|---|
| ![Start screen](docs/startbildschirm.png) | ![Game](docs/spiel.png) |

## Features

- **Three game modes**: player vs player, player vs computer, computer vs computer
- **Three AI strategies**, selectable per player:
  - **S1** – random moves, limited to the area around existing stones
  - **S2** – rule-based: completes its own lines and blocks the opponent's threats
  - **S3** – **Minimax with alpha-beta pruning** and a heuristic board evaluation; the search depth can be set
    from 1 to 6 (default: 4)
- **JavaFX interface** with a start screen, game settings and a move log that shows the computing time and the
  number of evaluated positions for every AI move
- **Console mode** for playing directly in the terminal
- **Save and load games as XML** (JAXB), including the move history and win/draw statistics
- **Command-line parameters**, validated with regular expressions, in any order
- **Debug output** to the console (`D`) or to a file (`DF`)
- Windows `.exe` via Launch4j

## Architecture

The project follows the **Model-View-Controller** pattern. The AI opponents use the **Strategy pattern**, and a
**Factory** creates a strategy from its parameter (`S1`–`S3`).

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

## Getting started

Requirement: **Java 17+**. Maven does not need to be installed; the Maven wrapper downloads it.
JavaFX is included as a Maven dependency.

```bash
./mvnw package                     # Windows: mvnw.cmd package
java -jar target/gomoku-1.0.0-executable.jar
```

### Command-line parameters

Without parameters the start screen opens. The parameters can be combined in any order:

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

### Windows executable

`build-exe.bat` builds the jar and wraps it into `releases/Gomoku-1.0.0.exe` with Launch4j.
See [BUILD-README.md](BUILD-README.md) for details.

## Tech stack

Java 17 · JavaFX 20 · Maven · JAXB (XML) · Launch4j
