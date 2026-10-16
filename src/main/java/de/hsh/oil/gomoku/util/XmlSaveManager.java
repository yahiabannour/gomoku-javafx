package de.hsh.oil.gomoku.util;

import de.hsh.oil.gomoku.controller.SpielController;
import de.hsh.oil.gomoku.model.*;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

/**
 * Verwaltet das Speichern und Laden von Spielständen im XML-Format
 * Verwendet eine Ordnerstruktur in gomoku-db/
 * 
 * @author OIL SS2025
 */
public class XmlSaveManager {
    
    private static final String DB_FOLDER = "gomoku-db";
    private static final String SAVES_FOLDER = "saves";
    private static final String STATS_FOLDER = "statistics";
    private static final String BACKUP_FOLDER = "backup";
    
    private final Path dbPath;
    private final Path savesPath;
    private final Path statsPath;
    private final Path backupPath;
    
    private JAXBContext jaxbContext;
    
    /**
     * Konstruktor - initialisiert die Ordnerstruktur
     */
    public XmlSaveManager() {
        this.dbPath = Paths.get(DB_FOLDER);
        this.savesPath = dbPath.resolve(SAVES_FOLDER);
        this.statsPath = dbPath.resolve(STATS_FOLDER);
        this.backupPath = dbPath.resolve(BACKUP_FOLDER);
        
        initializeDatabase();
        initializeJAXB();
    }
    
    /**
     * Initialisiert die Ordnerstruktur
     */
    private void initializeDatabase() {
        try {
            // Erstelle Hauptordner
            Files.createDirectories(dbPath);
            Files.createDirectories(savesPath);
            Files.createDirectories(statsPath);
            Files.createDirectories(backupPath);
            
            System.out.println("📁 Gomoku-Datenbank initialisiert: " + dbPath.toAbsolutePath());
            
            // Erstelle README-Datei
            createReadmeFile();
            
        } catch (IOException e) {
            System.err.println("❌ Fehler beim Initialisieren der Datenbank: " + e.getMessage());
        }
    }
    
    /**
     * Erstellt eine README-Datei für die Datenbank
     */
    private void createReadmeFile() {
        Path readmePath = dbPath.resolve("README.txt");
        if (!Files.exists(readmePath)) {
            try {
                String content = """
                    GOMOKU DATENBANK
                    ================
                    
                    Diese Ordnerstruktur enthält alle gespeicherten Daten des Gomoku-Spiels:
                    
                    📁 saves/       - Gespeicherte Spielstände (XML-Format)
                    📁 statistics/  - Spielstatistiken und Verlaufsdaten
                    📁 backup/      - Automatische Backups
                    
                    Generiert von: Gomoku OIL SS2025
                    Datum: """ + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"));
                
                Files.writeString(readmePath, content);
            } catch (IOException e) {
                System.err.println("⚠️ Konnte README nicht erstellen: " + e.getMessage());
            }
        }
    }
    
    /**
     * Initialisiert JAXB für XML-Serialisierung
     */
    private void initializeJAXB() {
        try {
            jaxbContext = JAXBContext.newInstance(GameSave.class);
        } catch (JAXBException e) {
            System.err.println("❌ JAXB-Initialisierung fehlgeschlagen: " + e.getMessage());
        }
    }
    
    /**
     * Speichert einen Spielstand
     */
    public boolean saveGame(SpielController controller, String saveName) {
        if (jaxbContext == null) {
            System.err.println("❌ JAXB nicht initialisiert");
            return false;
        }
        
        try {
            // GameSave erstellen
            GameSave gameSave = GameSave.fromCurrentGame(controller, saveName);
            
            // Dateiname generieren
            String fileName = generateFileName(saveName);
            Path saveFile = savesPath.resolve(fileName);
            
            // XML marshalling
            Marshaller marshaller = jaxbContext.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            marshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");
            
            marshaller.marshal(gameSave, saveFile.toFile());
            
            System.out.println("💾 Spiel gespeichert: " + saveFile.getFileName());
            
            // Backup erstellen
            createBackup(saveFile);
            
            return true;
            
        } catch (JAXBException e) {
            System.err.println("❌ Fehler beim Speichern: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Speichert einen Spielstand an einem benutzerdefinierten Pfad
     */
    public boolean saveGameToPath(SpielController controller, String saveName, String filePath) {
        if (jaxbContext == null) {
            System.err.println("❌ JAXB nicht initialisiert");
            return false;
        }
        
        try {
            // GameSave erstellen
            GameSave gameSave = GameSave.fromCurrentGame(controller, saveName);
            
            // Datei-Objekt erstellen
            File saveFile = new File(filePath);
            
            // Übergeordnetes Verzeichnis erstellen falls nötig
            File parentDir = saveFile.getParentFile();
            if (parentDir != null && !parentDir.exists()) {
                parentDir.mkdirs();
            }
            
            // XML marshalling
            Marshaller marshaller = jaxbContext.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            marshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");
            
            marshaller.marshal(gameSave, saveFile);
            
            System.out.println("💾 Spiel gespeichert: " + filePath);
            
            // Backup erstellen (nur wenn in Standard-Verzeichnis)
            if (filePath.contains("gomoku-db")) {
                createBackup(saveFile.toPath());
            }
            
            return true;
            
        } catch (JAXBException | SecurityException e) {
            System.err.println("❌ Fehler beim Speichern: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Lädt einen Spielstand
     */
    public GameSave loadGame(String saveId) {
        if (jaxbContext == null) {
            System.err.println("❌ JAXB nicht initialisiert");
            return null;
        }
        
        try {
            // Suche Save-Datei
            Path saveFile = findSaveFile(saveId);
            if (saveFile == null) {
                System.err.println("❌ Save-Datei nicht gefunden: " + saveId);
                return null;
            }
            
            // XML unmarshalling
            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
            GameSave gameSave = (GameSave) unmarshaller.unmarshal(saveFile.toFile());
            
            System.out.println("📂 Spiel geladen: " + saveFile.getFileName());
            return gameSave;
            
        } catch (JAXBException e) {
            System.err.println("❌ Fehler beim Laden: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * Listet alle verfügbaren Saves auf
     */
    public List<GameSave> listSaves() {
        List<GameSave> saves = new ArrayList<>();
        
        if (!Files.exists(savesPath)) {
            return saves;
        }
        
        try (Stream<Path> files = Files.list(savesPath)) {
            files.filter(path -> path.toString().endsWith(".xml"))
                 .forEach(path -> {
                     try {
                         Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
                         GameSave save = (GameSave) unmarshaller.unmarshal(path.toFile());
                         saves.add(save);
                     } catch (JAXBException e) {
                         System.err.println("⚠️ Konnte Save nicht laden: " + path.getFileName());
                     }
                 });
        } catch (IOException e) {
            System.err.println("❌ Fehler beim Auflisten der Saves: " + e.getMessage());
        }
        
        return saves;
    }
    
    /**
     * Löscht einen Save
     */
    public boolean deleteSave(String saveId) {
        Path saveFile = findSaveFile(saveId);
        if (saveFile == null) {
            return false;
        }
        
        try {
            Files.delete(saveFile);
            System.out.println("🗑️ Save gelöscht: " + saveFile.getFileName());
            return true;
        } catch (IOException e) {
            System.err.println("❌ Fehler beim Löschen: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Erstellt ein Backup
     */
    private void createBackup(Path originalFile) {
        try {
            String backupName = "backup_" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) 
                              + "_" + originalFile.getFileName();
            Path backupFile = backupPath.resolve(backupName);
            Files.copy(originalFile, backupFile);
        } catch (IOException e) {
            System.err.println("⚠️ Backup-Erstellung fehlgeschlagen: " + e.getMessage());
        }
    }
    
    /**
     * Generiert einen Dateinamen für einen Save
     */
    private String generateFileName(String saveName) {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        String cleanName = saveName.replaceAll("[^a-zA-Z0-9_-]", "_");
        return String.format("%s_%s.xml", timestamp, cleanName);
    }
    
    /**
     * Findet eine Save-Datei basierend auf der Save-ID
     */
    private Path findSaveFile(String saveId) {
        if (!Files.exists(savesPath)) {
            return null;
        }
        
        try (Stream<Path> files = Files.list(savesPath)) {
            return files.filter(path -> path.toString().endsWith(".xml"))
                       .filter(path -> {
                           try {
                               Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
                               GameSave save = (GameSave) unmarshaller.unmarshal(path.toFile());
                               return saveId.equals(save.getSaveId());
                           } catch (JAXBException e) {
                               return false;
                           }
                       })
                       .findFirst()
                       .orElse(null);
        } catch (IOException e) {
            System.err.println("❌ Fehler beim Suchen der Save-Datei: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * Prüft ob die Datenbank existiert und funktionsfähig ist
     */
    public boolean isDatabaseReady() {
        return Files.exists(dbPath) && 
               Files.exists(savesPath) && 
               Files.exists(statsPath) && 
               Files.exists(backupPath) &&
               jaxbContext != null;
    }
    
    /**
     * Gibt Informationen über die Datenbank aus
     */
    public void printDatabaseInfo() {
        System.out.println("\n📊 GOMOKU DATENBANK STATUS");
        System.out.println("========================");
        System.out.println("📁 Pfad: " + dbPath.toAbsolutePath());
        System.out.println("✅ Status: " + (isDatabaseReady() ? "Bereit" : "Fehler"));
        
        try {
            long saveCount = Files.list(savesPath).count();
            long backupCount = Files.list(backupPath).count();
            
            System.out.println("💾 Gespeicherte Spiele: " + saveCount);
            System.out.println("🔄 Backups: " + backupCount);
        } catch (IOException e) {
            System.out.println("⚠️ Konnte Statistiken nicht laden");
        }
        
        System.out.println("========================\n");
    }
} 