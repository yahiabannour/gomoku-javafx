package de.hsh.oil.gomoku.util;

import javafx.scene.text.Font;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;

/**
 * FontAwesome Icon Utility Class
 * 
 * Provides FontAwesome icons for the Gomoku application
 * Uses Unicode characters for FontAwesome icons
 * 
 * @author OIL SS2025
 */
public class FontAwesome {
    
    // Fallback to emojis since FontAwesome font is not available
    public static final String GAMEPAD = "🎮";           // Game controller
    public static final String USERS = "👥";             // Users
    public static final String ROBOT = "🤖";             // Robot
    public static final String FIRE = "🔥";              // Fire
    public static final String HOME = "🏠";              // Home
    public static final String COG = "⚙️";               // Settings
    public static final String BRAIN = "🧠";             // Brain
    public static final String TARGET = "🎯";            // Target
    public static final String BOLT = "⚡";              // Lightning
    public static final String INFO_CIRCLE = "ℹ️";       // Info
    public static final String QUESTION_CIRCLE = "❓";   // Question
    public static final String REFRESH = "🔄";           // Refresh
    public static final String TIMES = "❌";             // Close
    public static final String ROCKET = "🚀";            // Rocket
    public static final String FOLDER = "📁";            // Folder
    public static final String FILE_TEXT = "📄";         // File
    public static final String CHART_BAR = "📊";         // Chart
    public static final String EYE = "👁️";               // Eye
    public static final String MAGIC = "✨";             // Magic
    public static final String TROPHY = "🏆";            // Trophy
    public static final String CLOCK = "⏰";             // Clock
    public static final String SEARCH = "🔍";            // Search
    public static final String PLAY = "▶️";              // Play
    public static final String PAUSE = "⏸️";             // Pause
    public static final String RULER = "📐";             // Ruler
    
    // Font instance (will be loaded from system fonts)
    private static Font fontAwesome;
    
    /**
     * Initializes emoji-compatible font
     */
    static {
        try {
            // Use system font that supports emojis
            fontAwesome = Font.font("Segoe UI Emoji", 16);
            
            // Fallback to regular system font if emoji font not available
            if (fontAwesome == null || fontAwesome.getName().equals("System")) {
                fontAwesome = Font.font("Segoe UI", 16);
            }
        } catch (Exception e) {
            fontAwesome = Font.font("Arial", 16);
        }
    }
    
    /**
     * Creates a FontAwesome icon label
     * 
     * @param iconCode The FontAwesome unicode
     * @param size Font size
     * @param color Text color
     * @return Label with FontAwesome icon
     */
    public static Label createIcon(String iconCode, double size, Color color) {
        Label icon = new Label(iconCode);
        icon.setFont(Font.font(fontAwesome.getFamily(), size));
        icon.setTextFill(color);
        return icon;
    }
    
    /**
     * Creates a FontAwesome icon label with default black color
     * 
     * @param iconCode The FontAwesome unicode
     * @param size Font size
     * @return Label with FontAwesome icon
     */
    public static Label createIcon(String iconCode, double size) {
        return createIcon(iconCode, size, Color.BLACK);
    }
    
    /**
     * Creates a FontAwesome icon label with default size and color
     * 
     * @param iconCode The FontAwesome unicode
     * @return Label with FontAwesome icon
     */
    public static Label createIcon(String iconCode) {
        return createIcon(iconCode, 16, Color.BLACK);
    }
    
    /**
     * Gets FontAwesome icon as string for use in text
     * 
     * @param iconCode The FontAwesome unicode
     * @return Icon string
     */
    public static String getIcon(String iconCode) {
        return iconCode;
    }
    
    /**
     * Creates a combined text with icon
     * 
     * @param iconCode FontAwesome unicode
     * @param text Text to display
     * @return Combined string
     */
    public static String iconText(String iconCode, String text) {
        return iconCode + " " + text;
    }
} 