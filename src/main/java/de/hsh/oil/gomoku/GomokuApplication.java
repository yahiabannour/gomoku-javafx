package de.hsh.oil.gomoku;

import javafx.application.Application;
import javafx.stage.Stage;
import de.hsh.oil.gomoku.controller.SpielController;
import de.hsh.oil.gomoku.model.SpielKonfiguration;

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
        try {
            // Standard-Konfiguration wenn keine gesetzt wurde
            if (konfiguration == null) {
                konfiguration = new SpielKonfiguration();
                konfiguration.setGuiModus(true);
            }
            
            // Spiel-Controller erstellen
            SpielController controller = new SpielController(konfiguration);
            
            // GUI-View erstellen und Primary Stage setzen
            if (controller.getView() instanceof de.hsh.oil.gomoku.view.GuiView) {
                de.hsh.oil.gomoku.view.GuiView guiView = (de.hsh.oil.gomoku.view.GuiView) controller.getView();
                guiView.setPrimaryStage(primaryStage);
            }
            
            // Spiel starten
            controller.starteSpiel();
            
        } catch (Exception e) {
            System.err.println("Fehler beim Starten der GUI: " + e.getMessage());
            e.printStackTrace();
            throw e;
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