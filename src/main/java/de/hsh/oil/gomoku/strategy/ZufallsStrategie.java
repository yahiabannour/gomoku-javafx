package de.hsh.oil.gomoku.strategy;

import de.hsh.oil.gomoku.model.Spielbrett;
import de.hsh.oil.gomoku.model.Spieler;
import de.hsh.oil.gomoku.model.Zug;
import de.hsh.oil.gomoku.model.Spielbrett.Position;

import java.util.List;
import java.util.Random;

/**
 * S1 - Zufallsstrategie für KI-Spieler
 * 
 * Macht zufällige Züge, aber eingeschränkt auf Felder in der Nähe bereits
 * belegter Positionen, um sinnvolle Züge zu gewährleisten.
 * 
 * @author OIL SS2025
 */
public class ZufallsStrategie implements ZugStrategie {
    
    private final Random random;
    private final int suchRadius;
    
    /**
     * Konstruktor mit Standard-Suchradius
     */
    public ZufallsStrategie() {
        this(2);
    }
    
    /**
     * Konstruktor mit angegebenem Suchradius
     * 
     * @param suchRadius Der Radius um belegte Felder für die Zufallsauswahl
     */
    public ZufallsStrategie(int suchRadius) {
        this.random = new Random();
        this.suchRadius = Math.max(1, suchRadius);
    }
    
    /**
     * Konstruktor mit Seed für deterministische Tests
     * 
     * @param seed Der Seed für den Zufallsgenerator
     * @param suchRadius Der Suchradius
     */
    public ZufallsStrategie(long seed, int suchRadius) {
        this.random = new Random(seed);
        this.suchRadius = Math.max(1, suchRadius);
    }
    
    @Override
    public Zug berechneZug(Spielbrett brett, Spieler spieler, Spieler gegner) {
        if (brett == null || spieler == null || !brett.hatFreieFelder()) {
            return null;
        }
        
        // Freie Positionen in der Nähe belegter Felder suchen
        List<Position> moeglichePositionen = brett.getFreiePositionenInNaehe(suchRadius);
        
        // Falls keine Positionen in der Nähe gefunden, alle freien Positionen nutzen
        if (moeglichePositionen.isEmpty()) {
            moeglichePositionen = brett.getFreiePositionen();
        }
        
        // Falls immer noch keine Positionen verfügbar
        if (moeglichePositionen.isEmpty()) {
            return null;
        }
        
        // Zufällige Position auswählen
        Position gewaehltePosition = moeglichePositionen.get(random.nextInt(moeglichePositionen.size()));
        
        // Zug erstellen
        return new Zug(gewaehltePosition.getX(), gewaehltePosition.getY(), 
                      spieler, brett.getAnzahlZuege() + 1);
    }
    
    @Override
    public String getName() {
        return "Zufallsstrategie (S1)";
    }
    
    @Override
    public String getBeschreibung() {
        return "Macht zufällige Züge in der Nähe belegter Felder (Radius: " + suchRadius + ")";
    }
    
    @Override
    public int getSchwierigkeitsstufe() {
        return 1;
    }
    
    @Override
    public void initialisiere(Object... parameter) {
        // Optionaler neuer Seed
        if (parameter.length > 0 && parameter[0] instanceof Long) {
            random.setSeed((Long) parameter[0]);
        }
    }
    
    @Override
    public void zuruecksetzen() {
        // Für Zufallsstrategie nichts zu tun
    }
    
    /**
     * Setzt einen neuen Seed für den Zufallsgenerator
     * 
     * @param seed Der neue Seed
     */
    public void setSeed(long seed) {
        random.setSeed(seed);
    }
    
    /**
     * Gibt den aktuellen Suchradius zurück
     * 
     * @return Der Suchradius
     */
    public int getSuchRadius() {
        return suchRadius;
    }
} 