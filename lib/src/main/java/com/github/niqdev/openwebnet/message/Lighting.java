package com.github.niqdev.openwebnet.message;

import static com.github.niqdev.openwebnet.message.Who.LIGHTING;
import static java.lang.String.format;

/**
 * OpenWebNet Lighting.
 */
public class Lighting extends BaseOpenMessage {

    private static final int ON = 1;
    private static final int OFF = 0;
    private static final int WHO = LIGHTING.value();
    private static final int WHERE_MIN_VALUE = 0;
    private static final int WHERE_MAX_VALUE = 10000;

    private Lighting(String value) {
        super(value);
    }

    /**
     * OpenWebNet message request to turn on light.
     *
     * @param where
     * @return value
     */
    public static Lighting requestTurnOn(Integer where) {
        checkRange(WHERE_MIN_VALUE, WHERE_MAX_VALUE, where);
        return new Lighting(format(FORMAT_REQUEST, WHO, ON, where));
    }

    /**
     * OpenWebNet message request to turn off light.
     *
     * @param where
     * @return value
     */
    public static Lighting requestTurnOff(Integer where) {
        checkRange(WHERE_MIN_VALUE, WHERE_MAX_VALUE, where);
        return new Lighting(format(FORMAT_REQUEST, WHO, OFF, where));
    }

    /**
     * OpenWebNet message request light status.
     *
     * @param where
     * @return value
     */
    public static Lighting requestStatus(Integer where) {
        checkRange(WHERE_MIN_VALUE, WHERE_MAX_VALUE, where);
        return new Lighting(format(FORMAT_STATUS, WHO, where));
    }

    /**
     * Verify OpenWebNet message response if light is on.
     *
     * @param value
     * @return true if light is on
     */
    public static boolean isOn(String value) {
        return verifyResponseStatus(value, ON);
    }

    /**
     * Verify OpenWebNet message response if light is off.
     *
     * @param value
     * @return true if light is off
     */
    public static boolean isOff(String value) {
        return verifyResponseStatus(value, OFF);
    }

    private static boolean verifyResponseStatus(String value, int status) {
        return value != null && value.startsWith(format(FORMAT_PREFIX_STATUS, WHO, status))
            && value.length() > 7 && value.length() < 12 && value.endsWith(FRAME_END);
    }
}
