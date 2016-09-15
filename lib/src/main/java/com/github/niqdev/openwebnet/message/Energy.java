package com.github.niqdev.openwebnet.message;

import static com.github.niqdev.openwebnet.message.Who.ENERGY_MANAGEMENT;
import static com.google.common.base.Preconditions.checkNotNull;
import static java.lang.String.format;

/**
 * OpenWebNet Energy Management.
 *
 * TODO
 *
 */
public class Energy extends BaseOpenMessage {

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

    protected Energy(String value) {
        super(value);
    }

    /**
     * OpenWebNet message request instantaneous power in Watt.
     *
     * @param where Value between 1 and 255
     * @param version Energy management {@link Version}
     * @return
     */
    public static Energy requestInstantaneous(String where, Version version) {
        return buildRequest(where, version, INSTANTANEOUS_POWER);
    }

    /**
     * OpenWebNet message request daily power in Watt.
     *
     * @param where Value between 1 and 255
     * @param version Energy management {@link Version}
     * @return
     */
    public static Energy requestDaily(String where, Version version) {
        return buildRequest(where, version, DAILY_POWER);
    }

    /**
     * OpenWebNet message request monthly power in Watt.
     *
     * @param where Value between 1 and 255
     * @param version Energy management {@link Version}
     * @return
     */
    public static Energy requestMonthly(String where, Version version) {
        return buildRequest(where, version, MONTHLY_POWER);
    }

    private static Energy buildRequest(String where, Version version, int period) {
        checkRange(WHERE_MIN_VALUE_ENERGY, WHERE_MAX_VALUE_ENERGY, checkIsInteger(where));
        checkNotNull(version, "invalid null version");
        switch (version) {
            case MODEL_F520: case MODEL_F523: case MODEL_3522:
                return new Energy(format(FORMAT_DIMENSION_ENERGY, WHO, where, period));
            case MODEL_F522_A: case MODEL_F523_A:
                return new Energy(format(FORMAT_DIMENSION_ENERGY_A, WHO, where, period));
        }
        throw new IllegalArgumentException("invalid version");
    }

}
