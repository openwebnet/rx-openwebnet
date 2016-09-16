package com.github.niqdev.openwebnet.message;

import com.github.niqdev.openwebnet.OpenSession;
import com.google.common.collect.Lists;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.functions.Func1;

import java.util.List;

import static com.github.niqdev.openwebnet.message.Who.ENERGY_MANAGEMENT;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static java.lang.String.format;

/**
 * OpenWebNet Energy Management.
 *
 * TODO
 *
 */
public class EnergyManagement extends BaseOpenMessage {

    public enum Version {
        // Energy Management Central Unit, Pulse Counter, Power Meter
        // 5N 1-255
        MODEL_F520,
        MODEL_F523,
        MODEL_3522,
        // Energy Management Actuators
        // 7N#0 1-255
        MODEL_F522_A,
        MODEL_F523_A
    }

    private static final String FORMAT_DIMENSION_ENERGY = "*#%d*5%s*%d##";
    private static final String FORMAT_DIMENSION_ENERGY_A = "*#%d*7%s#0*%d##";
    private static final int INSTANTANEOUS_POWER = 113;
    private static final int DAILY_POWER = 54;
    private static final int MONTHLY_POWER = 53;
    private static final int WHERE_MIN_VALUE_ENERGY = 1;
    private static final int WHERE_MAX_VALUE_ENERGY = 255;
    private static final int WHO = ENERGY_MANAGEMENT.value();

    protected EnergyManagement(String value) {
        super(value);
    }

    /**
     * OpenWebNet message request instantaneous power in Watt.
     *
     * @param where Value between 1 and 255
     * @param version Energy management {@link Version}
     * @return
     */
    public static EnergyManagement requestInstantaneousPower(String where, Version version) {
        return buildRequestPower(where, version, INSTANTANEOUS_POWER);
    }

    /**
     * OpenWebNet message request daily power in Watt.
     *
     * @param where Value between 1 and 255
     * @param version Energy management {@link Version}
     * @return
     */
    public static EnergyManagement requestDailyPower(String where, Version version) {
        return buildRequestPower(where, version, DAILY_POWER);
    }

    /**
     * OpenWebNet message request monthly power in Watt.
     *
     * @param where Value between 1 and 255
     * @param version Energy management {@link Version}
     * @return
     */
    public static EnergyManagement requestMonthlyPower(String where, Version version) {
        return buildRequestPower(where, version, MONTHLY_POWER);
    }

    private static EnergyManagement buildRequestPower(String where, Version version, int period) {
        checkRange(WHERE_MIN_VALUE_ENERGY, WHERE_MAX_VALUE_ENERGY, checkIsInteger(where));
        checkNotNull(version, "invalid null version");
        switch (version) {
            case MODEL_F520: case MODEL_F523: case MODEL_3522:
                return new EnergyManagement(format(FORMAT_DIMENSION_ENERGY, WHO, where, period));
            case MODEL_F522_A: case MODEL_F523_A:
                return new EnergyManagement(format(FORMAT_DIMENSION_ENERGY_A, WHO, where, period));
        }
        throw new IllegalArgumentException("invalid version");
    }

    /**
     * TODO
     *
     * @param onSuccess
     * @param onError
     * @return
     */
    public static Func1<List<OpenSession>, List<OpenSession>> handlePowers(Action1<List<String>> onSuccess, Action0 onError) {
        return openSessions -> {
            List<String> results = Lists.newArrayList();

            for (OpenSession openSession: openSessions) {
                isValidPrefixType(openSession.getRequest(), FORMAT_PREFIX_DIMENSION_WHO, WHO);
                List<OpenMessage> response = openSession.getResponse();
                checkNotNull(response, "response is null");
                // allow invalid response like *#*1##1*54*3897##*#*1## (3 messages) - skip later!
                checkArgument(response.size() >= 1 || response.size() <=3, "invalid response");

                final String responseValue = response.get(0).getValue();
                checkNotNull(responseValue, "response value is null");

                // strict validation
                if (response.size() != 3 && isValidPower(responseValue)) {
                    results.add(getPower(openSession.getRequest(), responseValue));
                } else {
                    results.add("");
                }
            }

            if (results.size() > 0 && results.size() == openSessions.size()) {
                onSuccess.call(results);
            } else {
                onError.call();
            }

            return openSessions;
        };
    }

    static boolean isValidPower(String value) {
        // example min *#18*51*54*V##
        // example max *#18*7255#0*113*9999999##
        return value != null && value.startsWith(format(FORMAT_PREFIX_DIMENSION, WHO))
            && value.length() > 13 && value.length() < 26 && value.endsWith(FRAME_END);
    }

    // Power in Watt
    static String getPower(OpenMessage request, String response) {
        checkArgument(request instanceof EnergyManagement, "invalid request type");

        // *#18*5WHERE*113##
        // *#18*7WHERE#0*54##
        String requestValue = request.getValue();
        // *#18*5WHERE*113
        // *#18*7WHERE#0*54
        String requestValueWithoutSuffix = requestValue.substring(0, requestValue.length() - 2);
        // *#18*5WHERE*113*POWER## --> *POWER##
        // *#18*7WHERE#0*54*POWER## --> *POWER##
        String powerValueWithPrefix = response.replace(requestValueWithoutSuffix, "");

        String powerValue = powerValueWithPrefix.substring(1, powerValueWithPrefix.length() - 2);

        try {
            // dirty check: parse as much as possible
            // response are often incomplete or impossible to validate strictly
            checkArgument(powerValue.length()>0 && powerValue.length()<8, "invalid power length");
            Integer.parseInt(powerValue);
        } catch (Throwable t) {
            powerValue = "";
        }

        return powerValue;
    }

}
