import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.*;
import java.util.stream.Collectors;

public class CityFileConverter {
    public static void main(String[] args) {
        JSONParser jsonParser = new JSONParser();
        Set<String> countries = new HashSet<>();
        try (FileReader reader = new FileReader("openweathermap.city.list.json")) {
            Map<Long, String> id2CityName = new HashMap<>();
            Map<String, Integer> cityNameToCount = new HashMap<>();
            JSONArray cities = (JSONArray) jsonParser.parse(reader);
            cities.forEach(cityJson -> {
                JSONObject cityJsonObject = (JSONObject) cityJson;
                String cityName = (String) cityJsonObject.get("name");
                String countryCode = ((String) cityJsonObject.get("country")).toLowerCase();
                long cityId = (long) cityJsonObject.get("id");
                String lat = getNumberAsString(((JSONObject) cityJsonObject.get("coord")).get("lat"));
                String longitude = getNumberAsString(((JSONObject) cityJsonObject.get("coord")).get("lon"));
                countries.add(countryCode);
                updateCityCount(cityNameToCount, getCityDisplayName(cityName, countryCode));
                id2CityName.put(cityId, getCityDisplayName(cityName, countryCode));
                try {
                    Integer count = cityNameToCount.get(getCityDisplayName(cityName, countryCode));
                    String cityNameForFile = cityName + (count > 1 ? "_" + count : "");
                    writeToFile(countryCode, cityId, lat, longitude, cityNameForFile);

                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            writeCountryFile(countries);

        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
    }

    private static void writeCountryFile(Set<String> countries) throws IOException {
        ArrayList<String> sortedCountries = new ArrayList<>(countries);
        Collections.sort(sortedCountries);
        byte[] countriesText = sortedCountries.stream().collect(Collectors.joining("\r\n")).getBytes();
        Files.write(Paths.get("..\\app\\src\\main\\res\\raw\\countries.txt"), countriesText, StandardOpenOption.CREATE);
    }

    private static void writeToFile(String country, long cityId, String lat, String longitude, String cityNameForFile) throws IOException {
        try (FileWriter writer = new FileWriter("..\\app\\src\\main\\res\\raw\\cities_" + country + ".properties", true)) {
            writer.write(cityId + ";" + cityNameForFile + ";" + lat + ";" + longitude + "\r\n");
        }
    }

    private static void updateCityCount(Map<String, Integer> cityNameToCount, String cityDisplayName) {
        Integer cityCount = cityNameToCount.get(cityDisplayName);
        if (cityCount == null) {
            cityCount = 0;
        }
        cityCount++;
        cityNameToCount.put(cityDisplayName, cityCount);
    }

    private static String getCityDisplayName(String cityName, String cityCountry) {
        return cityName + ", " + cityCountry;
    }

    private static String getNumberAsString(Object numberObject) {
        return String.valueOf(numberObject);
    }
}
