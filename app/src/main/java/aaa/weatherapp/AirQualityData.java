package aaa.weatherapp;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

public class AirQualityData {

    private Double aqi;
    private Double pm25;
    private Double pm10;
    private Double so2;
    private Double o3;
    private Double co;
    private Double no2;
    private String cityName;
    private int airQualityStationId;
    private Date lastUpdated;
    private Date measurementDate;
    private String triggeredByOpenWeatherId;
    private List<HyperLink> attributions;


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
        airQualityData.co = getIaqiValue("co", iaqiObject);
        airQualityData.no2 = getIaqiValue("no2", iaqiObject);
        airQualityData.measurementDate = getDate(dataObject.getJSONObject("time").getLong("v"));
        airQualityData.attributions = getAttributions(dataObject);
        return airQualityData;
    }

    private static List<HyperLink> getAttributions(JSONObject dataObject) throws JSONException {
        LinkedList<HyperLink> attributions = new LinkedList<>();
        JSONArray attributionsObject = dataObject.getJSONArray("attributions");
        for(int i=0;i<attributionsObject.length();i++) {
            JSONObject attribution = (JSONObject) attributionsObject.get(i);
            String name = attribution.getString("name");
            String url = attribution.getString("url");
            HyperLink hyperLink = new HyperLink(name, url);
            attributions.add(hyperLink);
        }
        return attributions;
    }

    private static Date getDate(long epochSeconds) {
        return new Date(epochSeconds * 1000);
    }

    private static Double getIaqiValue(String key, JSONObject iaqiObject) throws JSONException {
        if (iaqiObject.has(key)) {
            return iaqiObject.getJSONObject(key).getDouble("v");
        }
        return null;
    }

    public Double getPm25() {
        return pm25;
    }
    public Double getPm10() {
        return pm10;
    }
    public Double getAqi() {
        return aqi;
    }
    public Double getSo2() {
        return so2;
    }
    public Double getO3() {
        return o3;
    }
    public Double getCo() {
        return co;
    }
    public Double getNo2() {
        return no2;
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
    public Date getMeasurementDate() {
        return measurementDate;
    }
    public String getTriggeredByOpenWeatherId() {
        return triggeredByOpenWeatherId;
    }

    public void setLastUpdatedToNow() {
        lastUpdated = new Date();
    }
    public void setTriggeredByOpenWeatherId(String openWeatherId) {
        this.triggeredByOpenWeatherId = openWeatherId;
    }
    public List<HyperLink> getAttributions() {
        return attributions;
    }
    public int getIndexLevelShortText() {
        if (aqi < 0) {
            throw new RuntimeException("AQI value should not be less than 0");
        }
        if (aqi<=50) return R.string.aqiLevelGood;
        if (aqi>50 && aqi<=100) return R.string.aqiLevelModerate;
        if (aqi>100 && aqi<=150) return R.string.aqiLevelUnhealthyForSensitive;
        if (aqi>150 && aqi<=200) return R.string.aqiLevelUnhealthy;
        if (aqi>200 && aqi<=300) return R.string.aqiLevelVeryUnhealthy;
        if (aqi>300 && aqi<=500) return R.string.aqiLevelHazardous;
        return R.string.aqiLevelOffTheChart;
    }

    public int getIndexLevelColor() {
        if (aqi < 0) {
            throw new RuntimeException("AQI value should not be less than 0");
        }
        if (aqi<=50) return R.color.airQualityIndexLevelGood;
        if (aqi>50 && aqi<=100) return R.color.airQualityIndexLevelModerate;
        if (aqi>100 && aqi<=150) return R.color.airQualityIndexLevelUnhealthyForSensitive;
        if (aqi>150 && aqi<=200) return R.color.airQualityIndexLevelUnhealthy;
        if (aqi>200 && aqi<=300) return R.color.airQualityIndexLevelVeryUnhealthy;
        if (aqi>300 && aqi<=500) return R.color.airQualityIndexLevelHazardous;
        return R.color.airQualityIndexLevelHazardous;
    }
}
