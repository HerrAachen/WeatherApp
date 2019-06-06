package aaa.weatherapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

public class SettingsActivity extends AppCompatActivity {

    private Map<String, Location> name2Location;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        setTitle(getString(R.string.settings));

        name2Location = readCitiesFromFileOrCache();
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line, name2Location.keySet().toArray(new String[]{}));
        AutoCompleteTextView textView = findViewById(R.id.cityDropdown);
        textView.setAdapter(adapter);
        textView.setOnItemClickListener((parent, view, position, id) -> {
            String cityName = adapter.getItem(position);
            AppState.setCityId(name2Location.get(cityName).getOpenWeatherId());
            updateMapsLink(cityName);
        });
    }

    private void updateMapsLink(String cityName) {
        Location location = name2Location.get(cityName);
        TextView locationMapsLinkView = findViewById(R.id.locationMapsLink);
        locationMapsLinkView.setText(Html.fromHtml(createGoogleMapsLink(location)));
        locationMapsLinkView.setMovementMethod(LinkMovementMethod.getInstance());
    }

    private String createGoogleMapsLink(Location location) {
        return "<a href=\"https://www.google.com/maps/search/?api=1&query=" +
                location.getLatitude() + "," +
                location.getLongitude() +
                "\">View " + location.getName() + " in Maps</a>";
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.settings_actions, menu);
        return true;
    }

    private Map<String, Location> readCitiesFromFileOrCache() {
        if (AppState.cityList == null) {
            AppState.cityList = readCitiesFromFile();
        }
        return AppState.cityList;
    }

    private Map<String, Location> readCitiesFromFile() {

        Map<String, Location> nameToLocation = new HashMap<>();
        BufferedReader cityReader = new BufferedReader(new InputStreamReader(getResources().openRawResource(R.raw.cities)));

        try {
            String line = cityReader.readLine();
            do {
                String[] parts = line.split(";");
                String id = parts[0];
                String cityName = parts[1];
                String countryCode = parts[2];
                String latitude = parts[3];
                String longitude = parts[4];
                Location location = new Location(cityName, countryCode, id, latitude, longitude);
                nameToLocation.put(location.getDisplayName(), location);
                line = cityReader.readLine();
            } while (line != null);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return nameToLocation;
    }

    public void navigateToMainActivity(MenuItem item) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}
