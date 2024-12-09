package fi.tuni.prog3.weatherapp;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import javax.imageio.ImageIO;
import java.util.HashMap;
import java.util.Map;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.ImageView;

/**
 * Class for creating a simple weather map. The WeatherMap class represents a 
 * graphical display of weather forecasts over a map. It utilizes the 
 * OpenWeatherAPI to fetch weather data for specified locations in Finland
 * an displays them on the map in a text format.
 * 
 * ChatGPT 4 was used in writing, debugging and commenting this class.
 * 
 * @author bpelmo
 */
public class WeatherMap extends JPanel {

    private BufferedImage mapImage;
    private Map<String, Forecast> locationForecasts;
    private OpenWeatherAPI weatherAPI;
    private WeatherApp weatherApp;

    /**
     * Builder. Constructs a new WeatherMap object that 
     * initializes the map image
     * and fetches the weather data.
     * 
     * @param weatherAPI 
     */
    public WeatherMap(OpenWeatherAPI weatherAPI) {
        this.weatherAPI = weatherAPI;
        weatherApp = new WeatherApp();
        locationForecasts = new HashMap<>();
        try {
            mapImage = ImageIO.read(getClass().getResource(
                    "/images/map_of_finland.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        loadForecastData();
    }

    /**
     * Function for loading the forecast data for each point shown on
     * the weather map.
     */
    public void loadForecastData() {
        String[] locations = {"Helsinki", "Turku", "Lappeenranta", "Tampere",
                              "Jyväskylä", "Vaasa", "Joensuu",
                              "Kajaani", "Oulu", "Kuusamo", "Rovaniemi",
                              "Inari", "Enontekiö", "Sodankylä"};
        
        for (String location : locations) {
            double[] coordinates = weatherAPI.lookUpLocation(location);
            Forecast forecast = new Forecast(weatherAPI.
                    getDailyForecast(coordinates));
            locationForecasts.put(location, forecast);
            
             drawText(location, getDrawXCoordinate(location),
                     getDrawYCoordinate(location), 0);
        }
    }

    /**
     * Called by Swing framework whenever a component needs to be redrawn.
     * 
     * @param g 
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (mapImage != null) {
            g.drawImage(mapImage, 0, 0, this);
        }
    }
    /**
     * Converts a JavaFX Image to a BufferedImage. Used for
     * integrating JavaFX images into Swing components.
     * 
     * @param image the JavaFX Image.
     * @return BufferedImage equivalent
     */
    private BufferedImage javafxImageToBufferedImage(javafx.scene
            .image.Image image) {
        BufferedImage bufferedImage = new BufferedImage((int) 
                image.getWidth(), (int) image.getHeight(),
                BufferedImage.TYPE_INT_ARGB);
        SwingFXUtils.fromFXImage(image, bufferedImage);
        return bufferedImage;
    }
    
    /**
     * Retrieves and resizes a weather icon based on the weather description.
     * Loads an image from the resources, converts it to a BufferedImage,
     * and resizes it to fit the display requirements on the map.
     * 
     * @param description weather description
     * @param newWidth width for resized image
     * @param newHeight height for resized image
     * @return weather icon (or null if an error occurs)
     */
    private BufferedImage getWeatherImage(String description, 
            int newWidth, int newHeight) {
        String imagePath = "/images/" + description.toLowerCase() + ".png";
        try (InputStream imageStream = getClass()
                .getResourceAsStream(imagePath)) {
            if (imageStream != null) {
                javafx.scene.image.Image fxImage =
                        new javafx.scene.image.Image(imageStream);
                BufferedImage originalImage = 
                        javafxImageToBufferedImage(fxImage);

                BufferedImage resizedImage = new 
        BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_ARGB);
                Graphics2D g2d = resizedImage.createGraphics();

                g2d.drawImage(originalImage, 0, 0, newWidth, newHeight, null);
                g2d.dispose();

                return resizedImage;
            } else {
            }
        } catch (IOException e) {
            return null;
        }
        return null;
    }

     /**
     * Draws the weather forecast text at a specified position on the map.
     *
     * @param location the name of the location for which the forecast is drawn.
     * @param x the x-coordinate on the map where the text should be drawn.
     * @param y the y-coordinate on the map where the text should be drawn.
     * @param n the index of the forecast entry to be displayed.
     */
    public void drawText(String location, int x, int y, int n) {
        Graphics g = mapImage.getGraphics();
        g.setFont(new Font("Arial", Font.BOLD, 12));
        g.setColor(Color.BLACK);

        Forecast forecast = locationForecasts.get(location);
        Forecast.ForecastEntry entry = forecast.getNthEntry(n);
        String text = entry.getMinTemp()
                + "C..." + entry.getMaxTemp() + "C"; 

        BufferedImage weatherIcon =
                getWeatherImage(entry.getDescription(), 75, 75);
        if (weatherIcon != null) {
            g.drawImage(weatherIcon, x+2, y+2 - weatherIcon.getHeight(), null); 
        } else {
        }
        g.drawString(text, x, y);
        g.dispose();
        repaint();
    }

    /**
     * Retrieves the x-coordinate for drawing the 
     * forecast text based on the location's name.
     *
     * @param location the location's name.
     * @return the x-coordinate on the map.
     */
    public int getDrawXCoordinate(String location) {
        switch (location) {
            case "Helsinki":
                return 150;
            case "Turku":
                return 75;
            case "Lappeenranta":
                return 220;
            case "Tampere":
                return 110;
            case "Jyväskylä":
                return 140;
            case "Vaasa":
                return 70;
            case "Joensuu":
                return 235;
            case "Kajaani":
                return 220;
            case "Oulu":
                return 170;
            case "Kuusamo":
                return 240;
            case "Rovaniemi":
                return 165;
            case "Inari":
                return 180;
            case "Enontekiö":
                return 130;
            case "Sodankylä":
                return 180;
            default:
                return 0;
        }
    }
    
    /**
     * Retrieves the y-coordinate for drawing the 
     * forecast text based on the location's name.
     * 
     * @param location the locations name.
     * @return the y-coordinate on the map.
     */
    public int getDrawYCoordinate(String location) {
        switch (location) {
            case "Helsinki":
                return 580;
            case "Turku":
                return 560;
            case "Lappeenranta":
                return 530;
            case "Tampere":
                return 500;
            case "Jyväskylä":
                return 460;
            case "Vaasa":
                return 400;
            case "Joensuu":
                return 430;
            case "Kajaani":
                return 350;
            case "Oulu":
                return 305;
            case "Kuusamo":
                return 250;
            case "Rovaniemi":
                return 210;
            case "Inari":
                return 55;
            case "Enontekiö":
                return 95;
            case "Sodankylä":
                return 155;
            default:
                return 0;
        }
    }
    
    /**
     * Makes sure that the map is displayed in the correct (preferred) size.
     */
    @Override
    public Dimension getPreferredSize() {
        return new Dimension(mapImage.getWidth(), mapImage.getHeight());
    }
}
