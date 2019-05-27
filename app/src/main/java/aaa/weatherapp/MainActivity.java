package aaa.weatherapp;

import android.content.Intent;
import android.net.Uri;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import org.json.JSONException;

public class MainActivity extends AppCompatActivity implements ChartFragment.OnFragmentInteractionListener {

    private ChartPagerAdapter chartPagerAdapter;
    private ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            if (!AppState.isInitialized()) {
                AppState.initialize(this.getApplicationContext());
            }
            setContentView(R.layout.activity_main);
            setCityName();

        chartPagerAdapter = new ChartPagerAdapter(getSupportFragmentManager());
        viewPager = findViewById(R.id.pager);
        viewPager.setAdapter(chartPagerAdapter);
        } catch (Exception e) {
            Log.e("Main Activity", e.getMessage());
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_settings, menu);
        return true;
    }

    private void setCityName() throws JSONException {
        if (AppState.getChartData() != null) {
            setTitle(AppState.getChartData().cityName + " (" + AppState.getChartData().country + ")");
        }
    }

    public void openSettingsView(MenuItem item) {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    public void refreshWeatherView(MenuItem item) {
        Log.e("Main", "Not implemented");
    }
}
