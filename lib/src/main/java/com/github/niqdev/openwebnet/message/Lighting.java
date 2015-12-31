package com.github.niqdev.openwebnet.message;

import com.github.niqdev.openwebnet.domain.Who;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static java.lang.String.format;

/**
 * OpenWebNet Lighting.
 */
public class Lighting extends BaseOpenMessage {

    private static final String FORMAT_PREFIX_STATUS = "*1*%d*";
    private static final int ON = 1;
    private static final int OFF = 0;

    private Lighting(String value) {
        super(Who.LIGHTING, value);
    }

    /**
     * OpenWebNet message request to turn on light.
     *
     * @param where
     * @return value
     */
    public Lighting requestTurnOn(Integer where) {
        checkRange(0, 9999, where);
        return new Lighting(format(FORMAT_REQUEST, getWho(), ON, where));
    }

    /**
     * OpenWebNet message request to turn off light.
     *
     * @param where
     * @return value
     */
    public Lighting requestTurnOff(Integer where) {
        checkRange(0, 9999, where);
        return new Lighting(format(FORMAT_REQUEST, getWho(), OFF, where));
    }

    /**
     * OpenWebNet message request light status.
     *
     * @param where
     * @return value
     */
    public Lighting requestStatus(Integer where) {
        checkRange(0, 9999, where);
        return new Lighting(format(FORMAT_STATUS, getWho(), where));
    }

    private static void checkRange(Integer from, Integer to, Integer value) {
        checkNotNull(value, "invalid null value");
        checkArgument(value > from && value < to,
            format("value must be between %d and %d", from, to));
    }

    /**
     * Verify OpenWebNet message response if light is on.
     *
     * @param value
     * @return true if light is on
     */
    public static boolean isOn(String value) {
        return value != null && value.startsWith(format(FORMAT_PREFIX_STATUS, ON))
            && value.length() <= 10 && value.endsWith(FRAME_END);
    }

    /**
     * Verify OpenWebNet message response if light is off.
     *
     * @param value
     * @return true if light is off
     */
    public static boolean isOff(String value) {
        return value != null && value.startsWith(format(FORMAT_PREFIX_STATUS, ON))
            && value.length() <= 10 && value.endsWith(FRAME_END);
    }
}
