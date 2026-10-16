package de.hsh.oil.gomoku.model;

import javax.xml.bind.annotation.*;

/**
 * Repräsentiert Spielstatistiken für XML-Serialisierung
 * 
 * @author OIL SS2025
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class GameStatistics {
    
    @XmlElement
    private int player1Wins;
    
    @XmlElement
    private int player2Wins;
    
    @XmlElement
    private int totalGames;
    
    @XmlElement
    private int draws;
    
    @XmlElement
    private double averageGameLength;
    
    @XmlElement
    private String player1Name;
    
    @XmlElement
    private String player2Name;
    
    // Konstruktoren
    public GameStatistics() {}
    
    public GameStatistics(int player1Wins, int player2Wins, int totalGames, int draws) {
        this.player1Wins = player1Wins;
        this.player2Wins = player2Wins;
        this.totalGames = totalGames;
        this.draws = draws;
    }
    
    /**
     * Aktualisiert die Statistiken basierend auf den Spielern
     */
    public void updateFromPlayers(Spieler spieler1, Spieler spieler2) {
        this.player1Name = spieler1.getName();
        this.player2Name = spieler2.getName();
        this.player1Wins = spieler1.getSiege();
        this.player2Wins = spieler2.getSiege();
        this.totalGames = spieler1.getGespielteSpiele();
        this.draws = 0; // Gomoku hat normalerweise keine Unentschieden
        
        // Durchschnittliche Spiellänge berechnen
        if (totalGames > 0) {
            this.averageGameLength = (double) (spieler1.getGesamtZuege() + spieler2.getGesamtZuege()) / totalGames;
        }
    }
    
    // Getter und Setter
    public int getPlayer1Wins() { return player1Wins; }
    public void setPlayer1Wins(int player1Wins) { this.player1Wins = player1Wins; }
    
    public int getPlayer2Wins() { return player2Wins; }
    public void setPlayer2Wins(int player2Wins) { this.player2Wins = player2Wins; }
    
    public int getTotalGames() { return totalGames; }
    public void setTotalGames(int totalGames) { this.totalGames = totalGames; }
    
    public int getDraws() { return draws; }
    public void setDraws(int draws) { this.draws = draws; }
    
    public double getAverageGameLength() { return averageGameLength; }
    public void setAverageGameLength(double averageGameLength) { this.averageGameLength = averageGameLength; }
    
    public String getPlayer1Name() { return player1Name; }
    public void setPlayer1Name(String player1Name) { this.player1Name = player1Name; }
    
    public String getPlayer2Name() { return player2Name; }
    public void setPlayer2Name(String player2Name) { this.player2Name = player2Name; }
    
    @Override
    public String toString() {
        return String.format("Statistics: %s (%d wins) vs %s (%d wins) - %d games total", 
                           player1Name, player1Wins, player2Name, player2Wins, totalGames);
    }
} 