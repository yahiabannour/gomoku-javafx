package de.hsh.oil.gomoku.model;

import de.hsh.oil.gomoku.strategy.ZugStrategie;

/**
 * Repräsentiert einen Spieler im Gomoku-Spiel
 * Kann sowohl ein menschlicher Spieler als auch ein Computer-Spieler sein
 * 
 * @author OIL SS2025
 */
public class Spieler {
    
    private final String name;
    private final char symbol;
    private final boolean istComputer;
    private ZugStrategie strategie;
    private int gewonneneSpiele;
    private int verloreneSpiele;
    private int unentschiedeneSpiele;
    
    /**
     * Konstruktor für einen menschlichen Spieler
     * 
     * @param name Der Name des Spielers
     * @param symbol Das Symbol des Spielers (z.B. 'X' oder 'O')
     */
    public Spieler(String name, char symbol) {
        this.name = name;
        this.symbol = symbol;
        this.istComputer = false;
        this.strategie = null;
        this.gewonneneSpiele = 0;
        this.verloreneSpiele = 0;
        this.unentschiedeneSpiele = 0;
    }
    
    /**
     * Konstruktor für einen Computer-Spieler
     * 
     * @param name Der Name des Spielers
     * @param symbol Das Symbol des Spielers (z.B. 'X' oder 'O')
     * @param strategie Die KI-Strategie des Computer-Spielers
     */
    public Spieler(String name, char symbol, ZugStrategie strategie) {
        this.name = name;
        this.symbol = symbol;
        this.istComputer = true;
        this.strategie = strategie;
        this.gewonneneSpiele = 0;
        this.verloreneSpiele = 0;
        this.unentschiedeneSpiele = 0;
    }
    
    // Getter und Setter
    
    public String getName() {
        return name;
    }
    
    public char getSymbol() {
        return symbol;
    }
    
    public boolean istComputer() {
        return istComputer;
    }
    
    public ZugStrategie getStrategie() {
        return strategie;
    }
    
    public void setStrategie(ZugStrategie strategie) {
        this.strategie = strategie;
    }
    
    public int getGewonneneSpiele() {
        return gewonneneSpiele;
    }
    
    public int getVerloreneSpiele() {
        return verloreneSpiele;
    }
    
    public int getUnentschiedeneSpiele() {
        return unentschiedeneSpiele;
    }
    
    public int getGesamtSpiele() {
        return gewonneneSpiele + verloreneSpiele + unentschiedeneSpiele;
    }
    
    /**
     * Berechnet die Gewinnrate des Spielers
     * 
     * @return Die Gewinnrate als Prozentsatz (0.0 - 1.0)
     */
    public double getGewinnrate() {
        int gesamtSpiele = getGesamtSpiele();
        if (gesamtSpiele == 0) {
            return 0.0;
        }
        return (double) gewonneneSpiele / gesamtSpiele;
    }
    
    // Statistik-Methoden
    
    /**
     * Registriert einen Sieg
     */
    public void registriereSieg() {
        gewonneneSpiele++;
    }
    
    /**
     * Registriert eine Niederlage
     */
    public void registriereNiederlage() {
        verloreneSpiele++;
    }
    
    /**
     * Registriert ein Unentschieden
     */
    public void registriereUnentschieden() {
        unentschiedeneSpiele++;
    }
    
    /**
     * Setzt die Statistiken zurück
     */
    public void resetteStatistiken() {
        gewonneneSpiele = 0;
        verloreneSpiele = 0;
        unentschiedeneSpiele = 0;
    }
    
    /**
     * Macht einen Zug basierend auf der Strategie (nur für Computer-Spieler)
     * 
     * @param brett Das aktuelle Spielbrett
     * @param gegner Der Gegenspieler
     * @return Der berechnete Zug oder null wenn kein Zug möglich
     */
    public Zug berechneZug(Spielbrett brett, Spieler gegner) {
        if (!istComputer || strategie == null) {
            throw new IllegalStateException("Zug kann nur für Computer-Spieler mit Strategie berechnet werden");
        }
        
        return strategie.berechneZug(brett, this, gegner);
    }
    
    /**
     * Überprüft, ob der Spieler einen gültigen Zug machen kann
     * 
     * @param brett Das Spielbrett
     * @return true wenn ein Zug möglich ist
     */
    public boolean kannZugMachen(Spielbrett brett) {
        if (!istComputer) {
            return true; // Menschliche Spieler können immer versuchen zu ziehen
        }
        
        return strategie != null && brett.hatFreieFelder();
    }
    
    /**
     * Erstellt eine Kopie des Spielers
     * 
     * @return Eine neue Instanz mit gleichen Werten
     */
    public Spieler kopiere() {
        Spieler kopie = new Spieler(name, symbol, strategie);
        kopie.gewonneneSpiele = this.gewonneneSpiele;
        kopie.verloreneSpiele = this.verloreneSpiele;
        kopie.unentschiedeneSpiele = this.unentschiedeneSpiele;
        return kopie;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        
        Spieler spieler = (Spieler) obj;
        return symbol == spieler.symbol && 
               istComputer == spieler.istComputer && 
               java.util.Objects.equals(name, spieler.name);
    }
    
    @Override
    public int hashCode() {
        return java.util.Objects.hash(name, symbol, istComputer);
    }
    
    @Override
    public String toString() {
        String typ = istComputer ? "Computer" : "Mensch";
        String strategieInfo = (istComputer && strategie != null) ? 
                              " (Strategie: " + strategie.getClass().getSimpleName() + ")" : "";
        return String.format("%s (%c) - %s%s", name, symbol, typ, strategieInfo);
    }
    
    /**
     * Gibt die Statistiken des Spielers als String zurück
     * 
     * @return Statistiken als formatierter String
     */
    public String getStatistikString() {
        return String.format("%s: %d Siege, %d Niederlagen, %d Unentschieden (%.1f%% Gewinnrate)", 
                           name, gewonneneSpiele, verloreneSpiele, unentschiedeneSpiele, 
                           getGewinnrate() * 100);
    }
    
    /**
     * Gibt die Anzahl der Siege zurück (Alias für getGewonneneSpiele)
     */
    public int getSiege() {
        return gewonneneSpiele;
    }
    
    /**
     * Gibt die Anzahl der gespielten Spiele zurück (Alias für getGesamtSpiele)
     */
    public int getGespielteSpiele() {
        return getGesamtSpiele();
    }
    
    /**
     * Gibt die Gesamtanzahl der Züge zurück (geschätzt basierend auf gespielten Spielen)
     */
    public int getGesamtZuege() {
        // Schätzung: Durchschnittlich 20 Züge pro Spiel
        return getGesamtSpiele() * 20;
    }
} 