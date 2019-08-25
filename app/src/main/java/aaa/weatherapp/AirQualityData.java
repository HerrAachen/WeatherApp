package aaa.weatherapp;

import org.json.JSONException;
import org.json.JSONObject;

public class AirQualityData {

    private double pm25;
    private String cityName;


    public static AirQualityData parse(JSONObject response) throws JSONException {
        AirQualityData airQualityData = new AirQualityData();
        JSONObject dataObject = response.getJSONObject("data");
        JSONObject cityObject = dataObject.getJSONObject("city");
        airQualityData.cityName = cityObject.getString("name");
        JSONObject iaqiObject = dataObject.getJSONObject("iaqi");
        airQualityData.pm25 = iaqiObject.getJSONObject("pm25").getDouble("v");
        return airQualityData;
    }

    public double getPm25() {
        return pm25;
    }

    public String getCityName() {
        return cityName;
    }
}
