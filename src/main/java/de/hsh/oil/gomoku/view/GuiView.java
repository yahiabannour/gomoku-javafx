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
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
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
    private TextArea nachrichtenArea;
    private Button neuesSpielButton;
    private Button beendenButton;
    private Button[][] feldButtons;
    
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
        primaryStage.setTitle("Gomoku - Strategisches Brettspiel");
        
        // Hauptlayout
        BorderPane hauptLayout = new BorderPane();
        hauptLayout.setPadding(new Insets(10));
        
        // Linker Bereich - Spieler-Info und Nachrichten
        VBox linkerBereich = new VBox(10);
        linkerBereich.setPadding(new Insets(0, 10, 0, 0));
        linkerBereich.setPrefWidth(300);
        linkerBereich.setMinWidth(250);
        
        spielerInfoLabel = new Label();
        spielerInfoLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-alignment: center;");
        spielerInfoLabel.setWrapText(true);
        
        statusLabel = new Label("Bereit zum Spielen");
        statusLabel.setStyle("-fx-font-size: 12px; -fx-text-alignment: center; -fx-text-fill: #2E8B57;");
        statusLabel.setWrapText(true);
        
        // Separator
        Separator separator = new Separator();
        
        // Nachrichten-Bereich - resizable
        Label nachrichtenLabel = new Label("📝 Spielverlauf:");
        nachrichtenLabel.setStyle("-fx-font-size: 12px; -fx-font-weight: bold;");
        
        nachrichtenArea = new TextArea();
        nachrichtenArea.setEditable(false);
        nachrichtenArea.setWrapText(true);
        nachrichtenArea.setStyle("-fx-font-family: 'Courier New', monospace; " +
                                "-fx-font-size: 11px; " +
                                "-fx-background-color: #f8f8f8; " +
                                "-fx-border-color: #ccc; " +
                                "-fx-border-radius: 5px; " +
                                "-fx-background-radius: 5px;");
        
        // Willkommensnachricht zeigen
        nachrichtenArea.setText("🎮 Willkommen bei Gomoku!\n" +
                               "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n" +
                               "📖 Ziel: Bringe 5 Steine in eine Reihe\n" +
                               "   (horizontal, vertikal oder diagonal)\n" +
                               "🖱️  Klicke auf ein freies Feld um zu spielen\n" +
                               "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n");
        
        // VBox.setVgrow für resizable TextArea
        VBox.setVgrow(nachrichtenArea, Priority.ALWAYS);
        
        linkerBereich.getChildren().addAll(spielerInfoLabel, statusLabel, separator, nachrichtenLabel, nachrichtenArea);
        hauptLayout.setLeft(linkerBereich);
        
        // Mittlerer Bereich - Spielfeld
        VBox spielfeldBereich = new VBox(10);
        spielfeldBereich.setAlignment(Pos.CENTER);
        spielfeldBereich.setPadding(new Insets(10));
        
        Label spielfeldLabel = new Label("🎯 Gomoku Spielfeld (15x15)");
        spielfeldLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");
        
        spielfeldGrid = new GridPane();
        spielfeldGrid.setAlignment(Pos.CENTER);
        spielfeldGrid.setHgap(1);
        spielfeldGrid.setVgap(1);
        spielfeldGrid.setStyle("-fx-background-color: #8B4513; -fx-padding: 5px;");
        
        erstelleSpielfeld();
        
        spielfeldBereich.getChildren().addAll(spielfeldLabel, spielfeldGrid);
        hauptLayout.setCenter(spielfeldBereich);
        
        // Unterer Bereich - Buttons
        HBox buttonBereich = new HBox(10);
        buttonBereich.setAlignment(Pos.CENTER);
        buttonBereich.setPadding(new Insets(10, 0, 0, 0));
        
        neuesSpielButton = new Button("Neues Spiel");
        neuesSpielButton.setOnAction(e -> controller.starteNeuesSpiel());
        
        beendenButton = new Button("Beenden");
        beendenButton.setOnAction(e -> {
            controller.beendeSpielVorzeitig();
            Platform.exit();
        });
        
        buttonBereich.getChildren().addAll(neuesSpielButton, beendenButton);
        hauptLayout.setBottom(buttonBereich);
        
        // Scene erstellen und anzeigen
        Scene scene = new Scene(hauptLayout, 1200, 800);
        primaryStage.setScene(scene);
        primaryStage.setMinWidth(1000);
        primaryStage.setMinHeight(600);
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
                feldButton.setPrefSize(40, 40);
                feldButton.setStyle("-fx-background-color: #DEB887; -fx-border-color: #8B4513;");
                
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
                            nachrichtenArea.appendText(String.format("👤 Zug %02d: %s → (%d,%d)\n", 
                                                     kommenderZugNummer, spielerName, finalX, finalY));
                            nachrichtenArea.setScrollTop(Double.MAX_VALUE);
                            aktualisiereAnzeige();
                        } else {
                            statusLabel.setText("Ungültiger Zug! Feld bereits belegt oder Spiel beendet.");
                            statusLabel.setStyle("-fx-font-size: 12px; -fx-text-alignment: center; -fx-text-fill: #DC143C;");
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
            nachrichtenArea.appendText("🤖 " + computer.getName() + " überlegt...\n");
            nachrichtenArea.setScrollTop(Double.MAX_VALUE);
        });
    }
    
    @Override
    public void zeigeComputerZug(Spieler computer, Zug zug) {
        Platform.runLater(() -> {
            statusLabel.setText(computer.getName() + " hat gezogen auf (" + 
                              zug.getX() + "," + zug.getY() + ")");
            String zugNummer = String.format("%02d", zug.getZugNummer());
            nachrichtenArea.appendText(String.format("🤖 Zug %s: %s → (%d,%d)\n", 
                                     zugNummer, computer.getName(), zug.getX(), zug.getY()));
            nachrichtenArea.setScrollTop(Double.MAX_VALUE);
        });
    }
    
    @Override
    public void zeigeSpielErgebnis(Spieler gewinner, int anzahlZuege) {
        Platform.runLater(() -> {
            String ergebnis;
            if (gewinner != null) {
                ergebnis = "🎉 SPIEL BEENDET 🎉\n" + 
                          gewinner.getName() + " hat gewonnen!\n" +
                          "Gewonnen nach " + anzahlZuege + " Zügen\n" +
                          "Herzlichen Glückwunsch! 🏆\n";
            } else {
                ergebnis = "🤝 SPIEL BEENDET 🤝\n" +
                          "Unentschieden!\n" +
                          "Spiel nach " + anzahlZuege + " Zügen beendet\n" +
                          "Gut gespielt von beiden Seiten!\n";
            }
            
            nachrichtenArea.appendText("\n" + ergebnis);
            nachrichtenArea.setScrollTop(Double.MAX_VALUE);
        });
    }
    
    @Override
    public void zeigeStatistiken(Spieler spieler1, Spieler spieler2) {
        Platform.runLater(() -> {
            String statistiken = "\n📊 SPIELER-STATISTIKEN 📊\n" +
                                spieler1.getStatistikString() + "\n" +
                                spieler2.getStatistikString() + "\n";
            
            nachrichtenArea.appendText(statistiken);
            nachrichtenArea.setScrollTop(Double.MAX_VALUE);
        });
    }
    
    @Override
    public void zeigeFehlermeldung(String nachricht) {
        Platform.runLater(() -> {
            statusLabel.setText("❌ " + nachricht);
            statusLabel.setStyle("-fx-font-size: 12px; -fx-text-alignment: center; -fx-text-fill: #DC143C;");
            nachrichtenArea.appendText("❌ FEHLER: " + nachricht + "\n");
            nachrichtenArea.setScrollTop(Double.MAX_VALUE);
        });
    }
    
    @Override
    public void zeigeNachricht(String nachricht) {
        Platform.runLater(() -> {
            statusLabel.setText("ℹ️ " + nachricht);
            statusLabel.setStyle("-fx-font-size: 12px; -fx-text-alignment: center; -fx-text-fill: #2E8B57;");
            nachrichtenArea.appendText("ℹ️ INFO: " + nachricht + "\n");
            nachrichtenArea.setScrollTop(Double.MAX_VALUE);
        });
    }
    
    @Override
    public boolean frageNeuesSpiel() {
        Platform.runLater(() -> {
            nachrichtenArea.appendText("\n🔄 Neues Spiel wird gestartet...\n");
            nachrichtenArea.setScrollTop(Double.MAX_VALUE);
        });
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
} 