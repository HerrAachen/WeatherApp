package aaa.weatherapp;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Date;

public class AboutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        setTitle(getString(R.string.about));
        ((TextView)findViewById(R.id.versionValue)).setText(getVersionName());
        ((TextView)findViewById(R.id.buildTimeValue)).setText(new Date(BuildConfig.buildTime).toString());
    }

    private String getVersionName() {
        try {
            PackageInfo packageInfo = getApplicationContext().getPackageManager().getPackageInfo(getPackageName(), 0);
            return packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return this.getApplicationContext().getString(R.string.couldNotRetrieveVersion);
        }
    }
}
