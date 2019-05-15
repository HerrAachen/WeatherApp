package aaa.weatherapp;

import android.util.Log;

import com.github.mikephil.charting.data.Entry;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ChartData {
    List<Integer> dates = new ArrayList<>();
    List<Double> temperatures = new ArrayList<>();
    List<Double> cloudCoverValues = new ArrayList<>();
    List<Double> humidity = new ArrayList<>();
    List<Double> pressureValues = new ArrayList<>();
    List<Double> rainValues = new ArrayList<>();

    public static ChartData parse(JSONObject response) throws JSONException {
        ChartData chartData = new ChartData();
        JSONArray weatherItems = response.getJSONArray("list");
        chartData.temperatures = getValues("main", "temp", weatherItems);
        chartData.cloudCoverValues = getValues("clouds", "all", weatherItems);
        chartData.humidity = getValues("main", "humidity", weatherItems);
        chartData.pressureValues = getValues("main", "pressure", weatherItems);
        chartData.rainValues = getValues("rain", "3h", weatherItems);
        for (int i = 0; i < weatherItems.length(); i++) {
            JSONObject weatherItem = weatherItems.getJSONObject(i);
            int date = weatherItem.getInt("dt");
            chartData.dates.add(date);
        }
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
            Log.i(level1, String.valueOf(value));
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

    public double getMinTemperatureValue() {
        double minValue = Double.MAX_VALUE;
        for(Double temperature: temperatures) {
            minValue = Math.min(minValue, temperature);
        }
        return minValue;
    }
}
