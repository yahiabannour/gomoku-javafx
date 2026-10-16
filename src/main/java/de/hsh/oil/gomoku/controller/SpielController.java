package de.hsh.oil.gomoku.controller;

import de.hsh.oil.gomoku.model.*;
import de.hsh.oil.gomoku.strategy.ZugStrategie;
import de.hsh.oil.gomoku.util.DebugLogger;
import de.hsh.oil.gomoku.util.StrategieFactory;
import de.hsh.oil.gomoku.util.XmlSaveManager;
import de.hsh.oil.gomoku.view.KonsoleView;
import de.hsh.oil.gomoku.view.GuiView;
import de.hsh.oil.gomoku.view.SpielView;
import javafx.application.Platform;

/**
 * Controller-Klasse für das Gomoku-Spiel
 * 
 * Implementiert das MVC-Pattern und koordiniert die Interaktion zwischen
 * Model (Spiellogik) und View (Benutzeroberfläche)
 * 
 * @author OIL SS2025
 */
public class SpielController {
    
    private final SpielKonfiguration konfiguration;
    private Spielbrett brett;
    private Spieler spieler1;
    private Spieler spieler2;
    private Spieler aktuellerSpieler;
    private SpielView view;
    private DebugLogger debugLogger;
    private XmlSaveManager saveManager;
    private boolean spielLaeuft;
    
    /**
     * Konstruktor für den SpielController
     * 
     * @param konfiguration Die Spielkonfiguration
     */
    public SpielController(SpielKonfiguration konfiguration) {
        this.konfiguration = konfiguration;
        this.spielLaeuft = false;
        
        initialisiereKomponenten();
    }
    
    /**
     * Initialisiert alle Komponenten des Spiels
     */
    private void initialisiereKomponenten() {
        // Spielbrett erstellen
        brett = new Spielbrett(konfiguration.getBretGroesse());
        
        // Debug-Logger initialisieren
        debugLogger = new DebugLogger(
            konfiguration.getDebugModus(),
            konfiguration.getDebugDatei()
        );
        
        // XML Save Manager initialisieren
        saveManager = new XmlSaveManager();
        
        // View erstellen
        if (konfiguration.isGuiModus()) {
            view = new GuiView(this);
        } else {
            view = new KonsoleView(this);
        }
        
        // Spieler erstellen
        erstelleSpieler();
    }
    
    /**
     * Erstellt die Spieler basierend auf der Konfiguration
     */
    private void erstelleSpieler() {
        switch (konfiguration.getSpielModus()) {
            case PLAYER_VS_PLAYER:
                spieler1 = new Spieler("Spieler 1", 'X');
                spieler2 = new Spieler("Spieler 2", 'O');
                break;
                
            case PLAYER_VS_COMPUTER:
                spieler1 = new Spieler("Spieler", 'X');
                
                // KI-Strategie für Computer
                ZugStrategie strategie = StrategieFactory.erstelleStrategie(
                    konfiguration.getStrategie1() != null ? 
                    konfiguration.getStrategie1() : konfiguration.getStrategie2(),
                    konfiguration
                );
                spieler2 = new Spieler("Computer", 'O', strategie);
                break;
                
            case COMPUTER_VS_COMPUTER:
                ZugStrategie strategie1 = StrategieFactory.erstelleStrategie(konfiguration.getStrategie1(), konfiguration);
                ZugStrategie strategie2 = StrategieFactory.erstelleStrategie(konfiguration.getStrategie2(), konfiguration);
                
                spieler1 = new Spieler("Computer 1", 'X', strategie1);
                spieler2 = new Spieler("Computer 2", 'O', strategie2);
                break;
        }
        
        aktuellerSpieler = spieler1;
    }
    
    /**
     * Startet das Spiel
     */
    public void starteSpiel() {
        spielLaeuft = true;
        
        debugLogger.loggeSpielStart(spieler1, spieler2, brett.getGroesse());
        view.zeigeWillkommensnachricht();
        view.zeigeSpielerInfo(spieler1, spieler2);
        
        if (konfiguration.isGuiModus()) {
            // GUI-Modus: Event-driven
            view.starteGui();
            
            // Wenn erstes Spieler Computer ist, ersten Zug initiieren
            if (aktuellerSpieler.istComputer()) {
                Platform.runLater(() -> {
                    new Thread(() -> {
                        try {
                            Thread.sleep(1000); // Kurze Pause für GUI-Aufbau
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                        }
                        
                        if (spielLaeuft && !brett.istSpielBeendet()) {
                            Platform.runLater(() -> {
                                macheComputerZug();
                                view.aktualisiereAnzeige();
                            });
                        }
                    }).start();
                });
            }
        } else {
            // Konsole-Modus: Linear
            spielSchleife();
        }
    }
    
    /**
     * Hauptspielschleife für Konsolen-Modus
     */
    private void spielSchleife() {
        while (spielLaeuft && !brett.istSpielBeendet()) {
            view.zeigeBrett(brett);
            
            boolean zugErfolgreich = false;
            
            if (aktuellerSpieler.istComputer()) {
                // Computer-Zug
                zugErfolgreich = macheComputerZug();
            } else {
                // Menschlicher Zug
                zugErfolgreich = macheMenschenZug();
            }
            
            if (zugErfolgreich) {
                naechsterSpieler();
                
                // Kurze Pause bei Computer vs Computer
                if (konfiguration.getSpielModus() == SpielModus.COMPUTER_VS_COMPUTER) {
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }
            }
        }
        
        beendeSpiel();
    }
    
    /**
     * Verarbeitet einen Zug (für GUI und Konsole)
     * 
     * @param x X-Koordinate
     * @param y Y-Koordinate
     * @return true wenn der Zug erfolgreich war
     */
    public boolean verarbeiteZug(int x, int y) {
        if (!spielLaeuft || brett.istSpielBeendet()) {
            return false;
        }
        
        Zug zug = new Zug(x, y, aktuellerSpieler, brett.getAnzahlZuege() + 1);
        
        if (brett.macheZug(zug)) {
            debugLogger.loggeZug(zug);
            
            // Spiel beendet?
            if (brett.istSpielBeendet()) {
                beendeSpiel();
            } else {
                naechsterSpieler();
                
                // Wenn nächster Spieler Computer ist (in GUI), automatisch ziehen
                if (konfiguration.isGuiModus() && aktuellerSpieler.istComputer()) {
                    // Kleine Verzögerung für bessere UX
                    new Thread(() -> {
                        try {
                            Thread.sleep(500);
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                        }
                        
                        if (spielLaeuft && !brett.istSpielBeendet()) {
                            macheComputerZug();
                            view.aktualisiereAnzeige();
                        }
                    }).start();
                }
            }
            
            return true;
        }
        
        return false;
    }
    
    /**
     * Lässt den Computer einen Zug machen
     * 
     * @return true wenn der Zug erfolgreich war
     */
    private boolean macheComputerZug() {
        if (!aktuellerSpieler.istComputer()) {
            return false;
        }
        
        view.zeigeComputerDenkt(aktuellerSpieler);
        
        long startZeit = System.currentTimeMillis();
        Zug computerZug = aktuellerSpieler.berechneZug(brett, getGegner(aktuellerSpieler));
        long berechnungsZeit = System.currentTimeMillis() - startZeit;
        
        if (computerZug != null && brett.macheZug(computerZug)) {
            debugLogger.loggeZug(computerZug);
            
            // Debug-Info für KI-Berechnung
            if (aktuellerSpieler.getStrategie() instanceof de.hsh.oil.gomoku.strategy.MinimaxStrategie) {
                de.hsh.oil.gomoku.strategy.MinimaxStrategie minimax = 
                    (de.hsh.oil.gomoku.strategy.MinimaxStrategie) aktuellerSpieler.getStrategie();
                debugLogger.loggeKiBerechnung(aktuellerSpieler, berechnungsZeit, 
                                            minimax.getEvaluierteStellungen());
            } else {
                debugLogger.loggeKiBerechnung(aktuellerSpieler, berechnungsZeit, 0);
            }
            
            view.zeigeComputerZug(aktuellerSpieler, computerZug);
            
            // Spiel beendet?
            if (brett.istSpielBeendet()) {
                beendeSpiel();
            } else {
                // Nächster Spieler
                naechsterSpieler();
                
                // Wenn nächster Spieler auch Computer ist, automatisch weiterspielen
                if (konfiguration.isGuiModus() && aktuellerSpieler.istComputer()) {
                    Platform.runLater(() -> {
                        new Thread(() -> {
                            try {
                                Thread.sleep(1000); // Längere Pause zwischen Computer-Zügen
                            } catch (InterruptedException e) {
                                Thread.currentThread().interrupt();
                            }
                            
                            if (spielLaeuft && !brett.istSpielBeendet()) {
                                Platform.runLater(() -> {
                                    macheComputerZug();
                                    view.aktualisiereAnzeige();
                                });
                            }
                        }).start();
                    });
                }
            }
            
            return true;
        }
        
        debugLogger.loggeFehler("Computer konnte keinen gültigen Zug finden");
        return false;
    }
    
    /**
     * Lässt einen Menschen einen Zug machen
     * 
     * @return true wenn der Zug erfolgreich war
     */
    private boolean macheMenschenZug() {
        Zug menschenZug = view.erfrageMenschenZug(aktuellerSpieler, brett);
        
        if (menschenZug != null && brett.macheZug(menschenZug)) {
            debugLogger.loggeZug(menschenZug);
            return true;
        }
        
        view.zeigeFehlermeldung("Ungültiger Zug! Bitte versuche es erneut.");
        return false;
    }
    
    /**
     * Wechselt zum nächsten Spieler
     */
    private void naechsterSpieler() {
        aktuellerSpieler = (aktuellerSpieler == spieler1) ? spieler2 : spieler1;
    }
    
    /**
     * Beendet das Spiel und zeigt die Ergebnisse an
     */
    private void beendeSpiel() {
        spielLaeuft = false;
        
        Spieler gewinner = brett.getGewinner();
        
        // Statistiken aktualisieren
        if (gewinner != null) {
            gewinner.registriereSieg();
            getGegner(gewinner).registriereNiederlage();
            
            debugLogger.loggeGewinnLinie(brett.getGewinnLinie());
        } else {
            spieler1.registriereUnentschieden();
            spieler2.registriereUnentschieden();
        }
        
        debugLogger.loggeSpielEnde(gewinner, brett.getAnzahlZuege());
        
        view.zeigeBrett(brett);
        view.zeigeSpielErgebnis(gewinner, brett.getAnzahlZuege());
        view.zeigeStatistiken(spieler1, spieler2);
        
        // Aufräumen
        debugLogger.schliessen();
    }
    
    /**
     * Startet ein neues Spiel
     */
    public void starteNeuesSpiel() {
        brett.reset();
        aktuellerSpieler = spieler1;
        spielLaeuft = true;
        
        // Strategien zurücksetzen
        if (spieler1.istComputer()) {
            spieler1.getStrategie().zuruecksetzen();
        }
        if (spieler2.istComputer()) {
            spieler2.getStrategie().zuruecksetzen();
        }
        
        debugLogger.loggeSpielStart(spieler1, spieler2, brett.getGroesse());
        
        if (!konfiguration.isGuiModus()) {
            spielSchleife();
        } else {
            // GUI-Modus: Ersten Computer-Zug initiieren falls nötig
            if (aktuellerSpieler.istComputer()) {
                Platform.runLater(() -> {
                    new Thread(() -> {
                        try {
                            Thread.sleep(500); // Kurze Pause
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                        }
                        
                        if (spielLaeuft && !brett.istSpielBeendet()) {
                            Platform.runLater(() -> {
                                macheComputerZug();
                                view.aktualisiereAnzeige();
                            });
                        }
                    }).start();
                });
            }
        }
    }
    
    /**
     * Beendet das Spiel vorzeitig
     */
    public void beendeSpielVorzeitig() {
        spielLaeuft = false;
        debugLogger.logge("Spiel vorzeitig beendet");
        debugLogger.schliessen();
    }
    
    // Getter-Methoden
    
    public Spielbrett getBrett() {
        return brett;
    }
    
    public Spieler getAktuellerSpieler() {
        return aktuellerSpieler;
    }
    
    public Spieler getSpieler1() {
        return spieler1;
    }
    
    public Spieler getSpieler2() {
        return spieler2;
    }
    
    public SpielKonfiguration getKonfiguration() {
        return konfiguration;
    }
    
    public boolean istSpielAktiv() {
        return spielLaeuft;
    }
    
    public DebugLogger getDebugLogger() {
        return debugLogger;
    }
    
    /**
     * Gibt die View zurück
     * 
     * @return Die aktuelle View
     */
    public SpielView getView() {
        return view;
    }
    
    /**
     * Gibt den Gegner eines Spielers zurück
     * 
     * @param spieler Der Spieler
     * @return Der Gegenspieler
     */
    private Spieler getGegner(Spieler spieler) {
        return (spieler == spieler1) ? spieler2 : spieler1;
    }
    
    /**
     * Gibt Informationen über die XML-Datenbank aus
     */
    public void printDatabaseInfo() {
        if (saveManager != null) {
            saveManager.printDatabaseInfo();
        }
    }
    
    /**
     * Speichert das aktuelle Spiel
     * 
     * @param saveName Name für den Spielstand
     * @return true wenn erfolgreich gespeichert
     */
    public boolean speichereSpiel(String saveName) {
        if (saveManager == null) {
            System.err.println("❌ Save Manager nicht initialisiert");
            return false;
        }
        
        boolean erfolg = saveManager.saveGame(this, saveName);
        if (erfolg) {
            view.zeigeNachricht("💾 Spiel gespeichert: " + saveName);
        } else {
            view.zeigeNachricht("❌ Fehler beim Speichern");
        }
        return erfolg;
    }
    
    /**
     * Speichert das aktuelle Spiel an einem benutzerdefinierten Pfad
     * 
     * @param saveName Name für den Spielstand
     * @param filePath Vollständiger Pfad zur Datei
     * @return true wenn erfolgreich gespeichert
     */
    public boolean speichereSpielMitPfad(String saveName, String filePath) {
        if (saveManager == null) {
            System.err.println("❌ Save Manager nicht initialisiert");
            return false;
        }
        
        boolean erfolg = saveManager.saveGameToPath(this, saveName, filePath);
        if (erfolg) {
            view.zeigeNachricht("💾 Spiel gespeichert: " + filePath);
        } else {
            view.zeigeNachricht("❌ Fehler beim Speichern");
        }
        return erfolg;
    }
    
    /**
     * Lädt einen Spielstand
     * 
     * @param saveId ID des zu ladenden Spielstands
     * @return true wenn erfolgreich geladen
     */
    public boolean ladeSpiel(String saveId) {
        if (saveManager == null) {
            System.err.println("❌ Save Manager nicht initialisiert");
            return false;
        }
        
        GameSave gameSave = saveManager.loadGame(saveId);
        if (gameSave == null) {
            view.zeigeNachricht("❌ Spielstand konnte nicht geladen werden");
            return false;
        }
        
        return restoreFromSave(gameSave);
    }
    
    /**
     * Stellt das Spiel aus einem GameSave wieder her
     */
    private boolean restoreFromSave(GameSave gameSave) {
        try {
            // Spiel stoppen
            spielLaeuft = false;
            
            // Brett zurücksetzen
            brett = new Spielbrett(gameSave.getBoardSize());
            
            // Brett-Zustand wiederherstellen
            for (int y = 0; y < gameSave.getBoardSize(); y++) {
                String row = gameSave.getBoardRows().get(y);
                for (int x = 0; x < gameSave.getBoardSize(); x++) {
                    char feld = row.charAt(x);
                    if (feld != '.') {
                        brett.setFeld(x, y, feld);
                    }
                }
            }
            
            // Züge wiederherstellen
            brett.getZugHistorie().clear();
            for (SavedMove savedMove : gameSave.getMoves()) {
                Spieler spieler = savedMove.getPlayerIndex() == 0 ? spieler1 : spieler2;
                Zug zug = new Zug(savedMove.getX(), savedMove.getY(), spieler, savedMove.getMoveNumber());
                brett.getZugHistorie().add(zug);
            }
            
            // Aktueller Spieler
            aktuellerSpieler = gameSave.getCurrentPlayerIndex() == 0 ? spieler1 : spieler2;
            
            // Spiel-Status
            spielLaeuft = gameSave.isGameActive();
            
            // View aktualisieren
            view.aktualisiereAnzeige();
            view.zeigeNachricht("📂 Spielstand geladen: " + gameSave.getSaveName());
            
            return true;
            
        } catch (Exception e) {
            System.err.println("❌ Fehler beim Wiederherstellen: " + e.getMessage());
            view.zeigeNachricht("❌ Fehler beim Laden des Spielstands");
            return false;
        }
    }
    
    /**
     * Listet alle verfügbaren Spielstände auf
     */
    public java.util.List<GameSave> getVerfuegbareSaves() {
        if (saveManager == null) {
            return new java.util.ArrayList<>();
        }
        return saveManager.listSaves();
    }
    
    /**
     * Löscht einen Spielstand
     */
    public boolean loescheSave(String saveId) {
        if (saveManager == null) {
            return false;
        }
        return saveManager.deleteSave(saveId);
    }
    
    /**
     * Prüft ob die XML-Datenbank bereit ist
     */
    public boolean istSaveSystemBereit() {
        return saveManager != null && saveManager.isDatabaseReady();
    }
} 