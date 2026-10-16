package de.hsh.oil.gomoku.model;

import javax.xml.bind.annotation.*;

/**
 * Repräsentiert einen gespeicherten Zug für XML-Serialisierung
 * 
 * @author OIL SS2025
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class SavedMove {
    
    @XmlElement(required = true)
    private int moveNumber;
    
    @XmlElement(required = true)
    private int playerIndex; // 0 = Spieler 1, 1 = Spieler 2
    
    @XmlElement(required = true)
    private int x;
    
    @XmlElement(required = true)
    private int y;
    
    @XmlElement
    private String timestamp;
    
    // Konstruktoren
    public SavedMove() {}
    
    public SavedMove(int moveNumber, int playerIndex, int x, int y, String timestamp) {
        this.moveNumber = moveNumber;
        this.playerIndex = playerIndex;
        this.x = x;
        this.y = y;
        this.timestamp = timestamp;
    }
    
    // Getter und Setter
    public int getMoveNumber() { return moveNumber; }
    public void setMoveNumber(int moveNumber) { this.moveNumber = moveNumber; }
    
    public int getPlayerIndex() { return playerIndex; }
    public void setPlayerIndex(int playerIndex) { this.playerIndex = playerIndex; }
    
    public int getX() { return x; }
    public void setX(int x) { this.x = x; }
    
    public int getY() { return y; }
    public void setY(int y) { this.y = y; }
    
    public String getTimestamp() { return timestamp; }
    public void setTimestamp(String timestamp) { this.timestamp = timestamp; }
    
    @Override
    public String toString() {
        return String.format("Move[%d] P%d: (%d,%d) at %s", 
                           moveNumber, playerIndex + 1, x, y, timestamp);
    }
} 