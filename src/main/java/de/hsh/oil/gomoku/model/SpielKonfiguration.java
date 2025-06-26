package de.hsh.oil.gomoku.model;

/**
 * Konfigurationsklasse für das Gomoku-Spiel
 * Enthält alle notwendigen Einstellungen für ein Spiel
 * 
 * @author OIL SS2025
 */
public class SpielKonfiguration {
    
    private SpielModus spielModus = SpielModus.PLAYER_VS_PLAYER;
    private DebugModus debugModus = DebugModus.KEIN;
    private KiStrategie strategie1;
    private KiStrategie strategie2;
    private boolean guiModus = false;
    private int bretGroesse = 15;
    private String debugDatei = "gomoku_debug.txt";
    
    /**
     * Standard-Konstruktor mit Defaultwerten
     */
    public SpielKonfiguration() {
        // Standardwerte sind bereits gesetzt
    }
    
    /**
     * Konstruktor mit vollständiger Konfiguration
     * 
     * @param spielModus Der Spielmodus
     * @param debugModus Der Debug-Modus
     * @param strategie1 Die Strategie für Spieler 1 (bei Computer-Spieler)
     * @param strategie2 Die Strategie für Spieler 2 (bei Computer-Spieler)
     */
    public SpielKonfiguration(SpielModus spielModus, DebugModus debugModus, 
                             KiStrategie strategie1, KiStrategie strategie2) {
        this.spielModus = spielModus;
        this.debugModus = debugModus;
        this.strategie1 = strategie1;
        this.strategie2 = strategie2;
    }
    
    // Getter und Setter
    
    public SpielModus getSpielModus() {
        return spielModus;
    }
    
    public void setSpielModus(SpielModus spielModus) {
        this.spielModus = spielModus;
    }
    
    public DebugModus getDebugModus() {
        return debugModus;
    }
    
    public void setDebugModus(DebugModus debugModus) {
        this.debugModus = debugModus;
    }
    
    public KiStrategie getStrategie1() {
        return strategie1;
    }
    
    public void setStrategie1(KiStrategie strategie1) {
        this.strategie1 = strategie1;
    }
    
    public KiStrategie getStrategie2() {
        return strategie2;
    }
    
    public void setStrategie2(KiStrategie strategie2) {
        this.strategie2 = strategie2;
    }
    
    public boolean isGuiModus() {
        return guiModus;
    }
    
    public void setGuiModus(boolean guiModus) {
        this.guiModus = guiModus;
    }
    
    public int getBretGroesse() {
        return bretGroesse;
    }
    
    public void setBretGroesse(int bretGroesse) {
        this.bretGroesse = bretGroesse;
    }
    
    public String getDebugDatei() {
        return debugDatei;
    }
    
    public void setDebugDatei(String debugDatei) {
        this.debugDatei = debugDatei;
    }
    
    /**
     * Überprüft, ob die Konfiguration gültig ist
     * 
     * @return true wenn gültig, false sonst
     */
    public boolean istGueltig() {
        // Basis-Validierung
        if (spielModus == null) {
            return false;
        }
        
        // Bei Computer-Modi müssen Strategien vorhanden sein
        switch (spielModus) {
            case PLAYER_VS_COMPUTER:
                return strategie1 != null || strategie2 != null;
            case COMPUTER_VS_COMPUTER:
                return strategie1 != null && strategie2 != null;
            case PLAYER_VS_PLAYER:
                // Bei PvP sollten keine Strategien gesetzt sein
                return true;
            default:
                return false;
        }
    }
    
    @Override
    public String toString() {
        return String.format("SpielKonfiguration{modus=%s, debug=%s, s1=%s, s2=%s, gui=%s}", 
                           spielModus, debugModus, strategie1, strategie2, guiModus);
    }
} 