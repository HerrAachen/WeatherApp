package aaa.weatherapp;

public class Location {
    private String name;
    private String countryCode;
    private String openWeatherId;
    private String latitude;
    private String longitude;

    public Location(String name, String countryCode, String openWeatherId, String latitude, String longitude) {
        this.name = name;
        this.countryCode = countryCode;
        this.openWeatherId = openWeatherId;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getName() {
        return name;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public String getOpenWeatherId() {
        return openWeatherId;
    }

    public String getLatitude() {
        return latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public String getDisplayName() {
        return name + " (" + countryCode + ")";
    }
}
