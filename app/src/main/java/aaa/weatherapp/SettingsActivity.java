package aaa.weatherapp;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Spinner;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class SettingsActivity extends AppCompatActivity {

    private Map<String, Location> name2Location;
    private String[] countries;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        setTitle(getString(R.string.settings));
        showLoadingIcon();

        new CityLoader(this).execute();
    }

    private void showLoadingIcon() {
        findViewById(R.id.settingsActivityLoadingIcon).setVisibility(View.VISIBLE);
        findViewById(R.id.cityDropdown).setVisibility(View.INVISIBLE);
        findViewById(R.id.cityText).setVisibility(View.INVISIBLE);
        findViewById(R.id.countryText).setVisibility(View.INVISIBLE);
        findViewById(R.id.countryDropdown).setVisibility(View.INVISIBLE);
    }

    private void showSettings() {
        findViewById(R.id.settingsActivityLoadingIcon).setVisibility(View.GONE);
        findViewById(R.id.cityDropdown).setVisibility(View.VISIBLE);
        findViewById(R.id.cityText).setVisibility(View.VISIBLE);
        findViewById(R.id.countryText).setVisibility(View.VISIBLE);
        findViewById(R.id.countryDropdown).setVisibility(View.VISIBLE);
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
        if (name2Location == null) {
            name2Location = readCitiesFromFile();
        }
        return name2Location;
    }

    private String[] readCountriesFromFileOrCache() {
        if (countries != null) {
            return countries;
        }
        return readCountriesFromFile();
    }

    private String[] readCountriesFromFile() {
        Set<String> countries = new HashSet<>();
        BufferedReader cityReader = new BufferedReader(new InputStreamReader(getResources().openRawResource(R.raw.cities)));
        try {
            String line = cityReader.readLine();
            do {
                String[] parts = line.split(";");
                String countryCode = parts[2];
                countries.add(countryCode);
                line = cityReader.readLine();
            } while (line != null);
        } catch (IOException e) {
            e.printStackTrace();
        }
        String[] countryArray = countries.toArray(new String[0]);
        Arrays.sort(countryArray);
        return countryArray;
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
        name2Location = null;
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    private class CityLoader extends AsyncTask<Void, Integer, String> {

        private final SettingsActivity parentActivity;
        public CityLoader(SettingsActivity parentActivity) {
            this.parentActivity = parentActivity;
        }

        @Override
        protected String doInBackground(Void... voids) {
            countries = readCountriesFromFileOrCache();
            name2Location = readCitiesFromFileOrCache();
            String cityId = AppState.getCityId();
            String cityLabel = null;
            System.out.println("City ID:" + cityId);
            for(Location loc: name2Location.values()) {
                if (loc.getOpenWeatherId().equals(cityId)) {
                    System.out.println("Found " + loc.getName());
                    cityLabel = loc.getDisplayName();
                }
            }
            System.out.println("City Label:" + cityLabel);
            return cityLabel;
        }

        @Override
        protected void onPostExecute(String cityDisplayName) {
            ArrayAdapter<String> countryAdapter = new ArrayAdapter<>(parentActivity,
                    android.R.layout.simple_dropdown_item_1line, countries);
            Spinner countryDropdown = findViewById(R.id.countryDropdown);
            countryDropdown.setAdapter(countryAdapter);
            setText(countryDropdown, AppState.getCountryCode());

            String[] dropdownOptions = name2Location.keySet().toArray(new String[]{});
            ArrayAdapter<String> adapter = new ArrayAdapter<>(parentActivity,
                    android.R.layout.simple_dropdown_item_1line, dropdownOptions);
            AutoCompleteTextView textView = findViewById(R.id.cityDropdown);
            textView.setAdapter(adapter);
            textView.setOnItemClickListener((parent, view, position, id) -> {
                String cityName = adapter.getItem(position);
                AppState.setCityId(name2Location.get(cityName).getOpenWeatherId());
                AppState.setCountryCode(name2Location.get(cityName).getCountryCode());
                updateMapsLink(cityName);
            });
            textView.setText(cityDisplayName);
            updateMapsLink(cityDisplayName);
            showSettings();
        }
    }

    private void setText(Spinner spinner, String text) {
        for(int i= 0; i < spinner.getAdapter().getCount(); i++)
        {
            if(spinner.getAdapter().getItem(i).toString().equals(text))
            {
                spinner.setSelection(i);
                return;
            }
        }
    }
}
