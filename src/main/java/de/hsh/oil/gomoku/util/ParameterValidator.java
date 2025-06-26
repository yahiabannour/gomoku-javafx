package de.hsh.oil.gomoku.util;

import de.hsh.oil.gomoku.model.*;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Validator für Kommandozeilenparameter mit regulären Ausdrücken
 * 
 * Validiert und parst die Parameter für das Gomoku-Spiel
 * Unterstützt beliebige Reihenfolge der Parameter
 * 
 * @author OIL SS2025
 */
public class ParameterValidator {
    
    // Regex-Patterns für die verschiedenen Parameter
    private static final Pattern SPIELMODUS_PATTERN = Pattern.compile("^(PP|PC|CC)$", Pattern.CASE_INSENSITIVE);
    private static final Pattern DEBUG_PATTERN = Pattern.compile("^(D|DF)$", Pattern.CASE_INSENSITIVE);
    private static final Pattern STRATEGIE_PATTERN = Pattern.compile("^(S[1-3])$", Pattern.CASE_INSENSITIVE);
    
    /**
     * Validiert und parst die Kommandozeilenparameter
     * 
     * @param args Die Kommandozeilenparameter
     * @return Eine gültige SpielKonfiguration oder null bei Fehlern
     */
    public SpielKonfiguration validiere(String[] args) {
        if (args == null || args.length == 0) {
            System.err.println("Fehler: Keine Parameter angegeben");
            return null;
        }
        
        SpielKonfiguration konfiguration = new SpielKonfiguration();
        List<KiStrategie> strategien = new ArrayList<>();
        
        // Parameter einzeln verarbeiten
        for (String arg : args) {
            if (arg == null || arg.trim().isEmpty()) {
                continue;
            }
            
            arg = arg.trim().toUpperCase();
            
            if (SPIELMODUS_PATTERN.matcher(arg).matches()) {
                // Spielmodus verarbeiten
                SpielModus modus = SpielModus.vonCode(arg);
                if (modus != null) {
                    konfiguration.setSpielModus(modus);
                } else {
                    System.err.println("Fehler: Unbekannter Spielmodus: " + arg);
                    return null;
                }
                
            } else if (DEBUG_PATTERN.matcher(arg).matches()) {
                // Debug-Modus verarbeiten
                DebugModus debug = DebugModus.vonCode(arg);
                if (debug != null) {
                    konfiguration.setDebugModus(debug);
                } else {
                    System.err.println("Fehler: Unbekannter Debug-Modus: " + arg);
                    return null;
                }
                
            } else if (STRATEGIE_PATTERN.matcher(arg).matches()) {
                // Strategie verarbeiten
                KiStrategie strategie = KiStrategie.vonCode(arg);
                if (strategie != null) {
                    strategien.add(strategie);
                } else {
                    System.err.println("Fehler: Unbekannte Strategie: " + arg);
                    return null;
                }
                
            } else {
                System.err.println("Fehler: Unbekannter Parameter: " + arg);
                zeigeMoeglicheParameter();
                return null;
            }
        }
        
        // Strategien zuweisen
        if (!strategien.isEmpty()) {
            konfiguration.setStrategie1(strategien.get(0));
            if (strategien.size() > 1) {
                konfiguration.setStrategie2(strategien.get(1));
            }
            if (strategien.size() > 2) {
                System.err.println("Warnung: Mehr als 2 Strategien angegeben, nur die ersten beiden werden verwendet");
            }
        }
        
        // Logik-Validierung
        if (!logikValidierung(konfiguration)) {
            return null;
        }
        
        return konfiguration;
    }
    
    /**
     * Überprüft die logische Konsistenz der Parameter
     * 
     * @param konfiguration Die zu überprüfende Konfiguration
     * @return true wenn logisch konsistent, false sonst
     */
    private boolean logikValidierung(SpielKonfiguration konfiguration) {
        SpielModus modus = konfiguration.getSpielModus();
        KiStrategie s1 = konfiguration.getStrategie1();
        KiStrategie s2 = konfiguration.getStrategie2();
        
        switch (modus) {
            case PLAYER_VS_PLAYER:
                // Bei PvP sollten keine Strategien angegeben werden
                if (s1 != null || s2 != null) {
                    System.err.println("Fehler: Bei Spieler vs Spieler (PP) sind keine KI-Strategien sinnvoll");
                    return false;
                }
                break;
                
            case PLAYER_VS_COMPUTER:
                // Bei PvC sollte genau eine Strategie angegeben werden
                if (s1 == null && s2 == null) {
                    System.err.println("Fehler: Bei Spieler vs Computer (PC) muss eine KI-Strategie angegeben werden");
                    return false;
                }
                if (s1 != null && s2 != null) {
                    System.err.println("Warnung: Bei Spieler vs Computer (PC) ist nur eine Strategie nötig, verwende erste Strategie");
                    konfiguration.setStrategie2(null);
                }
                break;
                
            case COMPUTER_VS_COMPUTER:
                // Bei CvC sollten zwei Strategien angegeben werden
                if (s1 == null || s2 == null) {
                    if (s1 == null && s2 == null) {
                        System.err.println("Fehler: Bei Computer vs Computer (CC) müssen zwei KI-Strategien angegeben werden");
                        return false;
                    }
                    if (s1 == null) {
                        System.err.println("Info: Nur eine Strategie angegeben, verwende diese für beide Computer");
                        konfiguration.setStrategie1(s2);
                    } else {
                        System.err.println("Info: Nur eine Strategie angegeben, verwende diese für beide Computer");
                        konfiguration.setStrategie2(s1);
                    }
                }
                break;
        }
        
        return true;
    }
    
    /**
     * Zeigt die möglichen Parameter an
     */
    private void zeigeMoeglicheParameter() {
        System.err.println("\nGültige Parameter:");
        System.err.println("  Spielmodi: PP, PC, CC");
        System.err.println("  Debug: D, DF");
        System.err.println("  Strategien: S1, S2, S3");
        System.err.println("  Beispiel: PC D S3");
    }
    
    /**
     * Überprüft, ob ein String einem Pattern entspricht
     * 
     * @param text Der zu überprüfende Text
     * @param pattern Das Pattern
     * @return true wenn der Text dem Pattern entspricht
     */
    public static boolean entsprichtPattern(String text, Pattern pattern) {
        return text != null && pattern.matcher(text).matches();
    }
    
    /**
     * Extrahiert alle gültigen Spielmodi aus einem Text-Array
     * 
     * @param args Die zu durchsuchenden Texte
     * @return Liste der gefundenen Spielmodi
     */
    public List<SpielModus> extrahiereSpielModi(String[] args) {
        List<SpielModus> modi = new ArrayList<>();
        for (String arg : args) {
            if (SPIELMODUS_PATTERN.matcher(arg).matches()) {
                SpielModus modus = SpielModus.vonCode(arg);
                if (modus != null) {
                    modi.add(modus);
                }
            }
        }
        return modi;
    }
} 