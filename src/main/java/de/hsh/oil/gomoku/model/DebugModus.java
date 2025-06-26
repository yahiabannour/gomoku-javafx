package de.hsh.oil.gomoku.model;

/**
 * Enumeration für die verschiedenen Debug-Modi
 * 
 * @author OIL SS2025
 */
public enum DebugModus {
    /**
     * Kein Debug-Modus
     */
    KEIN("", "Kein Debug"),
    
    /**
     * Debug-Ausgabe auf der Konsole
     */
    KONSOLE("D", "Debug auf Konsole"),
    
    /**
     * Debug-Ausgabe in ASCII-Datei
     */
    DATEI("DF", "Debug in Datei");
    
    private final String code;
    private final String beschreibung;
    
    DebugModus(String code, String beschreibung) {
        this.code = code;
        this.beschreibung = beschreibung;
    }
    
    public String getCode() {
        return code;
    }
    
    public String getBeschreibung() {
        return beschreibung;
    }
    
    /**
     * Findet einen DebugModus anhand des Codes
     * 
     * @param code Der Code des DebugModus
     * @return Der entsprechende DebugModus oder null
     */
    public static DebugModus vonCode(String code) {
        for (DebugModus modus : values()) {
            if (modus.code.equalsIgnoreCase(code)) {
                return modus;
            }
        }
        return null;
    }
} 