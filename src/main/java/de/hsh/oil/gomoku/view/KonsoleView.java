package de.hsh.oil.gomoku.view;

import de.hsh.oil.gomoku.controller.SpielController;
import de.hsh.oil.gomoku.model.Spielbrett;
import de.hsh.oil.gomoku.model.Spieler;
import de.hsh.oil.gomoku.model.Zug;

import java.util.Scanner;
import java.util.regex.Pattern;

/**
 * Konsolen-View für das Gomoku-Spiel
 * 
 * Implementiert die Benutzeroberfläche für die Kommandozeile
 * 
 * @author OIL SS2025
 */
public class KonsoleView implements SpielView {
    
    private final SpielController controller;
    private final Scanner scanner;
    private final Pattern koordinatenPattern;
    
    /**
     * Konstruktor für die Konsolen-View
     * 
     * @param controller Der Spiel-Controller
     */
    public KonsoleView(SpielController controller) {
        this.controller = controller;
        this.scanner = new Scanner(System.in);
        
        // Regex für Koordinaten-Eingabe: "x y" oder "x,y" oder "x-y"
        this.koordinatenPattern = Pattern.compile("^\\s*(\\d+)\\s*[,\\s-]\\s*(\\d+)\\s*$");
    }
    
    @Override
    public void zeigeWillkommensnachricht() {
        System.out.println();
        System.out.println("██████  ███████  ███    ███  ███████  ██   ██  ██    ██");
        System.out.println("██       ██    ██ ████  ████ ██    ██ ██  ██   ██    ██");
        System.out.println("██   ███ ██    ██ ██ ████ ██ ██    ██ █████    ██    ██");
        System.out.println("██    ██ ██    ██ ██  ██  ██ ██    ██ ██  ██   ██    ██");
        System.out.println("██████  ███████  ██      ██ ███████  ██   ██  ████████");
        System.out.println();
        System.out.println("Willkommen zum strategischen Brettspiel Gomoku!");
        System.out.println("Ziel: Bringe 5 Steine in eine Reihe (horizontal, vertikal oder diagonal)");
        System.out.println();
    }
    
    @Override
    public void zeigeSpielerInfo(Spieler spieler1, Spieler spieler2) {
        System.out.println("Spieler-Information:");
        System.out.println("  " + spieler1);
        System.out.println("  " + spieler2);
        System.out.println();
        
        System.out.println("Spielfeld-Größe: " + controller.getBrett().getGroesse() + "x" + controller.getBrett().getGroesse());
        System.out.println("Debug-Modus: " + controller.getKonfiguration().getDebugModus().getBeschreibung());
        System.out.println();
    }
    
    @Override
    public void zeigeBrett(Spielbrett brett) {
        System.out.println("\nAktuelles Spielfeld:");
        System.out.println(brett.toString());
        
        // Gewinnlinie hervorheben, falls vorhanden
        if (!brett.getGewinnLinie().isEmpty()) {
            System.out.print("Gewinnlinie: ");
            for (int i = 0; i < brett.getGewinnLinie().size(); i++) {
                if (i > 0) System.out.print(" -> ");
                var pos = brett.getGewinnLinie().get(i);
                System.out.print("(" + pos.getX() + "," + pos.getY() + ")");
            }
            System.out.println();
        }
    }
    
    @Override
    public Zug erfrageMenschenZug(Spieler spieler, Spielbrett brett) {
        System.out.println("\n" + spieler.getName() + " (" + spieler.getSymbol() + ") ist am Zug.");
        System.out.print("Gib deine Koordinaten ein (x y) oder 'quit' zum Beenden: ");
        
        String eingabe = scanner.nextLine().trim();
        
        // Beenden-Kommando prüfen
        if (eingabe.equalsIgnoreCase("quit") || eingabe.equalsIgnoreCase("exit")) {
            System.out.println("Spiel wird beendet...");
            controller.beendeSpielVorzeitig();
            return null;
        }
        
        // Hilfe-Kommando
        if (eingabe.equalsIgnoreCase("help") || eingabe.equalsIgnoreCase("?")) {
            zeigeHilfe();
            return erfrageMenschenZug(spieler, brett);
        }
        
        // Brett anzeigen
        if (eingabe.equalsIgnoreCase("show") || eingabe.equalsIgnoreCase("s")) {
            zeigeBrett(brett);
            return erfrageMenschenZug(spieler, brett);
        }
        
        // Koordinaten parsen
        var matcher = koordinatenPattern.matcher(eingabe);
        if (matcher.matches()) {
            try {
                int x = Integer.parseInt(matcher.group(1));
                int y = Integer.parseInt(matcher.group(2));
                
                // Validierung
                if (x >= 0 && x < brett.getGroesse() && y >= 0 && y < brett.getGroesse()) {
                    return new Zug(x, y, spieler, brett.getAnzahlZuege() + 1);
                } else {
                    System.out.println("Koordinaten außerhalb des Spielfelds! Gültige Werte: 0 bis " + 
                                     (brett.getGroesse() - 1));
                }
            } catch (NumberFormatException e) {
                System.out.println("Ungültige Zahlen-Eingabe!");
            }
        } else {
            System.out.println("Ungültiges Format! Beispiele: '7 8', '7,8', '7-8'");
        }
        
        // Bei ungültiger Eingabe erneut fragen
        return erfrageMenschenZug(spieler, brett);
    }
    
    @Override
    public void zeigeComputerDenkt(Spieler computer) {
        System.out.print(computer.getName() + " (" + computer.getSymbol() + ") denkt");
        
        // Kleine Animation
        for (int i = 0; i < 3; i++) {
            try {
                Thread.sleep(300);
                System.out.print(".");
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
        System.out.println();
    }
    
    @Override
    public void zeigeComputerZug(Spieler computer, Zug zug) {
        System.out.println(computer.getName() + " setzt " + computer.getSymbol() + 
                          " auf Position (" + zug.getX() + "," + zug.getY() + ")");
        
        // Strategie-Info anzeigen
        if (computer.getStrategie() != null) {
            System.out.println("  Verwendete Strategie: " + computer.getStrategie().getName());
        }
        System.out.println();
    }
    
    @Override
    public void zeigeSpielErgebnis(Spieler gewinner, int anzahlZuege) {
        System.out.println();
        System.out.println("=".repeat(50));
        System.out.println("           SPIEL BEENDET");
        System.out.println("=".repeat(50));
        
        if (gewinner != null) {
            System.out.println("🎉 GEWINNER: " + gewinner.getName() + " (" + gewinner.getSymbol() + ")");
            System.out.println("Gewonnen nach " + anzahlZuege + " Zügen");
            
            if (gewinner.istComputer() && gewinner.getStrategie() != null) {
                System.out.println("Gewinnstrategie: " + gewinner.getStrategie().getName());
            }
        } else {
            System.out.println("🤝 UNENTSCHIEDEN");
            System.out.println("Spiel nach " + anzahlZuege + " Zügen beendet");
        }
        System.out.println("=".repeat(50));
        System.out.println();
    }
    
    @Override
    public void zeigeStatistiken(Spieler spieler1, Spieler spieler2) {
        System.out.println("SPIELER-STATISTIKEN:");
        System.out.println("  " + spieler1.getStatistikString());
        System.out.println("  " + spieler2.getStatistikString());
        System.out.println();
        
        // Gesamtstatistik
        int gesamtSpiele = spieler1.getGesamtSpiele();
        if (gesamtSpiele > 0) {
            System.out.println("Gesamt: " + gesamtSpiele + " Spiel(e) gespielt");
        }
    }
    
    @Override
    public void zeigeFehlermeldung(String nachricht) {
        System.err.println("❌ FEHLER: " + nachricht);
    }
    
    @Override
    public void zeigeNachricht(String nachricht) {
        System.out.println("ℹ️  " + nachricht);
    }
    
    @Override
    public boolean frageNeuesSpiel() {
        System.out.print("Möchtest du ein neues Spiel starten? (j/n): ");
        String antwort = scanner.nextLine().trim().toLowerCase();
        
        return antwort.equals("j") || antwort.equals("ja") || 
               antwort.equals("y") || antwort.equals("yes");
    }
    
    @Override
    public void schliessen() {
        System.out.println("\nAuf Wiedersehen! Danke fürs Spielen! 👋");
        scanner.close();
    }
    
    /**
     * Zeigt die Hilfe an
     */
    private void zeigeHilfe() {
        System.out.println();
        System.out.println("=== HILFE ===");
        System.out.println("Kommandos:");
        System.out.println("  x y      - Setze Stein auf Position (x,y)");
        System.out.println("  x,y      - Alternative Eingabe mit Komma");
        System.out.println("  x-y      - Alternative Eingabe mit Bindestrich");
        System.out.println("  show/s   - Zeige das aktuelle Spielfeld");
        System.out.println("  help/?   - Zeige diese Hilfe");
        System.out.println("  quit     - Beende das Spiel");
        System.out.println();
        System.out.println("Beispiele:");
        System.out.println("  7 8      - Stein auf Spalte 7, Zeile 8");
        System.out.println("  0,0      - Stein auf linke obere Ecke");
        System.out.println("  14-14    - Stein auf rechte untere Ecke");
        System.out.println();
        System.out.println("Koordinaten gehen von 0 bis " + (controller.getBrett().getGroesse() - 1));
        System.out.println("=============");
        System.out.println();
    }
} 