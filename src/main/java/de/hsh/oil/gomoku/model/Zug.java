package de.hsh.oil.gomoku.model;

import java.time.LocalDateTime;

/**
 * Repräsentiert einen Spielzug im Gomoku-Spiel
 * 
 * @author OIL SS2025
 */
public class Zug {
    
    private final int x;
    private final int y;
    private final Spieler spieler;
    private final LocalDateTime zeitpunkt;
    private final int zugNummer;
    
    /**
     * Konstruktor für einen Spielzug
     * 
     * @param x Die x-Koordinate (Spalte)
     * @param y Die y-Koordinate (Zeile)
     * @param spieler Der Spieler, der den Zug gemacht hat
     * @param zugNummer Die laufende Nummer des Zuges
     */
    public Zug(int x, int y, Spieler spieler, int zugNummer) {
        this.x = x;
        this.y = y;
        this.spieler = spieler;
        this.zugNummer = zugNummer;
        this.zeitpunkt = LocalDateTime.now();
    }
    
    /**
     * Konstruktor für einen Spielzug mit Zeitpunkt
     * 
     * @param x Die x-Koordinate (Spalte)
     * @param y Die y-Koordinate (Zeile)
     * @param spieler Der Spieler, der den Zug gemacht hat
     * @param zugNummer Die laufende Nummer des Zuges
     * @param zeitpunkt Der Zeitpunkt des Zuges
     */
    public Zug(int x, int y, Spieler spieler, int zugNummer, LocalDateTime zeitpunkt) {
        this.x = x;
        this.y = y;
        this.spieler = spieler;
        this.zugNummer = zugNummer;
        this.zeitpunkt = zeitpunkt;
    }
    
    // Getter-Methoden
    
    public int getX() {
        return x;
    }
    
    public int getY() {
        return y;
    }
    
    public Spieler getSpieler() {
        return spieler;
    }
    
    public LocalDateTime getZeitpunkt() {
        return zeitpunkt;
    }
    
    public int getZugNummer() {
        return zugNummer;
    }
    
    /**
     * Überprüft, ob dieser Zug die gleiche Position hat wie ein anderer
     * 
     * @param andererZug Der andere Zug
     * @return true wenn die Positionen gleich sind
     */
    public boolean hatGleichePosition(Zug andererZug) {
        return andererZug != null && this.x == andererZug.x && this.y == andererZug.y;
    }
    
    /**
     * Überprüft, ob die Koordinaten gültig sind
     * 
     * @param bretGroesse Die Größe des Spielbretts
     * @return true wenn die Koordinaten gültig sind
     */
    public boolean istGueltig(int bretGroesse) {
        return x >= 0 && x < bretGroesse && y >= 0 && y < bretGroesse;
    }
    
    /**
     * Erstellt eine Kopie des Zuges
     * 
     * @return Eine neue Instanz mit gleichen Werten
     */
    public Zug kopiere() {
        return new Zug(x, y, spieler, zugNummer, zeitpunkt);
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        
        Zug zug = (Zug) obj;
        return x == zug.x && y == zug.y && zugNummer == zug.zugNummer;
    }
    
    @Override
    public int hashCode() {
        return java.util.Objects.hash(x, y, zugNummer);
    }
    
    @Override
    public String toString() {
        return String.format("Zug{Spieler=%s, Position=(%d,%d), Nummer=%d}", 
                           spieler.getName(), x, y, zugNummer);
    }
    
    /**
     * Gibt eine kompakte String-Darstellung für Debug-Ausgaben zurück
     * 
     * @return Kompakte String-Darstellung
     */
    public String toDebugString() {
        return String.format("%s %d %d", spieler.getName(), x, y);
    }
} 