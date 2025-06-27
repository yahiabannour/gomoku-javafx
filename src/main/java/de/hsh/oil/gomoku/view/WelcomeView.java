package de.hsh.oil.gomoku.view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import de.hsh.oil.gomoku.model.*;

/**
 * Willkommens-Ansicht für die Spielkonfiguration
 * 
 * Bietet eine benutzerfreundliche Oberfläche zur Auswahl von:
 * - Spielmodus (PvP, PvC, CvC)
 * - KI-Strategien (S1, S2, S3)
 * - Debug-Modus
 * 
 * @author OIL SS2025
 */
public class WelcomeView {
    
    private Stage stage;
    private SpielKonfiguration konfiguration;
    private WelcomeCallback callback;
    
    // UI-Komponenten
    private ToggleGroup spielModusGroup;
    private ComboBox<KiStrategie> strategie1ComboBox;
    private ComboBox<KiStrategie> strategie2ComboBox;
    private CheckBox debugCheckBox;
    private CheckBox debugDateiCheckBox;
    private Button startButton;
    private Label strategieLabel1;
    private Label strategieLabel2;
    
    /**
     * Callback-Interface für Spielstart
     */
    public interface WelcomeCallback {
        void onGameStart(SpielKonfiguration konfiguration);
    }
    
    /**
     * Konstruktor
     * 
     * @param stage Hauptfenster
     * @param callback Callback für Spielstart
     */
    public WelcomeView(Stage stage, WelcomeCallback callback) {
        this.stage = stage;
        this.callback = callback;
        this.konfiguration = new SpielKonfiguration();
        
        erstelleWillkommensansicht();
    }
    
    /**
     * Erstellt die Willkommens-Benutzeroberfläche
     */
    private void erstelleWillkommensansicht() {
        // Hauptlayout
        VBox hauptLayout = new VBox(20);
        hauptLayout.setAlignment(Pos.CENTER);
        hauptLayout.setPadding(new Insets(40));
        
        // Gradient-Hintergrund
        LinearGradient gradient = new LinearGradient(0, 0, 0, 1, true, null,
            new Stop(0, Color.web("#2E3440")),
            new Stop(0.5, Color.web("#3B4252")),
            new Stop(1, Color.web("#434C5E"))
        );
        hauptLayout.setBackground(new Background(new BackgroundFill(gradient, null, null)));
        
        // Titel
        Label titelLabel = new Label("🎯 GOMOKU");
        titelLabel.setFont(Font.font("Arial", FontWeight.BOLD, 48));
        titelLabel.setTextFill(Color.web("#ECEFF4"));
        titelLabel.setEffect(new DropShadow(10, Color.BLACK));
        
        Label untertitelLabel = new Label("Strategisches Brettspiel • 5 in einer Reihe gewinnen");
        untertitelLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 16));
        untertitelLabel.setTextFill(Color.web("#D8DEE9"));
        
        // Hauptkarten-Container
        HBox kartenContainer = new HBox(30);
        kartenContainer.setAlignment(Pos.CENTER);
        
        // Spielmodus-Karte
        VBox spielModusKarte = erstelleSpielModusKarte();
        
        // Einstellungen-Karte
        VBox einstellungenKarte = erstelleEinstellungenKarte();
        
        kartenContainer.getChildren().addAll(spielModusKarte, einstellungenKarte);
        
        // Start-Button
        startButton = new Button("🚀 SPIEL STARTEN");
        startButton.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        startButton.setPrefSize(300, 60);
        startButton.setStyle(
            "-fx-background-color: linear-gradient(to bottom, #5E81AC, #4C6C92);" +
            "-fx-text-fill: white;" +
            "-fx-background-radius: 30;" +
            "-fx-border-radius: 30;" +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 10, 0, 0, 5);"
        );
        
        startButton.setOnMouseEntered(e -> startButton.setStyle(
            "-fx-background-color: linear-gradient(to bottom, #6B8EB8, #5877A0);" +
            "-fx-text-fill: white;" +
            "-fx-background-radius: 30;" +
            "-fx-border-radius: 30;" +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.4), 15, 0, 0, 7);" +
            "-fx-scale-x: 1.05;" +
            "-fx-scale-y: 1.05;"
        ));
        
        startButton.setOnMouseExited(e -> startButton.setStyle(
            "-fx-background-color: linear-gradient(to bottom, #5E81AC, #4C6C92);" +
            "-fx-text-fill: white;" +
            "-fx-background-radius: 30;" +
            "-fx-border-radius: 30;" +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 10, 0, 0, 5);" +
            "-fx-scale-x: 1.0;" +
            "-fx-scale-y: 1.0;"
        ));
        
        startButton.setOnAction(e -> starteSpiel());
        
        // Footer
        Label footerLabel = new Label("OIL SS2025 • Hochschule Hannover • Version 1.0");
        footerLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 12));
        footerLabel.setTextFill(Color.web("#81A1C1"));
        
        hauptLayout.getChildren().addAll(
            titelLabel, untertitelLabel, 
            new Region(), // Spacer
            kartenContainer,
            new Region(), // Spacer
            startButton,
            new Region(), // Spacer
            footerLabel
        );
        
        // Spacer konfigurieren
        hauptLayout.getChildren().stream()
            .filter(node -> node instanceof Region)
            .forEach(node -> VBox.setVgrow(node, Priority.ALWAYS));
        
        // Scene erstellen
        Scene scene = new Scene(hauptLayout, 1000, 700);
        stage.setTitle("Gomoku - Willkommen");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.centerOnScreen();
        
        // Icon aus Resources laden
        try {
            var iconInputStream = getClass().getResourceAsStream("/icon.png");
            if (iconInputStream != null) {
                stage.getIcons().add(new Image(iconInputStream));
            }
        } catch (Exception e) {
            System.err.println("🚨 Icon konnte nicht geladen werden: " + e.getMessage());
        }
        
        // Initiale Konfiguration
        aktualisiereKonfiguration();
    }
    
    /**
     * Erstellt die Spielmodus-Karte
     */
    private VBox erstelleSpielModusKarte() {
        VBox karte = new VBox(15);
        karte.setAlignment(Pos.TOP_CENTER);
        karte.setPadding(new Insets(25));
        karte.setPrefSize(400, 350);
        karte.setStyle(
            "-fx-background-color: rgba(236, 239, 244, 0.95);" +
            "-fx-background-radius: 20;" +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 20, 0, 0, 10);"
        );
        
        // Titel
        Label titel = new Label("🎮 SPIELMODUS");
        titel.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        titel.setTextFill(Color.web("#2E3440"));
        
        // Spielmodus-Optionen
        spielModusGroup = new ToggleGroup();
        
        RadioButton pvpButton = erstelleRadioButton("👥 Spieler vs Spieler", 
            "Zwei menschliche Spieler gegeneinander", SpielModus.PLAYER_VS_PLAYER);
        RadioButton pvcButton = erstelleRadioButton("🤖 Spieler vs Computer", 
            "Mensch gegen künstliche Intelligenz", SpielModus.PLAYER_VS_COMPUTER);
        RadioButton cvcButton = erstelleRadioButton("🤖🆚🤖 Computer vs Computer", 
            "Zwei KI-Strategien gegeneinander", SpielModus.COMPUTER_VS_COMPUTER);
        
        spielModusGroup.getToggles().addAll(pvpButton, pvcButton, cvcButton);
        
        // Standard-Auswahl
        pvcButton.setSelected(true);
        
        // Listener für Spielmodus-Änderungen
        spielModusGroup.selectedToggleProperty().addListener((obs, oldVal, newVal) -> {
            aktualisiereKonfiguration();
        });
        
        karte.getChildren().addAll(titel, pvpButton, pvcButton, cvcButton);
        
        return karte;
    }
    
    /**
     * Erstellt die Einstellungen-Karte
     */
    private VBox erstelleEinstellungenKarte() {
        VBox karte = new VBox(15);
        karte.setAlignment(Pos.TOP_CENTER);
        karte.setPadding(new Insets(25));
        karte.setPrefSize(400, 350);
        karte.setStyle(
            "-fx-background-color: rgba(236, 239, 244, 0.95);" +
            "-fx-background-radius: 20;" +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 20, 0, 0, 10);"
        );
        
        // Titel
        Label titel = new Label("⚙️ EINSTELLUNGEN");
        titel.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        titel.setTextFill(Color.web("#2E3440"));
        
        // KI-Strategien
        strategieLabel1 = new Label("🧠 KI-Strategie:");
        strategieLabel1.setFont(Font.font("Arial", FontWeight.NORMAL, 14));
        strategieLabel1.setTextFill(Color.web("#3B4252"));
        
        strategie1ComboBox = new ComboBox<>();
        strategie1ComboBox.setPrefWidth(300);
        strategie1ComboBox.setStyle("-fx-font-size: 12px;");
        strategie1ComboBox.getItems().addAll(KiStrategie.values());
        strategie1ComboBox.setValue(KiStrategie.MINIMAX);
        strategie1ComboBox.setOnAction(e -> aktualisiereKonfiguration());
        
        strategieLabel2 = new Label("🧠 KI-Strategie Spieler 2:");
        strategieLabel2.setFont(Font.font("Arial", FontWeight.NORMAL, 14));
        strategieLabel2.setTextFill(Color.web("#3B4252"));
        strategieLabel2.setVisible(false);
        
        strategie2ComboBox = new ComboBox<>();
        strategie2ComboBox.setPrefWidth(300);
        strategie2ComboBox.setStyle("-fx-font-size: 12px;");
        strategie2ComboBox.getItems().addAll(KiStrategie.values());
        strategie2ComboBox.setValue(KiStrategie.EINFACHE_LOGIK);
        strategie2ComboBox.setOnAction(e -> aktualisiereKonfiguration());
        strategie2ComboBox.setVisible(false);
        
        // Debug-Optionen
        debugCheckBox = new CheckBox("📊 Debug-Modus (Konsole)");
        debugCheckBox.setFont(Font.font("Arial", FontWeight.NORMAL, 14));
        debugCheckBox.setTextFill(Color.web("#3B4252"));
        debugCheckBox.setOnAction(e -> aktualisiereKonfiguration());
        
        debugDateiCheckBox = new CheckBox("📄 Debug-Ausgabe in Datei");
        debugDateiCheckBox.setFont(Font.font("Arial", FontWeight.NORMAL, 14));
        debugDateiCheckBox.setTextFill(Color.web("#3B4252"));
        debugDateiCheckBox.setOnAction(e -> aktualisiereKonfiguration());
        
        karte.getChildren().addAll(
            titel, 
            strategieLabel1, strategie1ComboBox,
            strategieLabel2, strategie2ComboBox,
            debugCheckBox, debugDateiCheckBox
        );
        
        return karte;
    }
    
    /**
     * Erstellt einen styled RadioButton
     */
    private RadioButton erstelleRadioButton(String text, String beschreibung, SpielModus modus) {
        VBox container = new VBox(5);
        container.setAlignment(Pos.CENTER_LEFT);
        
        RadioButton radioButton = new RadioButton();
        radioButton.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        radioButton.setTextFill(Color.BLACK);
        radioButton.setUserData(modus);
        
        Label hauptText = new Label(text);
        hauptText.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        hauptText.setTextFill(Color.BLACK);
        
        Label beschreibungText = new Label(beschreibung);
        beschreibungText.setFont(Font.font("Arial", FontWeight.NORMAL, 12));
        beschreibungText.setTextFill(Color.BLACK);
        
        HBox hbox = new HBox(10);
        hbox.setAlignment(Pos.CENTER_LEFT);
        hbox.getChildren().addAll(radioButton, hauptText);
        
        container.getChildren().addAll(hbox, beschreibungText);
        
        // Click-Handler für den ganzen Container
        container.setOnMouseClicked(e -> radioButton.setSelected(true));
        
        return radioButton;
    }
    /**
     * Aktualisiert die Spielkonfiguration basierend auf UI-Eingaben
     */
    private void aktualisiereKonfiguration() {
        if (spielModusGroup.getSelectedToggle() != null) {
            SpielModus modus = (SpielModus) spielModusGroup.getSelectedToggle().getUserData();
            konfiguration.setSpielModus(modus);
            
            // Strategien-UI basierend auf Spielmodus anpassen
            boolean showStrategie1 = (modus == SpielModus.PLAYER_VS_COMPUTER || modus == SpielModus.COMPUTER_VS_COMPUTER);
            boolean showStrategie2 = (modus == SpielModus.COMPUTER_VS_COMPUTER);
            
            strategieLabel1.setVisible(showStrategie1);
            strategie1ComboBox.setVisible(showStrategie1);
            strategieLabel2.setVisible(showStrategie2);
            strategie2ComboBox.setVisible(showStrategie2);
            
            // Strategien setzen
            if (showStrategie1) {
                konfiguration.setStrategie1(strategie1ComboBox.getValue());
            }
            if (showStrategie2) {
                konfiguration.setStrategie2(strategie2ComboBox.getValue());
            }
        }
        
        // Debug-Modus
        if (debugCheckBox.isSelected() && debugDateiCheckBox.isSelected()) {
            konfiguration.setDebugModus(DebugModus.DATEI);
        } else if (debugCheckBox.isSelected()) {
            konfiguration.setDebugModus(DebugModus.KONSOLE);
        } else {
            konfiguration.setDebugModus(DebugModus.KEIN);
        }
        
        // GUI-Modus aktivieren
        konfiguration.setGuiModus(true);
    }
    
    /**
     * Startet das Spiel mit der konfigurierten Einstellung
     */
    private void starteSpiel() {
        aktualisiereKonfiguration();
        
        if (callback != null) {
            callback.onGameStart(konfiguration);
        }
    }
    
    /**
     * Zeigt die Willkommens-Ansicht
     */
    public void zeigen() {
        stage.show();
    }
} 