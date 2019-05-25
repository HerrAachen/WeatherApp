package aaa.weatherapp;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
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

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private WeatherApiClient weatherApiClient;
    private static SimpleDateFormat lastUpdatedDateFormat = new SimpleDateFormat("E hh:mm a");
    private ChartView viewToShow = ChartView.DAY;

    private enum ChartView {
        DAY,
        FIVE_DAYS
    }

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_settings, menu);
        return true;
    }

    private void updateChart(ChartData chartData) {
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
        chart.getXAxis().setLabelRotationAngle(-15);
        configureRainAxis(chartData, chart.getAxisRight());
        configureTemperatureAxis(chartData, chart.getAxisLeft());
        chart.setDescription(null);
        Runnable swipeRightAction = () -> {
            if (viewToShow != ChartView.DAY) {
                viewToShow = ChartView.DAY;
                refreshWeatherView(null);
            }
        };
        Runnable swipeLeftAction = () -> {
            if (viewToShow != ChartView.FIVE_DAYS) {
                viewToShow = ChartView.FIVE_DAYS;
                refreshWeatherView(null);
            }
        };
        chart.setOnChartGestureListener(new ChartGestureListener(swipeLeftAction, swipeRightAction));
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

    private void setCityName(ChartData chartData) throws JSONException {
        setTitle(chartData.cityName + " (" + chartData.country + ")");
    }

    public void openSettingsView(MenuItem item) {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }

    public void refreshWeatherView(MenuItem item) {
        showLoadingScreen();
        weatherApiClient.getAndCacheForecast(AppState.getCityId(), fullChartData -> {
            try {
                ChartData chartData;
                if (viewToShow == ChartView.DAY) {
                    chartData = fullChartData.getSubSet(9);
                } else {
                    chartData = fullChartData;
                }

                setCityName(chartData);
                updateChart(chartData);
                showChart(chartData);
                showLastUpdatedLabel(chartData);
            } catch (JSONException e) {
                showError(e.getMessage());
                e.printStackTrace();
            }
        }, errorMessage -> showError("Error: " + errorMessage));
    }

    private void showLastUpdatedLabel(ChartData chartData) {
        findViewById(R.id.lastUpdated).setVisibility(View.VISIBLE);
        findViewById(R.id.lastUpdatedDateTime).setVisibility(View.VISIBLE);
        ((TextView) findViewById(R.id.lastUpdatedDateTime)).setText(lastUpdatedDateFormat.format(chartData.getLastUpdated()));
    }

    private void showChart(ChartData chartData) {
        findViewById(R.id.mainActivityLoadingIcon).setVisibility(View.GONE);
        findViewById(R.id.temperatureChart).setVisibility(View.VISIBLE);
        findViewById(R.id.errorText).setVisibility(View.GONE);
        showLastUpdatedLabel(chartData);
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
