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

    private Lighting(String value) {
        super(value);
    }

    /**
     * OpenWebNet message request to turn on light.
     *
     * @param where
     * @return value
     */
    public static Lighting requestTurnOn(String where) {
        checkRange(WHERE_MIN_VALUE, WHERE_MAX_VALUE, checkIsInteger(where));
        return new Lighting(format(FORMAT_REQUEST, WHO, ON, where));
    }

    /**
     * OpenWebNet message request to turn off light.
     *
     * @param where
     * @return value
     */
    public static Lighting requestTurnOff(String where) {
        checkRange(WHERE_MIN_VALUE, WHERE_MAX_VALUE, checkIsInteger(where));
        return new Lighting(format(FORMAT_REQUEST, WHO, OFF, where));
    }

    /**
     * Handle response from {@link Lighting#requestTurnOn(String)} and {@link Lighting#requestTurnOff(String)}.
     *
     * @param onSuccess invoked if request have been successfully received
     * @param onFail invoked otherwise
     * @return {@code Observable<OpenSession>}
     */
    public static Func1<OpenSession, OpenSession> handleResponse(Action0 onSuccess, Action0 onFail) {
        return openSession -> {
            isValidLightingRequest(openSession.getRequest(), FORMAT_PREFIX_REQUEST_WHO);
            List<OpenMessage> response = openSession.getResponse();
            checkNotNull(response, "response is null");
            checkArgument(response.size() == 1, "invalid response");
            checkNotNull(response.get(0).getValue(), "response value is null");

            if (response.get(0).getValue().equals(ACK)) {
                onSuccess.call();
                return openSession;
            } else {
                onFail.call();
                return openSession;
            }
        };
    }

    /**
     * OpenWebNet message request light status.
     *
     * @param where
     * @return value
     */
    public static Lighting requestStatus(String where) {
        checkRange(WHERE_MIN_VALUE, WHERE_MAX_VALUE, checkIsInteger(where));
        return new Lighting(format(FORMAT_STATUS, WHO, where));
    }

    /**
     * Handle response from {@link Lighting#requestStatus(String)}.
     *
     * @param onStatus invoked if light is on
     * @param offStatus invoked if light is off
     * @return {@code Observable<OpenSession>}
     */
    public static Func1<OpenSession, OpenSession> handleStatus(Action0 onStatus, Action0 offStatus) {
        return openSession -> {
            isValidLightingRequest(openSession.getRequest(), FORMAT_PREFIX_STATUS_WHO);
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
        return verifyMessage(value, ON);
    }

    /**
     * Verify OpenWebNet message response if light is off.
     *
     * @param value
     * @return true if light is off
     */
    public static boolean isOff(String value) {
        return verifyMessage(value, OFF);
    }

    private static boolean verifyMessage(String value, int status) {
        return value != null && value.startsWith(format(FORMAT_PREFIX_REQUEST, WHO, status))
            && value.length() > 7 && value.length() < 12 && value.endsWith(FRAME_END);
    }

    private static void isValidLightingRequest(OpenMessage request, String format) {
        checkNotNull(request, "request is null");
        checkNotNull(request.getValue(), "request value is null");
        boolean isValidWho = request.getValue().startsWith(format(format, WHO));
        checkArgument(isValidWho, "invalid lighting request");
    }
}
