package aaa.weatherapp;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class SettingsActivity extends AppCompatActivity implements OnMapReadyCallback {

    private Map<String, Location> name2Location;
    private Map<String, String> countries;
    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        setTitle(getString(R.string.settings));
        showLoadingIcon();
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        new CountryLoader(this).execute();
        CheckBox checkBox = findViewById(R.id.darkMode);
        checkBox.setChecked(AppState.isDarkModeEnabled());
        checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            AppState.setDarkMode(isChecked);
        });
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

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.settings_actions, menu);
        return true;
    }

    private Map<String, Location> readCitiesFromFileOrCache(String countryCode) {
        return readCitiesFromFile(countryCode);
    }

    private Map<String, String> readCountriesFromFileOrCache() {
        if (countries != null) {
            return countries;
        }
        return readCountriesFromFile();
    }

    private Map<String, String> readCountriesFromFile() {
        Map<String, String> countries = new HashMap<>();
        BufferedReader countryReader = new BufferedReader(new InputStreamReader(getResources().openRawResource(R.raw.countries)));
        try {
            String line = countryReader.readLine();
            do {
                String[] parts = line.split(",");
                if (parts.length > 1) {
                    String countryCode = parts[0];
                    String countryName = parts[1];
                    countries.put(countryName, countryCode);
                }
                line = countryReader.readLine();
            } while (line != null);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return countries;
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

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
    }

    private void focusMapOn(Location location) {
        LatLng geoCoordinates = new LatLng(Double.parseDouble(location.getLatitude()), Double.parseDouble(location.getLongitude()));
        mMap.clear();
        mMap.addMarker(new MarkerOptions().position(geoCoordinates).title(location.getDisplayName()));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(geoCoordinates, 10.0f));
    }

    private class CityLoader extends AsyncTask<Void, Integer, String> {

        private final SettingsActivity parentActivity;
        private final String countryCode;
        private final String preSelectedCityId;
        public CityLoader(SettingsActivity parentActivity, String countryCode, String preSelectedCityId) {
            this.parentActivity = parentActivity;
            this.countryCode = countryCode;
            this.preSelectedCityId = preSelectedCityId;
            System.out.println("New City Dropdown for country: " + this.countryCode);
        }

        @Override
        protected String doInBackground(Void... voids) {
            name2Location = readCitiesFromFileOrCache(this.countryCode);
            String cityLabel = null;
            if (this.preSelectedCityId != null) {
                for (Location loc : name2Location.values()) {
                    if (loc.getOpenWeatherId().equals(this.preSelectedCityId)) {
                        cityLabel = loc.getDisplayName();
                    }
                }
            }
            return cityLabel;
        }

        @Override
        protected void onPostExecute(String cityDisplayName) {

            String[] dropdownOptions = name2Location.keySet().toArray(new String[]{});
            ArrayAdapter<String> adapter = new ArrayAdapter<>(parentActivity,
                    android.R.layout.simple_dropdown_item_1line, dropdownOptions);
            AutoCompleteTextView cityTextView = findViewById(R.id.cityDropdown);
            cityTextView.setAdapter(adapter);
            System.out.println("Setting city dropdown. First option: " + dropdownOptions[0]);
            cityTextView.setOnItemClickListener((parent, view, position, id) -> {
                String cityName = adapter.getItem(position);
                Location location = name2Location.get(cityName);
                AppState.setLocation(location);
                focusMapOn(location);
            });
            if (cityDisplayName!= null) {
                cityTextView.setText(cityDisplayName);
                focusMapOn(name2Location.get(cityDisplayName));
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
            String[] countryOptions = getCountryDropdownOptions();
            ArrayAdapter<String> countryAdapter = new ArrayAdapter<>(parentActivity,
                    android.R.layout.simple_dropdown_item_1line, countryOptions);
            Spinner countryDropdown = findViewById(R.id.countryDropdown);
            countryDropdown.setAdapter(countryAdapter);
            setText(countryDropdown, getCountryFromCode(AppState.getCountryCode()));
            countryDropdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    String selectedCountry = countryAdapter.getItem(position);
                    System.out.println("Selected country: " + selectedCountry);
                    String selectedCountryCode = countries.get(selectedCountry);
                    new CityLoader(parentActivity, selectedCountryCode, AppState.getCityId()).execute();
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
            new CityLoader(this.parentActivity, AppState.getCountryCode(), AppState.getCityId()).execute();
        }
    }

    private String getCountryFromCode(String countryCode) {
        for(Map.Entry<String,String> countryNameToCode: countries.entrySet()) {
            if (countryNameToCode.getValue().equals(countryCode)) {
                return countryNameToCode.getKey();
            }
        }
        throw new RuntimeException("Could not find country name for code:" + countryCode);
    }

    private String[] getCountryDropdownOptions() {
        List<String> countryDropdownOptions = new LinkedList<>();
        for(String countryLongName: countries.keySet()) {
            countryDropdownOptions.add(countryLongName);
        }
        Collections.sort(countryDropdownOptions);
        return countryDropdownOptions.toArray(new String[0]);
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
