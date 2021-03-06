package aaa.weatherapp;

import com.github.mikephil.charting.data.Entry;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ChartData {
    String cityId;
    String cityName;
    String country;
    List<Integer> dates = new ArrayList<>();
    List<Double> temperatures = new ArrayList<>();
    List<Double> cloudCoverValues = new ArrayList<>();
    List<Double> humidity = new ArrayList<>();
    List<Double> pressureValues = new ArrayList<>();
    List<Double> rainValues = new ArrayList<>();
    List<Double> snowfallValues = new ArrayList<>();
    Date lastUpdated;

    public static ChartData parse(JSONObject response) throws JSONException {
        ChartData chartData = new ChartData();
        JSONArray weatherItems = response.getJSONArray("list");
        chartData.temperatures = getValues("main", "temp", weatherItems);
        chartData.cloudCoverValues = getValues("clouds", "all", weatherItems);
        chartData.humidity = getValues("main", "humidity", weatherItems);
        chartData.pressureValues = getValues("main", "pressure", weatherItems);
        chartData.rainValues = getValues("rain", "3h", weatherItems);
        chartData.snowfallValues = getValues("snow", "3h", weatherItems);
        for (int i = 0; i < weatherItems.length(); i++) {
            JSONObject weatherItem = weatherItems.getJSONObject(i);
            int date = weatherItem.getInt("dt");
            chartData.dates.add(date);
        }
        chartData.cityId = response.getJSONObject("city").getString("id");
        chartData.cityName = response.getJSONObject("city").getString("name");
        chartData.country = response.getJSONObject("city").getString("country");
        return chartData;
    }

    public ChartData getSubSet(int dataPoints) {
        ChartData chartData = new ChartData();
        chartData.temperatures = this.temperatures.subList(0, dataPoints);
        chartData.cloudCoverValues = this.cloudCoverValues.subList(0, dataPoints);
        chartData.humidity = this.humidity.subList(0, dataPoints);
        chartData.pressureValues = this.pressureValues.subList(0, dataPoints);
        chartData.rainValues = this.rainValues.subList(0, dataPoints);
        chartData.snowfallValues = this.snowfallValues.subList(0, dataPoints);
        chartData.dates = this.dates.subList(0, dataPoints);
        chartData.cityId = this.cityId;
        chartData.cityName = this.cityName;
        chartData.country = this.country;
        chartData.lastUpdated = this.lastUpdated;
        return chartData;
    }

    private static List<Double> getValues(String level1, String level2, JSONArray weatherItems) throws JSONException {
        List<Double> values = new ArrayList<>();
        for (int i = 0; i < weatherItems.length(); i++) {
            JSONObject weatherItem = weatherItems.getJSONObject(i);
            double value = 0;
            if (weatherItem.has(level1) && weatherItem.getJSONObject(level1).has(level2)) {
                value = weatherItem.getJSONObject(level1).getDouble(level2);
            }
            values.add(value);
        }
        return values;
    }

    public Integer getFirstDate() {
        return dates.get(0);
    }

    public List<Entry> getTemperatureEntries() {
        return getEntries(temperatures);
    }

    public List<Entry> getCloudCoverEntries() {
        return getEntries(cloudCoverValues);
    }

    public List<Entry> getHumidities() {
        return getEntries(humidity);
    }

    public List<Entry> getPressures() {
        return getEntries(pressureValues);
    }

    public List<Entry> getRainEntries() {
        return getEntries(rainValues);
    }

    public List<Entry> getSnowEntries() {
        return getEntries(snowfallValues);
    }

    public double get24HourRain() {
        int numberOfRainValuesToSum = 24 / 3;
        return getSumOfRain(numberOfRainValuesToSum);
    }

    public double get5DayRainRain() {
        int numberOfRainValuesToSum = (5 * 24) / 3;
        return getSumOfRain(numberOfRainValuesToSum);
    }

    private double getSumOfRain(int numberOfRainValuesToSum) {
        double sumOfRain = 0;
        for(int i = 0; i < rainValues.size() && i <= numberOfRainValuesToSum; i++) {
            Double rainValue = rainValues.get(i);
            if (rainValue != null) {
                sumOfRain += rainValue;
            }
        }
        return sumOfRain;
    }

    private List<Entry> getEntries(List<Double> values) {
        List<Entry> entries = new ArrayList<>();
        for (int i = 0; i < dates.size(); i++) {
            entries.add(new Entry(dates.get(i), values.get(i).floatValue()));
        }
        return entries;
    }

    public double getMaxRainValue() {
        double maxValue = Double.MIN_VALUE;
        for(Double rainValue: rainValues) {
            maxValue = Math.max(maxValue, rainValue);
        }
        return maxValue;
    }

    public double getMaxSnowValue() {
        double maxValue = Double.MIN_VALUE;
        for(Double snowValue: snowfallValues) {
            maxValue = Math.max(maxValue, snowValue);
        }
        return maxValue;
    }

    public double getMinTemperatureValue() {
        double minValue = Double.MAX_VALUE;
        for(Double temperature: temperatures) {
            minValue = Math.min(minValue, temperature);
        }
        return minValue;
    }

    public double getMaxTemperatureValue() {
        int aNumberLowerThanAnyTemperature = -1000000;
        double maxValue = aNumberLowerThanAnyTemperature;
        for(Double temperature: temperatures) {
            maxValue = Math.max(maxValue, temperature);
        }
        return maxValue;
    }

    public Date getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdatedToNow() {
        lastUpdated = new Date();
    }

    public int getIndex(Integer date) {
        int i=0;
        int minDistance = Integer.MAX_VALUE;
        int indexOfClosestMatch = -1;
        for(Integer d: this.dates) {
            int distance = Math.abs(date - d);
            if (distance < minDistance) {
                indexOfClosestMatch = i;
                minDistance = distance;
            }
            i++;
        }
        return indexOfClosestMatch;
    }
}
