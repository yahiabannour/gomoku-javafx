package de.hsh.oil.gomoku.model;

/**
 * Enumeration für die verschiedenen Spielmodi
 * 
 * @author OIL SS2025
 */
public enum SpielModus {
    /**
     * Spieler gegen Spieler
     */
    PLAYER_VS_PLAYER("PP", "Spieler gegen Spieler"),
    
    /**
     * Spieler gegen Computer
     */
    PLAYER_VS_COMPUTER("PC", "Spieler gegen Computer"),
    
    /**
     * Computer gegen Computer
     */
    COMPUTER_VS_COMPUTER("CC", "Computer gegen Computer");
    
    private final String code;
    private final String beschreibung;
    
    SpielModus(String code, String beschreibung) {
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
     * Findet einen SpielModus anhand des Codes
     * 
     * @param code Der Code des SpielModus
     * @return Der entsprechende SpielModus oder null
     */
    public static SpielModus vonCode(String code) {
        for (SpielModus modus : values()) {
            if (modus.code.equalsIgnoreCase(code)) {
                return modus;
            }
        }
        return null;
    }
} 