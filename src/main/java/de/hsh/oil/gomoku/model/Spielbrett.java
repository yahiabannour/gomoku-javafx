package de.hsh.oil.gomoku.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Repräsentiert das Spielbrett für Gomoku
 * 
 * Ein 15x15 Gitter für das Gomoku-Spiel mit Methoden zur Verwaltung
 * der Züge und Überprüfung der Spielregeln
 * 
 * @author OIL SS2025
 */
public class Spielbrett {
    
    public static final int STANDARD_GROESSE = 15;
    public static final int GEWINN_ANZAHL = 5;
    public static final char LEER = '.';
    
    private final int groesse;
    private final char[][] brett;
    private final List<Zug> zugVerlauf;
    private Spieler gewinner;
    private List<Position> gewinnLinie;
    
    /**
     * Konstruktor für ein Spielbrett mit Standardgröße
     */
    public Spielbrett() {
        this(STANDARD_GROESSE);
    }
    
    /**
     * Konstruktor für ein Spielbrett mit angegebener Größe
     * 
     * @param groesse Die Größe des quadratischen Spielbretts
     */
    public Spielbrett(int groesse) {
        if (groesse < 5 || groesse > 25) {
            throw new IllegalArgumentException("Spielbrettgröße muss zwischen 5 und 25 liegen");
        }
        
        this.groesse = groesse;
        this.brett = new char[groesse][groesse];
        this.zugVerlauf = new ArrayList<>();
        this.gewinner = null;
        this.gewinnLinie = new ArrayList<>();
        
        // Brett initialisieren
        initialisiereBrett();
    }
    
    /**
     * Initialisiert das Brett mit leeren Feldern
     */
    private void initialisiereBrett() {
        for (int i = 0; i < groesse; i++) {
            for (int j = 0; j < groesse; j++) {
                brett[i][j] = LEER;
            }
        }
    }
    
    /**
     * Macht einen Zug auf dem Brett
     * 
     * @param zug Der zu machende Zug
     * @return true wenn der Zug erfolgreich war, false sonst
     */
    public boolean macheZug(Zug zug) {
        if (!istZugGueltig(zug)) {
            return false;
        }
        
        // Zug auf Brett setzen
        brett[zug.getY()][zug.getX()] = zug.getSpieler().getSymbol();
        zugVerlauf.add(zug);
        
        // Prüfen ob dieser Zug gewonnen hat
        if (pruefeGewinn(zug)) {
            gewinner = zug.getSpieler();
        }
        
        return true;
    }
    
    /**
     * Überprüft, ob ein Zug gültig ist
     * 
     * @param zug Der zu überprüfende Zug
     * @return true wenn der Zug gültig ist
     */
    public boolean istZugGueltig(Zug zug) {
        if (zug == null) {
            return false;
        }
        
        int x = zug.getX();
        int y = zug.getY();
        
        // Koordinaten im gültigen Bereich?
        if (x < 0 || x >= groesse || y < 0 || y >= groesse) {
            return false;
        }
        
        // Feld bereits belegt?
        return brett[y][x] == LEER;
    }
    
    /**
     * Überprüft, ob der letzte Zug zu einem Gewinn geführt hat
     * 
     * @param zug Der zu überprüfende Zug
     * @return true wenn dieser Zug gewonnen hat
     */
    private boolean pruefeGewinn(Zug zug) {
        char symbol = zug.getSpieler().getSymbol();
        int x = zug.getX();
        int y = zug.getY();
        
        // Alle vier Richtungen prüfen: horizontal, vertikal, diagonal
        int[][] richtungen = {
            {1, 0},   // horizontal
            {0, 1},   // vertikal
            {1, 1},   // diagonal \
            {1, -1}   // diagonal /
        };
        
        for (int[] richtung : richtungen) {
            int dx = richtung[0];
            int dy = richtung[1];
            
            List<Position> linie = new ArrayList<>();
            linie.add(new Position(x, y));
            
            // In positive Richtung zählen
            int count = 1;
            int nx = x + dx;
            int ny = y + dy;
            while (nx >= 0 && nx < groesse && ny >= 0 && ny < groesse && 
                   brett[ny][nx] == symbol) {
                linie.add(new Position(nx, ny));
                count++;
                nx += dx;
                ny += dy;
            }
            
            // In negative Richtung zählen
            nx = x - dx;
            ny = y - dy;
            while (nx >= 0 && nx < groesse && ny >= 0 && ny < groesse && 
                   brett[ny][nx] == symbol) {
                linie.add(0, new Position(nx, ny)); // Am Anfang einfügen
                count++;
                nx -= dx;
                ny -= dy;
            }
            
            // Gewinn wenn 5 oder mehr in einer Reihe
            if (count >= GEWINN_ANZAHL) {
                gewinnLinie = linie;
                return true;
            }
        }
        
        return false;
    }
    
    /**
     * Nimmt den letzten Zug zurück
     * 
     * @return true wenn erfolgreich, false wenn kein Zug vorhanden
     */
    public boolean nimmZugZurueck() {
        if (zugVerlauf.isEmpty()) {
            return false;
        }
        
        Zug letzterZug = zugVerlauf.remove(zugVerlauf.size() - 1);
        brett[letzterZug.getY()][letzterZug.getX()] = LEER;
        
        // Gewinner zurücksetzen
        gewinner = null;
        gewinnLinie.clear();
        
        return true;
    }
    
    /**
     * Erstellt eine Kopie des Bretts
     * 
     * @return Eine Kopie des Spielbretts
     */
    public Spielbrett kopiere() {
        Spielbrett kopie = new Spielbrett(groesse);
        
        // Brett kopieren
        for (int i = 0; i < groesse; i++) {
            System.arraycopy(brett[i], 0, kopie.brett[i], 0, groesse);
        }
        
        // Zugverlauf kopieren
        for (Zug zug : zugVerlauf) {
            kopie.zugVerlauf.add(zug.kopiere());
        }
        
        kopie.gewinner = gewinner;
        kopie.gewinnLinie = new ArrayList<>(gewinnLinie);
        
        return kopie;
    }
    
    /**
     * Gibt alle freien Positionen zurück
     * 
     * @return Liste der freien Positionen
     */
    public List<Position> getFreiePositionen() {
        List<Position> freiePositionen = new ArrayList<>();
        
        for (int y = 0; y < groesse; y++) {
            for (int x = 0; x < groesse; x++) {
                if (brett[y][x] == LEER) {
                    freiePositionen.add(new Position(x, y));
                }
            }
        }
        
        return freiePositionen;
    }
    
    /**
     * Gibt freie Positionen in der Nähe belegter Felder zurück
     * 
     * @param umkreis Der Suchradius um belegte Felder
     * @return Liste der freien Positionen in der Nähe
     */
    public List<Position> getFreiePositionenInNaehe(int umkreis) {
        List<Position> naheFrei = new ArrayList<>();
        
        if (zugVerlauf.isEmpty()) {
            // Erstes Feld in der Mitte
            naheFrei.add(new Position(groesse / 2, groesse / 2));
            return naheFrei;
        }
        
        for (int y = 0; y < groesse; y++) {
            for (int x = 0; x < groesse; x++) {
                if (brett[y][x] == LEER && istInNaeheBelegterFelder(x, y, umkreis)) {
                    naheFrei.add(new Position(x, y));
                }
            }
        }
        
        return naheFrei;
    }
    
    /**
     * Überprüft, ob eine Position in der Nähe belegter Felder ist
     * 
     * @param x Die x-Koordinate
     * @param y Die y-Koordinate  
     * @param umkreis Der Suchradius
     * @return true wenn in der Nähe belegter Felder
     */
    private boolean istInNaeheBelegterFelder(int x, int y, int umkreis) {
        for (int dy = -umkreis; dy <= umkreis; dy++) {
            for (int dx = -umkreis; dx <= umkreis; dx++) {
                int nx = x + dx;
                int ny = y + dy;
                
                if (nx >= 0 && nx < groesse && ny >= 0 && ny < groesse && 
                    brett[ny][nx] != LEER) {
                    return true;
                }
            }
        }
        return false;
    }
    
    // Getter-Methoden
    
    public int getGroesse() {
        return groesse;
    }
    
    public char getFeld(int x, int y) {
        if (x < 0 || x >= groesse || y < 0 || y >= groesse) {
            return ' '; // Ungültige Koordinaten
        }
        return brett[y][x];
    }
    
    public List<Zug> getZugVerlauf() {
        return new ArrayList<>(zugVerlauf);
    }
    
    public Spieler getGewinner() {
        return gewinner;
    }
    
    public List<Position> getGewinnLinie() {
        return new ArrayList<>(gewinnLinie);
    }
    
    public boolean istVoll() {
        return getFreiePositionen().isEmpty();
    }
    
    public boolean hatFreieFelder() {
        return !istVoll();
    }
    
    public boolean istSpielBeendet() {
        return gewinner != null || istVoll();
    }
    
    public int getAnzahlZuege() {
        return zugVerlauf.size();
    }
    
    /**
     * Setzt das Brett zurück
     */
    public void reset() {
        initialisiereBrett();
        zugVerlauf.clear();
        gewinner = null;
        gewinnLinie.clear();
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        
        // Spalten-Header
        sb.append("   ");
        for (int x = 0; x < groesse; x++) {
            sb.append(String.format("%2d ", x));
        }
        sb.append("\n");
        
        // Zeilen mit Zeilennummern
        for (int y = 0; y < groesse; y++) {
            sb.append(String.format("%2d ", y));
            for (int x = 0; x < groesse; x++) {
                char feld = brett[y][x];
                sb.append(" ").append(feld).append(" ");
            }
            sb.append("\n");
        }
        
        return sb.toString();
    }
    
    /**
     * Innere Klasse für Positionen
     */
    public static class Position {
        private final int x;
        private final int y;
        
        public Position(int x, int y) {
            this.x = x;
            this.y = y;
        }
        
        public int getX() {
            return x;
        }
        
        public int getY() {
            return y;
        }
        
        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null || getClass() != obj.getClass()) return false;
            Position position = (Position) obj;
            return x == position.x && y == position.y;
        }
        
        @Override
        public int hashCode() {
            return java.util.Objects.hash(x, y);
        }
        
        @Override
        public String toString() {
            return String.format("(%d,%d)", x, y);
        }
    }
} 