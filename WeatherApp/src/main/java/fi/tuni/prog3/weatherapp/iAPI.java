package fi.tuni.prog3.weatherapp;

import java.util.Map;

/**
 * Interface for extracting data from the OpenWeatherMap API.
 */
public interface iAPI {

    /**
     * Returns coordinates for a location.
     * @param loc Name of the location for which coordinates should be fetched.
     * @return array
     */
    public double[] lookUpLocation(String loc);
    
    public String getHourlyForecast(double[] coordinates);

    /**
     * Returns the current weather as a map for the given coordinates.
     * @param coordinates
     * @return map
     */
    public String getCurrentWeather(double[] coordinates);

    /**
     * Returns a forecast as a map for the given coordinates.
     * @param coordinates
     * @return map
     */
    public String getDailyForecast(double[] coordinates);
}
