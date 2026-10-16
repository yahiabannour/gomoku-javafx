package de.hsh.oil.gomoku;

import javafx.application.Application;
import de.hsh.oil.gomoku.controller.SpielController;
import de.hsh.oil.gomoku.util.ParameterValidator;
import de.hsh.oil.gomoku.util.XmlSaveManager;
import de.hsh.oil.gomoku.model.SpielKonfiguration;

/**
 * Hauptklasse für das Gomoku-Spiel
 * 
 * Einstiegspunkt der Anwendung mit Parameterverarbeitung
 * Unterstützt verschiedene Spielmodi: PP (Player vs Player), PC (Player vs Computer), CC (Computer vs Computer)
 * Strategien: S1 (Zufall), S2 (Einfache Logik), S3 (Minimax)
 * Debug-Modi: D (Konsole), DF (Datei)
 * 
 * Beispielaufruf: java -jar gomoku.jar PC D S3
 * 
 * @author OIL SS2025
 * @version 1.0
 */
public class Main {
    
    private static final String PROGRAMMNAME = "Gomoku";
    private static final String VERSION = "1.0.0";
    
    /**
     * Hauptmethode der Anwendung
     * 
     * @param args Kommandozeilenparameter für Spielkonfiguration
     */
    public static void main(String[] args) {
        try {
            // Begrüßung ausgeben
            System.out.println("=".repeat(50));
            System.out.println("  " + PROGRAMMNAME + " - Strategisches Brettspiel");
            System.out.println("  Version: " + VERSION);
            System.out.println("  OIL SS2025 - Hochschule Hannover");
            System.out.println("=".repeat(50));
            
            // XML-Datenbank initialisieren (nur wenn verfügbar)
            try {
                XmlSaveManager saveManager = new XmlSaveManager();
                if (saveManager.isDatabaseReady()) {
                    System.out.println("✅ XML-Datenbank erfolgreich initialisiert");
                } else {
                    System.out.println("⚠️ XML-Datenbank-Initialisierung fehlgeschlagen");
                }
            } catch (Exception e) {
                System.out.println("⚠️ XML-Funktionen nicht verfügbar (JAXB fehlt): " + e.getMessage());
            }
            
            // Parameter validieren und Konfiguration erstellen
            SpielKonfiguration konfiguration;
            
            if (args.length == 0) {
                // GUI-Modus ohne Parameter starten - Welcome Screen zeigen
                System.out.println("Starte Gomoku Welcome Screen...");
                
                // Keine Konfiguration setzen -> Welcome Screen wird angezeigt
                GomokuApplication.setKonfiguration(null);
                Application.launch(GomokuApplication.class, args);
                
            } else {
                // Parameter verarbeiten
                ParameterValidator validator = new ParameterValidator();
                konfiguration = validator.validiere(args);
                
                if (konfiguration == null) {
                    hilfeAnzeigen();
                    System.exit(1);
                }
                
                // Konfiguration ausgeben
                System.out.println("Spielkonfiguration:");
                System.out.println("  Modus: " + konfiguration.getSpielModus());
                System.out.println("  Debug: " + konfiguration.getDebugModus());
                if (konfiguration.getStrategie1() != null) {
                    System.out.println("  Strategie Spieler 1: " + konfiguration.getStrategie1());
                }
                if (konfiguration.getStrategie2() != null) {
                    System.out.println("  Strategie Spieler 2: " + konfiguration.getStrategie2());
                }
                System.out.println();
                
                // Prüfen ob GUI-Modus angefordert wird
                if (konfiguration.isGuiModus()) {
                    // JavaFX-Anwendung starten
                    GomokuApplication.setKonfiguration(konfiguration);
                    Application.launch(GomokuApplication.class, args);
                } else {
                    // Konsolen-Modus
                    SpielController controller = new SpielController(konfiguration);
                    controller.starteSpiel();
                }
            }
            
        } catch (Exception e) {
            System.err.println("Fehler beim Starten des Spiels: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }
    
    /**
     * Zeigt die Hilfe mit verfügbaren Parametern an
     */
    private static void hilfeAnzeigen() {
        System.out.println("\nVerwendung: java -jar gomoku.jar [Go] [MODUS] [DEBUG] [STRATEGIE]");
        System.out.println("\nParameter (Reihenfolge beliebig):");
        System.out.println("  GUI-MODUS:");
        System.out.println("    Go  - Startet das Spiel in der GUI (statt Konsole)");
        System.out.println();
        System.out.println("  SPIELMODUS:");
        System.out.println("    PP  - Player vs Player (Spieler gegen Spieler)");
        System.out.println("    PC  - Player vs Computer (Spieler gegen Computer)");
        System.out.println("    CC  - Computer vs Computer");
        System.out.println();
        System.out.println("  DEBUG:");
        System.out.println("    D   - Debug-Ausgabe auf der Konsole");
        System.out.println("    DF  - Debug-Ausgabe in ASCII-Datei");
        System.out.println();
        System.out.println("  STRATEGIE:");
        System.out.println("    S1  - Zufällige Züge (eingeschränkt)");
        System.out.println("    S2  - Einfache Logik (Blocken)");
        System.out.println("    S3  - Minimax-Algorithmus");
        System.out.println();
        System.out.println("Beispiele:");
        System.out.println("  java -jar gomoku.jar PC D S3       (Konsole)");
        System.out.println("  java -jar gomoku.jar Go PC D S3    (GUI-Simulation)");
        System.out.println("  java -jar gomoku.jar Go CC S1 S2 D (GUI Computer vs Computer)");
        System.out.println();
        System.out.println("Ohne Parameter: Startet im interaktiven GUI-Modus");
    }
} 