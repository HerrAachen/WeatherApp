package aaa.weatherapp;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.Map;

public class AppState {
    private static final String VANCOUVER_ID = "6173331";
    private static final String CITY_ID_KEY = "cityId";
    private static final String PREFERENCES_KEY = "Preferences";
    private static String cityId;
    public static Map<String, String> cityList;
    private static Context context;

    public static void initialize(Context applicationContext) {
        context = applicationContext;
        SharedPreferences preferences = context.getSharedPreferences(PREFERENCES_KEY, 0);
        cityId = preferences.getString(CITY_ID_KEY, VANCOUVER_ID);
    }

    public static boolean isInitialized() {
        return context != null;
    }

    public static void setCityId(String newCityId) {
        cityId = newCityId;
        SharedPreferences preferences = context.getSharedPreferences(PREFERENCES_KEY, 0);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(CITY_ID_KEY, cityId);
        editor.commit();
    }

    public static String getCityId() {
        return cityId;
    }
}
