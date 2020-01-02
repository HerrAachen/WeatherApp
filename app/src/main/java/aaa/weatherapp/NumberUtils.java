package aaa.weatherapp;

import java.text.DecimalFormat;

public final class NumberUtils {

    public static String roundToOneDecimal(Double aDouble) {
        DecimalFormat oneDecimalFormat = new DecimalFormat("#.#");
        return oneDecimalFormat.format(aDouble);
    }
}
