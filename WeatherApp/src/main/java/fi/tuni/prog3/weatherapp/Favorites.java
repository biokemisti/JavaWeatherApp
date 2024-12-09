/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package fi.tuni.prog3.weatherapp;

/**
 * Manages a list of favorite cities for a weather application.
 * It provides functionalities to add, check, load, and remove favorite cities.
 * Cities are stored in a text file.
 * ChatGPT 3.5 was used in coding, debugging and testing this class.
 * @author pgjomo
 */
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;


public class Favorites {
    // Path to the file where favorites are stored
    private String favoriteFilePath = "favorites.txt";

    // List to store favorite cities as observable for GUI updates
    private ObservableList<String> favoritesList =
            FXCollections.observableArrayList();
    
    /**
     * Adds a city to the favorites list if it is not already a favorite.
     * @param city The name of the city to add as a favorite.
     * @return true if the city was successfully added, false if the 
     * city was already a favorite.
     * @throws IOException If there is an error accessing the storage file.
     */
    public boolean addFavorite(String city) throws IOException {
        File favoritesFile = new File(favoriteFilePath);
        if (!favoritesFile.exists()) {
                favoritesFile.createNewFile();
            }
        if (!isFavorite(city)) {
            try (FileWriter writer = new FileWriter(favoritesFile, true)) {
                writer.write(city + "\n");
                return true;
            }
        } else {
            return false;
        }
    }

    /**
     * Checks if a given city is already marked as a favorite.
     * @param city The name of the city to check.
     * @return true if the city is a favorite, false otherwise.
     * @throws IOException If there is an error reading from the file.
     */
    public boolean isFavorite(String city) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader
        (favoriteFilePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.equals(city)) {
                    reader.close();
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Loads the favorites from the storage file into the observable list.
     * @return An ObservableList containing the names of all favorite cities.
     */
    public ObservableList<String> loadFavoritesFromFile() {
        favoritesList.clear();
        File favoritesFile = new File(favoriteFilePath);
        if (favoritesFile.exists()) {
            try (BufferedReader reader = new BufferedReader(new
         FileReader(favoritesFile))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    favoritesList.add(line);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return favoritesList;
    }
    
    /**
     * Removes a city from the list of favorites.
     * @param city The name of the city to remove from the favorites.
     * @throws IOException If there is an error accessing the file.
     */
    public void removeFavorite(String city) throws IOException {
        File file = new File(favoriteFilePath);
        if (!file.exists()) {
            return;
        }

        List<String> remainingFavorites = new ArrayList<>();
        try (Scanner scanner = new Scanner(file)) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                if (!line.trim().equals(city)) {
                    remainingFavorites.add(line);
                }
            }
        }

        try (FileWriter writer = new FileWriter(file, false)) {
            for (String favorite : remainingFavorites) {
                writer.write(favorite + System.lineSeparator());
            }
        }
    }
}
