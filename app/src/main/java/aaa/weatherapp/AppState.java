package aaa.weatherapp;

import android.content.Context;
import android.content.SharedPreferences;

import io.paperdb.Paper;

public class AppState {
    private static final String VANCOUVER_ID = "6173331";
    private static final String CITY_ID_KEY = "cityId";
    private static final String COUNTRY_CODE_KEY = "countryCOde";
    private static final String LATITUDE_KEY = "latitude";
    private static final String LONGITUDE_KEY = "longitude";
    private static final String PREFERENCES_KEY = "Preferences";
    private static String cityId;
    private static String countryCode;
    private static String latitude;
    private static String longitude;
    private static Context context;
    private static ChartData chartData;

    public static void initialize(Context applicationContext) {
        context = applicationContext;
        SharedPreferences preferences = context.getSharedPreferences(PREFERENCES_KEY, 0);
        cityId = preferences.getString(CITY_ID_KEY, VANCOUVER_ID);
        countryCode = preferences.getString(COUNTRY_CODE_KEY, "ca");
        latitude = preferences.getString(LATITUDE_KEY, "54");
        longitude = preferences.getString(LONGITUDE_KEY, "-135");
        System.out.println("Initial Country Code:" + countryCode);
        Paper.init(applicationContext);
    }

    public static boolean isInitialized() {
        return context != null;
    }

    private static void setCityId(String newCityId) {
        cityId = newCityId;
        SharedPreferences preferences = context.getSharedPreferences(PREFERENCES_KEY, 0);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(CITY_ID_KEY, cityId);
        editor.commit();
    }

    private static void setCountryCode(String countryCode) {
        AppState.countryCode = countryCode;
        SharedPreferences preferences = context.getSharedPreferences(PREFERENCES_KEY, 0);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(COUNTRY_CODE_KEY, countryCode);
        editor.commit();
    }

    private static void setLatitude(String latitude) {
        AppState.latitude = latitude;
        SharedPreferences preferences = context.getSharedPreferences(PREFERENCES_KEY, 0);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(LATITUDE_KEY, latitude);
        editor.commit();
    }

    private static void setLongitude(String longitude) {
        AppState.longitude = longitude;
        SharedPreferences preferences = context.getSharedPreferences(PREFERENCES_KEY, 0);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(LONGITUDE_KEY, longitude);
        editor.commit();
    }

    public static void setLocation(Location location) {
        setCityId(location.getOpenWeatherId());
        setCountryCode(location.getCountryCode());
        setLatitude(location.getLatitude());
        setLongitude(location.getLongitude());
    }

    public static String getCityId() {
        return cityId;
    }
    public static String getCountryCode() {return countryCode;}
    public static String getLatitude() { return latitude;}
    public static String getLongitude() { return longitude;}

    public static void setChartData(ChartData inputChartData) {
        chartData = inputChartData;
    }

    public static ChartData getChartData() {
        return chartData;
    }
}
