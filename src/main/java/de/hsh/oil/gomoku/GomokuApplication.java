package de.hsh.oil.gomoku;

import javafx.application.Application;
import javafx.stage.Stage;
import de.hsh.oil.gomoku.controller.SpielController;
import de.hsh.oil.gomoku.model.SpielKonfiguration;
import de.hsh.oil.gomoku.view.WelcomeView;

/**
 * JavaFX Application-Klasse für das Gomoku-Spiel
 * 
 * Diese Klasse wird verwendet, um JavaFX richtig zu initialisieren
 * und die GUI-Version des Spiels zu starten.
 * 
 * @author OIL SS2025
 * @version 1.0
 */
public class GomokuApplication extends Application {
    
    private static SpielKonfiguration konfiguration;
    private Stage primaryStage;
    
    /**
     * Setzt die Spielkonfiguration für die Anwendung
     * 
     * @param config Die zu verwendende Spielkonfiguration
     */
    public static void setKonfiguration(SpielKonfiguration config) {
        konfiguration = config;
    }
    
    /**
     * Startet die JavaFX-Anwendung
     * 
     * @param primaryStage Die Hauptbühne der Anwendung
     * @throws Exception Falls ein Fehler beim Start auftritt
     */
    @Override
    public void start(Stage primaryStage) throws Exception {
        this.primaryStage = primaryStage;
        
        try {
            // Wenn keine Konfiguration übergeben wurde (Default-Start), Welcome-Screen zeigen
            if (konfiguration == null) {
                zeigeWelcomeScreen();
            } else {
                // Direkt das Spiel mit übergebener Konfiguration starten
                starteSpielMitKonfiguration(konfiguration);
            }
            
        } catch (Exception e) {
            System.err.println("Fehler beim Starten der GUI: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }
    
    /**
     * Zeigt den Welcome-Screen für die Spielkonfiguration
     */
    private void zeigeWelcomeScreen() {
        WelcomeView welcomeView = new WelcomeView(primaryStage, this::starteSpielMitKonfiguration);
        welcomeView.zeigen();
    }
    
    /**
     * Startet das Spiel mit der gegebenen Konfiguration
     * 
     * @param config Die Spielkonfiguration
     */
    private void starteSpielMitKonfiguration(SpielKonfiguration config) {
        try {
            // Spiel-Controller erstellen
            SpielController controller = new SpielController(config);
            
            // GUI-View erstellen und Primary Stage setzen
            if (controller.getView() instanceof de.hsh.oil.gomoku.view.GuiView) {
                de.hsh.oil.gomoku.view.GuiView guiView = (de.hsh.oil.gomoku.view.GuiView) controller.getView();
                guiView.setPrimaryStage(primaryStage);
            }
            
            // Spiel starten
            controller.starteSpiel();
            
        } catch (Exception e) {
            System.err.println("Fehler beim Starten des Spiels: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Wird beim Beenden der Anwendung aufgerufen
     */
    @Override
    public void stop() {
        System.out.println("Gomoku-Anwendung wird beendet...");
    }
} 