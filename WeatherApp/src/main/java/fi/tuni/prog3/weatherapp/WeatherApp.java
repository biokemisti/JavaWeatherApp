package fi.tuni.prog3.weatherapp;

import java.io.IOException;
import java.io.InputStream;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.util.Duration;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import java.util.List;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.geometry.Side;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.stage.Modality;
import javafx.embed.swing.SwingNode;


/**
 * JavaFX Weather Application.
 * ChatGPT 3.5 was used in coding, debugging and testing this class.
 */
public class WeatherApp extends Application {
    
    
    /**
     * Current coordinates of the searched location.
     */
    public static double[] CURRENT_COORDINATES;
    /**
     * Current weather data.
     */
    public static CurrentWeather CURRENT_WEATHER;
    /**
     * Forecast data for the next 14 days.
     */
    public static Forecast FORECAST;
    /**
     * Hourly forecast data for the next 24 hours.
     */
    public static HourlyForecast HOURLY_FORECAST;
     
    /**
     * Panels and Labels
     */
    private ForecastBox[] forecastPanels;
    private HourForecastBox[] hourForecastBoxes;
    private Label temperatureLabel;
    private Label windSpeedLabel;
    private Label windDirectionLabel;
    private Label descriptionLabel;
    private Label sunsetLabel;
    private Label hourlyLabel;
    private Label feelsLikeLabel;
    private Label favoritesLabel;
    private Label twoWeekTitleLabel;
    private Label lastSearchLabel;
    private int FORECAST_DAYS = 14;
    private int FORECAST_HOURS = 24;
    
    private ComboBox<String> searchHistoryButton;
    private List<String> searchHistory = new ArrayList<>();
    
    private Favorites favoritesManager;
    private Management management;
    
    private TabPane tabPane;
    
    private String favoriteFilePath = "favorites.txt";
    private String lastSearch; 
    private final String lastSearchFilePath = "last_search.txt";
    private final String searchHistoryFilePath = "search_history.txt";
   
    /**
    * Path to the background image file.
    */
    private String BackgroundImagePath = "/images/border.jpg";
        
    private Stage popupStage;
    private ListView<String> favoritesListView;
    private ObservableList<String> favoritesList;
    
    private OpenWeatherAPI openWeatherAPI;
    
     /**
     * Start method of this class, initializes alot of different things
     * used in this class
     */
    @Override
    public void start(Stage stage) {
        searchHistoryButton = new ComboBox<>();
        temperatureLabel = new Label("");
        feelsLikeLabel = new Label("");
        favoritesLabel = new Label("");
        windSpeedLabel = new Label("");
        windDirectionLabel = new Label("");
        openWeatherAPI = new OpenWeatherAPI(); 
        favoritesManager = new Favorites();
        management = new Management();
        

        
        BorderPane root = new BorderPane();
        root.setPadding(new Insets(10, 10, 10, 10));
        root.setBackground(createBackgroundFromImage());
        
        BorderPane topPane = new BorderPane();
        topPane.setPadding(new Insets(10));

        tabPane = new TabPane();
        tabPane.setSide(Side.TOP);


        Tab tab1 = new Tab("Weather");
        tab1.setClosable(false);
        Tab tab2 = new Tab("Weather map");
        tab2.setClosable(false);

        loadSearchHistory();
        updateSearchHistoryDropdown();
        HBox searchPane = getSearchPane();
        BorderPane.setAlignment(searchPane, Pos.TOP_RIGHT);
        topPane.setRight(searchPane);

        root.setTop(topPane);
        root.setBottom(getQuitButton());

        VBox centerVBox = getCenterVBox();
        ScrollPane mapVBox = getMapVBox();
        tab1.setContent(centerVBox);
        tab2.setContent(mapVBox);
        tabPane.getTabs().addAll(tab1, tab2);
        root.setCenter(tabPane);
        tabPane.setBackground(createBackgroundFromImage());
        Scene scene = new Scene(root, 500, 900);
        stage.setScene(scene);
        stage.setTitle("WeatherApp");
        stage.show();        
    }
    
    /**
    * @param args The command line arguments passed to the application.
    * An application may get these parameters using the getParameters() method.
    */
    public static void main(String[] args) {
        launch();
    }

    /**
     * Constructs the central VBox layout containing weather, feels 
     * like, wind information, and others.
     * This method organizes the middle part of the application interface.
     *
     * @return VBox The center box containing additional weather information 
     * and layout settings.
     */
    private VBox getCenterVBox() {
        
        VBox centerVBox = new VBox(10);

        
        centerVBox.getChildren().addAll(getTopVBox(),
                getMiddleVBox(), getBottomVBox());
        
        loadLastSearch();
        
        return centerVBox;
    }

    /**
    * Constructs and returns a ScrollPane containing the weather map.
    * This component utilizes a SwingNode to embed a weather map.
    *
    * @return ScrollPane The scroll pane that contains the weather map.
    */
    private ScrollPane getMapVBox() {
        VBox mapVBox = new VBox();
        mapVBox.setBackground(createBackgroundFromImage());
        mapVBox.setPadding(new Insets(10));
        mapVBox.setSpacing(5);
        mapVBox.setAlignment(Pos.CENTER);

        SwingNode swingNode = new SwingNode();
        swingNode.setContent(new WeatherMap(openWeatherAPI));

        mapVBox.getChildren().add(swingNode);
        
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setContent(mapVBox);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);

        return scrollPane;
    }   
    
    /**
    * Creates the top part of the VBox layout with weather 
    * images and primary weather indicators.
    *
    * @return VBox The top VBox containing labels and 
    * images for the current weather.
    */
    private VBox getTopVBox() {
        VBox topVBox = new VBox();
        topVBox.setPadding(new Insets(5));
        topVBox.setSpacing(0);
        topVBox.setAlignment(Pos.CENTER);
        topVBox.setBackground(createBackgroundFromImage());

        lastSearchLabel = new Label("");
        

        temperatureLabel = new Label("");
        feelsLikeLabel = new Label("");
        windSpeedLabel = new Label("");
        windDirectionLabel = new Label("");
        descriptionLabel = new Label("");
        favoritesLabel = new Label("");

        setLabelStyles();

        HBox row1 = new HBox();
        row1.setSpacing(100);
        row1.setAlignment(Pos.CENTER);
        row1.getChildren().addAll(getWeatherImage(), temperatureLabel);

        HBox row2 = new HBox();
        row2.setSpacing(60);
        row2.setAlignment(Pos.CENTER);
        row2.getChildren().addAll(descriptionLabel, feelsLikeLabel);

        HBox row3 = new HBox();
        row3.setSpacing(50);
        row3.setAlignment(Pos.CENTER);
        row3.getChildren().addAll(windSpeedLabel, windDirectionLabel);

        topVBox.getChildren().addAll(lastSearchLabel, row1, row2, row3);

        return topVBox;
    }

    /**
    * Sets the styles for various labels used in the application.
    */
    private void setLabelStyles() {
        lastSearchLabel.setStyle("-fx-font-size: 50px; "
                + "-fx-font-weight: bold; -fx-font-family: SansSerif;");
        temperatureLabel.setStyle("-fx-font-size: 50px;"
                + " -fx-font-weight: bold; -fx-font-family: SansSerif;");
        temperatureLabel.setMinWidth(120);
        descriptionLabel.setStyle("-fx-font-size: 20px;"
                + " -fx-font-weight: bold; -fx-font-family: SansSerif;");
        feelsLikeLabel.setStyle("-fx-font-size: 20px;"
                + "-fx-font-weight: bold; -fx-font-family: SansSerif;");
        windSpeedLabel.setStyle("-fx-font-size: 20px;"
                + " -fx-font-weight: bold; -fx-font-family: SansSerif;");
        windDirectionLabel.setStyle("-fx-font-size: 20px;");
        lastSearchLabel.setStyle("-fx-font-size: 50px; "
                + "-fx-font-weight: bold; -fx-font-family: SansSerif;");
    }
    
    /**
    * Retrieves a weather icon based on the current weather description.
    *
    * @return ImageView An image view containing the appropriate weather icon.
    */
    public ImageView getWeatherImage() {
        String weatherDescription = (CURRENT_WEATHER 
                != null) ? CURRENT_WEATHER.getDescription() : "default";
        String imagePath = "/images/" + 
                weatherDescription.toLowerCase() + ".png";
        try (InputStream imageStream = getClass()
                .getResourceAsStream(imagePath)) {
            if (imageStream != null) {
                Image image = new Image(imageStream);
                ImageView imageView = new ImageView(image);
                imageView.setFitWidth(125);
                imageView.setFitHeight(125);
                return imageView;
            } else {
                return getDefaultImage();
            }
        } catch (IOException e) {
            return getDefaultImage();
        }
    }

    /**
    * Provides a default weather image when no specific 
    * image is available for current weather conditions.
    *
    * @return ImageView A default image view used when
    * no specific weather icon is available.
    */
    public ImageView getDefaultImage() {
        Image defaultImage = new Image(getClass().
                getResourceAsStream("/images/default.png"));
        ImageView defaultImageView = new ImageView(defaultImage);
        defaultImageView.setFitWidth(50);
        defaultImageView.setFitHeight(50);
        return defaultImageView;
    }

    /**
    * Constructs the bottom part of the VBox layout c
    * ontaining the hourly forecast.
    *
    * @return VBox The bottom VBox containing the hourly
    * forecast scroll pane.
    */
    private VBox getBottomVBox() {
        
        VBox bottomVBox = new VBox();
        bottomVBox.setSpacing(10);
        bottomVBox.setPadding(new Insets(10));
        hourlyLabel = new Label();
        hourlyLabel.setStyle("-fx-font-size: 25px;-fx-font-weight: bold;");
        hourlyLabel.setText("Hourly forecast");
        
        HBox bottomHBox = new HBox();
        bottomHBox.setSpacing(1);
        bottomHBox.setBackground(createBackgroundFromImage());
        
        hourForecastBoxes = new HourForecastBox[FORECAST_HOURS];
               
        for (int i = 0; i < FORECAST_HOURS; i++) {
            hourForecastBoxes[i] = new HourForecastBox();
            bottomHBox.getChildren().add(hourForecastBoxes[i]
                    .getHourForecastBox());
        }
                
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setContent(bottomHBox);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);
        scrollPane.setFitToWidth(true);
        bottomVBox.getChildren().addAll(hourlyLabel, scrollPane);
        
        return bottomVBox;
    }

    /**
    * Constructs the middle part of the VBox layout 
    * containing the 14-day forecast.
    *
    * @return VBox The middle VBox containing the 14-day forecast scroll pane.
    */
    private VBox getMiddleVBox() {
        
        VBox middleVBox = new VBox();
        middleVBox.setSpacing(10);
        middleVBox.setPadding(new Insets(10));
        middleVBox.setPrefHeight(235);
        middleVBox.setBackground(createBackgroundFromImage());
        
        twoWeekTitleLabel = new Label();
        twoWeekTitleLabel.setAlignment(Pos.CENTER);
        
        twoWeekTitleLabel.setText("14-day forecast");
        twoWeekTitleLabel.setStyle("-fx-font-size: 25px; "
                + "-fx-font-weight: bold; -fx-font-family: SansSerif;");
        
        HBox middleHBox = new HBox();
        middleHBox.setSpacing(10);
        middleHBox.setBackground(createBackgroundFromImage());
        
        forecastPanels = new ForecastBox[FORECAST_DAYS];
               
        for (int i = 0; i < FORECAST_DAYS; i++) {
            forecastPanels[i] = new ForecastBox();
            middleHBox.getChildren().add(forecastPanels[i].getForecastPanel());
        }
                
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setContent(middleHBox);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);
        scrollPane.setFitToWidth(true);
        scrollPane.setBackground(createBackgroundFromImage());
        middleVBox.getChildren().addAll(twoWeekTitleLabel, scrollPane);
        
        return middleVBox;
    }
    
    /**
    * Creates a Quit button that closes the application.
    *
    * @return a quit button
    */
    private Button getQuitButton() {
        
        Button button = new Button("Quit");

        button.setOnAction((ActionEvent event) -> {
            Platform.exit();
        });

        return button;
    }

    /**
    * Constructs and sets up the search pane including
    * the search text field and buttons.
    *
    * @return HBox The search pane containing the search controls.
    */
    private HBox getSearchPane() {
        HBox searchPane = new HBox(10);
        searchPane.setAlignment(Pos.CENTER_RIGHT);
        searchPane.setBackground(createBackgroundFromImage());

        TextField textField = new TextField();
        textField.setPromptText("Enter location");

        Button searchButton = new Button("Search");
        searchButton.setOnAction(e -> {
            String searchText = textField.getText();
            if (!searchText.isEmpty()) {
                performSearch(searchText);
                textField.clear();
            }
        });

        textField.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                String searchText = textField.getText();
                if (!searchText.isEmpty()) {
                    performSearch(searchText);
                    textField.clear();
                }
            }
        });
        
        Button favoriteButton = new Button("<3");
        favoriteButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                try {
                    if (!favoritesManager.addFavorite(lastSearch)) {
                        lastSearchLabel.setStyle("-fx-font-size: "
                                + "25px; -fx-font-weight: bold;");
                        lastSearchLabel.setText(lastSearch + ""
                                + " is already favorited");
                    } else {
                        lastSearchLabel.setStyle("-fx-font-size:"
                                + " 25px; -fx-font-weight: bold;");
                        lastSearchLabel.setText(lastSearch + ""
                                + " added to favorites");
                    }
                    
                    Timeline timeline = new Timeline(new KeyFrame(
                            Duration.seconds(2),
                            ae -> {
                                lastSearchLabel.setStyle("-fx-font-size:"
                                        + " 50px; -fx-font-weight: bold;");
                                lastSearchLabel.setText(lastSearch); 
                            }));
                    timeline.setCycleCount(1);
                    timeline.play();
                    
                } catch (IOException ex) {
                    Logger.getLogger(WeatherApp.class.getName()).
                            log(Level.SEVERE, null, ex);
                }
            }
        });

        Button favoritePopupButton = new Button("Favorites");
        favoritePopupButton.setOnAction(e -> showFavorites());

        searchHistoryButton.setPrefWidth(50);
        searchHistoryButton = new ComboBox<>();
        searchHistoryButton.setEditable(false);
        searchHistoryButton.setPromptText("History");
        searchHistoryButton.setOnAction(e -> {
            String selected = searchHistoryButton.getSelectionModel
        ().getSelectedItem();
            if (selected != null) {
                textField.setText(selected);
                performSearch(selected);
            }
        });

        searchPane.getChildren().addAll(searchHistoryButton,
                textField, searchButton, favoriteButton, favoritePopupButton);

        return searchPane;
    }
 
    /**
    * Updates the label texts based on the current weather data.
    *
    * @param currentWeather The current weather data.
    * @param location The location for which the weather data is displayed.
    */
    private void setLabelTexts(CurrentWeather currentWeather, String location) {
        temperatureLabel.setText("" + currentWeather.getTemperatureC());
        windSpeedLabel.setText("Wind Speed: " + currentWeather.
                getWindSpeed() + " m/s");
        feelsLikeLabel.setText("Feels like: " + currentWeather.getFeelsLikeC());
        descriptionLabel.setText(currentWeather.getDescription());
        lastSearchLabel.setText(location);
        
        String windDirection = currentWeather.getWindDirection();
        ImageView arrowImageView = createArrowImageView(windDirection);
        windDirectionLabel.setGraphic(arrowImageView);
    }
   
    /**
    * Creates an arrow image view that indicates the wind direction.
    *
    * @param windDirection The direction of the wind in degrees.
    * @return ImageView An image view containing an arrow rotated 
    * to indicate wind direction.
    */
    private ImageView createArrowImageView(String windDirection) {
        try (InputStream imageStream = getClass().
                getResourceAsStream("/images/wind_arrow.png")) {
            if (imageStream!= null) {
                Image arrowImage = new Image(imageStream);
                ImageView arrowImageView = new ImageView(arrowImage);
                arrowImageView.setFitWidth(100); 
                arrowImageView.setFitHeight(100);

                double angle = calculateAngleFromDirection(windDirection);
                arrowImageView.setRotate(angle);

                return arrowImageView;
            }
        } catch (IOException e) {
        }      
        return null;
    }
    
    /**
    * Calculates the rotation angle for the wind direction 
    * arrow based on the wind direction in degrees.
    *
    * @param windDirection The wind direction in degrees from true north.
    * @return double The calculated angle for the arrow rotation.
    */
    private double calculateAngleFromDirection(String windDirection) {
        double angle = 270 - Double.parseDouble(windDirection);
        if (angle < 0) {
            angle += 360;
        }
        return angle;
    }
    
    /**
    * Loads the last search from storage and updates the view accordingly.
    */
    private void loadLastSearch() {
        String location = management.loadLastSearch();
        if (location != null && !location.isEmpty()) {
            CURRENT_COORDINATES = openWeatherAPI.lookUpLocation(location);
            if (CURRENT_COORDINATES == null) {
                setDefaultView("Enter a search");
                return;
            }
            CURRENT_WEATHER = new CurrentWeather(openWeatherAPI
                    .getCurrentWeather(CURRENT_COORDINATES));
            setLabelTexts(CURRENT_WEATHER, location);
            performSearch(location);
        } else {
            setDefaultView("Please enter a location to see weather data.");
        }
    }
    
    /**
    * Saves the last search to a persistent storage file.
    *
    * @param location The location string to save as the last search.
    */
    private void saveLastSearch(String location) {
        Management.saveLastSearch(location);
    }
    
    /**
    * Initiates a weather data search for a specific location.
    *
    * @param location The location string to search for.
    */
    private void performSearch(String location) {
        if (location != null && !location.isEmpty()) {
            String formattedLocation = Management.formatSearch(location);
            CURRENT_COORDINATES = openWeatherAPI.lookUpLocation
        (formattedLocation);

            if (CURRENT_COORDINATES == null) {
                lastSearch = "Unknown location";
                saveLastSearch(lastSearch);
                setDefaultView("Unknown location.");
                return; 
            }

            CURRENT_WEATHER = new CurrentWeather(openWeatherAPI
                    .getCurrentWeather(CURRENT_COORDINATES));
            FORECAST = new Forecast(openWeatherAPI.
                    getDailyForecast(CURRENT_COORDINATES));
            HOURLY_FORECAST = new HourlyForecast(openWeatherAPI
                    .getHourlyForecast(CURRENT_COORDINATES));

            lastSearch = formattedLocation;
            saveSearchHistory(formattedLocation);
            setLabelTexts(CURRENT_WEATHER, formattedLocation);
            saveLastSearch(formattedLocation);

            ImageView newWeatherImage = getWeatherImage();
            HBox row1 = (HBox) temperatureLabel.getParent();
            row1.getChildren().set(0, newWeatherImage);
            row1.getChildren().set(1, temperatureLabel);

            if (forecastPanels != null) {
                int i = 0;
                for (Forecast.ForecastEntry entry : FORECAST) {
                    if (i < forecastPanels.length &&
                            forecastPanels[i] != null) {
                        forecastPanels[i].updateForecastLabel(entry);
                    }
                    i += 1;
                    if (i == FORECAST_DAYS) {
                        break;
                    }
                }
            }
            if (hourForecastBoxes != null) {
                int i = 0;
                for (HourlyForecast.HourlyForecastEntry entry 
                        : HOURLY_FORECAST) {
                    if (i < hourForecastBoxes.length && 
                            hourForecastBoxes[i] != null) {
                        hourForecastBoxes[i].
                                updateHourlyForecastLabel(entry);
                    }
                    i += 1;
                    if (i == FORECAST_HOURS) {
                        break;
                    }
                }
            }
        }
    }
    
    /**
    * Sets a default view with a message indicating that no location data is available.
    *
    * @param message The message to display.
    */
    private void setDefaultView(String message) {
        temperatureLabel.setText("N/A");
        windSpeedLabel.setText("Wind Speed: N/A");
        feelsLikeLabel.setText("Feels like: N/A");
        descriptionLabel.setText("N/A");
        lastSearchLabel.setStyle("-fx-font-size:"
                + " 50px; -fx-font-weight: bold;");
        lastSearchLabel.setText(message);
        Timeline timeline = new Timeline(new KeyFrame(
                Duration.seconds(4),
                ae -> {
                    lastSearchLabel.setStyle("-fx-font-size:"
                            + " 50px; -fx-font-weight: bold;");
                    lastSearchLabel.setText("Search location"); 
                }));
        timeline.setCycleCount(1);
        timeline.play();
        }
                    
    /**
    * Shows the favorites management popup.
    */
    private void showFavorites() {
        if (popupStage == null) {
            popupStage = new Stage();
            popupStage.initModality(Modality.APPLICATION_MODAL);
            popupStage.setTitle("Favorites");

            VBox popupLayout = new VBox(10);
            popupLayout.setPadding(new Insets(10));

            favoritesListView = new ListView<>();
            favoritesList = FXCollections.observableArrayList
        (favoritesManager.loadFavoritesFromFile());
            favoritesListView.setItems(favoritesList);

            favoritesListView.setCellFactory(lv -> new 
        ListCell<String>() {
                private final Button removeButton = new Button("x");
                private final HBox content = new HBox();
                private final Label label = new Label();

                {
                    removeButton.setMaxWidth(20);
                    removeButton.setMaxHeight(20);
                    removeButton.setMinWidth(20);
                    removeButton.setMinHeight(20);
                    removeButton.setStyle("-fx-font-size:"
                            + " 11px");
                    content.getChildren().addAll(removeButton, label);
                    content.setSpacing(10);
                    removeButton.setOnAction(event -> {
                        String item = getItem();
                        favoritesList.remove(item);
                        try {
                            favoritesManager.
                                    removeFavorite(item);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    });
                }

                @Override
                protected void updateItem(String item,
                        boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setGraphic(null);
                    } else {
                        label.setText(item);
                        setGraphic(content);
                    }
                }
            });

            favoritesListView.setOnMouseClicked(e -> {
                String selectedTown = favoritesListView.
                        getSelectionModel().getSelectedItem();
                if (selectedTown != null) {
                    performSearch(selectedTown);
                    popupStage.close();
                }
            });

            Button closeButton = new Button("Close");
            closeButton.setOnAction(e -> popupStage.close());

            popupLayout.getChildren().addAll(favoritesListView,
                    closeButton);

            Scene popupScene = new Scene(popupLayout, 300, 400);
            popupStage.setScene(popupScene);
        } else {
            favoritesList.clear();
            favoritesList.addAll(favoritesManager.loadFavoritesFromFile());
            favoritesListView.setItems(favoritesList);
            popupStage.sizeToScene();
        }

        popupStage.show();
    }
    
    /**
    * Updates the dropdown menu with the latest search history.
    */
    private void updateSearchHistoryDropdown() {
        Platform.runLater(() -> {
            searchHistoryButton.getItems().setAll(searchHistory);
        });
    }
    
    /**
    * Loads the search history from persistent 
    * storage and updates the dropdown menu.
    */
    private void loadSearchHistory() {
        try {
            searchHistory = Management.loadSearchHistory();
            updateSearchHistoryDropdown();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
    * Saves the current search history to persistent storage.
    *
    * @param location The new location to add to the search history.
    */
    private void saveSearchHistory(String location) {
        if (!searchHistory.contains(location)) {
            searchHistory.add(location);
            try {
                Management.saveSearchHistory(searchHistory);
                updateSearchHistoryDropdown();
            } catch (IOException e) {
            }
        }
    }
    
    /**
    * Creates a Background object from an image for use as a UI background.
    *
    * @return Background A new Background object based on the specified image.
    */
    public Background createBackgroundFromImage() {
    try (InputStream is = getClass().
            getResourceAsStream(BackgroundImagePath)) {
        if (is == null) {
            return null; 
        }
        Image image = new Image(is);
        BackgroundImage backgroundImage = new BackgroundImage(
                image,
                BackgroundRepeat.NO_REPEAT,
                BackgroundRepeat.NO_REPEAT,
                BackgroundPosition.CENTER,
                BackgroundSize.DEFAULT);
        return new Background(backgroundImage);
    } catch (Exception e) {
        return null; 
        }
    }
}
    