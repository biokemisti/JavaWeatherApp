/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package fi.tuni.prog3.weatherapp;

/**
 * Manages search-related data for a weather application,
 * including formatting search input, saving and retrieving the last search,
 * and managing search history.
 * ChatGPT 3.5 was used in coding, debugging and testing this class.
 * @author pgjomo
 */
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class Management {
    
    // File path for storing the last search
    private static final String LAST_SEARCH_FILE_PATH = "last_search.txt";
    // File path for storing search history
    private static final String SEARCH_HISTORY_FILE_PATH = "search_history.txt";

    /**
     * Formats the input search string by trimming, converting to lower case,
     * and capitalizing the first letter of each word.
     * @param input The search string to be formatted.
     * @return A formatted string with each word's first letter capitalized.
     */
    public static String formatSearch(String input) {
        if (input == null || input.isEmpty()) {
            return "";
        }
        
        input = input.trim();
        
        String normalized = input.toLowerCase();
        String[] parts = normalized.split("\\s+");
        StringBuilder formatted = new StringBuilder();
        
        for (String part : parts) {
            if (!part.isEmpty()) {
                formatted.append(Character.toUpperCase(part.charAt(0)));
                formatted.append(part.substring(1));
                formatted.append(" ");
            }
        }
        
        return formatted.toString().trim();
    } 
    
    /**
     * Saves the location of the last search to a file.
     * @param location The location to save as the last search.
     */
    public static void saveLastSearch(String location) {
        try (BufferedWriter writer = new BufferedWriter(new 
        FileWriter(LAST_SEARCH_FILE_PATH))) {
            writer.write(location);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Loads the last search location from a file.
     * @return The last searched location, or null if no data is found.
     */
    public static String loadLastSearch() {
        File file = new File(LAST_SEARCH_FILE_PATH);
        if (file.exists()) {
            try (BufferedReader reader = new BufferedReader(new
         FileReader(file))) {
                return reader.readLine();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
    
    /**
     * Loads the search history from a file.
     * @return A list of strings, each representing a past searched location.
     * @throws IOException If there is an error reading the file.
     */
    public static List<String> loadSearchHistory() throws IOException {
        List<String> history = new ArrayList<>();
        File file = new File(SEARCH_HISTORY_FILE_PATH);
        if (file.exists()) {
            try (BufferedReader reader = new BufferedReader(new
         FileReader(file))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    if (!line.isEmpty()) {
                        history.add(line);
                    }
                }
            }
        }
        return history;
    }

    /**
     * Saves the search history to a file, overwriting any existing content.
     * @param history A list of location strings to be saved as search history.
     * @throws IOException If there is an error writing to the file.
     */
    public static void saveSearchHistory(List<String> history)
            throws IOException {
        try (BufferedWriter writer = new BufferedWriter
        (new FileWriter(SEARCH_HISTORY_FILE_PATH, false))) {  
            for (String location : history) {
                writer.write(location + "\n");
            }
        }
    }
}
