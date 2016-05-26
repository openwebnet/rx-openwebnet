package com.github.niqdev.openwebnet.message;

import com.github.niqdev.openwebnet.OpenSession;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.functions.Func1;

import java.text.DecimalFormat;
import java.util.List;

import static com.github.niqdev.openwebnet.message.Who.TEMPERATURE_CONTROL;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static java.lang.String.format;

/**
 * OpenWebNet Heating.
 */
public class Heating extends BaseOpenMessage {

    /**
     * Common temperature scale.
     */
    public enum TemperatureScale {
        CELSIUS, FAHRENHEIT, KELVIN
    }

    private static final int READ_TEMPERATURE = 0;
    private static final int WHERE_MAX_VALUE_TEMPERATURE = 899;
    private static final int WHO = TEMPERATURE_CONTROL.value();

    private final TemperatureScale temperatureScale;

    private Heating(String value, TemperatureScale scale) {
        super(value);
        this.temperatureScale = scale;
    }

    /**
     * OpenWebNet message request to read temperature with a specific {@link TemperatureScale} and value <b>*4*WHERE*0##</b>.
     *
     * @param where Value between 0 and 899
     * @param scale Temperature scale
     * @return message
     */
    public static Heating requestTemperature(String where, TemperatureScale scale) {
        checkRange(WHERE_MIN_VALUE, WHERE_MAX_VALUE_TEMPERATURE, checkIsInteger(where));
        checkNotNull(scale, "invalid null scale");
        return new Heating(format(FORMAT_REQUEST_DIMENSION, WHO, where, READ_TEMPERATURE), scale);
    }

    /**
     * OpenWebNet message request to read temperature in {@link TemperatureScale#CELSIUS} and value <b>*4*WHERE*0##</b>.
     *
     * @param where Value between 0 and 899
     * @return message
     */
    public static Heating requestTemperature(String where) {
        return requestTemperature(where, TemperatureScale.CELSIUS);
    }

    /**
     * Handle response from {@link Heating#requestTemperature(String, TemperatureScale)}.
     *
     * @param onSuccess invoked if the temperature has been read correctly
     * @param onError   invoked otherwise
     * @return {@code Observable<OpenSession>}
     */
    public static Func1<OpenSession, OpenSession> handleTemperature(Action1<Double> onSuccess, Action0 onError) {
        return openSession -> {
            isValidPrefixType(openSession.getRequest(), FORMAT_PREFIX_REQUEST_WHO, WHO);
            List<OpenMessage> response = openSession.getResponse();
            checkNotNull(response, "response is null");
            checkArgument(response.size() == 1 || response.size() == 2, "invalid response");

            final String responseValue = response.get(0).getValue();
            checkNotNull(responseValue, "response value is null");

            if (isValidTemperature(responseValue)) {
                onSuccess.call(getTemperature(openSession.getRequest(), responseValue));
            } else {
                onError.call();
            }
            return openSession;
        };
    }

    static boolean isValidTemperature(String value) {
        return value != null && value.startsWith(format(FORMAT_PREFIX_RESPONSE_DIMENSION, WHO))
            && value.length() > 13 && value.length() < 17 && value.endsWith(FRAME_END);
    }

    static Double getTemperature(OpenMessage request, String response) {
        checkArgument(request instanceof Heating, "invalid request type");
        TemperatureScale currentScale = ((Heating) request).temperatureScale;

        // *#4*WHERE*0##
        String requestValue = request.getValue();
        // *#4*WHERE*0
        String requestValueWithoutSuffix = requestValue.substring(0, requestValue.length() - 2);
        // *#4*WHERE*0*TEMPERATURE## --> TEMPERATURE##
        String temperatureValueWithSuffix = response.substring(response.indexOf(requestValueWithoutSuffix));
        /*
         * The TEMPERATURE field is composed of 4 digits: c1c2c3c4,
         * included between "0000" (0° temperature) and "0500" (50° temperature).
         * c1 is always equal to 0, it indicates a positive temperature.
         * The c2c3 couple indicates the temperature values between [00° - 50°].
         * c4 indicates the decimal Celsius degree by 0.1° step.
         */
        String temperatureValue = temperatureValueWithSuffix.substring(0, temperatureValueWithSuffix.length() - 2);

        checkArgument(temperatureValue.length() == 4, "invalid temperature length");
        checkArgument(temperatureValue.startsWith("0"), "invalid negative temperature");

        String temperatureStr = temperatureValue.substring(1, 3)
            .concat(".")
            .concat(temperatureValue.substring(-1));

        double temperature = Double.parseDouble(temperatureStr);

        switch (currentScale) {
            case CELSIUS: return temperature;
            case FAHRENHEIT: return toFahrenheit(temperature);
            case KELVIN: return toKelvin(temperature);
        }
        throw new IllegalStateException("unhandled temperature scale");
    }

    static Double toFahrenheit(Double value) {
        return formatDouble((value * 9.0/5.0) + 32.0);
    }

    static Double toKelvin(Double value) {
        return formatDouble(value + 273.15);
    }

    private static Double formatDouble(Double value) {
        DecimalFormat df = new DecimalFormat("###.##");
        //df.setRoundingMode(RoundingMode.DOWN);
        return Double.parseDouble(df.format(value));
    }

}
