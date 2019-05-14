package aaa.weatherapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

public class SettingsActivity extends AppCompatActivity {

    private Map<String, String> city2id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        setTitle(getString(R.string.settings));

        city2id = readCitiesFromFileOrCache();
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line, city2id.keySet().toArray(new String[]{}));
        AutoCompleteTextView textView = findViewById(R.id.cityDropdown);
        textView.setAdapter(adapter);
        textView.setOnItemClickListener((parent, view, position, id) -> {
            String cityName = adapter.getItem(position);
            AppState.cityId = city2id.get(cityName);
        });
    }
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.settings_actions, menu);
        return true;
    }

    private Map<String, String> readCitiesFromFileOrCache() {
        if (AppState.cityList == null) {
            AppState.cityList = readCitiesFromFile();
        }
        return AppState.cityList;
    }

    private Map<String, String> readCitiesFromFile() {

        Map<String, String> id2city = new HashMap<>();
        BufferedReader cityReader = new BufferedReader(new InputStreamReader(getResources().openRawResource(R.raw.cities)));

        try {
            String line = cityReader.readLine();
            do {
                String[] parts = line.split("=");
                String id = parts[0];
                String cityName = parts[1];
                id2city.put(cityName, id);
                line = cityReader.readLine();
            } while (line != null);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return id2city;
    }

    public void navigateToMainActivity(MenuItem item) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}
