
package fi.tuni.prog3.weatherapp;


import java.io.IOException;
import java.io.InputStream;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.time.LocalDate;
import java.time.ZoneId;
import javafx.geometry.Pos;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

/**
 * Represent a box to display days' forecast information
 * This class creates a VBox containing labels for date, description,
 * temperature and rainfall information and handles updating their values
 * based on forecast data.
 * 
 * ChatGPT 3.5 was used in coding, debugging and testing this class
 * @author jaani
 */
public class ForecastBox {
    private final VBox forecastPanel;
    private final Label dateLabel;
    private final Label descriptionLabel;
    private final Label tempLabel;
    private final Label rainLabel;
    private WeatherApp weatherApp;

    /**
     * Constructs a ForecastBox object
     * Initializes the VBox and labels, configures their styles and positions
     */
    public ForecastBox() {
        forecastPanel = new VBox();
        dateLabel = new Label();
        descriptionLabel = new Label();
        tempLabel = new Label();
        rainLabel = new Label();
        weatherApp = new WeatherApp();


        configureForecastPanel();
        configureLabels();

        forecastPanel.getChildren().addAll(dateLabel, descriptionLabel, 
                tempLabel, rainLabel);
        
        
        setLabelStyles();
    }
    /**
     * Finds an image fitting to the description and returns it
     * if no image name matches returns default image.
     * @param description The description of the weather condition
     * @return An ImageView displaying the weather image
     */
        private ImageView getWeatherImage(String description) {
            String imagePath = "/images/" + description.toLowerCase() + ".png";
            try (InputStream imageStream = getClass()
                    .getResourceAsStream(imagePath)) {
                if (imageStream != null) {
                    Image image = new Image(imageStream);
                    ImageView imageView = new ImageView(image);
                    imageView.setFitWidth(75);
                    imageView.setFitHeight(75);
                    return imageView;
                } else {
                    return weatherApp.getDefaultImage();
                }
            } catch (IOException e) {
                return weatherApp.getDefaultImage();
            }
        }
    

    /**
     * Configures the appearance and layout of the forecast panel
     */    
    private void configureForecastPanel() {
        forecastPanel.setMinWidth(107);
        forecastPanel.setMinHeight(145);
        forecastPanel.setStyle("-fx-border-color: black; -fx-padding: 10px;");

    }

    /**
     * Configures the position of the labels
     */
    private void configureLabels() {
        dateLabel.setMaxWidth(Double.MAX_VALUE);
        dateLabel.setAlignment(Pos.CENTER);
        descriptionLabel.setMaxWidth(Double.MAX_VALUE);
        descriptionLabel.setAlignment(Pos.CENTER);
        tempLabel.setMaxWidth(Double.MAX_VALUE);
        tempLabel.setAlignment(Pos.CENTER);
        rainLabel.setMaxWidth(Double.MAX_VALUE);
        rainLabel.setAlignment(Pos.CENTER);
        
    }
    
    /**
     * Sets the styles of the labels
     */
    private void setLabelStyles() {
        rainLabel.setStyle("-fx-font-size: 14px;-fx-font-weight: bold;");
        tempLabel.setStyle("-fx-font-size: 14px;-fx-font-weight: bold;");
        dateLabel.setStyle("-fx-font-size: 14px;-fx-font-weight: bold;");
    }

    /**
     * Sets the forecast data to the labels with the
     * provided forecast entry data
     * @param entry The forecast entry containing date, weather description,
     * temperature and rainfall information
     */
    public void updateForecastLabel(Forecast.ForecastEntry entry) {
        LocalDate date = entry.getDate().toInstant()
                .atZone(ZoneId.systemDefault()).toLocalDate();
        DateTimeFormatter formatter = DateTimeFormatter
                .ofPattern("E d.M", Locale.ENGLISH);
        String formattedDate = date.format(formatter);
        dateLabel.setText(formattedDate);
        descriptionLabel.setGraphic(getWeatherImage(entry.getDescription()));
        tempLabel.setText(entry.getMinTemp() + "°C..." 
                + entry.getMaxTemp()+ "°C");
        rainLabel.setText("Rain: " + entry.getRain() + "ml");
        
    }
    /**
     * Gets the forecast panel containing t
     * he forecast information and returns it
     * @return The VBox containing the forecast information
     */
    public VBox getForecastPanel() {
        return forecastPanel;
    }
}
