package com.github.niqdev.openwebnet.message;

import com.github.niqdev.openwebnet.OpenSession;
import rx.functions.Action0;
import rx.functions.Func1;

import java.util.List;

import static com.github.niqdev.openwebnet.message.Who.LIGHTING;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
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

    public static Func1<OpenSession, OpenSession> handleStatus(Action0 onStatus, Action0 offStatus) {
        return openSession -> {
            List<OpenMessage> response = openSession.getResponse();
            checkNotNull(response, "response is null");
            checkArgument(response.size() == 2, "invalid response");
            checkNotNull(response.get(0).getValue(), "response value is null");
            checkNotNull(response.get(1).getValue(), "response value is null");
            checkArgument(response.get(1).getValue().equals(ACK), "bad response");

            if (isOn(response.get(0).getValue())) {
                onStatus.call();
                return openSession;
            }
            if (isOff(response.get(0).getValue())) {
                offStatus.call();
                return openSession;
            }
            throw new IllegalStateException("unhandled lighting status");
        };
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
