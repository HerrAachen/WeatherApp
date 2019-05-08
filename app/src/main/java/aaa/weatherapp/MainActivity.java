package aaa.weatherapp;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        WeatherApiClient weatherApiClient = new WeatherApiClient(this.getApplicationContext());
        weatherApiClient.callFiveDayForecast(response -> {
            try {
                setCityName(response);
                updateChart(response);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        });
    }

    private void updateChart(JSONObject response) throws JSONException {
        ChartData chartData = ChartData.parse(response);
        LineChart temperatureChart = findViewById(R.id.temperatureChart);
        LineDataSet temperatureDataSet = new LineDataSet(chartData.getTemperatureEntries(), "Temperature");
        temperatureDataSet.setColor(Color.GREEN);
        temperatureDataSet.setCircleRadius(5);
        temperatureDataSet.setCircleColor(Color.GREEN);
        LineDataSet cloudCoverDataSet = new LineDataSet(chartData.getCloudCoverEntries(), "Cloud Cover");
        cloudCoverDataSet.setColor(Color.GRAY);
        cloudCoverDataSet.setCircleRadius(4);
        cloudCoverDataSet.setCircleColor(Color.GRAY);
        LineDataSet humidityDataSet = new LineDataSet(chartData.getHumidities(), "Humidity");
        humidityDataSet.setColor(Color.MAGENTA);
        humidityDataSet.setCircleRadius(2);
        humidityDataSet.setCircleColor(Color.MAGENTA);
//        LineDataSet pressureDataSet = new LineDataSet(chartData.getPressures(), "Pressure");
//        pressureDataSet.setColor(Color.BLACK);
//        pressureDataSet.setCircleRadius(3);
//        pressureDataSet.setCircleColor(Color.MAGENTA);
        temperatureChart.setData(new LineData(temperatureDataSet, cloudCoverDataSet, humidityDataSet));
        temperatureChart.getXAxis().setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return new Date((long)value).toString();
            }
        });
        temperatureChart.invalidate();
    }

    private void setCityName(JSONObject response) throws JSONException {
        String cityName = response.getJSONObject("city").getString("name");
        String country = response.getJSONObject("city").getString("country");
        ((TextView)findViewById(R.id.cityInfo)).setText(cityName + " (" + country + ")");
    }
}
