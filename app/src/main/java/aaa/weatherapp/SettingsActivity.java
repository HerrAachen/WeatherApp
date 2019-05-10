package aaa.weatherapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

public class SettingsActivity extends AppCompatActivity {

    private Map<String, String> id2city = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        setTitle(getString(R.string.settings));

        BufferedReader cityReader = new BufferedReader(new InputStreamReader(getResources().openRawResource(R.raw.cities)));

        try {
            String line = cityReader.readLine();
            do {
                String[] parts = line.split("=");
                id2city.put(parts[0], parts[1]);
                ((TextView) findViewById(R.id.countryText)).setText(parts[1]);
                line = cityReader.readLine();
            } while (line != null);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
