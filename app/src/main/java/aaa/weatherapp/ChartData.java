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

    public static ChartData parse(JSONObject response) throws JSONException {
        ChartData chartData = new ChartData();
        JSONArray weatherItems = response.getJSONArray("list");
        for(int i=0;i<weatherItems.length();i++) {
            JSONObject weatherItem = weatherItems.getJSONObject(i);
            double temperature = weatherItem.getJSONObject("main").getDouble("temp");
            double cloudCover = weatherItem.getJSONObject("clouds").getDouble("all");
            double humidity = weatherItem.getJSONObject("main").getDouble("humidity");
            double pressure = weatherItem.getJSONObject("main").getDouble("pressure");
            int date = weatherItem.getInt("dt");
            chartData.temperatures.add(temperature);
            chartData.cloudCoverValues.add(cloudCover);
            chartData.humidity.add(humidity);
            chartData.pressureValues.add(pressure);
            chartData.dates.add(date);
        }
        return chartData;
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

    private List<Entry> getEntries(List<Double> values) {
        List<Entry> entries = new ArrayList<>();
        for(int i=0;i<dates.size();i++) {
            entries.add(new Entry(dates.get(i), values.get(i).floatValue()));
        }
        return entries;
    }
}
