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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Spinner;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
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

        new CountryLoader(this).execute();
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

    private Map<String, Location> readCitiesFromFileOrCache(String countryCode) {
        if (name2Location == null) {
            name2Location = readCitiesFromFile(countryCode);
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
        BufferedReader countryReader = new BufferedReader(new InputStreamReader(getResources().openRawResource(R.raw.countries)));
        try {
            String line = countryReader.readLine();
            do {
                String countryCode = line;
                countries.add(countryCode);
                line = countryReader.readLine();
            } while (line != null);
        } catch (IOException e) {
            e.printStackTrace();
        }
        String[] countryArray = countries.toArray(new String[0]);
        Arrays.sort(countryArray);
        return countryArray;
    }

    private Map<String, Location> readCitiesFromFile(String countryCode) {

        Map<String, Location> nameToLocation = new HashMap<>();
        BufferedReader cityReader = new BufferedReader(new InputStreamReader(getResources().openRawResource(getCityFile(countryCode))));

        try {
            String line = cityReader.readLine();
            do {
                String[] parts = line.split(";");
                String id = parts[0];
                String cityName = parts[1];
                String latitude = parts[2];
                String longitude = parts[3];
                Location location = new Location(cityName, countryCode, id, latitude, longitude);
                nameToLocation.put(location.getDisplayName(), location);
                line = cityReader.readLine();
            } while (line != null);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return nameToLocation;
    }

    private int getCityFile(String countryCode) {
        Field[] fields = R.raw.class.getFields();
        try {
            for (int i = 0; i < fields.length - 1; i++) {
                String name = fields[i].getName();
                if (name.equals("cities_" + countryCode)) {
                        return fields[i].getInt(null);
                }
            }
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
        throw new IllegalArgumentException("Could not find file for country code " + countryCode);
    }

    public void navigateToMainActivity(MenuItem item) {
        name2Location = null;
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    private class CityLoader extends AsyncTask<Void, Integer, String> {

        private final SettingsActivity parentActivity;
        private final String countryCode;
        private final String preSelectedCityId;
        public CityLoader(SettingsActivity parentActivity, String countryCode, String preSelectedCityId) {
            this.parentActivity = parentActivity;
            this.countryCode = countryCode;
            this.preSelectedCityId = preSelectedCityId;
        }

        @Override
        protected String doInBackground(Void... voids) {
            name2Location = readCitiesFromFileOrCache(this.countryCode);
            String cityLabel = null;
            if (this.preSelectedCityId != null) {
                for (Location loc : name2Location.values()) {
                    if (loc.getOpenWeatherId().equals(this.preSelectedCityId)) {
                        System.out.println("Found " + loc.getName());
                        cityLabel = loc.getDisplayName();
                    }
                }
            }
            System.out.println("City Label:" + cityLabel);
            return cityLabel;
        }

        @Override
        protected void onPostExecute(String cityDisplayName) {

            String[] dropdownOptions = name2Location.keySet().toArray(new String[]{});
            ArrayAdapter<String> adapter = new ArrayAdapter<>(parentActivity,
                    android.R.layout.simple_dropdown_item_1line, dropdownOptions);
            AutoCompleteTextView cityTextView = findViewById(R.id.cityDropdown);
            cityTextView.setAdapter(adapter);
            cityTextView.setOnItemClickListener((parent, view, position, id) -> {
                String cityName = adapter.getItem(position);
                AppState.setCityId(name2Location.get(cityName).getOpenWeatherId());
                AppState.setCountryCode(name2Location.get(cityName).getCountryCode());
                updateMapsLink(cityName);
            });
            if (cityDisplayName!= null) {
                cityTextView.setText(cityDisplayName);
                updateMapsLink(cityDisplayName);
            } else {
                cityTextView.setText("");
            }
            showSettings();
        }
    }

    private class CountryLoader extends AsyncTask<Void, Integer, Void> {

        private final SettingsActivity parentActivity;
        public CountryLoader(SettingsActivity parentActivity) {
            this.parentActivity = parentActivity;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            countries = readCountriesFromFileOrCache();
            return null;
        }

        @Override
        protected void onPostExecute(Void v) {
            ArrayAdapter<String> countryAdapter = new ArrayAdapter<>(parentActivity,
                    android.R.layout.simple_dropdown_item_1line, countries);
            Spinner countryDropdown = findViewById(R.id.countryDropdown);
            countryDropdown.setAdapter(countryAdapter);
            setText(countryDropdown, AppState.getCountryCode());
            countryDropdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    String selectedCountryCode = countryAdapter.getItem(position);
                    System.out.println("Selected country " + selectedCountryCode);
                    new CityLoader(parentActivity, selectedCountryCode, null).execute();
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
            new CityLoader(this.parentActivity, AppState.getCountryCode(), AppState.getCityId()).execute();
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
