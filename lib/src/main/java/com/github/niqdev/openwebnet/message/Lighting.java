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
 *
 * <pre>
 * {@code
 *
 * import static com.github.niqdev.openwebnet.OpenWebNet.defaultGateway;
 *
 * OpenWebNet client = OpenWebNet.newClient(defaultGateway("IP_ADDRESS"));
 *
 * // requests status light 21
 * client
 *    .send(Lighting.requestStatus("21"))
 *    .map(Lighting.handleStatus(() -> System.out.println("ON"), () -> System.out.println("OFF")))
 *    .subscribe(System.out::println);
 *
 * // turns light 21 on
 * client
 *    .send(Lighting.requestTurnOn("21"))
 *    .map(Lighting.handleResponse(() -> System.out.println("success"), () -> System.out.println("fail")))
 *    .subscribe(System.out::println);
 * }
 * </pre>
 */
public class Lighting extends BaseOpenMessage {

    private static final int ON = 1;
    private static final int OFF = 0;
    private static final int WHO = LIGHTING.value();

    private Lighting(String value) {
        super(value);
    }

    /**
     * OpenWebNet message request to turn light <i>ON</i> with value <b>*1*1*WHERE##</b>.
     *
     * @param where Value between 0 and 9999
     * @return message
     */
    public static Lighting requestTurnOn(String where) {
        checkRange(WHERE_MIN_VALUE, WHERE_MAX_VALUE, checkIsInteger(where));
        return new Lighting(format(FORMAT_REQUEST, WHO, ON, where));
    }

    /**
     * OpenWebNet message request to turn light <i>OFF</i> with value <b>*1*0*WHERE##</b>.
     *
     * @param where Value between 0 and 9999
     * @return message
     */
    public static Lighting requestTurnOff(String where) {
        checkRange(WHERE_MIN_VALUE, WHERE_MAX_VALUE, checkIsInteger(where));
        return new Lighting(format(FORMAT_REQUEST, WHO, OFF, where));
    }

    /**
     * Handle response from {@link Lighting#requestTurnOn(String)} and {@link Lighting#requestTurnOff(String)}.
     *
     * @param onSuccess invoked if the request has been successfully received
     * @param onFail    invoked otherwise
     * @return {@code Observable<OpenSession>}
     */
    public static Func1<OpenSession, OpenSession> handleResponse(Action0 onSuccess, Action0 onFail) {
        return openSession -> {
            isValidPrefixType(openSession.getRequest(), FORMAT_PREFIX_REQUEST_WHO, WHO);
            List<OpenMessage> response = openSession.getResponse();
            checkNotNull(response, "response is null");
            checkArgument(response.size() >= 1, "invalid response");
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
     * OpenWebNet message request light status with value <b>*#1*WHERE##</b>.
     *
     * @param where Value between 0 and 9999
     * @return message
     */
    public static Lighting requestStatus(String where) {
        checkRange(WHERE_MIN_VALUE, WHERE_MAX_VALUE, checkIsInteger(where));
        return new Lighting(format(FORMAT_STATUS, WHO, where));
    }

    /**
     * Handle response from {@link Lighting#requestStatus(String)}.
     *
     * @param onStatus  invoked if light is on
     * @param offStatus invoked if light is off
     * @return {@code Observable<OpenSession>}
     */
    public static Func1<OpenSession, OpenSession> handleStatus(Action0 onStatus, Action0 offStatus) {
        return openSession -> {
            isValidPrefixType(openSession.getRequest(), FORMAT_PREFIX_STATUS_WHO, WHO);
            List<OpenMessage> response = openSession.getResponse();
            checkNotNull(response, "response is null");
            checkArgument(response.size() == 1 || response.size() == 2, "invalid response");
            checkNotNull(response.get(0).getValue(), "response value is null");

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

    /*
     * Verify OpenWebNet message response if light is on.
     *
     * @param value
     * @return true if light is on
     */
    static boolean isOn(String value) {
        return verifyMessage(value, ON);
    }

    /*
     * Verify OpenWebNet message response if light is off.
     *
     * @param value
     * @return true if light is off
     */
    static boolean isOff(String value) {
        return verifyMessage(value, OFF);
    }

    private static boolean verifyMessage(String value, int status) {
        return value != null && value.startsWith(format(FORMAT_PREFIX_RESPONSE, WHO, status))
            && value.length() > 7 && value.length() < 12 && value.endsWith(FRAME_END);
    }

}
