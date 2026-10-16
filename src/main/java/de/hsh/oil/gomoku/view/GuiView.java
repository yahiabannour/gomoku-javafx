package de.hsh.oil.gomoku.view;

import de.hsh.oil.gomoku.controller.SpielController;
import de.hsh.oil.gomoku.model.GameSave;
import de.hsh.oil.gomoku.model.Spielbrett;
import de.hsh.oil.gomoku.model.Spieler;
import de.hsh.oil.gomoku.model.SpielKonfiguration;
import de.hsh.oil.gomoku.model.Zug;
import de.hsh.oil.gomoku.util.FontAwesome;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.stage.FileChooser;
import java.util.Optional;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import javafx.util.StringConverter;

/**
 * JavaFX GUI-View für das Gomoku-Spiel
 * 
 * Implementiert eine grafische Benutzeroberfläche mit JavaFX
 * 
 * @author OIL SS2025
 */
public class GuiView implements SpielView {
    
    private SpielController controller;
    private Stage primaryStage;
    private GridPane spielfeldGrid;
    private Label statusLabel;
    private Label spielerInfoLabel;
    private ScrollPane nachrichtenScrollPane;
    private VBox nachrichtenContainer;
    private Button neuesSpielButton;
    private Button neueRundeButton;
    private Button beendenButton;
    private Button[][] feldButtons;
    
    // View-Optionen
    private ToggleGroup viewModeGroup;
    private RadioButton simpleViewRadio;
    private RadioButton enhancedViewRadio;
    
    // Debug-Info-Speicher
    private java.util.Map<String, String> debugInfoMap = new java.util.HashMap<>();
    
    // Message-Speicher für View-Switching
    private java.util.List<MessageData> messageHistory = new java.util.ArrayList<>();
    
    // Message-Data-Klasse
    private static class MessageData {
        final String type;
        final String[] args;
        final long timestamp;
        
        MessageData(String type, String... args) {
            this.type = type;
            this.args = args;
            this.timestamp = System.currentTimeMillis();
        }
    }
    
    // Statische Referenz für Application.launch()
    private static GuiView instance;
    
    /**
     * Konstruktor für die GUI-View
     * 
     * @param controller Der Spiel-Controller
     */
    public GuiView(SpielController controller) {
        this.controller = controller;
        instance = this;
    }
    
    @Override
    public void starteGui() {
        // GUI wird von der GomokuApplication gestartet
        Platform.runLater(() -> {
            initialisiereGui();
            aktualisiereAnzeige();
        });
    }
    
    /**
     * Setzt die Primary Stage von der JavaFX Application
     */
    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }
    
    /**
     * Initialisiert die GUI-Komponenten
     */
    private void initialisiereGui() {
        primaryStage.setTitle("🎯 Gomoku - Strategisches Brettspiel");
        
        // Hauptlayout mit modernem Design
        BorderPane hauptLayout = new BorderPane();
        hauptLayout.setPadding(new Insets(20));
        
        // Heller Gradient-Hintergrund für bessere Lesbarkeit
        LinearGradient gradient = new LinearGradient(0, 0, 0, 1, true, null,
            new Stop(0, Color.web("#ECEFF4")),
            new Stop(0.5, Color.web("#D8DEE9")),
            new Stop(1, Color.web("#C0C5D0"))
        );
        hauptLayout.setBackground(new Background(new BackgroundFill(gradient, null, null)));
        
        // Linker Bereich - Spieler-Info und Nachrichten (Moderne Karte) - VERGRÖSSERT
        VBox linkerBereich = new VBox(15);
        linkerBereich.setPadding(new Insets(25));
        linkerBereich.setPrefWidth(450);
        linkerBereich.setMinWidth(400);
        linkerBereich.setStyle(
            "-fx-background-color: rgba(236, 239, 244, 0.95);" +
            "-fx-background-radius: 15;" +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 15, 0, 0, 5);"
        );
        
        spielerInfoLabel = new Label();
        spielerInfoLabel.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        spielerInfoLabel.setTextFill(Color.BLACK);
        spielerInfoLabel.setWrapText(true);
        spielerInfoLabel.setAlignment(Pos.CENTER);
        
        statusLabel = new Label("Bereit zum Spielen");
        statusLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 14));
        statusLabel.setTextFill(Color.BLACK);
        statusLabel.setWrapText(true);
        statusLabel.setAlignment(Pos.CENTER);
        
        // Separator mit verbessertem Design
        Separator separator = new Separator();
        separator.setStyle("-fx-background-color: #81A1C1;");
        
        // View-Optionen
        Label viewLabel = new Label(FontAwesome.iconText(FontAwesome.EYE, "Ansichtsmodus"));
        viewLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        viewLabel.setTextFill(Color.BLACK);
        
        viewModeGroup = new ToggleGroup();
        
        HBox viewOptionsBox = new HBox(15);
        viewOptionsBox.setAlignment(Pos.CENTER_LEFT);
        
        simpleViewRadio = new RadioButton(FontAwesome.iconText(FontAwesome.FILE_TEXT, "Einfach"));
        simpleViewRadio.setFont(Font.font("Arial", FontWeight.NORMAL, 12));
        simpleViewRadio.setTextFill(Color.BLACK);
        simpleViewRadio.setToggleGroup(viewModeGroup);
        
        enhancedViewRadio = new RadioButton(FontAwesome.iconText(FontAwesome.MAGIC, "Erweitert"));
        enhancedViewRadio.setFont(Font.font("Arial", FontWeight.NORMAL, 12));
        enhancedViewRadio.setTextFill(Color.BLACK);
        enhancedViewRadio.setToggleGroup(viewModeGroup);
        enhancedViewRadio.setSelected(true); // Default
        
        viewOptionsBox.getChildren().addAll(simpleViewRadio, enhancedViewRadio);
        
        // View-Mode-Listener hinzufügen
        viewModeGroup.selectedToggleProperty().addListener((obs, oldVal, newVal) -> {
            refreshMessageDisplay();
        });
        
        // Separator 2
        Separator separator2 = new Separator();
        separator2.setStyle("-fx-background-color: #81A1C1;");
        
        // Nachrichten-Container mit Card-System
        nachrichtenContainer = new VBox(8);
        nachrichtenContainer.setPadding(new Insets(5));
        
        nachrichtenScrollPane = new ScrollPane(nachrichtenContainer);
        nachrichtenScrollPane.setFitToWidth(true);
        nachrichtenScrollPane.setStyle(
            "-fx-background-color: rgba(255, 255, 255, 0.9); " +
            "-fx-border-color: #D8DEE9; " +
            "-fx-border-radius: 8px; " +
            "-fx-background-radius: 8px;"
        );
        nachrichtenScrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        nachrichtenScrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        
        // Willkommensnachricht hinzufügen
        addWelcomeMessage();
        
        // VBox.setVgrow für resizable ScrollPane
        VBox.setVgrow(nachrichtenScrollPane, Priority.ALWAYS);
        
        linkerBereich.getChildren().addAll(spielerInfoLabel, statusLabel, separator, 
                                         viewLabel, viewOptionsBox, separator2, nachrichtenScrollPane);
        hauptLayout.setLeft(linkerBereich);
        
        // Mittlerer Bereich - Spielfeld (Moderne Karte)
        VBox spielfeldBereich = new VBox(15);
        spielfeldBereich.setAlignment(Pos.CENTER);
        spielfeldBereich.setPadding(new Insets(25));
        spielfeldBereich.setStyle(
            "-fx-background-color: rgba(236, 239, 244, 0.95);" +
            "-fx-background-radius: 15;" +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 15, 0, 0, 5);"
        );
        
        int boardSize = controller.getBrett().getGroesse();
        Label spielfeldLabel = new Label("🎯 Gomoku Spielfeld (" + boardSize + "×" + boardSize + ")");
        spielfeldLabel.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        spielfeldLabel.setTextFill(Color.BLACK);
        spielfeldLabel.setEffect(new DropShadow(3, Color.web("#81A1C1")));
        
        spielfeldGrid = new GridPane();
        spielfeldGrid.setAlignment(Pos.CENTER);
        spielfeldGrid.setHgap(2);
        spielfeldGrid.setVgap(2);
        spielfeldGrid.setStyle(
            "-fx-background-color: #8B4513; " +
            "-fx-padding: 8px; " +
            "-fx-background-radius: 10px; " +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.4), 10, 0, 0, 3);"
        );
        
        erstelleSpielfeld();
        
        spielfeldBereich.getChildren().addAll(spielfeldLabel, spielfeldGrid);
        hauptLayout.setCenter(spielfeldBereich);
        
        // Unterer Bereich - Moderne Buttons
        HBox buttonBereich = new HBox(15);
        buttonBereich.setAlignment(Pos.CENTER);
        buttonBereich.setPadding(new Insets(20, 0, 0, 0));
        
        neuesSpielButton = new Button(FontAwesome.iconText(FontAwesome.REFRESH, "Neues Spiel"));
        neuesSpielButton.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        neuesSpielButton.setPrefSize(120, 40);
        neuesSpielButton.setStyle(
            "-fx-background-color: linear-gradient(to bottom, #5E81AC, #4C6C92);" +
            "-fx-text-fill: white;" +
            "-fx-background-radius: 25;" +
            "-fx-border-radius: 25;" +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 8, 0, 0, 3);"
        );
        neuesSpielButton.setOnMouseEntered(e -> neuesSpielButton.setStyle(
            "-fx-background-color: linear-gradient(to bottom, #6B8EB8, #5877A0);" +
            "-fx-text-fill: white;" +
            "-fx-background-radius: 25;" +
            "-fx-border-radius: 25;" +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.4), 12, 0, 0, 5);" +
            "-fx-scale-x: 1.05;" +
            "-fx-scale-y: 1.05;"
        ));
        neuesSpielButton.setOnMouseExited(e -> neuesSpielButton.setStyle(
            "-fx-background-color: linear-gradient(to bottom, #5E81AC, #4C6C92);" +
            "-fx-text-fill: white;" +
            "-fx-background-radius: 25;" +
            "-fx-border-radius: 25;" +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 8, 0, 0, 3);" +
            "-fx-scale-x: 1.0;" +
            "-fx-scale-y: 1.0;"
        ));
        neuesSpielButton.setOnAction(e -> {
            // Message-History und Debug-Info löschen
            messageHistory.clear();
            debugInfoMap.clear();
            nachrichtenContainer.getChildren().clear();
            
            // Willkommensnachricht wieder hinzufügen
            addWelcomeMessage();
            
            controller.starteNeuesSpiel();
        });
        
        // Neue Runde Button (zurück zum Welcome Screen)
        neueRundeButton = new Button(FontAwesome.iconText(FontAwesome.HOME, "Neue Runde"));
        neueRundeButton.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        neueRundeButton.setPrefSize(120, 40);
        neueRundeButton.setStyle(
            "-fx-background-color: linear-gradient(to bottom, #88C0D0, #7FB3C3);" +
            "-fx-text-fill: white;" +
            "-fx-background-radius: 25;" +
            "-fx-border-radius: 25;" +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 8, 0, 0, 3);"
        );
        neueRundeButton.setOnMouseEntered(e -> neueRundeButton.setStyle(
            "-fx-background-color: linear-gradient(to bottom, #95CCD6, #8BC6D1);" +
            "-fx-text-fill: white;" +
            "-fx-background-radius: 25;" +
            "-fx-border-radius: 25;" +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.4), 12, 0, 0, 5);" +
            "-fx-scale-x: 1.05;" +
            "-fx-scale-y: 1.05;"
        ));
        neueRundeButton.setOnMouseExited(e -> neueRundeButton.setStyle(
            "-fx-background-color: linear-gradient(to bottom, #88C0D0, #7FB3C3);" +
            "-fx-text-fill: white;" +
            "-fx-background-radius: 25;" +
            "-fx-border-radius: 25;" +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 8, 0, 0, 3);" +
            "-fx-scale-x: 1.0;" +
            "-fx-scale-y: 1.0;"
        ));
        neueRundeButton.setOnAction(e -> zeigeWelcomeScreen());
        
        beendenButton = new Button(FontAwesome.iconText(FontAwesome.TIMES, "Beenden"));
        beendenButton.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        beendenButton.setPrefSize(120, 40);
        beendenButton.setStyle(
            "-fx-background-color: linear-gradient(to bottom, #BF616A, #A54E56);" +
            "-fx-text-fill: white;" +
            "-fx-background-radius: 25;" +
            "-fx-border-radius: 25;" +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 8, 0, 0, 3);"
        );
        beendenButton.setOnMouseEntered(e -> beendenButton.setStyle(
            "-fx-background-color: linear-gradient(to bottom, #D08770, #B8705F);" +
            "-fx-text-fill: white;" +
            "-fx-background-radius: 25;" +
            "-fx-border-radius: 25;" +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.4), 12, 0, 0, 5);" +
            "-fx-scale-x: 1.05;" +
            "-fx-scale-y: 1.05;"
        ));
        beendenButton.setOnMouseExited(e -> beendenButton.setStyle(
            "-fx-background-color: linear-gradient(to bottom, #BF616A, #A54E56);" +
            "-fx-text-fill: white;" +
            "-fx-background-radius: 25;" +
            "-fx-border-radius: 25;" +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 8, 0, 0, 3);" +
            "-fx-scale-x: 1.0;" +
            "-fx-scale-y: 1.0;"
        ));
        beendenButton.setOnAction(e -> zeigeBeendenDialog());
        
        // Save/Load Buttons
        Button speichernButton = new Button(FontAwesome.iconText(FontAwesome.FILE_TEXT, "Speichern"));
        speichernButton.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        speichernButton.setPrefSize(120, 40);
        speichernButton.setStyle(
            "-fx-background-color: linear-gradient(to bottom, #A3BE8C, #8FAC72);" +
            "-fx-text-fill: white;" +
            "-fx-background-radius: 25;" +
            "-fx-border-radius: 25;" +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 8, 0, 0, 3);"
        );
        speichernButton.setOnMouseEntered(e -> speichernButton.setStyle(
            "-fx-background-color: linear-gradient(to bottom, #B0CB99, #9CB87F);" +
            "-fx-text-fill: white;" +
            "-fx-background-radius: 25;" +
            "-fx-border-radius: 25;" +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.4), 12, 0, 0, 5);" +
            "-fx-scale-x: 1.05;" +
            "-fx-scale-y: 1.05;"
        ));
        speichernButton.setOnMouseExited(e -> speichernButton.setStyle(
            "-fx-background-color: linear-gradient(to bottom, #A3BE8C, #8FAC72);" +
            "-fx-text-fill: white;" +
            "-fx-background-radius: 25;" +
            "-fx-border-radius: 25;" +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 8, 0, 0, 3);" +
            "-fx-scale-x: 1.0;" +
            "-fx-scale-y: 1.0;"
        ));
        speichernButton.setOnAction(e -> zeigeSpeichernDialog());
        
        Button ladenButton = new Button(FontAwesome.iconText(FontAwesome.FOLDER, "Laden"));
        ladenButton.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        ladenButton.setPrefSize(120, 40);
        ladenButton.setStyle(
            "-fx-background-color: linear-gradient(to bottom, #EBCB8B, #D7B76F);" +
            "-fx-text-fill: white;" +
            "-fx-background-radius: 25;" +
            "-fx-border-radius: 25;" +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 8, 0, 0, 3);"
        );
        ladenButton.setOnMouseEntered(e -> ladenButton.setStyle(
            "-fx-background-color: linear-gradient(to bottom, #F2D896, #E4C47C);" +
            "-fx-text-fill: white;" +
            "-fx-background-radius: 25;" +
            "-fx-border-radius: 25;" +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.4), 12, 0, 0, 5);" +
            "-fx-scale-x: 1.05;" +
            "-fx-scale-y: 1.05;"
        ));
        ladenButton.setOnMouseExited(e -> ladenButton.setStyle(
            "-fx-background-color: linear-gradient(to bottom, #EBCB8B, #D7B76F);" +
            "-fx-text-fill: white;" +
            "-fx-background-radius: 25;" +
            "-fx-border-radius: 25;" +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 8, 0, 0, 3);" +
            "-fx-scale-x: 1.0;" +
            "-fx-scale-y: 1.0;"
        ));
        ladenButton.setOnAction(e -> zeigeLadenDialog());

        buttonBereich.getChildren().addAll(neuesSpielButton, neueRundeButton, speichernButton, ladenButton, beendenButton);
        hauptLayout.setBottom(buttonBereich);
        
        // Scene erstellen und anzeigen
        Scene scene = new Scene(hauptLayout, 1200, 800);
        primaryStage.setScene(scene);
        primaryStage.setTitle("🎯 Gomoku - 5 Gewinnt!");
        primaryStage.setMinWidth(1000);
        primaryStage.setMinHeight(600);
        
        // Icon aus Resources laden
        try {
            var iconInputStream = getClass().getResourceAsStream("/icon.png");
            if (iconInputStream != null) {
                primaryStage.getIcons().add(new Image(iconInputStream));
            }
        } catch (Exception e) {
            System.err.println("🚨 Icon konnte nicht geladen werden: " + e.getMessage());
        }
        
        primaryStage.setOnCloseRequest(e -> {
            e.consume(); // Prevent default close
            zeigeBeendenDialog();
        });
        primaryStage.show();
        
        VBox.setVgrow(spielfeldBereich, Priority.ALWAYS);
    }
    
    /**
     * Erstellt das visuelle Spielfeld
     */
    private void erstelleSpielfeld() {
        int groesse = controller.getBrett().getGroesse();
        feldButtons = new Button[groesse][groesse];
        
        for (int y = 0; y < groesse; y++) {
            for (int x = 0; x < groesse; x++) {
                Button feldButton = new Button();
                feldButton.setPrefSize(42, 42);
                feldButton.setStyle(
                    "-fx-background-color: linear-gradient(to bottom, #DEB887, #D2B48C);" +
                    "-fx-border-color: #8B4513;" +
                    "-fx-border-width: 1px;" +
                    "-fx-background-radius: 3px;" +
                    "-fx-border-radius: 3px;" +
                    "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 3, 0, 0, 1);"
                );
                
                // Event-Handler für Klicks
                final int finalX = x;
                final int finalY = y;
                feldButton.setOnAction(e -> {
                    if (controller.istSpielAktiv() && !controller.getAktuellerSpieler().istComputer()) {
                        // Menschlichen Zug loggen (vor dem Zug)
                        String spielerName = controller.getAktuellerSpieler().getName();
                        int kommenderZugNummer = controller.getBrett().getAnzahlZuege() + 1;
                        
                        boolean erfolg = controller.verarbeiteZug(finalX, finalY);
                        if (erfolg) {
                            addMoveMessage(spielerName, finalX, finalY, kommenderZugNummer);
                            aktualisiereAnzeige();
                        } else {
                            statusLabel.setText("Ungültiger Zug! Feld bereits belegt oder Spiel beendet.");
                            statusLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 14));
                            statusLabel.setTextFill(Color.web("#BF616A"));
                        }
                    }
                });
                
                feldButtons[y][x] = feldButton;
                spielfeldGrid.add(feldButton, x, y);
            }
        }
    }
    
    @Override
    public void aktualisiereAnzeige() {
        Platform.runLater(() -> {
            aktualisiereSpielerInfo();
            aktualisiereSpielfeld();
            aktualisiereStatus();
        });
    }
    
    /**
     * Aktualisiert die Spieler-Informationen
     */
    private void aktualisiereSpielerInfo() {
        Spieler spieler1 = controller.getSpieler1();
        Spieler spieler2 = controller.getSpieler2();
        Spieler aktuell = controller.getAktuellerSpieler();
        
        String info = String.format("%s (%c) vs %s (%c)", 
                                   spieler1.getName(), spieler1.getSymbol(),
                                   spieler2.getName(), spieler2.getSymbol());
        
        if (controller.istSpielAktiv()) {
            info += "\n→ " + aktuell.getName() + " ist am Zug";
        }
        
        spielerInfoLabel.setText(info);
    }
    
    /**
     * Aktualisiert das visuelle Spielfeld
     */
    private void aktualisiereSpielfeld() {
        Spielbrett brett = controller.getBrett();
        
        for (int y = 0; y < brett.getGroesse(); y++) {
            for (int x = 0; x < brett.getGroesse(); x++) {
                char feld = brett.getFeld(x, y);
                Button button = feldButtons[y][x];
                
                if (feld == Spielbrett.LEER) {
                    button.setText("");
                    button.setStyle("-fx-background-color: #DEB887; -fx-border-color: #8B4513;");
                } else {
                    button.setText(String.valueOf(feld));
                    button.setStyle("-fx-background-color: #F5F5DC; -fx-border-color: #8B4513; " +
                                  "-fx-font-size: 16px; -fx-font-weight: bold; " +
                                  (feld == 'X' ? "-fx-text-fill: red;" : "-fx-text-fill: blue;"));
                }
                
                // Gewinnlinie hervorheben
                if (istInGewinnlinie(x, y)) {
                    button.setStyle(button.getStyle() + "-fx-background-color: #FFD700;");
                }
            }
        }
    }
    
    /**
     * Überprüft, ob eine Position in der Gewinnlinie ist
     * 
     * @param x X-Koordinate
     * @param y Y-Koordinate
     * @return true wenn in der Gewinnlinie
     */
    private boolean istInGewinnlinie(int x, int y) {
        return controller.getBrett().getGewinnLinie().stream()
                .anyMatch(pos -> pos.getX() == x && pos.getY() == y);
    }
    
    /**
     * Aktualisiert die Status-Anzeige
     */
    private void aktualisiereStatus() {
        if (!controller.istSpielAktiv()) {
            Spieler gewinner = controller.getBrett().getGewinner();
            if (gewinner != null) {
                statusLabel.setText("🎉 " + gewinner.getName() + " hat gewonnen!");
            } else {
                statusLabel.setText("🤝 Unentschieden!");
            }
        } else if (controller.getAktuellerSpieler().istComputer()) {
            statusLabel.setText(controller.getAktuellerSpieler().getName() + " denkt...");
        } else {
            statusLabel.setText("Klicke auf ein freies Feld um zu ziehen");
        }
    }
    
    // SpielView Interface-Implementierungen (vereinfacht für GUI)
    
    @Override
    public void zeigeWillkommensnachricht() {
        // Willkommensnachricht wird bereits im initialisiereGui() gesetzt
    }
    
    @Override
    public void zeigeSpielerInfo(Spieler spieler1, Spieler spieler2) {
        // Wird durch aktualisiereAnzeige() abgedeckt
    }
    
    @Override
    public void zeigeBrett(Spielbrett brett) {
        // Wird durch aktualisiereAnzeige() abgedeckt
    }
    
    @Override
    public Zug erfrageMenschenZug(Spieler spieler, Spielbrett brett) {
        // GUI ist event-driven, kein blockierendes Erfragen nötig
        return null;
    }
    
    @Override
    public void zeigeComputerDenkt(Spieler computer) {
        Platform.runLater(() -> {
            statusLabel.setText(computer.getName() + " denkt...");
            addComputerThinkingMessage(computer.getName());
        });
    }
    
    @Override
    public void zeigeComputerZug(Spieler computer, Zug zug) {
        Platform.runLater(() -> {
            statusLabel.setText(computer.getName() + " hat gezogen auf (" + 
                              (zug.getX()+1) + "," + (zug.getY()+1) + ")");
            
            // Debug-Info für erweiterte Ansicht erstellen
            String debugInfo = null;
            if (zug.getDebugInfo() != null && !zug.getDebugInfo().isEmpty()) {
                debugInfo = zug.getDebugInfo();
            }
            
            addComputerMoveMessage(computer.getName(), zug.getX(), zug.getY(), 
                                 zug.getZugNummer(), debugInfo);
        });
    }
    
    @Override
    public void zeigeSpielErgebnis(Spieler gewinner, int anzahlZuege) {
        Platform.runLater(() -> {
            String ergebnis;
            if (gewinner != null) {
                ergebnis = "🎉 " + gewinner.getName() + " hat gewonnen!\n" +
                          "Gewonnen nach " + anzahlZuege + " Zügen\n" +
                          "Herzlichen Glückwunsch! 🏆";
            } else {
                ergebnis = "🤝 Unentschieden!\n" +
                          "Spiel nach " + anzahlZuege + " Zügen beendet\n" +
                          "Gut gespielt von beiden Seiten!";
            }
            
            addSystemMessage(ergebnis);
        });
    }
    
    @Override
    public void zeigeStatistiken(Spieler spieler1, Spieler spieler2) {
        Platform.runLater(() -> {
            String statistiken = spieler1.getStatistikString() + "\n" +
                                spieler2.getStatistikString();
            
            addSystemMessage(statistiken);
        });
    }
    
    @Override
    public void zeigeFehlermeldung(String nachricht) {
        Platform.runLater(() -> {
            statusLabel.setText("❌ " + nachricht);
            statusLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 14));
            statusLabel.setTextFill(Color.web("#BF616A"));
            addSystemMessage("❌ FEHLER: " + nachricht);
        });
    }
    
    @Override
    public void zeigeNachricht(String nachricht) {
        Platform.runLater(() -> {
            statusLabel.setText("ℹ️ " + nachricht);
            statusLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 14));
            statusLabel.setTextFill(Color.web("#5E81AC"));
            addSystemMessage(nachricht);
        });
    }
    
    @Override
    public boolean frageNeuesSpiel() {
        addSystemMessage("🔄 Neues Spiel wird gestartet...");
        return true; // Immer ja für bessere UX - User kann jederzeit "Neues Spiel" Button klicken
    }
    
    @Override
    public void schliessen() {
        Platform.runLater(() -> {
            if (primaryStage != null) {
                primaryStage.close();
            }
        });
    }
    
    // ========================================
    // CARD NOTIFICATION SYSTEM
    // ========================================
    
    /**
     * Fügt eine Willkommensnachricht hinzu
     */
    private void addWelcomeMessage() {
        Platform.runLater(() -> {
            int boardSize = controller.getBrett().getGroesse();
            if (enhancedViewRadio.isSelected()) {
                addEnhancedCard(FontAwesome.iconText(FontAwesome.GAMEPAD, "Willkommen bei Gomoku!"), 
                    "Spielfeld: " + boardSize + "×" + boardSize + " Gitter\n" +
                    "Ziel: Bringe 5 Steine in eine Reihe (horizontal, vertikal oder diagonal)\n" +
                    "Klicke auf ein freies Feld um zu spielen",
                    "#4A90E2", null);
            } else {
                addSimpleMessage(FontAwesome.iconText(FontAwesome.GAMEPAD, "Willkommen bei Gomoku (" + boardSize + "×" + boardSize + ")! Ziel: 5 in einer Reihe"));
            }
        });
    }
    
    /**
     * Fügt eine Zug-Nachricht hinzu
     */
    public void addMoveMessage(String spielerName, int x, int y, int zugNummer) {
        // In Message-History speichern
        messageHistory.add(new MessageData("move", spielerName, String.valueOf(x), String.valueOf(y), String.valueOf(zugNummer)));
        
        // Sofort anzeigen
        Platform.runLater(() -> {
            displayMoveMessage(spielerName, x, y, zugNummer);
        });
    }
    
    /**
     * Zeigt eine Zug-Nachricht basierend auf aktueller View-Einstellung an
     */
    private void displayMoveMessage(String spielerName, int x, int y, int zugNummer) {
        String messageId = "move_" + zugNummer;
        String debugInfo = debugInfoMap.get(messageId);
        
        if (enhancedViewRadio.isSelected()) {
            String title = String.format("Zug #%d - %s", zugNummer, spielerName);
            String content = String.format("Position: (%d, %d)", x+1, y+1);
            String color = spielerName.contains("Computer") ? "#7B68EE" : "#32CD32";
            
            addEnhancedCard(title, content, color, debugInfo);
        } else {
            String message = String.format("Zug #%d: %s auf (%d, %d)", 
                zugNummer, spielerName, x+1, y+1);
            if (debugInfo != null && spielerName.contains("Computer")) {
                message += " (" + debugInfo + ")";
            }
            addSimpleMessage(message);
        }
    }
    
    /**
     * Fügt eine Computer-Denk-Nachricht hinzu
     */
    public void addComputerThinkingMessage(String computerName) {
        messageHistory.add(new MessageData("thinking", computerName));
        
        Platform.runLater(() -> {
            displayThinkingMessage(computerName);
        });
    }
    
    /**
     * Zeigt eine Computer-Denk-Nachricht an
     */
    private void displayThinkingMessage(String computerName) {
        if (enhancedViewRadio.isSelected()) {
            addEnhancedCard(FontAwesome.iconText(FontAwesome.ROBOT, computerName + " denkt..."), 
                "Berechne optimalen Zug...", "#FF8C00", null);
        } else {
            addSimpleMessage(FontAwesome.iconText(FontAwesome.ROBOT, computerName + " denkt..."));
        }
    }
    
    /**
     * Fügt eine Computer-Zug-Nachricht mit Debug-Info hinzu
     */
    public void addComputerMoveMessage(String computerName, int x, int y, int zugNummer, 
                                     String debugInfo) {
        // Debug-Info speichern
        if (debugInfo != null) {
            debugInfoMap.put("move_" + zugNummer, debugInfo);
        }
        
        // Normale Zug-Nachricht hinzufügen
        addMoveMessage(computerName, x, y, zugNummer);
    }
    
    /**
     * Fügt eine System-Nachricht hinzu
     */
    public void addSystemMessage(String message) {
        messageHistory.add(new MessageData("system", message));
        
        Platform.runLater(() -> {
            displaySystemMessage(message);
        });
    }
    
    /**
     * Zeigt eine System-Nachricht an
     */
    private void displaySystemMessage(String message) {
        if (enhancedViewRadio.isSelected()) {
            addEnhancedCard(FontAwesome.iconText(FontAwesome.INFO_CIRCLE, "System"), message, "#FFA500", null);
        } else {
            addSimpleMessage(FontAwesome.iconText(FontAwesome.INFO_CIRCLE, message));
        }
    }
    
    /**
     * Fügt eine Enhanced Card hinzu
     */
    private void addEnhancedCard(String title, String content, String borderColor, String debugInfo) {
        VBox card = new VBox(8);
        card.setPadding(new Insets(12));
        card.setStyle(
            "-fx-background-color: rgba(255, 255, 255, 0.95);" +
            "-fx-background-radius: 8px;" +
            "-fx-border-color: " + borderColor + ";" +
            "-fx-border-width: 2px;" +
            "-fx-border-radius: 8px;" +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 4, 0, 0, 2);"
        );
        
        // Titel
        Label titleLabel = new Label(title);
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 13));
        titleLabel.setTextFill(Color.BLACK);
        
        // Inhalt
        Label contentLabel = new Label(content);
        contentLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 12));
        contentLabel.setTextFill(Color.BLACK);
        contentLabel.setWrapText(true);
        
        // Zeitstempel
        Label timestampLabel = new Label(FontAwesome.iconText(FontAwesome.CLOCK, java.time.LocalTime.now().toString().substring(0, 8)));
        timestampLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 10));
        timestampLabel.setTextFill(Color.web("#5E5E5E"));
        
        card.getChildren().addAll(titleLabel, contentLabel, timestampLabel);
        
        // Debug-Info hinzufügen falls vorhanden
        if (debugInfo != null && !debugInfo.trim().isEmpty()) {
            Label debugLabel = new Label(FontAwesome.iconText(FontAwesome.SEARCH, debugInfo));
            debugLabel.setFont(Font.font("Courier New", FontWeight.NORMAL, 10));
            debugLabel.setTextFill(Color.web("#333333"));
            debugLabel.setWrapText(true);
            debugLabel.setStyle("-fx-background-color: rgba(143, 188, 187, 0.2); " +
                               "-fx-padding: 4px; -fx-background-radius: 4px;");
            card.getChildren().add(debugLabel);
        }
        
        nachrichtenContainer.getChildren().add(card);
        
        // Auto-scroll nach unten
        Platform.runLater(() -> {
            nachrichtenScrollPane.setVvalue(1.0);
        });
    }
    
    /**
     * Fügt eine einfache Nachricht hinzu
     */
    private void addSimpleMessage(String message) {
        Label messageLabel = new Label(message);
        messageLabel.setFont(Font.font("Courier New", FontWeight.NORMAL, 11));
        messageLabel.setTextFill(Color.BLACK);
        messageLabel.setWrapText(true);
        messageLabel.setPadding(new Insets(4, 8, 4, 8));
        messageLabel.setStyle(
            "-fx-background-color: rgba(255, 255, 255, 0.8);" +
            "-fx-background-radius: 4px;" +
            "-fx-border-color: rgba(0, 0, 0, 0.1);" +
            "-fx-border-width: 1px;" +
            "-fx-border-radius: 4px;"
        );
        
        nachrichtenContainer.getChildren().add(messageLabel);
        
        // Auto-scroll nach unten
        Platform.runLater(() -> {
            nachrichtenScrollPane.setVvalue(1.0);
        });
    }
    
    /**
     * Aktualisiert die Nachrichten-Anzeige beim Wechsel des View-Modus
     */
    private void refreshMessageDisplay() {
        Platform.runLater(() -> {
            // Nachrichten-Container leeren
            nachrichtenContainer.getChildren().clear();
            
            // Willkommensnachricht wieder hinzufügen
            int boardSize = controller.getBrett().getGroesse();
            if (enhancedViewRadio.isSelected()) {
                addEnhancedCard("🎮 Willkommen bei Gomoku!", 
                    "Spielfeld: " + boardSize + "×" + boardSize + " Gitter\n" +
                    "Ziel: Bringe 5 Steine in eine Reihe (horizontal, vertikal oder diagonal)\n" +
                    "🖱️ Klicke auf ein freies Feld um zu spielen",
                    "#4A90E2", null);
            } else {
                addSimpleMessage("🎮 Willkommen bei Gomoku (" + boardSize + "×" + boardSize + ")! Ziel: 5 in einer Reihe");
            }
            
            // Alle gespeicherten Nachrichten wieder anzeigen
            for (MessageData data : messageHistory) {
                switch (data.type) {
                    case "move":
                        String spielerName = data.args[0];
                        int x = Integer.parseInt(data.args[1]);
                        int y = Integer.parseInt(data.args[2]);
                        int zugNummer = Integer.parseInt(data.args[3]);
                        displayMoveMessage(spielerName, x, y, zugNummer);
                        break;
                    case "thinking":
                        displayThinkingMessage(data.args[0]);
                        break;
                    case "system":
                        displaySystemMessage(data.args[0]);
                        break;
                }
                         }
         });
     }
    
    /**
     * Zeigt den Welcome Screen für eine neue Runde
     */
    private void zeigeWelcomeScreen() {
        controller.beendeSpielVorzeitig();
        primaryStage.close();
        
        // Neue Welcome View erstellen
        Platform.runLater(() -> {
            try {
                Stage newStage = new Stage();
                WelcomeView welcomeView = new WelcomeView(newStage, new WelcomeView.WelcomeCallback() {
                    @Override
                    public void onGameStart(SpielKonfiguration config) {
                        // Neue Konfiguration starten
                        try {
                            SpielController neuerController = new SpielController(config);
                            
                            if (neuerController.getView() instanceof GuiView) {
                                GuiView neueGuiView = (GuiView) neuerController.getView();
                                neueGuiView.setPrimaryStage(newStage);
                            }
                            
                            neuerController.starteSpiel();
                            
                        } catch (Exception e) {
                            System.err.println("Fehler beim Starten der neuen Runde: " + e.getMessage());
                            e.printStackTrace();
                        }
                    }
                    
                    @Override
                    public void onGameLoad() {
                        // Load functionality - not needed for new game welcome screen
                        System.out.println("Load not supported from new game welcome screen");
                    }
                });
                welcomeView.zeigen();
            } catch (Exception e) {
                System.err.println("Fehler beim Anzeigen des Welcome Screens: " + e.getMessage());
                e.printStackTrace();
            }
        });
    }
    
    /**
     * Zeigt einen Dialog zum Beenden des Spiels mit Optionen
     */
    private void zeigeBeendenDialog() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Gomoku beenden");
        alert.setHeaderText("Was möchten Sie tun?");
        alert.setContentText("Wählen Sie eine Option:");
        
        // Icon für den Dialog
        try {
            var iconInputStream = getClass().getResourceAsStream("/icon.png");
            if (iconInputStream != null) {
                Stage alertStage = (Stage) alert.getDialogPane().getScene().getWindow();
                alertStage.getIcons().add(new Image(iconInputStream));
            }
        } catch (Exception e) {
            // Icon loading failed, continue without icon
        }
        
        // Custom buttons
        ButtonType welcomeButton = new ButtonType(FontAwesome.iconText(FontAwesome.HOME, "Zum Willkommensbildschirm"));
        ButtonType exitButton = new ButtonType(FontAwesome.iconText(FontAwesome.TIMES, "Spiel komplett beenden"));
        ButtonType cancelButton = new ButtonType("Abbrechen", ButtonBar.ButtonData.CANCEL_CLOSE);
        
        alert.getButtonTypes().setAll(welcomeButton, exitButton, cancelButton);
        
        // Styling für den Dialog
        alert.getDialogPane().setStyle(
            "-fx-background-color: linear-gradient(to bottom, #ECEFF4, #D8DEE9);" +
            "-fx-font-family: Arial;" +
            "-fx-font-size: 14px;"
        );
        
        // Button-Styling nach Dialog-Erstellung
        Platform.runLater(() -> {
            Button exitBtn = (Button) alert.getDialogPane().lookupButton(exitButton);
            Button welcomeBtn = (Button) alert.getDialogPane().lookupButton(welcomeButton);
            Button cancelBtn = (Button) alert.getDialogPane().lookupButton(cancelButton);
            
            // Roter Exit-Button
            if (exitBtn != null) {
                exitBtn.setStyle(
                    "-fx-background-color: linear-gradient(to bottom, #BF616A, #A54E56);" +
                    "-fx-text-fill: white;" +
                    "-fx-font-weight: bold;" +
                    "-fx-background-radius: 20;" +
                    "-fx-border-radius: 20;" +
                    "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 5, 0, 0, 2);"
                );
                exitBtn.setOnMouseEntered(e -> exitBtn.setStyle(
                    "-fx-background-color: linear-gradient(to bottom, #D08770, #B8705F);" +
                    "-fx-text-fill: white;" +
                    "-fx-font-weight: bold;" +
                    "-fx-background-radius: 20;" +
                    "-fx-border-radius: 20;" +
                    "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.4), 8, 0, 0, 3);" +
                    "-fx-scale-x: 1.05;" +
                    "-fx-scale-y: 1.05;"
                ));
                exitBtn.setOnMouseExited(e -> exitBtn.setStyle(
                    "-fx-background-color: linear-gradient(to bottom, #BF616A, #A54E56);" +
                    "-fx-text-fill: white;" +
                    "-fx-font-weight: bold;" +
                    "-fx-background-radius: 20;" +
                    "-fx-border-radius: 20;" +
                    "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 5, 0, 0, 2);" +
                    "-fx-scale-x: 1.0;" +
                    "-fx-scale-y: 1.0;"
                ));
            }
            
            // Blauer Welcome-Button
            if (welcomeBtn != null) {
                welcomeBtn.setStyle(
                    "-fx-background-color: linear-gradient(to bottom, #5E81AC, #4C6C92);" +
                    "-fx-text-fill: white;" +
                    "-fx-font-weight: bold;" +
                    "-fx-background-radius: 20;" +
                    "-fx-border-radius: 20;" +
                    "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 5, 0, 0, 2);"
                );
            }
            
            // Grauer Cancel-Button
            if (cancelBtn != null) {
                cancelBtn.setStyle(
                    "-fx-background-color: linear-gradient(to bottom, #D8DEE9, #C0C5D0);" +
                    "-fx-text-fill: #2E3440;" +
                    "-fx-font-weight: bold;" +
                    "-fx-background-radius: 20;" +
                    "-fx-border-radius: 20;" +
                    "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 5, 0, 0, 2);"
                );
            }
        });
        
        // Response handling
        Optional<ButtonType> result = alert.showAndWait();
        
        if (result.isPresent()) {
            if (result.get() == welcomeButton) {
                // Zurück zum Willkommensbildschirm
                zeigeWelcomeScreen();
            } else if (result.get() == exitButton) {
                // Spiel komplett beenden
                controller.beendeSpielVorzeitig();
                Platform.exit();
            }
            // Bei cancelButton oder X-Button: Nichts tun, Dialog schließt sich
        }
    }
    
    /**
     * Zeigt einen Dialog zum Speichern des Spiels
     */
    private void zeigeSpeichernDialog() {
        if (!controller.istSaveSystemBereit()) {
            zeigeFehlermeldung("❌ Save-System nicht verfügbar");
            return;
        }
        
        // File Chooser für Save-Location
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("💾 Spiel Speichern");
        fileChooser.setInitialFileName("Mein_Gomoku_Spiel.xml");
        
        // Extension Filter für XML-Dateien
        FileChooser.ExtensionFilter xmlFilter = new FileChooser.ExtensionFilter("XML Dateien (*.xml)", "*.xml");
        fileChooser.getExtensionFilters().add(xmlFilter);
        
        // Standard-Verzeichnis auf gomoku-db/saves setzen
        try {
            java.io.File savesDir = new java.io.File("gomoku-db/saves");
            if (savesDir.exists() && savesDir.isDirectory()) {
                fileChooser.setInitialDirectory(savesDir);
            }
        } catch (Exception e) {
            // Fallback: Aktuelles Verzeichnis verwenden
        }
        
        // File Chooser anzeigen
        java.io.File selectedFile = fileChooser.showSaveDialog(primaryStage);
        
        if (selectedFile != null) {
            String fileName = selectedFile.getName();
            String saveName = fileName.endsWith(".xml") ? fileName.substring(0, fileName.length() - 4) : fileName;
            
            // Speichern mit custom path
            boolean erfolg = controller.speichereSpielMitPfad(saveName, selectedFile.getAbsolutePath());
            if (erfolg) {
                addSystemMessage("💾 Spiel erfolgreich gespeichert: " + selectedFile.getAbsolutePath());
            } else {
                zeigeFehlermeldung("❌ Fehler beim Speichern des Spiels");
            }
        }
    }
    
    /**
     * Zeigt einen Dialog zum Laden eines Spiels
     */
    public void zeigeLadenDialog() {
        if (!controller.istSaveSystemBereit()) {
            zeigeFehlermeldung("❌ Save-System nicht verfügbar");
            return;
        }
        
        // Verfügbare Saves laden
        java.util.List<GameSave> saves = controller.getVerfuegbareSaves();
        
        if (saves.isEmpty()) {
            zeigeNachricht("ℹ️ Keine gespeicherten Spiele gefunden");
            return;
        }
        
        // Choice Dialog erstellen
        ChoiceDialog<GameSave> dialog = new ChoiceDialog<>(saves.get(0), saves);
        dialog.setTitle("📂 Spiel Laden");
        dialog.setHeaderText("Wählen Sie einen Spielstand zum Laden:");
        dialog.setContentText("Spielstand:");
        
        // Dialog stylen
        dialog.getDialogPane().setStyle(
            "-fx-background-color: linear-gradient(to bottom, #ECEFF4, #D8DEE9);" +
            "-fx-border-color: #81A1C1;" +
            "-fx-border-width: 2px;" +
            "-fx-border-radius: 10px;" +
            "-fx-background-radius: 10px;"
        );
        
        // Custom string converter für bessere Anzeige
        ComboBox<GameSave> comboBox = (ComboBox<GameSave>) dialog.getDialogPane().lookup(".combo-box");
        if (comboBox != null) {
            comboBox.setConverter(new StringConverter<GameSave>() {
                @Override
                public String toString(GameSave save) {
                    if (save == null) return "";
                    return String.format("%s (%s - %d Züge)", 
                                       save.getSaveName(), 
                                       save.getTimestamp().substring(0, 16).replace("T", " "), 
                                       save.getTotalMoves());
                }
                
                @Override
                public GameSave fromString(String string) {
                    return null; // Nicht benötigt
                }
            });
        }
        
        // Icon setzen
        try {
            var iconInputStream = getClass().getResourceAsStream("/icon.png");
            if (iconInputStream != null) {
                Stage dialogStage = (Stage) dialog.getDialogPane().getScene().getWindow();
                dialogStage.getIcons().add(new Image(iconInputStream));
            }
        } catch (Exception e) {
            // Icon laden fehlgeschlagen, ignorieren
        }
        
        Optional<GameSave> result = dialog.showAndWait();
        result.ifPresent(selectedSave -> {
            boolean erfolg = controller.ladeSpiel(selectedSave.getSaveId());
            if (erfolg) {
                // Message-History löschen und neu aufbauen
                messageHistory.clear();
                debugInfoMap.clear();
                nachrichtenContainer.getChildren().clear();
                
                addWelcomeMessage();
                addSystemMessage("📂 Spielstand geladen: " + selectedSave.getSaveName());
                
                // GUI aktualisieren
                aktualisiereAnzeige();
            } else {
                zeigeFehlermeldung("❌ Fehler beim Laden des Spielstands");
            }
        });
    }
} 