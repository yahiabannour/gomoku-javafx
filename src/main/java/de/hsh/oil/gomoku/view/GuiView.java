package de.hsh.oil.gomoku.view;

import de.hsh.oil.gomoku.controller.SpielController;
import de.hsh.oil.gomoku.model.Spielbrett;
import de.hsh.oil.gomoku.model.Spieler;
import de.hsh.oil.gomoku.model.Zug;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
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
        
        // Moderner Gradient-Hintergrund
        LinearGradient gradient = new LinearGradient(0, 0, 0, 1, true, null,
            new Stop(0, Color.web("#2E3440")),
            new Stop(0.5, Color.web("#3B4252")),
            new Stop(1, Color.web("#434C5E"))
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
        spielerInfoLabel.setTextFill(Color.web("#2E3440"));
        spielerInfoLabel.setWrapText(true);
        spielerInfoLabel.setAlignment(Pos.CENTER);
        
        statusLabel = new Label("Bereit zum Spielen");
        statusLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 14));
        statusLabel.setTextFill(Color.web("#5E81AC"));
        statusLabel.setWrapText(true);
        statusLabel.setAlignment(Pos.CENTER);
        
        // Separator mit verbessertem Design
        Separator separator = new Separator();
        separator.setStyle("-fx-background-color: #81A1C1;");
        
        // View-Optionen
        Label viewLabel = new Label("👁️ Ansichtsmodus");
        viewLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        viewLabel.setTextFill(Color.web("#2E3440"));
        
        viewModeGroup = new ToggleGroup();
        
        HBox viewOptionsBox = new HBox(15);
        viewOptionsBox.setAlignment(Pos.CENTER_LEFT);
        
        simpleViewRadio = new RadioButton("📄 Einfach");
        simpleViewRadio.setFont(Font.font("Arial", FontWeight.NORMAL, 12));
        simpleViewRadio.setTextFill(Color.web("#3B4252"));
        simpleViewRadio.setToggleGroup(viewModeGroup);
        
        enhancedViewRadio = new RadioButton("💫 Erweitert");
        enhancedViewRadio.setFont(Font.font("Arial", FontWeight.NORMAL, 12));
        enhancedViewRadio.setTextFill(Color.web("#3B4252"));
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
        
        Label spielfeldLabel = new Label("🎯 Gomoku Spielfeld (15×15)");
        spielfeldLabel.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        spielfeldLabel.setTextFill(Color.web("#2E3440"));
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
        HBox buttonBereich = new HBox(20);
        buttonBereich.setAlignment(Pos.CENTER);
        buttonBereich.setPadding(new Insets(20, 0, 0, 0));
        
        neuesSpielButton = new Button("🔄 Neues Spiel");
        neuesSpielButton.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        neuesSpielButton.setPrefSize(150, 45);
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
        
        beendenButton = new Button("❌ Beenden");
        beendenButton.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        beendenButton.setPrefSize(150, 45);
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
        beendenButton.setOnAction(e -> {
            controller.beendeSpielVorzeitig();
            Platform.exit();
        });
        
        buttonBereich.getChildren().addAll(neuesSpielButton, beendenButton);
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
            controller.beendeSpielVorzeitig();
            Platform.exit();
        });
        primaryStage.show();
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
            if (enhancedViewRadio.isSelected()) {
                addEnhancedCard("🎮 Willkommen bei Gomoku!", 
                    "Ziel: Bringe 5 Steine in eine Reihe (horizontal, vertikal oder diagonal)\n" +
                    "🖱️ Klicke auf ein freies Feld um zu spielen",
                    "#4A90E2", null);
            } else {
                addSimpleMessage("🎮 Willkommen bei Gomoku! Ziel: 5 in einer Reihe");
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
            addEnhancedCard("🤖 " + computerName + " denkt...", 
                "Berechne optimalen Zug...", "#FF8C00", null);
        } else {
            addSimpleMessage("🤖 " + computerName + " denkt...");
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
            addEnhancedCard("ℹ️ System", message, "#FFA500", null);
        } else {
            addSimpleMessage("ℹ️ " + message);
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
        titleLabel.setTextFill(Color.web("#2E3440"));
        
        // Inhalt
        Label contentLabel = new Label(content);
        contentLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 12));
        contentLabel.setTextFill(Color.web("#4C566A"));
        contentLabel.setWrapText(true);
        
        // Zeitstempel
        Label timestampLabel = new Label("⏰ " + java.time.LocalTime.now().toString().substring(0, 8));
        timestampLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 10));
        timestampLabel.setTextFill(Color.web("#81A1C1"));
        
        card.getChildren().addAll(titleLabel, contentLabel, timestampLabel);
        
        // Debug-Info hinzufügen falls vorhanden
        if (debugInfo != null && !debugInfo.trim().isEmpty()) {
            Label debugLabel = new Label("🔍 " + debugInfo);
            debugLabel.setFont(Font.font("Courier New", FontWeight.NORMAL, 10));
            debugLabel.setTextFill(Color.web("#8FBCBB"));
            debugLabel.setWrapText(true);
            debugLabel.setStyle("-fx-background-color: rgba(143, 188, 187, 0.1); " +
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
        messageLabel.setTextFill(Color.web("#2E3440"));
        messageLabel.setWrapText(true);
        messageLabel.setPadding(new Insets(4, 8, 4, 8));
        messageLabel.setStyle(
            "-fx-background-color: rgba(236, 239, 244, 0.7);" +
            "-fx-background-radius: 4px;"
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
            if (enhancedViewRadio.isSelected()) {
                addEnhancedCard("🎮 Willkommen bei Gomoku!", 
                    "Ziel: Bringe 5 Steine in eine Reihe (horizontal, vertikal oder diagonal)\n" +
                    "🖱️ Klicke auf ein freies Feld um zu spielen",
                    "#4A90E2", null);
            } else {
                addSimpleMessage("🎮 Willkommen bei Gomoku! Ziel: 5 in einer Reihe");
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
} 