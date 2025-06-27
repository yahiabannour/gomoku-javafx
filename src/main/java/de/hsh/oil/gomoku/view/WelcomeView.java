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
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import de.hsh.oil.gomoku.model.*;
import de.hsh.oil.gomoku.util.FontAwesome;

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
    
    // Erweiterte Einstellungen
    private TextField debugDateiPfadField;
    private Button debugDateiAuswahlButton;
    private Spinner<Integer> bretGroesseSpinner;
    private Spinner<Integer> minimaxTiefeSpinner;
    private HBox minimaxTiefeButtons;
    private ToggleGroup minimaxTiefeGroup;
    private Label minimaxTiefeLabel;
    private CheckBox alphaBetaCheckBox;
    private Label strategieInfoLabel;
    
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
            new Stop(0, Color.web("#ECEFF4")),
            new Stop(0.5, Color.web("#D8DEE9")),
            new Stop(1, Color.web("#C0C5D0"))
        );
        hauptLayout.setBackground(new Background(new BackgroundFill(gradient, null, null)));
        
        // Titel
        Label titelLabel = new Label(FontAwesome.iconText(FontAwesome.TARGET, "GOMOKU"));
        titelLabel.setFont(Font.font("Arial", FontWeight.BOLD, 48));
        titelLabel.setTextFill(Color.web("#2E3440")); // Dunklere Farbe für bessere Lesbarkeit
        titelLabel.setEffect(new DropShadow(10, Color.WHITE));
        
        Label untertitelLabel = new Label("Strategisches Brettspiel • 5 in einer Reihe gewinnen");
        untertitelLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 16));
        untertitelLabel.setTextFill(Color.web("#D8DEE9"));
        
        // Hauptinhalt - Alle Einstellungen in einer großen Ansicht
        HBox inhaltsContainer = new HBox(30);
        inhaltsContainer.setAlignment(Pos.CENTER);
        inhaltsContainer.setPadding(new Insets(20));
        
        // Linke Spalte - Spielmodus
        VBox linkeSpalte = erstelleSpielModusKarte();
        
        // Mittlere Spalte - KI-Einstellungen  
        VBox mittlereSpalte = erstelleKiEinstellungenKarte();
        
        // Rechte Spalte - Erweiterte Einstellungen
        VBox rechteSpalte = erstelleErweiterteEinstellungenKarte();
        
        inhaltsContainer.getChildren().addAll(linkeSpalte, mittlereSpalte, rechteSpalte);
        
        // Start-Button
        startButton = new Button(FontAwesome.iconText(FontAwesome.ROCKET, "SPIEL STARTEN"));
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
            inhaltsContainer,
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
        Scene scene = new Scene(hauptLayout, 1200, 900);
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
        aktualisiereSichtbarkeit();
    }
    
    /**
     * Aktualisiert die Sichtbarkeit der UI-Elemente basierend auf dem gewählten Spielmodus und den KI-Strategien
     */
    private void aktualisiereSichtbarkeit() {
        if (spielModusGroup.getSelectedToggle() == null) return;
        
        RadioButton selectedButton = (RadioButton) spielModusGroup.getSelectedToggle();
        SpielModus modus = (SpielModus) selectedButton.getUserData();
        
        // KI-Einstellungen nur bei Computer-Modi anzeigen
        boolean zeigeKiEinstellungen = modus != SpielModus.PLAYER_VS_PLAYER;
        boolean zeigeZweiteSpielerKi = modus == SpielModus.COMPUTER_VS_COMPUTER;
        
        // KI-Strategien-Karte sichtbar/unsichtbar machen
        if (strategieLabel1 != null) {
            strategieLabel1.setVisible(zeigeKiEinstellungen);
            strategieLabel1.setManaged(zeigeKiEinstellungen);
        }
        if (strategie1ComboBox != null) {
            strategie1ComboBox.setVisible(zeigeKiEinstellungen);
            strategie1ComboBox.setManaged(zeigeKiEinstellungen);
        }
        if (strategieLabel2 != null) {
            strategieLabel2.setVisible(zeigeZweiteSpielerKi);
            strategieLabel2.setManaged(zeigeZweiteSpielerKi);
        }
        if (strategie2ComboBox != null) {
            strategie2ComboBox.setVisible(zeigeZweiteSpielerKi);
            strategie2ComboBox.setManaged(zeigeZweiteSpielerKi);
        }
        
        // Minimax-spezifische Einstellungen nur bei Minimax-Strategie anzeigen
        boolean zeigeMinimaxEinstellungen = zeigeKiEinstellungen && istMinimaxStrategieAktiv();
        
        if (minimaxTiefeLabel != null) {
            minimaxTiefeLabel.setVisible(zeigeMinimaxEinstellungen);
            minimaxTiefeLabel.setManaged(zeigeMinimaxEinstellungen);
        }
        if (minimaxTiefeButtons != null) {
            minimaxTiefeButtons.setVisible(zeigeMinimaxEinstellungen);
            minimaxTiefeButtons.setManaged(zeigeMinimaxEinstellungen);
        }
        if (alphaBetaCheckBox != null) {
            alphaBetaCheckBox.setVisible(zeigeMinimaxEinstellungen);
            alphaBetaCheckBox.setManaged(zeigeMinimaxEinstellungen);
        }
        
        // Info-Label zeigen wenn KI-Einstellungen sichtbar sind, aber keine Minimax-Strategie aktiv ist
        if (strategieInfoLabel != null) {
            boolean zeigeInfo = zeigeKiEinstellungen && !istMinimaxStrategieAktiv();
            strategieInfoLabel.setVisible(zeigeInfo);
            strategieInfoLabel.setManaged(zeigeInfo);
        }
    }
    
    /**
     * Überprüft, ob eine Minimax-Strategie aktiv ist
     * @return true wenn mindestens eine Minimax-Strategie ausgewählt ist
     */
    private boolean istMinimaxStrategieAktiv() {
        boolean strategie1IstMinimax = strategie1ComboBox != null && 
            strategie1ComboBox.isVisible() && 
            strategie1ComboBox.getValue() == KiStrategie.MINIMAX;
            
        boolean strategie2IstMinimax = strategie2ComboBox != null && 
            strategie2ComboBox.isVisible() && 
            strategie2ComboBox.getValue() == KiStrategie.MINIMAX;
            
        return strategie1IstMinimax || strategie2IstMinimax;
    }
    
    /**
     * Erstellt die Spielmodus-Karte
     */
    private VBox erstelleSpielModusKarte() {
        VBox karte = new VBox(15);
        karte.setAlignment(Pos.TOP_CENTER);
        karte.setPadding(new Insets(25));
        karte.setPrefSize(450, 350); // SPIELMODUS breiter machen
        karte.setStyle(
            "-fx-background-color: rgba(236, 239, 244, 0.95);" +
            "-fx-background-radius: 20;" +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 20, 0, 0, 10);"
        );
        
        // Titel
        Label titel = new Label(FontAwesome.iconText(FontAwesome.GAMEPAD, "SPIELMODUS"));
        titel.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        titel.setTextFill(Color.BLACK);
        
        // Spielmodus-Optionen
        spielModusGroup = new ToggleGroup();
        
        VBox pvpContainer = erstelleRadioButton(FontAwesome.iconText(FontAwesome.USERS, "Spieler vs Spieler (PP)"), 
            "Zwei menschliche Spieler gegeneinander\n• Abwechselnd klicken Sie auf das Spielfeld\n• Erstes Ziel: 5 Steine in einer Reihe", SpielModus.PLAYER_VS_PLAYER, Color.web("#000000"));
        VBox pvcContainer = erstelleRadioButton(FontAwesome.iconText(FontAwesome.ROBOT, "Spieler vs Computer (PC)"), 
            "Sie spielen gegen eine künstliche Intelligenz\n• Sie sind Spieler X, Computer ist O\n• KI-Strategie wählbar", SpielModus.PLAYER_VS_COMPUTER, Color.web("#000000"));
        VBox cvcContainer = erstelleRadioButton(FontAwesome.iconText(FontAwesome.FIRE, "Computer vs Computer (CC)"), 
            "Zwei KI-Strategien spielen gegeneinander\n• Automatisches Spiel zum Zuschauen\n• Beide KI-Strategien wählbar", SpielModus.COMPUTER_VS_COMPUTER, Color.web("#000000"));
        
        // RadioButtons aus Containern extrahieren für ToggleGroup
        RadioButton pvpButton = (RadioButton) pvpContainer.getUserData();
        RadioButton pvcButton = (RadioButton) pvcContainer.getUserData();
        RadioButton cvcButton = (RadioButton) cvcContainer.getUserData();
        
        spielModusGroup.getToggles().addAll(pvpButton, pvcButton, cvcButton);
        
        // Standard-Auswahl
        pvcButton.setSelected(true);
        
        // Listener für Spielmodus-Änderungen
        spielModusGroup.selectedToggleProperty().addListener((obs, oldVal, newVal) -> {
            aktualisiereKonfiguration();
            aktualisiereSichtbarkeit();
        });
        
        karte.getChildren().addAll(titel, pvpContainer, pvcContainer, cvcContainer);
        
        return karte;
    }
    
    /**
     * Erstellt die KI-Einstellungen-Karte
     */
    private VBox erstelleKiEinstellungenKarte() {
        VBox karte = new VBox(15);
        karte.setAlignment(Pos.TOP_CENTER);
        karte.setPadding(new Insets(25));
        karte.setPrefSize(360, 350); // KI-Strategien schmaler machen
        karte.setStyle(
            "-fx-background-color: rgba(236, 239, 244, 0.95);" +
            "-fx-background-radius: 20;" +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 20, 0, 0, 10);"
        );
        
        // Titel
        Label titel = new Label(FontAwesome.iconText(FontAwesome.BRAIN, "KI-STRATEGIEN"));
        titel.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        titel.setTextFill(Color.web("#2E3440"));
        
        // KI-Strategien
        strategieLabel1 = new Label(FontAwesome.iconText(FontAwesome.BRAIN, "KI-Strategie:"));
        strategieLabel1.setFont(Font.font("Arial", FontWeight.NORMAL, 14));
        strategieLabel1.setTextFill(Color.web("#000000"));
        
        strategie1ComboBox = new ComboBox<>();
        strategie1ComboBox.setPrefWidth(300);
        strategie1ComboBox.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");
        strategie1ComboBox.getItems().addAll(KiStrategie.values());
        strategie1ComboBox.setValue(KiStrategie.MINIMAX);
        strategie1ComboBox.setOnAction(e -> {
            aktualisiereKonfiguration();
            aktualisiereSichtbarkeit();
        });
        
        strategieLabel2 = new Label(FontAwesome.iconText(FontAwesome.BRAIN, "KI-Strategie Spieler 2:"));
        strategieLabel2.setFont(Font.font("Arial", FontWeight.NORMAL, 14));
        strategieLabel2.setTextFill(Color.web("#3B4252"));
        strategieLabel2.setVisible(false);
        
        strategie2ComboBox = new ComboBox<>();
        strategie2ComboBox.setPrefWidth(300);
        strategie2ComboBox.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");
        strategie2ComboBox.getItems().addAll(KiStrategie.values());
        strategie2ComboBox.setValue(KiStrategie.EINFACHE_LOGIK);
        strategie2ComboBox.setOnAction(e -> {
            aktualisiereKonfiguration();
            aktualisiereSichtbarkeit();
        });
        strategie2ComboBox.setVisible(false);
        
        // Minimax-Tiefe
        minimaxTiefeLabel = new Label(FontAwesome.iconText(FontAwesome.TARGET, "Minimax-Suchtiefe:"));
        minimaxTiefeLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 14));
        minimaxTiefeLabel.setTextFill(Color.web("#000000"));
        
        // Circular buttons for Minimax depth
        minimaxTiefeButtons = new HBox(8);
        minimaxTiefeButtons.setAlignment(Pos.CENTER);
        minimaxTiefeGroup = new ToggleGroup();
        
        for (int i = 1; i <= 6; i++) {
            ToggleButton button = new ToggleButton(String.valueOf(i));
            button.setToggleGroup(minimaxTiefeGroup);
            button.setUserData(i);
            button.setPrefSize(40, 40);
            button.setStyle(
                "-fx-background-color: rgba(255, 255, 255, 0.9);" +
                "-fx-border-color: #5E81AC;" +
                "-fx-border-width: 2;" +
                "-fx-border-radius: 20;" +
                "-fx-background-radius: 20;" +
                "-fx-font-weight: bold;" +
                "-fx-font-size: 14px;"
            );
            
            // Selected style
            button.selectedProperty().addListener((obs, wasSelected, isSelected) -> {
                if (isSelected) {
                    button.setStyle(
                        "-fx-background-color: #5E81AC;" +
                        "-fx-border-color: #5E81AC;" +
                        "-fx-border-width: 2;" +
                        "-fx-border-radius: 20;" +
                        "-fx-background-radius: 20;" +
                        "-fx-font-weight: bold;" +
                        "-fx-font-size: 14px;" +
                        "-fx-text-fill: white;"
                    );
                } else {
                    button.setStyle(
                        "-fx-background-color: rgba(255, 255, 255, 0.9);" +
                        "-fx-border-color: #5E81AC;" +
                        "-fx-border-width: 2;" +
                        "-fx-border-radius: 20;" +
                        "-fx-background-radius: 20;" +
                        "-fx-font-weight: bold;" +
                        "-fx-font-size: 14px;" +
                        "-fx-text-fill: black;"
                    );
                }
                aktualisiereKonfiguration();
            });
            
            if (i == 4) { // Default selection
                button.setSelected(true);
            }
            
            minimaxTiefeButtons.getChildren().add(button);
        }
        
        // Keep spinner for backward compatibility but hide it
        minimaxTiefeSpinner = new Spinner<>(1, 6, 4);
        minimaxTiefeSpinner.setPrefWidth(100);
        minimaxTiefeSpinner.setVisible(false);
        minimaxTiefeSpinner.setManaged(false);
        
        // Alpha-Beta-Pruning
        alphaBetaCheckBox = new CheckBox(FontAwesome.iconText(FontAwesome.BOLT, "Alpha-Beta-Pruning aktiviert"));
        alphaBetaCheckBox.setFont(Font.font("Arial", FontWeight.NORMAL, 14));
        alphaBetaCheckBox.setTextFill(Color.web("#000000"));
        alphaBetaCheckBox.setSelected(true);
        alphaBetaCheckBox.setOnAction(e -> aktualisiereKonfiguration());
        
        // Info-Label für nicht-Minimax Strategien
        strategieInfoLabel = new Label(FontAwesome.iconText(FontAwesome.INFO_CIRCLE, "Minimax-Einstellungen sind nur für Minimax-Strategie verfügbar"));
        strategieInfoLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 12));
        strategieInfoLabel.setTextFill(Color.BLACK);
        strategieInfoLabel.setWrapText(true);
        strategieInfoLabel.setPrefWidth(350);
        strategieInfoLabel.setVisible(false);
        strategieInfoLabel.setManaged(false);
        
        karte.getChildren().addAll(
            titel, 
            strategieLabel1, strategie1ComboBox,
            strategieLabel2, strategie2ComboBox,
            minimaxTiefeLabel, minimaxTiefeButtons,
            alphaBetaCheckBox,
            strategieInfoLabel
        );
        
        return karte;
    }
    
    /**
     * Erstellt die Erweiterte-Einstellungen-Karte
     */
    private VBox erstelleErweiterteEinstellungenKarte() {
        VBox karte = new VBox(15);
        karte.setAlignment(Pos.TOP_CENTER);
        karte.setPadding(new Insets(25));
        karte.setPrefSize(360, 350); // Erweiterte Einstellungen schmaler machen
        karte.setStyle(
            "-fx-background-color: rgba(236, 239, 244, 0.95);" +
            "-fx-background-radius: 20;" +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 20, 0, 0, 10);"
        );
        
        // Titel
        Label titel = new Label(FontAwesome.iconText(FontAwesome.COG, "EINSTELLUNGEN"));
        titel.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        titel.setTextFill(Color.web("#2E3440"));
        
        // Spielfeld-Größe
        Label bretGroesseLabel = new Label(FontAwesome.iconText(FontAwesome.RULER, "Spielfeld-Größe:"));
        bretGroesseLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 14));
        bretGroesseLabel.setTextFill(Color.web("#000000"));
        
        bretGroesseSpinner = new Spinner<>(10, 20, 15);
        bretGroesseSpinner.setPrefWidth(100);
        bretGroesseSpinner.valueProperty().addListener((obs, oldVal, newVal) -> aktualisiereKonfiguration());
        
        // Debug-Optionen
        debugCheckBox = new CheckBox(FontAwesome.iconText(FontAwesome.CHART_BAR, "Debug-Modus (Konsole)"));
        debugCheckBox.setFont(Font.font("Arial", FontWeight.NORMAL, 14));
        debugCheckBox.setTextFill(Color.web("#000000"));
        debugCheckBox.setOnAction(e -> aktualisiereKonfiguration());
        
        debugDateiCheckBox = new CheckBox(FontAwesome.iconText(FontAwesome.FILE_TEXT, "Debug-Ausgabe in Datei"));
        debugDateiCheckBox.setFont(Font.font("Arial", FontWeight.NORMAL, 14));
        debugDateiCheckBox.setTextFill(Color.web("#000000"));
        
        // Debug-Datei-Pfad
        Label debugDateiLabel = new Label(FontAwesome.iconText(FontAwesome.FOLDER, "Debug-Datei Pfad:"));
        debugDateiLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 14));
        debugDateiLabel.setTextFill(Color.web("#000000"));
        debugDateiLabel.setVisible(false);
        
        HBox debugDateiBox = new HBox(10);
        debugDateiBox.setAlignment(Pos.CENTER_LEFT);
        
        debugDateiPfadField = new TextField();
        debugDateiPfadField.setPromptText("Leer = auto-generiert in gomoku-logs/");
        debugDateiPfadField.setPrefWidth(250);
        debugDateiPfadField.setVisible(false);
        debugDateiPfadField.textProperty().addListener((obs, oldVal, newVal) -> aktualisiereKonfiguration());
        
        debugDateiAuswahlButton = new Button(FontAwesome.getIcon(FontAwesome.FOLDER));
        debugDateiAuswahlButton.setPrefSize(30, 25);
        debugDateiAuswahlButton.setVisible(false);
        debugDateiAuswahlButton.setOnAction(e -> waehleDebugDatei());
        
        debugDateiBox.getChildren().addAll(debugDateiPfadField, debugDateiAuswahlButton);
        
        debugDateiCheckBox.setOnAction(e -> {
            aktualisiereKonfiguration();
            debugDateiLabel.setVisible(debugDateiCheckBox.isSelected());
            debugDateiPfadField.setVisible(debugDateiCheckBox.isSelected());
            debugDateiAuswahlButton.setVisible(debugDateiCheckBox.isSelected());
        });

        karte.getChildren().addAll(
            titel,
            bretGroesseLabel, bretGroesseSpinner,
            debugCheckBox, debugDateiCheckBox,
            debugDateiLabel, debugDateiBox
        );
        
        return karte;
    }
    
    /**
     * Öffnet einen Datei-Dialog zur Auswahl der Debug-Datei
     */
    private void waehleDebugDatei() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Debug-Datei auswählen");
        fileChooser.getExtensionFilters().addAll(
            new FileChooser.ExtensionFilter("Text-Dateien", "*.txt"),
            new FileChooser.ExtensionFilter("Log-Dateien", "*.log"),
            new FileChooser.ExtensionFilter("Alle Dateien", "*.*")
        );
        
        // Standard-Verzeichnis setzen
        try {
            java.io.File defaultDir = new java.io.File("gomoku-logs");
            if (defaultDir.exists()) {
                fileChooser.setInitialDirectory(defaultDir);
            }
        } catch (Exception e) {
            // Ignorieren - Fallback auf System-Standard
        }
        
        java.io.File selectedFile = fileChooser.showSaveDialog(stage);
        if (selectedFile != null) {
            debugDateiPfadField.setText(selectedFile.getAbsolutePath());
        }
    }
    
    /**
     * Erstellt einen styled RadioButton-Container mit Tooltip für Beschreibung
     */
    private VBox erstelleRadioButton(String text, String beschreibung, SpielModus modus, Color textColor) {
        VBox container = new VBox(8);
        container.setAlignment(Pos.CENTER_LEFT);
        container.setPadding(new Insets(15));
        container.setStyle(
            "-fx-background-color: rgba(255, 255, 255, 0.8);" +
            "-fx-background-radius: 15;" +
            "-fx-border-color: #D8DEE9;" +
            "-fx-border-radius: 15;" +
            "-fx-border-width: 2;" +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 5, 0, 0, 2);"
        );
        
        RadioButton radioButton = new RadioButton();
        radioButton.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        radioButton.setTextFill(Color.BLACK);
        radioButton.setUserData(modus);
        
        Label hauptText = new Label(text);
        hauptText.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        hauptText.setTextFill(textColor);
        
        // Tooltip-Icon für Beschreibung
        Label tooltipIcon = FontAwesome.createIcon(FontAwesome.QUESTION_CIRCLE, 14, Color.web("#5E81AC"));
        Tooltip tooltip = new Tooltip(beschreibung);
        tooltip.setFont(Font.font("Arial", FontWeight.NORMAL, 12));
        tooltip.setWrapText(true);
        tooltip.setMaxWidth(300);
        Tooltip.install(tooltipIcon, tooltip);
        
        HBox hbox = new HBox(10);
        hbox.setAlignment(Pos.CENTER_LEFT);
        hbox.getChildren().addAll(radioButton, hauptText, tooltipIcon);
        
        container.getChildren().add(hbox);
        
        // Hover-Effekte
        container.setOnMouseEntered(e -> container.setStyle(
            "-fx-background-color: rgba(143, 188, 187, 0.3);" +
            "-fx-background-radius: 15;" +
            "-fx-border-color: #88C0D0;" +
            "-fx-border-radius: 15;" +
            "-fx-border-width: 2;" +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 8, 0, 0, 3);"
        ));
        container.setOnMouseExited(e -> container.setStyle(
            "-fx-background-color: rgba(255, 255, 255, 0.8);" +
            "-fx-background-radius: 15;" +
            "-fx-border-color: #D8DEE9;" +
            "-fx-border-radius: 15;" +
            "-fx-border-width: 2;" +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 5, 0, 0, 2);"
        ));
        
        // Click-Handler für den ganzen Container
        container.setOnMouseClicked(e -> {
            radioButton.setSelected(true);
            radioButton.fireEvent(new javafx.event.ActionEvent());
        });
        
        // RadioButton für ToggleGroup-Zugriff zugänglich machen
        container.setUserData(radioButton);
        
        return container;
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
        if (debugCheckBox != null && debugDateiCheckBox != null) {
            if (debugCheckBox.isSelected() && debugDateiCheckBox.isSelected()) {
                konfiguration.setDebugModus(DebugModus.DATEI);
                // Debug-Datei-Pfad setzen
                if (debugDateiPfadField != null && !debugDateiPfadField.getText().trim().isEmpty()) {
                    konfiguration.setDebugDatei(debugDateiPfadField.getText().trim());
                } else {
                    konfiguration.setDebugDatei(null); // Auto-generieren
                }
            } else if (debugCheckBox.isSelected()) {
                konfiguration.setDebugModus(DebugModus.KONSOLE);
            } else {
                konfiguration.setDebugModus(DebugModus.KEIN);
            }
        }
        
        // Erweiterte Einstellungen
        if (bretGroesseSpinner != null) {
            konfiguration.setBretGroesse(bretGroesseSpinner.getValue());
        }
        if (minimaxTiefeGroup != null && minimaxTiefeGroup.getSelectedToggle() != null) {
            int tiefe = (Integer) minimaxTiefeGroup.getSelectedToggle().getUserData();
            konfiguration.setMinimaxTiefe(tiefe);
        }
        if (alphaBetaCheckBox != null) {
            konfiguration.setAlphaBetaPruning(alphaBetaCheckBox.isSelected());
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