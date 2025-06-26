package de.hsh.oil.gomoku.util;

import de.hsh.oil.gomoku.model.DebugModus;
import de.hsh.oil.gomoku.model.Spieler;
import de.hsh.oil.gomoku.model.Spielbrett;
import de.hsh.oil.gomoku.model.Zug;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Debug-Logger für Gomoku-Spiel
 * 
 * Unterstützt Debug-Ausgabe auf der Konsole (D) und in eine ASCII-Datei (DF)
 * Protokolliert alle Spielzüge und wichtige Ereignisse
 * 
 * @author OIL SS2025
 */
public class DebugLogger {
    
    private final DebugModus debugModus;
    private final String debugDatei;
    private BufferedWriter dateiWriter;
    private boolean istAktiv;
    private final DateTimeFormatter zeitFormat = DateTimeFormatter.ofPattern("HH:mm:ss");
    
    /**
     * Konstruktor für Debug-Logger
     * 
     * @param debugModus Der Debug-Modus
     * @param debugDatei Der Pfad zur Debug-Datei (nur für DF-Modus)
     */
    public DebugLogger(DebugModus debugModus, String debugDatei) {
        this.debugModus = debugModus;
        this.debugDatei = debugDatei;
        this.istAktiv = debugModus != DebugModus.KEIN;
        
        if (debugModus == DebugModus.DATEI) {
            initialisiereDebugDatei();
        }
    }
    
    /**
     * Initialisiert die Debug-Datei
     */
    private void initialisiereDebugDatei() {
        try {
            dateiWriter = new BufferedWriter(new FileWriter(debugDatei, false));
            schreibeKopfzeile();
        } catch (IOException e) {
            System.err.println("Fehler beim Erstellen der Debug-Datei: " + e.getMessage());
            istAktiv = false;
        }
    }
    
    /**
     * Schreibt eine Kopfzeile in die Debug-Datei
     */
    private void schreibeKopfzeile() throws IOException {
        if (dateiWriter != null) {
            dateiWriter.write("=".repeat(60));
            dateiWriter.newLine();
            dateiWriter.write("GOMOKU SPIEL DEBUG-LOG");
            dateiWriter.newLine();
            dateiWriter.write("Erstellt am: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss")));
            dateiWriter.newLine();
            dateiWriter.write("=".repeat(60));
            dateiWriter.newLine();
            dateiWriter.newLine();
            dateiWriter.flush();
        }
    }
    
    /**
     * Protokolliert den Start eines neuen Spiels
     * 
     * @param spieler1 Erster Spieler
     * @param spieler2 Zweiter Spieler
     * @param bretGroesse Die Größe des Spielbretts
     */
    public void loggeSpielStart(Spieler spieler1, Spieler spieler2, int bretGroesse) {
        if (!istAktiv) return;
        
        String nachricht = String.format(
            "[%s] SPIEL GESTARTET - %s vs %s (Brett: %dx%d)",
            getZeitstempel(), spieler1.getName(), spieler2.getName(), bretGroesse, bretGroesse
        );
        
        ausgeben(nachricht);
    }
    
    /**
     * Protokolliert einen Spielzug
     * 
     * @param zug Der gemachte Zug
     */
    public void loggeZug(Zug zug) {
        if (!istAktiv) return;
        
        String nachricht = String.format(
            "[%s] %s",
            getZeitstempel(), zug.toDebugString()
        );
        
        ausgeben(nachricht);
    }
    
    /**
     * Protokolliert das aktuelle Spielbrett
     * 
     * @param brett Das Spielbrett
     * @param nachZug Ob das Brett nach einem Zug angezeigt wird
     */
    public void loggeBrett(Spielbrett brett, boolean nachZug) {
        if (!istAktiv) return;
        
        if (nachZug) {
            ausgeben("Brett nach Zug " + brett.getAnzahlZuege() + ":");
        } else {
            ausgeben("Aktuelles Brett:");
        }
        
        ausgeben(brett.toString());
    }
    
    /**
     * Protokolliert das Spielende
     * 
     * @param gewinner Der Gewinner oder null bei Unentschieden
     * @param anzahlZuege Die Gesamtanzahl der Züge
     */
    public void loggeSpielEnde(Spieler gewinner, int anzahlZuege) {
        if (!istAktiv) return;
        
        String nachricht;
        if (gewinner != null) {
            nachricht = String.format(
                "[%s] SPIEL BEENDET - %s gewonnen nach %d Zügen",
                getZeitstempel(), gewinner.getName(), anzahlZuege
            );
        } else {
            nachricht = String.format(
                "[%s] SPIEL BEENDET - Unentschieden nach %d Zügen",
                getZeitstempel(), anzahlZuege
            );
        }
        
        ausgeben(nachricht);
        ausgeben("=".repeat(50));
    }
    
    /**
     * Protokolliert die Gewinnlinie
     * 
     * @param gewinnLinie Die Positionen der Gewinnlinie
     */
    public void loggeGewinnLinie(java.util.List<Spielbrett.Position> gewinnLinie) {
        if (!istAktiv || gewinnLinie.isEmpty()) return;
        
        StringBuilder sb = new StringBuilder();
        sb.append("Gewinnlinie: ");
        for (int i = 0; i < gewinnLinie.size(); i++) {
            if (i > 0) sb.append(" -> ");
            Spielbrett.Position pos = gewinnLinie.get(i);
            sb.append(String.format("(%d,%d)", pos.getX(), pos.getY()));
        }
        
        ausgeben(sb.toString());
    }
    
    /**
     * Protokolliert eine KI-Berechnung
     * 
     * @param spieler Der KI-Spieler
     * @param berechnungsZeit Die benötigte Zeit in Millisekunden
     * @param evaluierteStellungen Anzahl evaluierter Stellungen
     */
    public void loggeKiBerechnung(Spieler spieler, long berechnungsZeit, int evaluierteStellungen) {
        if (!istAktiv) return;
        
        String nachricht = String.format(
            "[%s] KI %s: %dms, %d Stellungen evaluiert",
            getZeitstempel(), spieler.getName(), berechnungsZeit, evaluierteStellungen
        );
        
        ausgeben(nachricht);
    }
    
    /**
     * Protokolliert eine allgemeine Nachricht
     * 
     * @param nachricht Die zu protokollierende Nachricht
     */
    public void logge(String nachricht) {
        if (!istAktiv) return;
        
        String formatierteNachricht = String.format(
            "[%s] %s", getZeitstempel(), nachricht
        );
        
        ausgeben(formatierteNachricht);
    }
    
    /**
     * Protokolliert einen Fehler
     * 
     * @param fehler Die Fehlernachricht
     */
    public void loggeFehler(String fehler) {
        if (!istAktiv) return;
        
        String nachricht = String.format(
            "[%s] FEHLER: %s", getZeitstempel(), fehler
        );
        
        ausgeben(nachricht);
    }
    
    /**
     * Gibt eine Nachricht auf der Konsole und/oder in die Datei aus
     * 
     * @param nachricht Die auszugebende Nachricht
     */
    private void ausgeben(String nachricht) {
        switch (debugModus) {
            case KONSOLE:
                System.out.println(nachricht);
                break;
                
            case DATEI:
                if (dateiWriter != null) {
                    try {
                        dateiWriter.write(nachricht);
                        dateiWriter.newLine();
                        dateiWriter.flush();
                    } catch (IOException e) {
                        System.err.println("Fehler beim Schreiben in Debug-Datei: " + e.getMessage());
                    }
                }
                break;
                
            case KEIN:
                // Nichts tun
                break;
        }
    }
    
    /**
     * Erstellt einen Zeitstempel
     * 
     * @return Der aktuelle Zeitstempel als String
     */
    private String getZeitstempel() {
        return LocalDateTime.now().format(zeitFormat);
    }
    
    /**
     * Schließt den Debug-Logger und gibt Ressourcen frei
     */
    public void schliessen() {
        if (dateiWriter != null) {
            try {
                dateiWriter.write("\n" + "=".repeat(60));
                dateiWriter.newLine();
                dateiWriter.write("DEBUG-LOG BEENDET: " + 
                                LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss")));
                dateiWriter.newLine();
                dateiWriter.close();
            } catch (IOException e) {
                System.err.println("Fehler beim Schließen der Debug-Datei: " + e.getMessage());
            }
        }
        istAktiv = false;
    }
    
    /**
     * Überprüft, ob der Debug-Logger aktiv ist
     * 
     * @return true wenn aktiv, false sonst
     */
    public boolean istAktiv() {
        return istAktiv;
    }
    
    /**
     * Gibt den Debug-Modus zurück
     * 
     * @return Der aktuelle Debug-Modus
     */
    public DebugModus getDebugModus() {
        return debugModus;
    }
    
    /**
     * Gibt den Pfad zur Debug-Datei zurück
     * 
     * @return Der Pfad zur Debug-Datei
     */
    public String getDebugDatei() {
        return debugDatei;
    }
} 