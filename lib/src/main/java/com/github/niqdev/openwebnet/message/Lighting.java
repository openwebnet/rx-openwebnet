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
 *    .send(Lighting.requestStatus("21", Lighting.Type.POINT_TO_POINT))
 *    .map(Lighting.handleStatus(() -> System.out.println("ON"), () -> System.out.println("OFF")))
 *    .subscribe(System.out::println);
 *
 * // turns light 21 on
 * client
 *    .send(Lighting.requestTurnOn("21", Lighting.Type.POINT_TO_POINT))
 *    .map(Lighting.handleResponse(() -> System.out.println("success"), () -> System.out.println("fail")))
 *    .subscribe(System.out::println);
 * }
 * </pre>
 */
public class Lighting extends BaseOpenMessage {

    public enum Type {
        // 0
        GENERAL,
        // [00, 1−9, 100]
        AREA,
        // #[1−255]
        GROUP,
        // AREA + PL
        // A [00] + PL [01-15]
        // A [1−9] + PL [1-9]
        // A [10] + PL [01-15]
        // A [01-09] + PL [10-15]
        POINT_TO_POINT
    }

    private static final int WHO = LIGHTING.value();
    private static final int ON = 1;
    private static final int OFF = 0;
    private static final int WHERE_MIN_VALUE_AREA = 1;
    private static final int WHERE_MAX_VALUE_AREA = 9;
    private static final int WHERE_MIN_VALUE_GROUP = 1;
    private static final int WHERE_MAX_VALUE_GROUP = 255;

    private Lighting(String value) {
        super(value);
    }

    /**
     * OpenWebNet message request to turn light <i>ON</i> with value <b>*1*1*WHERE##</b>.
     *
     * @param where Value between 0 and 9999
     * @return message
     *
     * @deprecated use {@link Lighting#requestTurnOn(String, Type)}
     */
    public static Lighting requestTurnOn(String where) {
        checkRange(WHERE_MIN_VALUE, WHERE_MAX_VALUE, checkIsInteger(where));
        return new Lighting(format(FORMAT_REQUEST, WHO, ON, where));
    }

    /**
     * OpenWebNet message request to turn light <i>ON</i> with value <b>*1*1*WHERE##</b>.
     *
     * @param where Value
     * @param type Type {@link Type}
     * @return message
     */
    public static Lighting requestTurnOn(String where, Type type) {
        checkRangeType(where, type);
        return new Lighting(format(FORMAT_REQUEST, WHO, ON, buildWhereValue(where, type)));
    }

    /**
     * OpenWebNet message request to turn light <i>OFF</i> with value <b>*1*0*WHERE##</b>.
     *
     * @param where Value between 0 and 9999
     * @return message
     *
     * @deprecated use {@link Lighting#requestTurnOff(String, Type)}
     */
    public static Lighting requestTurnOff(String where) {
        checkRange(WHERE_MIN_VALUE, WHERE_MAX_VALUE, checkIsInteger(where));
        return new Lighting(format(FORMAT_REQUEST, WHO, OFF, where));
    }

    /**
     * OpenWebNet message request to turn light <i>OFF</i> with value <b>*1*0*WHERE##</b>.
     *
     * @param where Value
     * @param type Type {@link Type}
     * @return message
     */
    public static Lighting requestTurnOff(String where, Type type) {
        checkRangeType(where, type);
        return new Lighting(format(FORMAT_REQUEST, WHO, OFF, buildWhereValue(where, type)));
    }

    /**
     * Handle response from {@link Lighting#requestTurnOn(String)} and {@link Lighting#requestTurnOff(String)}.
     *
     * @param onSuccess invoked if the request has been successfully received
     * @param onFail    invoked otherwise
     * @return {@code Observable<OpenSession>}
     */
    public static Func1<OpenSession, OpenSession> handleResponse(Action0 onSuccess, Action0 onFail) {
        return handleResponse(onSuccess, onFail, WHO);
    }

    /**
     * OpenWebNet message request light status with value <b>*#1*WHERE##</b>.
     *
     * @param where Value between 0 and 9999
     * @return message
     *
     * @deprecated use {@link Lighting#requestStatus(String, Type)}
     */
    public static Lighting requestStatus(String where) {
        checkRange(WHERE_MIN_VALUE, WHERE_MAX_VALUE, checkIsInteger(where));
        return new Lighting(format(FORMAT_STATUS, WHO, where));
    }

    /**
     * OpenWebNet message request light status with value <b>*#1*WHERE##</b>.
     *
     * @param where Value
     * @param type Type {@link Type}
     * @return message
     */
    public static Lighting requestStatus(String where, Type type) {
        checkRangeType(where, type);
        return new Lighting(format(FORMAT_STATUS, WHO, buildWhereValue(where, type)));
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

    /*
     * GENERAL
     * 0
     *
     * AREA
     * [00, 1−9, 100]
     *
     * GROUP
     * #[1−255]
     *
     * POINT_TO_POINT (AREA + PL)
     * A [00] + PL [01-15]
     * A [1−9] + PL [1-9]
     * A [10] + PL [01-15]
     * A [01-09] + PL [10-15]
     */
    protected static void checkRangeType(String where, Type type) {
        checkNotNull(where, "invalid null value: [where]");
        checkNotNull(type, "invalid null value: [type]");
        checkArgument(where.length() >= 1 && where.length() <=4, "invalid length [1-4]");
        switch (type) {
            case GENERAL:
                checkArgument(WHERE_GENERAL_VALUE.equals(where), "allowed value [0]");
                break;
            case AREA:
                checkArgument(
                    "00".equals(where) ||
                    (isInRange(WHERE_MIN_VALUE_AREA, WHERE_MAX_VALUE_AREA, checkIsInteger(where)) && where.length() == 1) ||
                    "100".equals(where),
                "allowed value [00, 1−9, 100]");
                break;
            case GROUP:
                checkRange(WHERE_MIN_VALUE_GROUP, WHERE_MAX_VALUE_GROUP, checkIsInteger(where));
                break;
            case POINT_TO_POINT:
                // coarse/shallow validation
                checkRange(WHERE_MIN_VALUE, WHERE_MAX_VALUE, checkIsInteger(where));
                break;
            default:
                throw new IllegalArgumentException("invalid type");
        }
    }

    public static boolean isValidRangeType(String where, Type type) {
        if (where == null || type == null || where.length() < 1 && where.length() > 4) {
            return false;
        }
        switch (type) {
            case GENERAL:
                return WHERE_GENERAL_VALUE.equals(where);
            case AREA:
                return "00".equals(where) ||
                        (isInRange(WHERE_MIN_VALUE_AREA, WHERE_MAX_VALUE_AREA, checkIsInteger(where)) && where.length() == 1) ||
                        "100".equals(where);
            case GROUP:
                return isInRange(WHERE_MIN_VALUE_GROUP, WHERE_MAX_VALUE_GROUP, checkIsInteger(where));
            case POINT_TO_POINT:
                // coarse/shallow validation
                return isInRange(WHERE_MIN_VALUE, WHERE_MAX_VALUE, checkIsInteger(where));
        }
        throw new IllegalArgumentException("invalid type");
    }

    private static String buildWhereValue(String where, Type type) {
        switch (type) {
            case GENERAL: case AREA: case POINT_TO_POINT:
                return where;
            case GROUP:
                return WHERE_GROUP_PREFIX.concat(where);
        }
        throw new IllegalArgumentException("invalid type");
    }

}
