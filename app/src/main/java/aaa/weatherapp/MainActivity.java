package aaa.weatherapp;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private WeatherApiClient weatherApiClient;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            if (!AppState.isInitialized()) {
                AppState.initialize(this.getApplicationContext());
            }
            setContentView(R.layout.activity_main);
            weatherApiClient = new WeatherApiClient(this.getApplicationContext());
            refreshWeatherView(null);
        } catch (Exception e) {
            showError("An error occurred: " + e.getMessage());
        }
    }
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_settings, menu);
        return true;
    }

    private void updateChart(JSONObject response) throws JSONException {
        ChartData chartData = ChartData.parse(response);
        LineChart chart = findViewById(R.id.temperatureChart);
        LineDataSet temperatureDataSet = createLineDataSet(chartData.getTemperatureEntries(), "Temperature °C", Color.GREEN, 5, YAxis.AxisDependency.LEFT);
        LineDataSet cloudCoverDataSet = createLineDataSet(chartData.getCloudCoverEntries(), "Cloud Cover %", Color.GRAY, 4, YAxis.AxisDependency.LEFT);
        LineDataSet humidityDataSet = createLineDataSet(chartData.getHumidities(), "Humidity %", Color.MAGENTA, 2, YAxis.AxisDependency.LEFT);
        LineDataSet rainDataSet = createLineDataSet(chartData.getRainEntries(), "Rain 3h mm", Color.BLUE, 2, YAxis.AxisDependency.RIGHT);
        chart.setData(new LineData(temperatureDataSet, cloudCoverDataSet, humidityDataSet, rainDataSet));
        chart.getXAxis().setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float epochSeconds) {
                return getWeekday((long) epochSeconds);
            }
        });
        configureRainAxis(chartData, chart.getAxisRight());
        configureTemperatureAxis(chartData, chart.getAxisLeft());
        chart.setDescription(null);
        chart.invalidate();
    }

    private void configureRainAxis(ChartData chartData, YAxis yAxis) {
        yAxis.setAxisMinimum(0);
        yAxis.setAxisMaximum((float) Math.max(4, chartData.getMaxRainValue()));
        yAxis.setGranularity(2f);
    }

    private void configureTemperatureAxis(ChartData chartData, YAxis yAxis) {
        yAxis.setAxisMinimum((float) Math.min(0, chartData.getMinTemperatureValue()));
        yAxis.setAxisMaximum(105);
        yAxis.setGranularity(20f);
    }

    private LineDataSet createLineDataSet(List<Entry> entries, String title, int color, int circleRadius, YAxis.AxisDependency axis) {
        LineDataSet temperatureDataSet = new LineDataSet(entries, title);
        temperatureDataSet.setColor(color);
        temperatureDataSet.setCircleRadius(circleRadius);
        temperatureDataSet.setCircleColor(color);
        temperatureDataSet.setAxisDependency(axis);
        return temperatureDataSet;
    }

    private String getWeekday(long epochSeconds) {
        long epochMilliseconds = epochSeconds * 1000;
        SimpleDateFormat weekdayFormat = new SimpleDateFormat("EEE ha");
        return weekdayFormat.format(new Date(epochMilliseconds));
    }

    private void setCityName(JSONObject response) throws JSONException {
        String cityName = response.getJSONObject("city").getString("name");
        String country = response.getJSONObject("city").getString("country");
        setTitle(cityName + " (" + country + ")");
    }

    public void openSettingsView(MenuItem item) {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }

    public void refreshWeatherView(MenuItem item) {
        showLoadingScreen();
        weatherApiClient.getOneDayForecast(AppState.getCityId(), response -> {
            try {
                AppState.setLastUpdatedToNow();
                setCityName(response);
                updateChart(response);
                showChart();
                showLastUpdatedLabel();
            } catch (JSONException e) {
                showError(e.getMessage());
                e.printStackTrace();
            }
        }, errorMessage -> showError("Error: " + errorMessage));
    }

    private void showLastUpdatedLabel() {
        findViewById(R.id.lastUpdated).setVisibility(View.VISIBLE);
        findViewById(R.id.lastUpdatedDateTime).setVisibility(View.VISIBLE);
        SimpleDateFormat lastUpdatedDateFormat = new SimpleDateFormat("E hh:mm a");
        ((TextView)findViewById(R.id.lastUpdatedDateTime)).setText(lastUpdatedDateFormat.format(AppState.getLastUpdated()));
    }

    private void showChart() {
        findViewById(R.id.mainActivityLoadingIcon).setVisibility(View.GONE);
        findViewById(R.id.temperatureChart).setVisibility(View.VISIBLE);
        findViewById(R.id.errorText).setVisibility(View.GONE);
        showLastUpdatedLabel();
    }

    private void showLoadingScreen() {
        findViewById(R.id.mainActivityLoadingIcon).setVisibility(View.VISIBLE);
        findViewById(R.id.temperatureChart).setVisibility(View.GONE);
        findViewById(R.id.errorText).setVisibility(View.GONE);
        hideLastUpdatedLabel();
    }

    private void showError(String errorText) {
        findViewById(R.id.mainActivityLoadingIcon).setVisibility(View.GONE);
        findViewById(R.id.temperatureChart).setVisibility(View.GONE);
        TextView errorTextView = findViewById(R.id.errorText);
        errorTextView.setVisibility(View.VISIBLE);
        errorTextView.setText(errorText);
        hideLastUpdatedLabel();
    }

    private void hideLastUpdatedLabel() {
        findViewById(R.id.lastUpdated).setVisibility(View.GONE);
        findViewById(R.id.lastUpdatedDateTime).setVisibility(View.GONE);
    }
}
