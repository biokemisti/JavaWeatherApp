package fi.tuni.prog3.weatherapp;

import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

/**
 * Class Forecast represents a collection of weather forecast entries.
 * Each entry contains details such as date, weather description, minimum
 * and maximum temperatures. 
 * 
 * ChatGPT 4 was used in writing, debugging and commenting this code.
 * 
 * @author bpelmo
 */
public class Forecast implements Iterable<Forecast.ForecastEntry> {
    private List<ForecastEntry> entries;
    
    /**
     * Inner class ForecastEntry which represents a single entry in the
     * list of forecast entries.
     */
    public class ForecastEntry {
        private Date date;
        private String description;
        private double minTemp;
        private double maxTemp;
        private double rain;

        /**
         * Builder. Constructs a ForecastEntry object from the parameters.
         * 
         * @param date
         * @param description
         * @param minTemp
         * @param maxTemp 
         */
        public ForecastEntry(Date date, String description,
                            double minTemp, double maxTemp) {
            
            this.date = date;
            this.description = description;
            this.minTemp = minTemp;
            this.maxTemp = maxTemp;
        }
        

        /**
         * Gets the amount of rain of the entry.
         * 
         * @return rain amount
         */
        public double getRain() {
            return rain;
        }
        
        /**
         * Gets the date of the entry.
         * 
         * @return date
         */
        public Date getDate() {
            return date;
        }
        
        /**
         * Gets the weather description for the entry.
         * 
         * @return description
         */
        public String getDescription() {
            return description;
        }

        /**
         * Gets the minimum temperature for the entry.
         * 
         * @return temperature minimum
         */
        public String getMinTemp() {
            return String.format("%.0f",(double) Math.round(minTemp));
        }

        /**
         * Gets the maximum temperature for the entry.
         * 
         * @return temperature maximum
         */
        public String getMaxTemp() {
            return String.format("%.0f",(double) Math.round(maxTemp));
        }
    }

    /**
     * Builder. Constructs a Forecast object from the json 
     * fetched from the API.
     * 
     * @param json 
     */
    public Forecast(String json) {
        entries = new ArrayList<>();
        parse(json);
    }

    /**
     * Parses the data from the json string.
     * 
     * @param json 
     */
    private void parse(String json) {
        JSONObject root = new JSONObject(json);
        JSONArray list = root.getJSONArray("list");

        for (int i = 0; i < list.length(); i++) {
            JSONObject item = list.getJSONObject(i);
            long timestamp = item.getLong("dt") * 1000L;
            Date date = new Date(timestamp);

            double rain = 0.0; 
            if (item.has("rain")) {
                Object rainObj = item.get("rain");
                if (rainObj instanceof JSONObject) {
                    rain = ((JSONObject) rainObj).optDouble("3h", 0.0);
                } else if (rainObj instanceof Number) {
                    rain = ((Number) rainObj).doubleValue();
                }
            }

            JSONObject temp = item.getJSONObject("temp");
            double minTemp = temp.getDouble("min") - 273.15; 
            double maxTemp = temp.getDouble("max") - 273.15;

            String description = item.getJSONArray("weather").getJSONObject(0)
                    .getString("description");

            ForecastEntry entry = new ForecastEntry(date, description,
                    minTemp, maxTemp);
            entry.rain = rain; 
            entries.add(entry);
        }
    }

    /**
     * Gets the list of forecast entries.
     * 
     * @return 
     */
    public List<ForecastEntry> getAllEntries() {
        return entries;
    }
    
    /**
     * Provides an iterator over the forecast entries.
     */
    @Override
    public Iterator<ForecastEntry> iterator() {
        return entries.iterator();
    }
    
    /**
     * Gets the Nth forecast entry from all entries.
     * 
     * @param n
     * @return forecast entry with index n
     */
    public Forecast.ForecastEntry getNthEntry(int n) {
        return entries.get(n);
    }
}
