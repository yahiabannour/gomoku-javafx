package de.hsh.oil.gomoku.strategy;

import de.hsh.oil.gomoku.model.Spielbrett;
import de.hsh.oil.gomoku.model.Spieler;
import de.hsh.oil.gomoku.model.Zug;
import de.hsh.oil.gomoku.model.Spielbrett.Position;

import java.util.List;

/**
 * S3 - Minimax-Strategie mit Alpha-Beta-Pruning für KI-Spieler
 * 
 * Implementiert den Minimax-Algorithmus mit Alpha-Beta-Pruning zur Optimierung.
 * Verwendet eine Bewertungsfunktion die Stellungen nach strategischem Wert bewertet.
 * 
 * @author OIL SS2025
 */
public class MinimaxStrategie implements ZugStrategie {
    
    private static final int MAX_TIEFE = 4;
    private static final int GEWINN_WERT = 10000;
    private static final int VERLUST_WERT = -10000;
    
    // Bewertungswerte für verschiedene Reihenlängen
    private static final int[] REIHEN_WERTE = {0, 1, 10, 100, 1000, GEWINN_WERT};
    
    private final int suchTiefe;
    private long berechnungsZeit;
    private int evaluierteStellungen;
    
    /**
     * Konstruktor mit Standard-Suchtiefe
     */
    public MinimaxStrategie() {
        this(MAX_TIEFE);
    }
    
    /**
     * Konstruktor mit angegebener Suchtiefe
     * 
     * @param suchTiefe Die maximale Suchtiefe
     */
    public MinimaxStrategie(int suchTiefe) {
        this.suchTiefe = Math.max(1, Math.min(suchTiefe, 6));
        this.berechnungsZeit = 0;
        this.evaluierteStellungen = 0;
    }
    
    @Override
    public Zug berechneZug(Spielbrett brett, Spieler spieler, Spieler gegner) {
        if (brett == null || spieler == null || !brett.hatFreieFelder()) {
            return null;
        }
        
        long startZeit = System.currentTimeMillis();
        evaluierteStellungen = 0;
        
        // Erste Züge optimieren - in der Mitte beginnen
        if (brett.getAnzahlZuege() == 0) {
            int mitte = brett.getGroesse() / 2;
            berechnungsZeit = System.currentTimeMillis() - startZeit;
            Zug erstZug = new Zug(mitte, mitte, spieler, 1);
            erstZug.setDebugInfo(String.format("Zeit: %dms, Evaluationen: %d", 
                berechnungsZeit, evaluierteStellungen));
            return erstZug;
        }
        
        // Minimax mit Alpha-Beta-Pruning ausführen
        MinimaxResult result = minimax(brett, suchTiefe, Integer.MIN_VALUE, Integer.MAX_VALUE, 
                                     true, spieler, gegner);
        
        berechnungsZeit = System.currentTimeMillis() - startZeit;
        
        // Debug-Info zum Zug hinzufügen
        if (result.besterZug != null) {
            result.besterZug.setDebugInfo(String.format("Zeit: %dms, Evaluationen: %,d", 
                berechnungsZeit, evaluierteStellungen));
        }
        
        return result.besterZug;
    }
    
    /**
     * Minimax-Algorithmus mit Alpha-Beta-Pruning
     * 
     * @param brett Das aktuelle Spielbrett
     * @param tiefe Die verbleibende Suchtiefe
     * @param alpha Alpha-Wert für Pruning
     * @param beta Beta-Wert für Pruning
     * @param maximierend Ob der aktuelle Spieler maximiert
     * @param spieler Der maximierende Spieler
     * @param gegner Der minimierende Spieler
     * @return Das Minimax-Ergebnis
     */
    private MinimaxResult minimax(Spielbrett brett, int tiefe, int alpha, int beta, 
                                 boolean maximierend, Spieler spieler, Spieler gegner) {
        evaluierteStellungen++;
        
        // Terminal-Zustand oder maximale Tiefe erreicht
        if (tiefe == 0 || brett.istSpielBeendet()) {
            int bewertung = evaluiereStellung(brett, spieler, gegner);
            return new MinimaxResult(bewertung, null);
        }
        
        Zug besterZug = null;
        
        if (maximierend) {
            int maxEval = Integer.MIN_VALUE;
            
            // Alle möglichen Züge durchgehen (begrenzt auf sinnvolle Positionen)
            List<Position> moeglichePositionen = brett.getFreiePositionenInNaehe(2);
            if (moeglichePositionen.isEmpty()) {
                moeglichePositionen = brett.getFreiePositionen();
            }
            
            for (Position pos : moeglichePositionen) {
                // Zug simulieren
                Zug testZug = new Zug(pos.getX(), pos.getY(), spieler, brett.getAnzahlZuege() + 1);
                brett.macheZug(testZug);
                
                // Rekursiver Aufruf
                MinimaxResult result = minimax(brett, tiefe - 1, alpha, beta, false, spieler, gegner);
                
                // Zug rückgängig machen
                brett.nimmZugZurueck();
                
                // Besten Zug aktualisieren
                if (result.bewertung > maxEval) {
                    maxEval = result.bewertung;
                    besterZug = testZug;
                }
                
                // Alpha-Beta-Pruning
                alpha = Math.max(alpha, result.bewertung);
                if (beta <= alpha) {
                    break; // Beta-Cutoff
                }
            }
            
            return new MinimaxResult(maxEval, besterZug);
            
        } else {
            int minEval = Integer.MAX_VALUE;
            
            List<Position> moeglichePositionen = brett.getFreiePositionenInNaehe(2);
            if (moeglichePositionen.isEmpty()) {
                moeglichePositionen = brett.getFreiePositionen();
            }
            
            for (Position pos : moeglichePositionen) {
                // Zug simulieren
                Zug testZug = new Zug(pos.getX(), pos.getY(), gegner, brett.getAnzahlZuege() + 1);
                brett.macheZug(testZug);
                
                // Rekursiver Aufruf
                MinimaxResult result = minimax(brett, tiefe - 1, alpha, beta, true, spieler, gegner);
                
                // Zug rückgängig machen
                brett.nimmZugZurueck();
                
                // Besten Zug aktualisieren
                if (result.bewertung < minEval) {
                    minEval = result.bewertung;
                    besterZug = testZug;
                }
                
                // Alpha-Beta-Pruning
                beta = Math.min(beta, result.bewertung);
                if (beta <= alpha) {
                    break; // Alpha-Cutoff
                }
            }
            
            return new MinimaxResult(minEval, besterZug);
        }
    }
    
    /**
     * Evaluiert eine Spielstellung
     * 
     * @param brett Das zu bewertende Brett
     * @param spieler Der maximierende Spieler
     * @param gegner Der minimierende Spieler
     * @return Die Bewertung der Stellung
     */
    private int evaluiereStellung(Spielbrett brett, Spieler spieler, Spieler gegner) {
        // Sofortiger Gewinn/Verlust
        if (brett.getGewinner() == spieler) {
            return GEWINN_WERT;
        }
        if (brett.getGewinner() == gegner) {
            return VERLUST_WERT;
        }
        
        // Stellungsbewertung basierend auf Reihen
        int spielerBewertung = bewerteReihenfuerSpieler(brett, spieler.getSymbol());
        int gegnerBewertung = bewerteReihenfuerSpieler(brett, gegner.getSymbol());
        
        return spielerBewertung - gegnerBewertung;
    }
    
    /**
     * Bewertet alle Reihen für einen Spieler
     * 
     * @param brett Das Spielbrett
     * @param symbol Das Symbol des Spielers
     * @return Die Gesamtbewertung
     */
    private int bewerteReihenfuerSpieler(Spielbrett brett, char symbol) {
        int gesamtBewertung = 0;
        
        // Alle Positionen durchgehen
        for (int y = 0; y < brett.getGroesse(); y++) {
            for (int x = 0; x < brett.getGroesse(); x++) {
                if (brett.getFeld(x, y) == symbol) {
                    gesamtBewertung += bewertePositionFuerSpieler(brett, x, y, symbol);
                }
            }
        }
        
        return gesamtBewertung;
    }
    
    /**
     * Bewertet eine Position für einen Spieler
     * 
     * @param brett Das Spielbrett
     * @param x X-Koordinate
     * @param y Y-Koordinate
     * @param symbol Das Symbol des Spielers
     * @return Die Bewertung der Position
     */
    private int bewertePositionFuerSpieler(Spielbrett brett, int x, int y, char symbol) {
        int bewertung = 0;
        
        // Alle vier Richtungen prüfen
        int[][] richtungen = {
            {1, 0},   // horizontal
            {0, 1},   // vertikal
            {1, 1},   // diagonal \
            {1, -1}   // diagonal /
        };
        
        for (int[] richtung : richtungen) {
            int reihenlaenge = berechneReihenlaenge(brett, x, y, richtung[0], richtung[1], symbol);
            if (reihenlaenge > 1) {
                bewertung += getReihenWert(reihenlaenge);
            }
        }
        
        return bewertung;
    }
    
    /**
     * Berechnet die Länge einer Reihe in eine Richtung
     * 
     * @param brett Das Spielbrett
     * @param startX Start X-Koordinate
     * @param startY Start Y-Koordinate
     * @param dx Richtung X
     * @param dy Richtung Y
     * @param symbol Das Symbol
     * @return Die Reihenlänge
     */
    private int berechneReihenlaenge(Spielbrett brett, int startX, int startY, 
                                   int dx, int dy, char symbol) {
        int laenge = 1; // Startposition zählt mit
        
        // In positive Richtung zählen
        int x = startX + dx;
        int y = startY + dy;
        while (x >= 0 && x < brett.getGroesse() && y >= 0 && y < brett.getGroesse() && 
               brett.getFeld(x, y) == symbol) {
            laenge++;
            x += dx;
            y += dy;
        }
        
        // In negative Richtung zählen
        x = startX - dx;
        y = startY - dy;
        while (x >= 0 && x < brett.getGroesse() && y >= 0 && y < brett.getGroesse() && 
               brett.getFeld(x, y) == symbol) {
            laenge++;
            x -= dx;
            y -= dy;
        }
        
        return laenge;
    }
    
    /**
     * Gibt den Bewertungswert für eine Reihenlänge zurück
     * 
     * @param laenge Die Reihenlänge
     * @return Der Bewertungswert
     */
    private int getReihenWert(int laenge) {
        if (laenge >= REIHEN_WERTE.length) {
            return REIHEN_WERTE[REIHEN_WERTE.length - 1];
        }
        return REIHEN_WERTE[laenge];
    }
    
    @Override
    public String getName() {
        return "Minimax mit Alpha-Beta (S3)";
    }
    
    @Override
    public String getBeschreibung() {
        return String.format("Minimax-Algorithmus mit Alpha-Beta-Pruning (Tiefe: %d)", suchTiefe);
    }
    
    @Override
    public int getSchwierigkeitsstufe() {
        return 3;
    }
    
    @Override
    public void initialisiere(Object... parameter) {
        // Kann für verschiedene Schwierigkeitsgrade erweitert werden
    }
    
    @Override
    public void zuruecksetzen() {
        berechnungsZeit = 0;
        evaluierteStellungen = 0;
    }
    
    // Getter für Statistiken
    
    public long getBerechnungsZeit() {
        return berechnungsZeit;
    }
    
    public int getEvaluierteStellungen() {
        return evaluierteStellungen;
    }
    
    public int getSuchTiefe() {
        return suchTiefe;
    }
    
    /**
     * Hilfsklasse für Minimax-Ergebnisse
     */
    private static class MinimaxResult {
        final int bewertung;
        final Zug besterZug;
        
        MinimaxResult(int bewertung, Zug besterZug) {
            this.bewertung = bewertung;
            this.besterZug = besterZug;
        }
    }
} 