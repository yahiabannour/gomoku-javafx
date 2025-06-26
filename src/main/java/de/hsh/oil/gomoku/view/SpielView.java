package de.hsh.oil.gomoku.view;

import de.hsh.oil.gomoku.model.Spielbrett;
import de.hsh.oil.gomoku.model.Spieler;
import de.hsh.oil.gomoku.model.Zug;

/**
 * Interface für die View-Schicht im MVC-Pattern
 * 
 * Definiert die Methoden, die alle View-Implementierungen
 * (Konsole, GUI) bereitstellen müssen
 * 
 * @author OIL SS2025
 */
public interface SpielView {
    
    /**
     * Zeigt die Willkommensnachricht an
     */
    void zeigeWillkommensnachricht();
    
    /**
     * Zeigt Informationen über die Spieler an
     * 
     * @param spieler1 Der erste Spieler
     * @param spieler2 Der zweite Spieler
     */
    void zeigeSpielerInfo(Spieler spieler1, Spieler spieler2);
    
    /**
     * Zeigt das aktuelle Spielbrett an
     * 
     * @param brett Das anzuzeigende Spielbrett
     */
    void zeigeBrett(Spielbrett brett);
    
    /**
     * Erfragt einen Zug von einem menschlichen Spieler
     * 
     * @param spieler Der Spieler, der ziehen soll
     * @param brett Das aktuelle Spielbrett
     * @return Der gewählte Zug oder null bei Abbruch
     */
    Zug erfrageMenschenZug(Spieler spieler, Spielbrett brett);
    
    /**
     * Zeigt an, dass der Computer gerade einen Zug berechnet
     * 
     * @param computer Der Computer-Spieler
     */
    void zeigeComputerDenkt(Spieler computer);
    
    /**
     * Zeigt den vom Computer gemachten Zug an
     * 
     * @param computer Der Computer-Spieler
     * @param zug Der gemachte Zug
     */
    void zeigeComputerZug(Spieler computer, Zug zug);
    
    /**
     * Zeigt das Spielergebnis an
     * 
     * @param gewinner Der Gewinner oder null bei Unentschieden
     * @param anzahlZuege Die Gesamtanzahl der Züge
     */
    void zeigeSpielErgebnis(Spieler gewinner, int anzahlZuege);
    
    /**
     * Zeigt Spielerstatistiken an
     * 
     * @param spieler1 Der erste Spieler
     * @param spieler2 Der zweite Spieler
     */
    void zeigeStatistiken(Spieler spieler1, Spieler spieler2);
    
    /**
     * Zeigt eine Fehlermeldung an
     * 
     * @param nachricht Die Fehlernachricht
     */
    void zeigeFehlermeldung(String nachricht);
    
    /**
     * Zeigt eine allgemeine Nachricht an
     * 
     * @param nachricht Die anzuzeigende Nachricht
     */
    void zeigeNachricht(String nachricht);
    
    /**
     * Startet die grafische Benutzeroberfläche (nur für GUI-Views)
     */
    default void starteGui() {
        // Standard-Implementierung für Nicht-GUI-Views
    }
    
    /**
     * Aktualisiert die Anzeige (besonders für GUI-Views)
     */
    default void aktualisiereAnzeige() {
        // Standard-Implementierung
    }
    
    /**
     * Fragt den Benutzer, ob ein neues Spiel gestartet werden soll
     * 
     * @return true wenn ein neues Spiel gewünscht wird
     */
    default boolean frageNeuesSpiel() {
        return false;
    }
    
    /**
     * Schließt die View und gibt Ressourcen frei
     */
    default void schliessen() {
        // Standard-Implementierung
    }
} 