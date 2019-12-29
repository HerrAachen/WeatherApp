package aaa.weatherapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import io.paperdb.Paper;

public class AppState {
    private static final String VANCOUVER_ID = "6173331";
    private static final String CITY_ID_KEY = "cityId";
    private static final String COUNTRY_CODE_KEY = "countryCOde";
    private static final String CITY_2_ID_KEY = "city2Id";
    private static final String COUNTRY_2_CODE_KEY = "country2Code";
    private static final String LATITUDE_KEY = "latitude";
    private static final String LONGITUDE_KEY = "longitude";
    private static final String LATITUDE_2_KEY = "latitude2";
    private static final String LONGITUDE_2_KEY = "longitude2";
    private static final String PREFERENCES_KEY = "Preferences";
    private static final String DARK_MODE_KEY = "DarkMode";
    private static String cityId;
    private static String countryCode;
    private static String latitude;
    private static String longitude;
    private static String city2Id;
    private static String country2Code;
    private static String latitude2;
    private static String longitude2;
    private static Context context;
    private static ChartData chartData;
    private static AirQualityData airQualityData;
    private static boolean darkMode;

    public static void initialize(Context applicationContext) {
        context = applicationContext;
        SharedPreferences preferences = context.getSharedPreferences(PREFERENCES_KEY, 0);
        cityId = preferences.getString(CITY_ID_KEY, VANCOUVER_ID);
        countryCode = preferences.getString(COUNTRY_CODE_KEY, "ca");
        latitude = preferences.getString(LATITUDE_KEY, "54");
        longitude = preferences.getString(LONGITUDE_KEY, "-135");
        city2Id = preferences.getString(CITY_2_ID_KEY, VANCOUVER_ID);
        country2Code = preferences.getString(COUNTRY_2_CODE_KEY, "ca");
        latitude2 = preferences.getString(LATITUDE_2_KEY, "54");
        longitude2 = preferences.getString(LONGITUDE_2_KEY, "-135");
        darkMode = preferences.getBoolean(DARK_MODE_KEY, false);
        Log.i("AppState", "Initial Country Code:" + countryCode);
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

    private static void setCity2Id(String newCityId) {
        city2Id = newCityId;
        SharedPreferences preferences = context.getSharedPreferences(PREFERENCES_KEY, 0);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(CITY_2_ID_KEY, newCityId);
        editor.commit();
    }

    private static void setCountry2Code(String countryCode) {
        AppState.country2Code = countryCode;
        SharedPreferences preferences = context.getSharedPreferences(PREFERENCES_KEY, 0);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(COUNTRY_2_CODE_KEY, countryCode);
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

    private static void setLatitude2(String latitude) {
        AppState.latitude2 = latitude;
        SharedPreferences preferences = context.getSharedPreferences(PREFERENCES_KEY, 0);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(LATITUDE_2_KEY, latitude);
        editor.commit();
    }

    private static void setLongitude2(String longitude) {
        AppState.longitude2 = longitude;
        SharedPreferences preferences = context.getSharedPreferences(PREFERENCES_KEY, 0);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(LONGITUDE_2_KEY, longitude);
        editor.commit();
    }

    public static void setLocation(Location location, int locationIndex) {
        if (locationIndex == 0) {
            setCityId(location.getOpenWeatherId());
            setCountryCode(location.getCountryCode());
            setLatitude(location.getLatitude());
            setLongitude(location.getLongitude());
        } else if (locationIndex == 1) {
            setCity2Id(location.getOpenWeatherId());
            setCountry2Code(location.getCountryCode());
            setLatitude2(location.getLatitude());
            setLongitude2(location.getLongitude());
        }
    }

    public static void setDarkMode(boolean enableDarkMode) {
        AppState.darkMode = enableDarkMode;
        SharedPreferences preferences = context.getSharedPreferences(PREFERENCES_KEY, 0);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(DARK_MODE_KEY, darkMode);
        editor.commit();
    }

    public static String getCityId(int locationIndex) {
        if (locationIndex == 0) {
            return cityId;
        } else if (locationIndex == 1) {
            return city2Id;
        }
        return throwUnexpectedLocationIndex(locationIndex);
    }
    public static String getCountryCode(int locationIndex) {
        if (locationIndex == 0) {
            return countryCode;
        } else if (locationIndex == 1) {
            return country2Code;
        }
        return throwUnexpectedLocationIndex(locationIndex);
    }

    private static String throwUnexpectedLocationIndex(int locationIndex) {
        throw new RuntimeException("Unexpected locationIndex:" + locationIndex);
    }
    public static String getLatitude() { return latitude;}
    public static String getLongitude() { return longitude;}

    public static void setChartData(ChartData inputChartData) {
        chartData = inputChartData;
    }

    public static void setAirQualityData(AirQualityData inputAirQualityData) {
        airQualityData = inputAirQualityData;
    }
    public static AirQualityData getAirQualityData() {
        return airQualityData;
    }

    public static ChartData getChartData() {
        return chartData;
    }
    public static boolean isDarkModeEnabled() { return darkMode; }
}
