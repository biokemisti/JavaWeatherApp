package fi.tuni.prog3.weatherapp;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;
import org.json.JSONObject;

/**
 * This class provides methods to interact with the OpenWeatherMap API.
 * It implements the iAPI interface to fetch weather data like current weather,
 * daily forecast, and hourly forecast based on location coordinates.
 * It uses Java's HttpURLConnection to make HTTP requests and JSONObjects 
 * to parse the received data. 
 * 
 * ChatGPT 4 was used in coding, debugging, testing and commenting this class.
 * 
 * @author bpelmo
 */
public class OpenWeatherAPI implements iAPI {
    
    private static final String API_KEY = 
            API_KEY_ENV;
    
    private static final String BASE_URL = 
            "BASE_URL_ENV";
    
    private static final String PRO_URL = 
            "BASE_URL_PRO_ENV";
    
    /**
     * Look up the geographic coordinates (latitude, longitude)
     * for a given location name.
     * 
     * @param loc the location name as a string
     * @return an array of two doubles representing latitude and 
     * longitude or null if an error occurs.
     */
    @Override
    public double[] lookUpLocation(String loc) {
        try {
            URL url = new URL(BASE_URL + "weather?q=" + loc + 
                    "&appid=" + API_KEY);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.connect();
            int responseCode = conn.getResponseCode();

            if (responseCode != 200) { // Handle unsuccessful responses
                return null; // Return null to indicate failure
            } else {
                Scanner sc = new Scanner(url.openStream());
                StringBuilder inline = new StringBuilder();
                while (sc.hasNext()) {
                    inline.append(sc.nextLine());
                }
                sc.close();

                JSONObject jsonObj = new JSONObject(inline.toString());
                double lat = jsonObj.getJSONObject("coord").getDouble("lat");
                double lon = jsonObj.getJSONObject("coord").getDouble("lon");
                return new double[]{lat, lon};
            }
        } catch (IOException e) {
            return null;
        }
    }

    /**
     * Fetch the current weather for a given set of coordinates.
     * 
     * @param coordinates a double array containing latitude and longitude
     * @return a string representation of the current weather data or null if 
     * an error occurs.
     */
    @Override
    public String getCurrentWeather(double[] coordinates) {
        String currentWeatherString= getWeatherData(coordinates[0], 
                coordinates[1], "weather", null);
        return currentWeatherString;
    }
    
    /**
     * Fetch the daily weather forecast for a given set of coordinates.
     * 
     * @param coordinates a double array containing latitude and longitude
     * @return a string representation of the daily forecast data or
     * null if an error occurs.
     */
    @Override
    public String getDailyForecast(double[] coordinates) {
        String forecastString = getWeatherData(coordinates[0],
                coordinates[1], "forecast/daily", "16");
        return forecastString;
    }
    
    /**
     * Fetch the hourly weather forecast for a given set of coordinates.
     * 
     * @param coordinates a double array containing latitude and longitude
     * @return string representation of the hourly forecast data or
     * null if an error occurs.
     */
    @Override
    public String getHourlyForecast(double[] coordinates) {
        String hourlyForecastString = getWeatherData(coordinates[0],
                coordinates[1], "forecast/hourly", null);
        return hourlyForecastString;
    }

    /**
     *  Helper method to fetch weather data from the OpenWeatherMap API.
     * 
     * @param lat
     * @param lon
     * @param endpoint
     * @param cnt
     */
    private String getWeatherData(double lat, double lon, String 
            endpoint, String cnt) {
        try {
            URL url;
            if ("forecast/hourly".equals(endpoint)){
                url = new URL(PRO_URL + endpoint + "?lat=" + lat
                        + "&lon=" + lon + "&appid=" + API_KEY);
            } else {
                            if (cnt != null) {
                url = new URL(BASE_URL + endpoint + "?lat=" + lat
                        + "&lon=" + lon + "&cnt=" + cnt + "&appid=" + API_KEY);
                } else {
                        url = new URL(BASE_URL + endpoint + "?lat=" + lat
                            + "&lon=" + lon + "&appid=" + API_KEY);
            }
            }

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.connect();
            int responseCode = conn.getResponseCode();

            if (responseCode != 200) {
                throw new RuntimeException("HttpResponseCode: " + responseCode);
            } else {
                Scanner sc = new Scanner(url.openStream());
                StringBuilder inline = new StringBuilder();
                while (sc.hasNext()) {
                    inline.append(sc.nextLine());
                }
                sc.close();
                return inline.toString();
            }
        } catch (IOException e) {
        }
        return null;
    }
}