package com.github.niqdev.openwebnet.message;

import com.github.niqdev.openwebnet.OpenSession;
import com.google.common.collect.FluentIterable;
import rx.functions.Action0;
import rx.functions.Func1;

import java.util.List;

import static com.github.niqdev.openwebnet.message.Who.SCENARIO_PROGRAMMING;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static java.lang.String.format;

/**
 * OpenWebNet Scenario.
 *
 * <pre>
 * {@code
 *
 * import static com.github.niqdev.openwebnet.OpenWebNet.defaultGateway;
 *
 * OpenWebNet client = OpenWebNet.newClient(defaultGateway("IP_ADDRESS"));
 *
 * // start scenario 31
 * client
 *    .send(Scenario.requestStart("31", Scenario.Version.MH200N))
 *    .map(Scenario.handleResponse(() -> System.out.println("START"), () -> System.out.println("STOP")))
 *    .subscribe(System.out::println);
 *
 * // requests status scenario 31
 * client
 *    .send(Scenario.requestStatus("31"))
 *    .map(Scenario.handleStatus(
 *       () -> System.out.println("STARTED"),
 *       () -> System.out.println("STOPPED"),
 *       () -> System.out.println("ENABLED"),
 *       () -> System.out.println("DISABLED")))
*     .subscribe(System.out::println);
 * }
 * </pre>
 */
public class Scenario extends BaseOpenMessage {

    private static final int START = 1;
    private static final int STOP = 2;
    private static final int ENABLE = 3;
    private static final int DISABLE = 4;
    private static final int WHO = SCENARIO_PROGRAMMING.value();

    public enum Version {
        // 0-300
        MH200N,
        // numeric 0-9999
        MH202
    }

    private Scenario(String value) {
        super(value);
    }

    /**
     * OpenWebNet message request to send the <i>START</i> scenario command with value <b>*17*1*WHERE##</b>.
     *
     * @param where Value between 0 and 9999
     * @return message
     */
    public static Scenario requestStart(String where) {
        checkRange(WHERE_MIN_VALUE, WHERE_MAX_VALUE, checkIsInteger(where));
        return new Scenario(format(FORMAT_REQUEST, WHO, START, where));
    }

    /**
     * OpenWebNet message request to send the <i>START</i> scenario command with value <b>*17*1*WHERE##</b>.
     *
     * @param where Value between 0 and 300 if MH200N or 0 and 9999 if MH202
     * @param version MH200N or MH202
     * @return message
     */
    public static Scenario requestStart(String where, Version version) {
        checkRangeVersion(where, version);
        return new Scenario(format(FORMAT_REQUEST, WHO, START, where));
    }

    /**
     * OpenWebNet message request to send the <i>STOP</i> scenario command with value <b>*17*2*WHERE##</b>.
     *
     * @param where Value between 0 and 9999
     * @return message
     */
    public static Scenario requestStop(String where) {
        checkRange(WHERE_MIN_VALUE, WHERE_MAX_VALUE, checkIsInteger(where));
        return new Scenario(format(FORMAT_REQUEST, WHO, STOP, where));
    }

    /**
     * OpenWebNet message request to send the <i>STOP</i> scenario command with value <b>*17*2*WHERE##</b>.
     *
     * @param where Value between 0 and 300 if MH200N or 0 and 9999 if MH202
     * @param version MH200N or MH202
     * @return message
     */
    public static Scenario requestStop(String where, Version version) {
        checkRangeVersion(where, version);
        return new Scenario(format(FORMAT_REQUEST, WHO, STOP, where));
    }

    /**
     * Handle response from {@link Scenario#requestStart(String)} and {@link Scenario#requestStop(String)}.
     *
     * @param onSuccess invoked if the request has been successfully received
     * @param onFail    invoked otherwise
     * @return {@code Observable<OpenSession>}
     */
    public static Func1<OpenSession, OpenSession> handleResponse(Action0 onSuccess, Action0 onFail) {
        return handleResponse(onSuccess, onFail, WHO);
    }

    /**
     * OpenWebNet message request scenario status with value <b>*#17*WHERE##</b>.
     *
     * @param where Value between 0 and 9999
     * @return message
     */
    public static Scenario requestStatus(String where) {
        checkRange(WHERE_MIN_VALUE, WHERE_MAX_VALUE, checkIsInteger(where));
        return new Scenario(format(FORMAT_STATUS, WHO, where));
    }

    /**
     * OpenWebNet message request scenario status with value <b>*#17*WHERE##</b>.
     *
     * @param where Value between 0 and 300 if MH200N or 0 and 9999 if MH202
     * @param version MH200N or MH202
     * @return message
     */
    public static Scenario requestStatus(String where, Version version) {
        checkRangeVersion(where, version);
        return new Scenario(format(FORMAT_STATUS, WHO, where));
    }

    /**
     * Handle response from {@link Scenario#requestStatus(String)}.
     *
     * @param startStatus invoked if scenario is started
     * @param stopStatus invoked if scenario is stopped
     * @param enableStatus invoked if scenario is enabled
     * @param disableStatus invoked if scenario is disabled
     * @return message
     */
    public static Func1<OpenSession, OpenSession> handleStatus(
            Action0 startStatus, Action0 stopStatus, Action0 enableStatus, Action0 disableStatus) {

        return openSession -> {
            isValidPrefixType(openSession.getRequest(), FORMAT_PREFIX_STATUS_WHO, WHO);
            List<OpenMessage> response = openSession.getResponse();
            checkNotNull(response, "response is null");
            checkArgument(response.size() == 2 || response.size() == 3, "invalid response");
            checkNotNull(response.get(0).getValue(), "response value is null");

            boolean isStatusHandled = false;
            if (FluentIterable.from(response).filter(value -> isStarted(value.getValue())).size() == 1) {
                startStatus.call();
                isStatusHandled = true;
            }
            if (FluentIterable.from(response).filter(value -> isStopped(value.getValue())).size() == 1) {
                stopStatus.call();
                isStatusHandled = true;
            }
            if (FluentIterable.from(response).filter(value -> isEnabled(value.getValue())).size() == 1) {
                enableStatus.call();
                isStatusHandled = true;
            }
            if (FluentIterable.from(response).filter(value -> isDisabled(value.getValue())).size() == 1) {
                disableStatus.call();
                isStatusHandled = true;
            }

            if (!isStatusHandled) {
                throw new IllegalStateException("unhandled scenario status");
            }
            return openSession;
        };
    }

    /*
     * Verify OpenWebNet message response if scenario is started.
     *
     * @param value
     * @return true if scenario is started
     */
    static boolean isStarted(String value) {
        return verifyMessage(value, START);
    }

    /*
     * Verify OpenWebNet message response if scenario is stopped.
     *
     * @param value
     * @return true if scenario is stopped
     */
    static boolean isStopped(String value) {
        return verifyMessage(value, STOP);
    }

    /*
     * Verify OpenWebNet message response if scenario is enabled.
     *
     * @param value
     * @return true if scenario is enabled
     */
    static boolean isEnabled(String value) {
        return verifyMessage(value, ENABLE);
    }

    /*
     * Verify OpenWebNet message response if scenario is disabled.
     *
     * @param value
     * @return true if scenario is disabled
     */
    static boolean isDisabled(String value) {
        return verifyMessage(value, DISABLE);
    }

    private static boolean verifyMessage(String value, int status) {
        return value != null && value.startsWith(format(FORMAT_PREFIX_RESPONSE, WHO, status))
            && value.length() > 8 && value.length() < 13 && value.endsWith(FRAME_END);
    }

    private static void checkRangeVersion(String where, Version version) {
        switch (version) {
            case MH200N:
                checkRange(0, 300, checkIsInteger(where));
                break;
            case MH202:
                checkRange(WHERE_MIN_VALUE, WHERE_MAX_VALUE, checkIsInteger(where));
                break;
            default:
                throw new IllegalArgumentException("invalid version");
        }
    }

}
