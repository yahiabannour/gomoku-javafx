package de.hsh.oil.gomoku.strategy;

import de.hsh.oil.gomoku.model.Spielbrett;
import de.hsh.oil.gomoku.model.Spieler;
import de.hsh.oil.gomoku.model.Zug;

/**
 * Strategy Pattern Interface für KI-Strategien im Gomoku-Spiel
 * 
 * Definiert das Interface für verschiedene KI-Strategien:
 * - S1: Zufällige Züge (eingeschränkt)
 * - S2: Einfache Logik (Blocken)
 * - S3: Minimax-Algorithmus
 * 
 * @author OIL SS2025
 */
public interface ZugStrategie {
    
    /**
     * Berechnet den nächsten Zug basierend auf der implementierten Strategie
     * 
     * @param brett Das aktuelle Spielbrett
     * @param spieler Der Spieler, für den der Zug berechnet wird
     * @param gegner Der Gegenspieler
     * @return Der berechnete Zug oder null wenn kein Zug möglich ist
     */
    Zug berechneZug(Spielbrett brett, Spieler spieler, Spieler gegner);
    
    /**
     * Gibt den Namen der Strategie zurück
     * 
     * @return Der Name der Strategie
     */
    String getName();
    
    /**
     * Gibt eine Beschreibung der Strategie zurück
     * 
     * @return Eine Beschreibung der Strategie
     */
    String getBeschreibung();
    
    /**
     * Gibt die Schwierigkeitsstufe der Strategie zurück
     * 
     * @return Die Schwierigkeitsstufe (1-3)
     */
    int getSchwierigkeitsstufe();
    
    /**
     * Überprüft, ob die Strategie einen Zug auf dem gegebenen Brett machen kann
     * 
     * @param brett Das zu überprüfende Spielbrett
     * @return true wenn ein Zug möglich ist, false sonst
     */
    default boolean kannZugMachen(Spielbrett brett) {
        return brett != null && brett.hatFreieFelder();
    }
    
    /**
     * Initialisiert die Strategie mit spezifischen Parametern
     * Kann von Implementierungen überschrieben werden
     * 
     * @param parameter Spezifische Parameter für die Strategie
     */
    default void initialisiere(Object... parameter) {
        // Standard-Implementierung - nichts zu tun
    }
    
    /**
     * Setzt die Strategie zurück
     * Kann von Implementierungen überschrieben werden
     */
    default void zuruecksetzen() {
        // Standard-Implementierung - nichts zu tun
    }
} 