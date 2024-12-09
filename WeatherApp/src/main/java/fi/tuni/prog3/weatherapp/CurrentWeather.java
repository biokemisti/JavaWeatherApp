package fi.tuni.prog3.weatherapp;

import java.text.SimpleDateFormat;
import java.util.Date;
import org.json.JSONObject;

/**
 * This class models the current weather conditions based on data provided in 
 * JSON format. It parses the JSON to extract weather-related attributes such 
 * as temperature, humidity, and wind details. 
 * 
 * ChatGPT 4 was used in writing, debugging and commenting this class.
 * 
 * @author bpelmo
 */
public class CurrentWeather {
    private String description;
    private double temperatureK;
    private double feelsLikeK;
    private int humidity;
    private double windSpeed;
    private int windDirection;
    private long sunrise;
    private long sunset;
    private long timezoneOffset;
    
    /**
     * Constructor that parses a JSON string to initialize the weather conditions.
     * 
     * @param json A string containing the JSON data about current weather.
     */
    public CurrentWeather(String json) {
        
        JSONObject currentWeatherJson = new JSONObject(json);
        
        // Parsing the Json data
        this.description = currentWeatherJson.getJSONArray("weather")
                .getJSONObject(0).getString("description");
        this.temperatureK = currentWeatherJson.getJSONObject("main")
                .getDouble("temp");
        this.feelsLikeK = currentWeatherJson.getJSONObject("main")
                .getDouble("feels_like");
        this.humidity = currentWeatherJson.getJSONObject("main")
                .getInt("humidity");
        this.windSpeed = currentWeatherJson.getJSONObject("wind")
                .getDouble("speed");
        this.windDirection = currentWeatherJson.getJSONObject("wind")
                .getInt("deg");
        this.sunrise = currentWeatherJson.getJSONObject("sys")
                .getLong("sunrise");
        this.sunset = currentWeatherJson.getJSONObject("sys")
                .getLong("sunset");
        this.timezoneOffset = currentWeatherJson
                .getLong("timezone");
    }
    
    /**
     * Gets the description of the weather.
     * 
     * @return description
     */
    public String getDescription() {
        return description;
    }
    
    /**
     * Gets the temperature in Celsius.
     * 
     * @return temperature
     */
    public String getTemperatureC() {
        double temperatureC= temperatureK - 273.15;
        return String.format("%.0f°C",(double) Math.round(temperatureC));
    }
    
    /**
     * Gets the "feels like" temperature in Celsius.
     * 
     * @return temperature
     */
    public String getFeelsLikeC() {
        double feelsLikeC= feelsLikeK - 273.15;
        return String.format("%.0f°C",(double) Math.round(feelsLikeC));
    }
    
    /**
     * Gets the humidity.
     * 
     * @return humidity
     */
    public String getHumidity() {
        return String.format("%d", humidity);
    }
    
    /**
     * Gets wind speed.
     * 
     * @return wind speed
     */
    public String getWindSpeed() {
        return String.format("%.0f", (double) Math.round(windSpeed));
    }
    
    /**
     * Gets the wind direction.
     * 
     * @return direction of wind
     */
    public String getWindDirection() {
        return String.format("%d", windDirection);
    }
    
    /**
     * Gets the time of sunrise.
     * 
     * @return time of sunrise
     */
    public String getSunrise() {
        return formatTime(sunrise, timezoneOffset);
    }
    
    /**
     * Gets the time of sunset.
     * 
     * @return time of sunset
     */
    public String getSunset() {
        return formatTime(sunset, timezoneOffset);
    }
    
    /**
     * Helper method to format the given timestamp into a time string adjusted
     * for the timezone.
     * 
     * @param timestamp Time in seconds since Unix epoch.
     * @param timeZoneOffset Offset in seconds from UTC.
     * @return Formatted time string
     */
    private String formatTime(long timestamp, long timeZoneOffset) {
        SimpleDateFormat timeFormat = new SimpleDateFormat("HHmm");

        int totalSecondsOffset = (int) timeZoneOffset;
        int hours = totalSecondsOffset / 3600;
        int minutes = (Math.abs(totalSecondsOffset) % 3600) / 60;

        String timeZoneId = String.format("GMT%+d:%02d", hours, minutes);
        timeFormat.setTimeZone(java.util.TimeZone.getTimeZone(timeZoneId));

        String formattedTime = timeFormat.format(new Date(timestamp * 1000));
        return formattedTime;
    }
}