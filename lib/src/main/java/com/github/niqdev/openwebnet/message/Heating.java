package com.github.niqdev.openwebnet.message;

import com.github.niqdev.openwebnet.OpenSession;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.functions.Func1;

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
     * OpenWebNet message request to read temperature with a specific {@link TemperatureScale}.
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
     * OpenWebNet message request to read temperature in {@link TemperatureScale#CELSIUS}.
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
    public static Func1<OpenSession, OpenSession> handleTemperature(Action1 onSuccess, Action0 onError) {
        return openSession -> {
            isValidPrefixType(openSession.getRequest(), FORMAT_PREFIX_REQUEST_WHO, WHO);
            List<OpenMessage> response = openSession.getResponse();
            checkNotNull(response, "response is null");
            checkArgument(response.size() == 1 || response.size() == 2, "invalid response");

            final String responseValue = response.get(0).getValue();
            checkNotNull(responseValue, "response value is null");

            if (isValidTemperature(responseValue)) {
                switch (getTemperatureScale(openSession.getRequest())) {
                    case CELSIUS: onSuccess.call(toCelsius(responseValue)); break;
                    case FAHRENHEIT: onSuccess.call(toFahrenheit(responseValue)); break;
                    case KELVIN: onSuccess.call(toKelvin(responseValue)); break;
                }
            } else {
                onError.call();
            }
            return openSession;
        };
    }

    private static boolean isValidTemperature(String value) {
        //*#4*2*0*0225##
        //http://www.allmeasures.com/temperature.html
        return true;
    }

    private static TemperatureScale getTemperatureScale(OpenMessage request) {
        checkArgument(request instanceof Heating, "invalid request type");
        return ((Heating) request).temperatureScale;
    }

    private static String toCelsius(String value) {
        return null;
    }

    private static String toFahrenheit(String value) {
        return null;
    }

    private static String toKelvin(String value) {
        return null;
    }

}
