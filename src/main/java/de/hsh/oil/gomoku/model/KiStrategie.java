package de.hsh.oil.gomoku.model;

/**
 * Enumeration für die verschiedenen KI-Strategien
 * 
 * @author OIL SS2025
 */
public enum KiStrategie {
    /**
     * Zufällige Züge (eingeschränkt)
     */
    ZUFALL("S1", "Zufällige Züge"),
    
    /**
     * Einfache Logik (Blocken)
     */
    EINFACHE_LOGIK("S2", "Einfache Logik"),
    
    /**
     * Minimax-Algorithmus
     */
    MINIMAX("S3", "Minimax-Algorithmus");
    
    private final String code;
    private final String beschreibung;
    
    KiStrategie(String code, String beschreibung) {
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
     * Findet eine KiStrategie anhand des Codes
     * 
     * @param code Der Code der KiStrategie
     * @return Die entsprechende KiStrategie oder null
     */
    public static KiStrategie vonCode(String code) {
        for (KiStrategie strategie : values()) {
            if (strategie.code.equalsIgnoreCase(code)) {
                return strategie;
            }
        }
        return null;
    }
} 