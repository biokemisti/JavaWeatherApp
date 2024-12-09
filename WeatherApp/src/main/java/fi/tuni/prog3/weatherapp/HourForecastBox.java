package fi.tuni.prog3.weatherapp;

import java.io.IOException;
import java.io.InputStream;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;

/**
 * Represents a box to display hourly forecast information
 * This class creates a VBox containing labels for hour, description,
 * temperature and rainfall and handles updating their values based on hourly
 * forecast data
 * ChatGPT 3.5 was used in coding, debugging and testing this class
 * @author jaani
 */
public class HourForecastBox {
    private final VBox hourlyForecastBox;
    private final Label hourLabel;
    private final Label descriptionLabel;
    private final Label temperature;
    private final Label rainLabel;
    private WeatherApp weatherApp;
    
    /**
     * Constructs a HourForecastBox object.
     * Initializes the VBox and labels, configures their styles
     */
    public HourForecastBox() {
        hourlyForecastBox = new VBox();
        hourLabel = new Label();
        descriptionLabel = new Label();
        temperature = new Label();
        weatherApp = new WeatherApp();
        rainLabel = new Label();
        
        configureHourForecastBox();
        configureLabels();
        hourlyForecastBox.getChildren().addAll(hourLabel, descriptionLabel
                , temperature, rainLabel);
        
    }
    /**
     * Configures the appearance and layout of the hourly forecast box.
     */
    private void configureHourForecastBox() {
        hourlyForecastBox.setMinWidth(50);
        hourlyForecastBox.setMinHeight(130);
        hourlyForecastBox.setBackground(weatherApp.createBackgroundFromImage());
    }
    /**
     * Finds an image fitting to the description and returns it
     * if no image name matches returns default image.
     * @param description The description of the weather condition
     * @return An ImageView displaying the weather image.
     */
    private ImageView getWeatherImage(String description) {
        String imagePath = "/images/" + description.toLowerCase() + ".png";
        try (InputStream imageStream = getClass()
                .getResourceAsStream(imagePath)) {
            if (imageStream != null) {
                Image image = new Image(imageStream);
                ImageView imageView = new ImageView(image);
                imageView.setFitWidth(50);
                imageView.setFitHeight(50);
                return imageView;
            } else {
                return weatherApp.getDefaultImage();
            }
        } catch (IOException e) {
            return weatherApp.getDefaultImage();
        }
}
    /**
     * Configures the styles of the labels
     */
    private void configureLabels() {
        hourLabel.setMaxWidth(Double.MAX_VALUE);
        hourLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
        hourLabel.setAlignment(Pos.CENTER);
        descriptionLabel.setMaxWidth(Double.MAX_VALUE);
        temperature.setMaxWidth(Double.MAX_VALUE);
        temperature.setAlignment(Pos.CENTER);
        rainLabel.setMaxWidth(Double.MAX_VALUE);
        rainLabel.setAlignment(Pos.CENTER);

    }
    /**
     * Updates the hourly forecast labels with the provided hourly 
     * forecast entry data
     * @param entry The hourly forecast entry containing hour, description, 
     * temperature and rainfall information
     */
        public void updateHourlyForecastLabel(HourlyForecast
                .HourlyForecastEntry entry) {
        hourLabel.setText(entry.getHour());
        descriptionLabel.setGraphic(getWeatherImage(entry.getDescription()));
        temperature.setText(entry.getTemperature() + "°C");
        rainLabel.setText(entry.getRain() + "ml");
    }
    
    /**
     * Gets the hourly forecast box containing the hourly forecast information
     * @return The VBox containing the hourly forecast information
     */
    public VBox getHourForecastBox() {
        return hourlyForecastBox;
    }
}
