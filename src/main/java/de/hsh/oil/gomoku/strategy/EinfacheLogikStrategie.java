package de.hsh.oil.gomoku.strategy;

import de.hsh.oil.gomoku.model.Spielbrett;
import de.hsh.oil.gomoku.model.Spieler;
import de.hsh.oil.gomoku.model.Zug;
import de.hsh.oil.gomoku.model.Spielbrett.Position;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * S2 - Einfache Logik-Strategie für KI-Spieler
 * 
 * Implementiert eine einfache Spiellogik:
 * 1. Versuche zu gewinnen (4 in einer Reihe zu 5 vervollständigen)
 * 2. Blockiere Gegner (verhindere 4 in einer Reihe)
 * 3. Baue eigene Angriffe auf (3 in einer Reihe erweitern)
 * 4. Blockiere Gegnerangriffe (3 in einer Reihe stoppen)
 * 5. Zufälliger Zug in der Nähe
 * 
 * @author OIL SS2025
 */
public class EinfacheLogikStrategie implements ZugStrategie {
    
    private final Random random;
    
    /**
     * Konstruktor mit Zufallsgenerator
     */
    public EinfacheLogikStrategie() {
        this.random = new Random();
    }
    
    /**
     * Konstruktor mit Seed für deterministische Tests
     * 
     * @param seed Der Seed für den Zufallsgenerator
     */
    public EinfacheLogikStrategie(long seed) {
        this.random = new Random(seed);
    }
    
    @Override
    public Zug berechneZug(Spielbrett brett, Spieler spieler, Spieler gegner) {
        if (brett == null || spieler == null || !brett.hatFreieFelder()) {
            return null;
        }
        
        char eigenesSymbol = spieler.getSymbol();
        char gegnerSymbol = (gegner != null) ? gegner.getSymbol() : '?';
        
        // 1. Priorität: Gewinnen (4 zu 5 vervollständigen)
        Zug gewinnZug = findeGewinnZug(brett, eigenesSymbol, spieler);
        if (gewinnZug != null) {
            return gewinnZug;
        }
        
        // 2. Priorität: Gegner blockieren (4er-Reihe verhindern)
        if (gegner != null) {
            Zug blockZug = findeGewinnZug(brett, gegnerSymbol, spieler);
            if (blockZug != null) {
                return blockZug;
            }
        }
        
        // 3. Priorität: Eigene 3er-Reihe zu 4er erweitern
        Zug angriffZug = findeAngriffZug(brett, eigenesSymbol, spieler, 3);
        if (angriffZug != null) {
            return angriffZug;
        }
        
        // 4. Priorität: Gegner 3er-Reihe blockieren
        if (gegner != null) {
            Zug gegnerBlock = findeAngriffZug(brett, gegnerSymbol, spieler, 3);
            if (gegnerBlock != null) {
                return gegnerBlock;
            }
        }
        
        // 5. Priorität: Eigene 2er-Reihe erweitern
        Zug aufbauZug = findeAngriffZug(brett, eigenesSymbol, spieler, 2);
        if (aufbauZug != null) {
            return aufbauZug;
        }
        
        // 6. Fallback: Zufälliger Zug in der Nähe
        return macheZufallsZug(brett, spieler);
    }
    
    /**
     * Findet einen Zug, der eine 4er-Reihe zu einer 5er-Reihe (Gewinn) vervollständigt
     * 
     * @param brett Das Spielbrett
     * @param symbol Das zu suchende Symbol
     * @param spieler Der Spieler für den Zug
     * @return Ein Gewinnzug oder null
     */
    private Zug findeGewinnZug(Spielbrett brett, char symbol, Spieler spieler) {
        List<Position> freiePositionen = brett.getFreiePositionen();
        
        for (Position pos : freiePositionen) {
            // Simuliere den Zug
            if (wuerdeGewinnBringen(brett, pos.getX(), pos.getY(), symbol)) {
                return new Zug(pos.getX(), pos.getY(), spieler, brett.getAnzahlZuege() + 1);
            }
        }
        
        return null;
    }
    
    /**
     * Findet einen Zug, der eine bestehende Reihe erweitert
     * 
     * @param brett Das Spielbrett
     * @param symbol Das zu suchende Symbol
     * @param spieler Der Spieler für den Zug
     * @param minLaenge Minimale Reihenlänge die erweitert werden soll
     * @return Ein Angriffszug oder null
     */
    private Zug findeAngriffZug(Spielbrett brett, char symbol, Spieler spieler, int minLaenge) {
        List<Position> freiePositionen = brett.getFreiePositionenInNaehe(2);
        List<Zug> moeglicheZuege = new ArrayList<>();
        
        for (Position pos : freiePositionen) {
            int maxReihe = getMaxReihenlaenge(brett, pos.getX(), pos.getY(), symbol);
            if (maxReihe >= minLaenge) {
                moeglicheZuege.add(new Zug(pos.getX(), pos.getY(), spieler, brett.getAnzahlZuege() + 1));
            }
        }
        
        // Besten Zug auswählen (zufällig aus den besten)
        if (!moeglicheZuege.isEmpty()) {
            return moeglicheZuege.get(random.nextInt(moeglicheZuege.size()));
        }
        
        return null;
    }
    
    /**
     * Überprüft, ob ein Zug an der Position zu einem Gewinn führen würde
     * 
     * @param brett Das Spielbrett
     * @param x X-Koordinate
     * @param y Y-Koordinate
     * @param symbol Das Symbol
     * @return true wenn der Zug zum Gewinn führt
     */
    private boolean wuerdeGewinnBringen(Spielbrett brett, int x, int y, char symbol) {
        // Alle vier Richtungen prüfen
        int[][] richtungen = {
            {1, 0},   // horizontal
            {0, 1},   // vertikal
            {1, 1},   // diagonal \
            {1, -1}   // diagonal /
        };
        
        for (int[] richtung : richtungen) {
            int count = 1; // Der Zug selbst zählt mit
            
            // In positive Richtung zählen
            count += zaehleSymboleInRichtung(brett, x, y, richtung[0], richtung[1], symbol);
            
            // In negative Richtung zählen
            count += zaehleSymboleInRichtung(brett, x, y, -richtung[0], -richtung[1], symbol);
            
            if (count >= 5) {
                return true;
            }
        }
        
        return false;
    }
    
    /**
     * Ermittelt die maximale Reihenlänge, die durch einen Zug an der Position entstehen würde
     * 
     * @param brett Das Spielbrett
     * @param x X-Koordinate
     * @param y Y-Koordinate
     * @param symbol Das Symbol
     * @return Die maximale Reihenlänge
     */
    private int getMaxReihenlaenge(Spielbrett brett, int x, int y, char symbol) {
        int maxLaenge = 0;
        
        // Alle vier Richtungen prüfen
        int[][] richtungen = {
            {1, 0},   // horizontal
            {0, 1},   // vertikal
            {1, 1},   // diagonal \
            {1, -1}   // diagonal /
        };
        
        for (int[] richtung : richtungen) {
            int count = 1; // Der Zug selbst zählt mit
            
            // In positive Richtung zählen
            count += zaehleSymboleInRichtung(brett, x, y, richtung[0], richtung[1], symbol);
            
            // In negative Richtung zählen
            count += zaehleSymboleInRichtung(brett, x, y, -richtung[0], -richtung[1], symbol);
            
            maxLaenge = Math.max(maxLaenge, count);
        }
        
        return maxLaenge;
    }
    
    /**
     * Zählt Symbole in einer bestimmten Richtung von einer Position aus
     * 
     * @param brett Das Spielbrett
     * @param startX Start X-Koordinate
     * @param startY Start Y-Koordinate
     * @param dx Richtung X
     * @param dy Richtung Y
     * @param symbol Das zu zählende Symbol
     * @return Anzahl aufeinanderfolgender Symbole
     */
    private int zaehleSymboleInRichtung(Spielbrett brett, int startX, int startY, 
                                       int dx, int dy, char symbol) {
        int count = 0;
        int x = startX + dx;
        int y = startY + dy;
        
        while (x >= 0 && x < brett.getGroesse() && y >= 0 && y < brett.getGroesse() && 
               brett.getFeld(x, y) == symbol) {
            count++;
            x += dx;
            y += dy;
        }
        
        return count;
    }
    
    /**
     * Macht einen zufälligen Zug in der Nähe belegter Felder
     * 
     * @param brett Das Spielbrett
     * @param spieler Der Spieler
     * @return Ein zufälliger Zug
     */
    private Zug macheZufallsZug(Spielbrett brett, Spieler spieler) {
        List<Position> moeglichePositionen = brett.getFreiePositionenInNaehe(2);
        
        if (moeglichePositionen.isEmpty()) {
            moeglichePositionen = brett.getFreiePositionen();
        }
        
        if (moeglichePositionen.isEmpty()) {
            return null;
        }
        
        Position gewaehltePosition = moeglichePositionen.get(random.nextInt(moeglichePositionen.size()));
        return new Zug(gewaehltePosition.getX(), gewaehltePosition.getY(), 
                      spieler, brett.getAnzahlZuege() + 1);
    }
    
    @Override
    public String getName() {
        return "Einfache Logik (S2)";
    }
    
    @Override
    public String getBeschreibung() {
        return "Versucht zu gewinnen, blockiert Gegner und baut strategische Positionen auf";
    }
    
    @Override
    public int getSchwierigkeitsstufe() {
        return 2;
    }
    
    @Override
    public void initialisiere(Object... parameter) {
        if (parameter.length > 0 && parameter[0] instanceof Long) {
            random.setSeed((Long) parameter[0]);
        }
    }
    
    @Override
    public void zuruecksetzen() {
        // Für einfache Logik nichts zu tun
    }
} 