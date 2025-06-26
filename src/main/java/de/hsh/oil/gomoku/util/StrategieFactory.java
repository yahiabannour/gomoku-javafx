package de.hsh.oil.gomoku.util;

import de.hsh.oil.gomoku.model.KiStrategie;
import de.hsh.oil.gomoku.strategy.ZugStrategie;
import de.hsh.oil.gomoku.strategy.ZufallsStrategie;
import de.hsh.oil.gomoku.strategy.EinfacheLogikStrategie;
import de.hsh.oil.gomoku.strategy.MinimaxStrategie;

/**
 * Factory-Klasse für die Erstellung von KI-Strategien
 * 
 * Implementiert das Factory Pattern zur Erstellung der verschiedenen
 * KI-Strategien basierend auf der KiStrategie-Enumeration
 * 
 * @author OIL SS2025
 */
public class StrategieFactory {
    
    /**
     * Erstellt eine KI-Strategie basierend auf der angegebenen Enumeration
     * 
     * @param kiStrategie Die gewünschte KI-Strategie
     * @return Eine Instanz der entsprechenden Strategie oder null bei Fehler
     */
    public static ZugStrategie erstelleStrategie(KiStrategie kiStrategie) {
        if (kiStrategie == null) {
            return null;
        }
        
        switch (kiStrategie) {
            case ZUFALL:
                return new ZufallsStrategie();
                
            case EINFACHE_LOGIK:
                return new EinfacheLogikStrategie();
                
            case MINIMAX:
                return new MinimaxStrategie();
                
            default:
                throw new IllegalArgumentException("Unbekannte KI-Strategie: " + kiStrategie);
        }
    }
    
    /**
     * Erstellt eine KI-Strategie mit spezifischen Parametern
     * 
     * @param kiStrategie Die gewünschte KI-Strategie
     * @param parameter Spezifische Parameter für die Strategie
     * @return Eine Instanz der entsprechenden Strategie oder null bei Fehler
     */
    public static ZugStrategie erstelleStrategie(KiStrategie kiStrategie, Object... parameter) {
        if (kiStrategie == null) {
            return null;
        }
        
        ZugStrategie strategie;
        
        switch (kiStrategie) {
            case ZUFALL:
                if (parameter.length >= 2 && parameter[0] instanceof Long && parameter[1] instanceof Integer) {
                    strategie = new ZufallsStrategie((Long) parameter[0], (Integer) parameter[1]);
                } else if (parameter.length >= 1 && parameter[0] instanceof Integer) {
                    strategie = new ZufallsStrategie((Integer) parameter[0]);
                } else {
                    strategie = new ZufallsStrategie();
                }
                break;
                
            case EINFACHE_LOGIK:
                if (parameter.length >= 1 && parameter[0] instanceof Long) {
                    strategie = new EinfacheLogikStrategie((Long) parameter[0]);
                } else {
                    strategie = new EinfacheLogikStrategie();
                }
                break;
                
            case MINIMAX:
                if (parameter.length >= 1 && parameter[0] instanceof Integer) {
                    strategie = new MinimaxStrategie((Integer) parameter[0]);
                } else {
                    strategie = new MinimaxStrategie();
                }
                break;
                
            default:
                throw new IllegalArgumentException("Unbekannte KI-Strategie: " + kiStrategie);
        }
        
        // Strategie mit Parametern initialisieren
        if (strategie != null && parameter.length > 0) {
            strategie.initialisiere(parameter);
        }
        
        return strategie;
    }
    
    /**
     * Erstellt eine Standard-Zufallsstrategie mit Seed
     * 
     * @param seed Der Seed für deterministische Zufälle
     * @return Eine Zufallsstrategie mit dem angegebenen Seed
     */
    public static ZugStrategie erstelleZufallsStrategie(long seed) {
        return new ZufallsStrategie(seed, 2);
    }
    
    /**
     * Erstellt eine einfache Logik-Strategie mit Seed
     * 
     * @param seed Der Seed für deterministische Zufälle
     * @return Eine einfache Logik-Strategie mit dem angegebenen Seed
     */
    public static ZugStrategie erstelleEinfacheLogikStrategie(long seed) {
        return new EinfacheLogikStrategie(seed);
    }
    
    /**
     * Erstellt eine Minimax-Strategie mit angegebener Suchtiefe
     * 
     * @param suchTiefe Die maximale Suchtiefe
     * @return Eine Minimax-Strategie mit der angegebenen Suchtiefe
     */
    public static ZugStrategie erstelleMinimaxStrategie(int suchTiefe) {
        return new MinimaxStrategie(suchTiefe);
    }
    
    /**
     * Überprüft, ob eine KI-Strategie verfügbar ist
     * 
     * @param kiStrategie Die zu überprüfende Strategie
     * @return true wenn die Strategie verfügbar ist, false sonst
     */
    public static boolean istStrategieVerfuegbar(KiStrategie kiStrategie) {
        if (kiStrategie == null) {
            return false;
        }
        
        try {
            ZugStrategie strategie = erstelleStrategie(kiStrategie);
            return strategie != null;
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * Gibt alle verfügbaren KI-Strategien zurück
     * 
     * @return Array aller verfügbaren KI-Strategien
     */
    public static KiStrategie[] getVerfuegbareStrategien() {
        return KiStrategie.values();
    }
    
    /**
     * Gibt eine Beschreibung aller verfügbaren Strategien zurück
     * 
     * @return String mit Beschreibungen aller Strategien
     */
    public static String getBeschreibungAllerStrategien() {
        StringBuilder sb = new StringBuilder();
        sb.append("Verfügbare KI-Strategien:\n");
        
        for (KiStrategie strategie : KiStrategie.values()) {
            ZugStrategie instance = erstelleStrategie(strategie);
            if (instance != null) {
                sb.append(String.format("  %s: %s (Schwierigkeitsstufe %d)\n",
                                      strategie.getCode(),
                                      instance.getBeschreibung(),
                                      instance.getSchwierigkeitsstufe()));
            }
        }
        
        return sb.toString();
    }
} 