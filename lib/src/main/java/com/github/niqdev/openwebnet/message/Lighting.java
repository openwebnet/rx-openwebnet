package com.github.niqdev.openwebnet.message;

import static com.github.niqdev.openwebnet.domain.Who.LIGHTING;
import static java.lang.String.format;

/**
 * OpenWebNet Lighting.
 */
public class Lighting extends BaseOpenMessage {

    private static final int ON = 1;
    private static final int OFF = 0;

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
        checkRange(0, 9999, where);
        return new Lighting(format(FORMAT_REQUEST, LIGHTING, ON, where));
    }

    /**
     * OpenWebNet message request to turn off light.
     *
     * @param where
     * @return value
     */
    public static Lighting requestTurnOff(Integer where) {
        checkRange(0, 9999, where);
        return new Lighting(format(FORMAT_REQUEST, LIGHTING, OFF, where));
    }

    /**
     * OpenWebNet message request light status.
     *
     * @param where
     * @return value
     */
    public static Lighting requestStatus(Integer where) {
        checkRange(0, 9999, where);
        return new Lighting(format(FORMAT_STATUS, LIGHTING, where));
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
        return value != null && value.startsWith(format(FORMAT_PREFIX_STATUS, status))
            && value.length() <= 10 && value.endsWith(FRAME_END);
    }
}
