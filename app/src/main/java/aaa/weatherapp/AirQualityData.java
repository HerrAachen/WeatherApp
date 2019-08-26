package aaa.weatherapp;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

public class AirQualityData {

    private double aqi;
    private Double pm25;
    private Double pm10;
    private Double so2;
    private Double o3;
    private String cityName;
    private int airQualityStationId;
    private Date lastUpdated;


    public static AirQualityData parse(JSONObject response) throws JSONException {
        AirQualityData airQualityData = new AirQualityData();
        JSONObject dataObject = response.getJSONObject("data");
        airQualityData.airQualityStationId = dataObject.getInt("idx");
        airQualityData.aqi = dataObject.getDouble("aqi");
        JSONObject cityObject = dataObject.getJSONObject("city");
        airQualityData.cityName = cityObject.getString("name");
        JSONObject iaqiObject = dataObject.getJSONObject("iaqi");
        airQualityData.pm10 = getIaqiValue("pm10", iaqiObject);
        airQualityData.pm25 = getIaqiValue("pm25", iaqiObject);
        airQualityData.so2 = getIaqiValue("so2", iaqiObject);
        airQualityData.o3 = getIaqiValue("o3", iaqiObject);
        return airQualityData;
    }

    private static Double getIaqiValue(String key, JSONObject iaqiObject) throws JSONException {
        if (iaqiObject.has(key)) {
            return iaqiObject.getJSONObject(key).getDouble("v");
        }
        return null;
    }

    public double getPm25() {
        return pm25;
    }
    public double getPm10() {
        return pm10;
    }
    public double getAqi() {
        return aqi;
    }
    public double getSo2() {
        return so2;
    }
    public double getO3() {
        return o3;
    }
    public int getAirQualityStationId() {
        return airQualityStationId;
    }

    public String getCityName() {
        return cityName;
    }

    public Date getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdatedToNow() {
        lastUpdated = new Date();
    }
}
