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

    private static Map<String, String> getAcronyms() {
        Map<String, String> countryAcronyms = new HashMap<>();
        countryAcronyms.put("ad", "Andorra");
        countryAcronyms.put("ae", "United Arab Emirates");
        countryAcronyms.put("af", "Afghanistan");
        countryAcronyms.put("ag", "Antigua and Barbuda");
        countryAcronyms.put("ai", "Anguilla");
        countryAcronyms.put("al", "Albania");
        countryAcronyms.put("am", "Armenia");
        countryAcronyms.put("ao", "Angola");
        countryAcronyms.put("aq", "Antartica");
        countryAcronyms.put("ar", "Argentina");
        countryAcronyms.put("as", "American Samoa");
        countryAcronyms.put("at", "Austria");
        countryAcronyms.put("ca", "Canada");
        countryAcronyms.put("de", "Germany");
        countryAcronyms.put("us", "USA");
        countryAcronyms.put("au", "Australia");
        countryAcronyms.put("aw", "Aruba");
        countryAcronyms.put("ax", "Aland Islands");
        countryAcronyms.put("az", "Azerbaijan");
        countryAcronyms.put("ba", "Bosnia and Herzegovina");
        countryAcronyms.put("bb", "Barbados");
        countryAcronyms.put("bd", "Bangladesh");
        countryAcronyms.put("be", "Belgium");
        countryAcronyms.put("bf", "Burkina Faso");
        countryAcronyms.put("bg", "Bulgaria");
        countryAcronyms.put("bh", "Bahrain");
        countryAcronyms.put("bi", "Burundi");
        countryAcronyms.put("bj", "Benin");
        countryAcronyms.put("bl", "Saint Barthélemy");
        countryAcronyms.put("bm", "Bermuda");
        countryAcronyms.put("bn", "Brunei Darussalam");
        countryAcronyms.put("bo", "Bolivia");
        countryAcronyms.put("bq", "Bonaire");
        countryAcronyms.put("br", "Brazil");
        countryAcronyms.put("bs", "Bahamas");
        countryAcronyms.put("bt", "Bhutan");
        countryAcronyms.put("bw", "Botswana");
        countryAcronyms.put("by", "Belarus");
        countryAcronyms.put("bz", "Belize");
        countryAcronyms.put("cc", "Cocos Islands");
        countryAcronyms.put("cd", "Congo (Democratic Republic)");
        countryAcronyms.put("cf", "Central African Republic");
        countryAcronyms.put("cg", "Congo");
        countryAcronyms.put("ch", "Switzerland");
        countryAcronyms.put("ci", "Côte d'Ivoire");
        countryAcronyms.put("ck", "Cook Islands");
        countryAcronyms.put("cl", "Chile");
        countryAcronyms.put("cm", "Cameroon");
        countryAcronyms.put("cn", "China");
        countryAcronyms.put("co", "Colombia");
        countryAcronyms.put("cr", "Costa Rica");
        countryAcronyms.put("cu", "Cuba");
        countryAcronyms.put("cv", "Cabo Verde");
        countryAcronyms.put("cw", "Curaçao");
        countryAcronyms.put("cx", "Christmas Island");
        countryAcronyms.put("cy", "Cyprus");
        countryAcronyms.put("cz", "Czechia");
        countryAcronyms.put("dj", "Djibouti");
        countryAcronyms.put("dk", "Denmark");
        countryAcronyms.put("dm", "Dominica");
        countryAcronyms.put("do", "Dominican Republic");
        countryAcronyms.put("dz", "Algeria");
        countryAcronyms.put("ec", "Ecuador");
        countryAcronyms.put("ee", "Estonia");
        countryAcronyms.put("eg", "Egypt");
        countryAcronyms.put("eh", "Western Sahara");
        countryAcronyms.put("er", "Eritrea");
        countryAcronyms.put("es", "Spain");
        countryAcronyms.put("et", "Ethiopia");
        countryAcronyms.put("fi", "Finland");
        countryAcronyms.put("fj", "Fiji");
        countryAcronyms.put("fk", "Falkland Islands (Malvinas)");
        countryAcronyms.put("fm", "Micronesia (Federated States of)");
        countryAcronyms.put("fo", "Faroe Islands");
        countryAcronyms.put("fr", "France");
        countryAcronyms.put("ga", "Gabon");
        countryAcronyms.put("gb", "United Kingdom of Great Britain and Northern Ireland");
        countryAcronyms.put("gd", "Grenada");
        countryAcronyms.put("ge", "Georgia");
        countryAcronyms.put("gf", "French Guiana");
        countryAcronyms.put("gg", "Guernsey");
        countryAcronyms.put("gh", "Ghana");
        countryAcronyms.put("gi", "Gibraltar");
        countryAcronyms.put("gl", "Greenland");
        countryAcronyms.put("gm", "Gambia");
        countryAcronyms.put("gn", "Guinea");
        countryAcronyms.put("gp", "Guadeloupe");
        countryAcronyms.put("gq", "Equatorial Guinea");
        countryAcronyms.put("gr", "Greece");
        countryAcronyms.put("gs", "South Georgia and the South Sandwich Islands");
        countryAcronyms.put("gt", "Guatemala");
        countryAcronyms.put("gu", "Guam");
        countryAcronyms.put("gw", "Guinea-Bissau");
        countryAcronyms.put("gy", "Guyana");
        countryAcronyms.put("hk", "Hong Kong");
        countryAcronyms.put("hn", "Honduras");
        countryAcronyms.put("hr", "Croatia");
        countryAcronyms.put("ht", "Haiti");
        countryAcronyms.put("hu", "Hungary");
        countryAcronyms.put("id", "Indonesia");
        countryAcronyms.put("ie", "Ireland");
        countryAcronyms.put("il", "Israel");
        countryAcronyms.put("im", "Isle of Man");
        countryAcronyms.put("in", "India");
        countryAcronyms.put("iq", "Iraq");
        countryAcronyms.put("ir", "Iran (Islamic Republic of)");
        countryAcronyms.put("is", "Iceland");
        countryAcronyms.put("it", "Italy");
        countryAcronyms.put("je", "Jersey");
        countryAcronyms.put("jm", "Jamaica");
        countryAcronyms.put("jo", "Jordan");
        countryAcronyms.put("jp", "Japan");
        countryAcronyms.put("ke", "Kenya");
        countryAcronyms.put("kg", "Kyrgyzstan");
        countryAcronyms.put("kh", "Cambodia");
        countryAcronyms.put("ki", "Kiribati");
        countryAcronyms.put("km", "Comoros");
        countryAcronyms.put("kn", "Saint Kitts and Nevis");
        countryAcronyms.put("kp", "Korea (Democratic People's Republic of)");
        countryAcronyms.put("kr", "Korea, Republic of");
        countryAcronyms.put("kw", "Kuwait");
        countryAcronyms.put("ky", "Cayman Islands");
        countryAcronyms.put("kz", "Kazakhstan");
        countryAcronyms.put("la", "Lao People's Democratic Republic");
        countryAcronyms.put("lb", "Lebanon");
        countryAcronyms.put("lc", "Saint Lucia");
        countryAcronyms.put("li", "Liechtenstein");
        countryAcronyms.put("lk", "Sri Lanka");
        countryAcronyms.put("lr", "Liberia");
        countryAcronyms.put("ls", "Lesotho");
        countryAcronyms.put("lt", "Lithuania");
        countryAcronyms.put("lu", "Luxembourg");
        countryAcronyms.put("lv", "Latvia");
        countryAcronyms.put("ly", "Libya");
        countryAcronyms.put("ma", "Morocco");
        countryAcronyms.put("mc", "Monaco");
        countryAcronyms.put("md", "Moldova, Republic of");
        countryAcronyms.put("me", "Montenegro");
        countryAcronyms.put("mf", "Saint Martin (French part)");
        countryAcronyms.put("mg", "Madagascar");
        countryAcronyms.put("mh", "Marshall Islands");
        countryAcronyms.put("mk", "North Macedonia");
        countryAcronyms.put("ml", "Mali");
        countryAcronyms.put("mm", "Myanmar");
        countryAcronyms.put("mn", "Mongolia");
        countryAcronyms.put("mo", "Macao");
        countryAcronyms.put("mp", "Northern Mariana Islands");
        countryAcronyms.put("mq", "Martinique");
        countryAcronyms.put("mr", "Mauritania");
        countryAcronyms.put("ms", "Montserrat");
        countryAcronyms.put("mt", "Malta");
        countryAcronyms.put("mu", "Mauritius");
        countryAcronyms.put("mv", "Maldives");
        countryAcronyms.put("mw", "Malawi");
        countryAcronyms.put("mx", "Mexico");
        countryAcronyms.put("my", "Malaysia");
        countryAcronyms.put("mz", "Mozambique");
        countryAcronyms.put("na", "Namibia");
        countryAcronyms.put("nc", "New Caledonia");
        countryAcronyms.put("ne", "Niger");
        countryAcronyms.put("nf", "Norfolk Island");
        countryAcronyms.put("ng", "Nigeria");
        countryAcronyms.put("ni", "Nicaragua");
        countryAcronyms.put("nl", "Netherlands");
        countryAcronyms.put("no", "Norway");
        countryAcronyms.put("np", "Nepal");
        countryAcronyms.put("nr", "Nauru");
        countryAcronyms.put("nu", "Niue");
        countryAcronyms.put("nz", "New Zealand");
        countryAcronyms.put("om", "Oman");
        countryAcronyms.put("pa", "Panama");
        countryAcronyms.put("pe", "Peru");
        countryAcronyms.put("pf", "French Polynesia");
        countryAcronyms.put("pg", "Papua New Guinea");
        countryAcronyms.put("ph", "Philippines");
        countryAcronyms.put("pk", "Pakistan");
        countryAcronyms.put("pl", "Poland");
        countryAcronyms.put("pm", "Saint Pierre and Miquelon");
        countryAcronyms.put("pn", "Pitcairn");
        countryAcronyms.put("pr", "Puerto Rico");
        countryAcronyms.put("ps", "Palestine");
        countryAcronyms.put("pt", "Portugal");
        countryAcronyms.put("pw", "Palau");
        countryAcronyms.put("py", "Paraguay");
        countryAcronyms.put("qa", "Qatar");
        countryAcronyms.put("re", "Réunion");
        countryAcronyms.put("ro", "Romania");
        countryAcronyms.put("rs", "Serbia");
        countryAcronyms.put("ru", "Russian Federation");
        countryAcronyms.put("rw", "Rwanda");
        countryAcronyms.put("sa", "Saudi Arabia");
        countryAcronyms.put("sb", "Solomon Islands");
        countryAcronyms.put("sc", "Seychelles");
        countryAcronyms.put("sd", "Sudan");
        countryAcronyms.put("se", "Sweden");
        countryAcronyms.put("sg", "Singapore");
        countryAcronyms.put("sh", "Saint Helena, Ascension and Tristan da Cunha");
        countryAcronyms.put("si", "Slovenia");
        countryAcronyms.put("sj", "Svalbard and Jan Mayen");
        countryAcronyms.put("sk", "Slovakia");
        countryAcronyms.put("sl", "Sierra Leone");
        countryAcronyms.put("sm", "San Marino");
        countryAcronyms.put("sn", "Senegal");
        countryAcronyms.put("so", "Somalia");
        countryAcronyms.put("sr", "Suriname");
        countryAcronyms.put("ss", "South Sudan");
        countryAcronyms.put("st", "Sao Tome and Principe");
        countryAcronyms.put("sv", "El Salvador");
        countryAcronyms.put("sx", "Sint Maarten (Dutch part)");
        countryAcronyms.put("sy", "Syrian Arab Republic");
        countryAcronyms.put("sz", "Eswatini");
        countryAcronyms.put("tc", "Turks and Caicos Islands");
        countryAcronyms.put("td", "Chad");
        countryAcronyms.put("tf", "French Southern Territories");
        countryAcronyms.put("tg", "Togo");
        countryAcronyms.put("th", "Thailand");
        countryAcronyms.put("tj", "Tajikistan");
        countryAcronyms.put("tk", "Tokelau");
        countryAcronyms.put("tl", "Timor-Leste");
        countryAcronyms.put("tm", "Turkmenistan");
        countryAcronyms.put("tn", "Tunisia");
        countryAcronyms.put("to", "Tonga");
        countryAcronyms.put("tr", "Turkey");
        countryAcronyms.put("tt", "Trinidad and Tobago");
        countryAcronyms.put("tv", "Tuvalu");
        countryAcronyms.put("tw", "Taiwan, Province of China[a]");
        countryAcronyms.put("tz", "Tanzania, United Republic of");
        countryAcronyms.put("ua", "Ukraine");
        countryAcronyms.put("ug", "Uganda");
        countryAcronyms.put("uy", "Uruguay");
        countryAcronyms.put("uz", "Uzbekistan");
        countryAcronyms.put("va", "Holy See");
        countryAcronyms.put("vc", "Saint Vincent and the Grenadines");
        countryAcronyms.put("ve", "Venezuela (Bolivarian Republic of)");
        countryAcronyms.put("vg", "Virgin Islands (British)");
        countryAcronyms.put("vi", "Virgin Islands (U.S.)");
        countryAcronyms.put("vn", "Viet Nam");
        countryAcronyms.put("vu", "Vanuatu");
        countryAcronyms.put("wf", "Wallis and Futuna");
        countryAcronyms.put("ws", "Samoa");
        countryAcronyms.put("ye", "Yemen");
        countryAcronyms.put("yt", "Mayotte");
        countryAcronyms.put("za", "South Africa");
        countryAcronyms.put("zm", "Zambia");
        countryAcronyms.put("zw", "Zimbabwe");
        countryAcronyms.put("tz", "TZ");
        countryAcronyms.put("ua", "UA");
        countryAcronyms.put("ug", "UG");
        countryAcronyms.put("uy", "UY");
        countryAcronyms.put("uz", "UZ");
        countryAcronyms.put("va", "VA");
        countryAcronyms.put("vc", "VC");
        countryAcronyms.put("ve", "VE");
        countryAcronyms.put("vg", "VG");
        countryAcronyms.put("vi", "VI");
        countryAcronyms.put("vn", "VN");
        countryAcronyms.put("vu", "VU");
        countryAcronyms.put("wf", "WF");
        countryAcronyms.put("ws", "WS");
        countryAcronyms.put("xk", "XK");
        countryAcronyms.put("ye", "YE");
        countryAcronyms.put("yt", "YT");
        countryAcronyms.put("za", "ZA");
        countryAcronyms.put("zm", "ZM");
        countryAcronyms.put("zw", "ZW");
        return countryAcronyms;
    }

    private static void writeCountryFile(Set<String> countries) throws IOException {
        Map<String, String> countryAcronyms = getAcronyms();
        ArrayList<String> sortedCountries = new ArrayList<>();
        for(String countryAcronym: countries) {
            sortedCountries.add(countryAcronym + "," + countryAcronyms.getOrDefault(countryAcronym, ""));
        }
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
