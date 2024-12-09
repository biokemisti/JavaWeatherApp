package fi.tuni.prog3.weatherapp;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Represents a hourly forecast, containing a list of hourly forecast entries
 * @author jaani
 */
public class HourlyForecast implements Iterable<HourlyForecast.
        HourlyForecastEntry> {
    private List<HourlyForecastEntry> entries;

    /**
     * Represents a single hourly forecast entry, containing information about
     * the hour, description, temperature and rainfall
     */
    public class HourlyForecastEntry {
        private String hour;
        private String description;
        private double temperature;
        private double rain;
        
        /**
         * Creates a new hourly forecast entry.
         * @param hour the hour of the forecast (in 24-hour format)
         * @param description a brief description of the weather
         * @param temperature the temperature in degrees Celsius
         * @param rain the amount of rainfall
         */
        public HourlyForecastEntry(String hour, String description, double
                temperature, double rain) {
            this.hour = hour;
            this.description = description;
            this.temperature = temperature;
            this.rain = rain;
        }
        
        /**
         * Returns the hour of the forecast
         * @return the hour (in 24-hour format)
         */
        public String getHour() {
            return hour;
        }
        /**
         * Returns the rainfall information in mm
         * @return the amount of rainfall
         */
        public double getRain() {
            return rain;
        }
        /**
         * Returns the description of the weather condition, round to the 
         * nearest integer
         * @return the description
         */
        public String getDescription() {
            return description;
        }
        /**
         * Returns the temperature in degrees celsius
         * @return the temperature
         */
        public String getTemperature() {
            return String.format("%.0f",(double) Math.round(temperature));
        }
    }
    /**
     * Creates a new HourlyForecast object from the JSON string.
     * Initializes the list of forecast entries and parses the JSON data
     * @param json a JSON string containing hourly forecast data
     */
    public HourlyForecast(String json) {
        entries = new ArrayList<>();
        parse(json);
    }
    /**
     * Parses the provided JSON string to exctract hourly forecast data.
     * Populates the list of forecast entries with parsed data.
     * @param json a JSOn string containing hourly forecast data
     */
    private void parse(String json) {
        JSONObject root = new JSONObject(json);
        JSONArray list = root.getJSONArray("list");

        for (int i = 0; i < list.length(); i++) {
            JSONObject item = list.getJSONObject(i);
            String dtTxt = item.getString("dt_txt");
            String hour = dtTxt.substring(11, 13);

            JSONObject main = item.getJSONObject("main");
            double temperature = main.getDouble("temp") - 273.15; 

            double rain = 0.0;
            if (item.has("rain") && item.getJSONObject("rain").has("1h")) {
                rain = item.getJSONObject("rain").getDouble("1h");
            }

            String description = item.getJSONArray("weather")
                    .getJSONObject(0).getString("description");

            HourlyForecastEntry entry = new HourlyForecastEntry(hour,
                    description, temperature, rain);
            entries.add(entry);
        }
    }

    public List<HourlyForecastEntry> getAllEntries() {
        return entries;
    }
    
    @Override
    public Iterator<HourlyForecastEntry> iterator() {
        return entries.iterator();
    }
}