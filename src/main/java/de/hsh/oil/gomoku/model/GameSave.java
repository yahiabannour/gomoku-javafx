package de.hsh.oil.gomoku.model;

import de.hsh.oil.gomoku.controller.SpielController;
import javax.xml.bind.annotation.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a saved game state for XML serialization
 * 
 * @author OIL SS2025
 */
@XmlRootElement(name = "gameSave")
@XmlAccessorType(XmlAccessType.FIELD)
public class GameSave {
    
    @XmlElement(required = true)
    private String saveId;
    
    @XmlElement(required = true)
    private String timestamp;
    
    @XmlElement(required = true)
    private String saveName;
    
    @XmlElement(required = true)
    private SpielModus spielModus;
    
    @XmlElement
    private KiStrategie strategie1;
    
    @XmlElement
    private KiStrategie strategie2;
    
    @XmlElement(required = true)
    private int boardSize;
    
    @XmlElement(required = true)
    private int currentPlayerIndex;
    
    @XmlElement(required = true)
    private boolean gameActive;
    
    @XmlElement
    private String winner;
    
    @XmlElement(required = true)
    private int totalMoves;
    
    @XmlElementWrapper(name = "boardState")
    @XmlElement(name = "row")
    private List<String> boardRows;
    
    @XmlElementWrapper(name = "moveHistory")
    @XmlElement(name = "move")
    private List<SavedMove> moves;
    
    @XmlElement
    private GameStatistics statistics;
    
    // Constructors
    public GameSave() {
        this.boardRows = new ArrayList<>();
        this.moves = new ArrayList<>();
        this.statistics = new GameStatistics();
    }
    
    public GameSave(String saveName) {
        this();
        this.saveId = generateSaveId();
        this.saveName = saveName;
        this.timestamp = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    }
    
    /**
     * Generates a unique save ID
     */
    private String generateSaveId() {
        return "save_" + System.currentTimeMillis();
    }
    
    /**
     * Creates a GameSave from the current game state
     */
    public static GameSave fromCurrentGame(SpielController controller, String saveName) {
        GameSave save = new GameSave(saveName);
        
        // Spielkonfiguration
        SpielKonfiguration config = controller.getKonfiguration();
        save.setSpielModus(config.getSpielModus());
        save.setStrategie1(config.getStrategie1());
        save.setStrategie2(config.getStrategie2());
        
        // Spielzustand
        Spielbrett brett = controller.getBrett();
        save.setBoardSize(brett.getGroesse());
        save.setCurrentPlayerIndex(controller.getAktuellerSpieler() == controller.getSpieler1() ? 0 : 1);
        save.setGameActive(controller.istSpielAktiv());
        save.setTotalMoves(brett.getAnzahlZuege());
        
        // Gewinner
        if (!controller.istSpielAktiv() && brett.getGewinner() != null) {
            save.setWinner(brett.getGewinner().getName());
        }
        
        // Brett-Zustand serialisieren
        save.serializeBoardState(brett);
        
        // Zug-Historie serialisieren
        save.serializeMoveHistory(brett.getZugHistorie());
        
        // Statistiken
        save.getStatistics().updateFromPlayers(controller.getSpieler1(), controller.getSpieler2());
        
        return save;
    }
    
    /**
     * Serializes the board state into string format
     */
    private void serializeBoardState(Spielbrett brett) {
        boardRows.clear();
        for (int y = 0; y < brett.getGroesse(); y++) {
            StringBuilder row = new StringBuilder();
            for (int x = 0; x < brett.getGroesse(); x++) {
                row.append(brett.getFeld(x, y));
            }
            boardRows.add(row.toString());
        }
    }
    
    /**
     * Serializes the move history
     */
    private void serializeMoveHistory(List<Zug> zugHistorie) {
        moves.clear();
        for (int i = 0; i < zugHistorie.size(); i++) {
            Zug zug = zugHistorie.get(i);
            SavedMove savedMove = new SavedMove();
            savedMove.setMoveNumber(i + 1);
            savedMove.setPlayerIndex(i % 2); // Abwechselnd 0 und 1
            savedMove.setX(zug.getX());
            savedMove.setY(zug.getY());
            savedMove.setTimestamp(zug.getZeitpunkt().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
            moves.add(savedMove);
        }
    }
    
    // Getter and Setter
    public String getSaveId() { return saveId; }
    public void setSaveId(String saveId) { this.saveId = saveId; }
    
    public String getTimestamp() { return timestamp; }
    public void setTimestamp(String timestamp) { this.timestamp = timestamp; }
    
    public String getSaveName() { return saveName; }
    public void setSaveName(String saveName) { this.saveName = saveName; }
    
    public SpielModus getSpielModus() { return spielModus; }
    public void setSpielModus(SpielModus spielModus) { this.spielModus = spielModus; }
    
    public KiStrategie getStrategie1() { return strategie1; }
    public void setStrategie1(KiStrategie strategie1) { this.strategie1 = strategie1; }
    
    public KiStrategie getStrategie2() { return strategie2; }
    public void setStrategie2(KiStrategie strategie2) { this.strategie2 = strategie2; }
    
    public int getBoardSize() { return boardSize; }
    public void setBoardSize(int boardSize) { this.boardSize = boardSize; }
    
    public int getCurrentPlayerIndex() { return currentPlayerIndex; }
    public void setCurrentPlayerIndex(int currentPlayerIndex) { this.currentPlayerIndex = currentPlayerIndex; }
    
    public boolean isGameActive() { return gameActive; }
    public void setGameActive(boolean gameActive) { this.gameActive = gameActive; }
    
    public String getWinner() { return winner; }
    public void setWinner(String winner) { this.winner = winner; }
    
    public int getTotalMoves() { return totalMoves; }
    public void setTotalMoves(int totalMoves) { this.totalMoves = totalMoves; }
    
    public List<String> getBoardRows() { return boardRows; }
    public void setBoardRows(List<String> boardRows) { this.boardRows = boardRows; }
    
    public List<SavedMove> getMoves() { return moves; }
    public void setMoves(List<SavedMove> moves) { this.moves = moves; }
    
    public GameStatistics getStatistics() { return statistics; }
    public void setStatistics(GameStatistics statistics) { this.statistics = statistics; }
    
    @Override
    public String toString() {
        return String.format("GameSave[%s] %s - %s (%d moves)", 
                           saveId, saveName, timestamp, totalMoves);
    }
}