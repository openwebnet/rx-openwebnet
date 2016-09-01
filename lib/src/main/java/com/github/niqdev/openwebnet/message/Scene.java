package com.github.niqdev.openwebnet.message;

import com.github.niqdev.openwebnet.OpenSession;
import rx.functions.Action0;
import rx.functions.Func1;

import java.util.List;

import static com.github.niqdev.openwebnet.message.Who.SCENARIO_PROGRAMMING;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static java.lang.String.format;

/**
 * TODO translation for Scenario - Scene??
 */
public class Scene extends BaseOpenMessage {

    private static final int START = 1;
    private static final int STOP = 2;
    private static final int ENABLE = 3;
    private static final int DISABLE = 4;
    private static final int WHO = SCENARIO_PROGRAMMING.value();

    public enum Version {
        // 0-300
        MH200N,
        // numeric 9999
        MH202
    }

    private Scene(String value) {
        super(value);
    }

    /**
     * TODO
     */
    public static Scene requestStart(String where) {
        checkRange(WHERE_MIN_VALUE, WHERE_MAX_VALUE, checkIsInteger(where));
        return new Scene(format(FORMAT_REQUEST, WHO, START, where));
    }

    /**
     * TODO
     */
    public static Scene requestStart(String where, Version version) {
        checkRangeVersion(where, version);
        return new Scene(format(FORMAT_REQUEST, WHO, START, where));
    }

    /**
     * TODO
     */
    public static Scene requestStop(String where) {
        checkRange(WHERE_MIN_VALUE, WHERE_MAX_VALUE, checkIsInteger(where));
        return new Scene(format(FORMAT_REQUEST, WHO, STOP, where));
    }

    /**
     * TODO
     */
    public static Scene requestStop(String where, Version version) {
        checkRangeVersion(where, version);
        return new Scene(format(FORMAT_REQUEST, WHO, STOP, where));
    }

    /**
     * TODO
     */
    public static Func1<OpenSession, OpenSession> handleResponse(Action0 onSuccess, Action0 onFail) {
        return handleResponse(onSuccess, onFail, WHO);
    }

    /**
     * TODO
     */
    public static Scene requestStatus(String where) {
        checkRange(WHERE_MIN_VALUE, WHERE_MAX_VALUE, checkIsInteger(where));
        return new Scene(format(FORMAT_STATUS, WHO, where));
    }

    /**
     * TODO
     */
    public static Scene requestStatus(String where, Version version) {
        checkRangeVersion(where, version);
        return new Scene(format(FORMAT_STATUS, WHO, where));
    }

    /**
     * TODO
     */
    public static Func1<OpenSession, OpenSession> handleStatus(Action0 startStatus, Action0 stopStatus) {
        return openSession -> {
            isValidPrefixType(openSession.getRequest(), FORMAT_PREFIX_STATUS_WHO, WHO);
            List<OpenMessage> response = openSession.getResponse();
            checkNotNull(response, "response is null");
            checkArgument(response.size() == 1 || response.size() == 2, "invalid response");
            checkNotNull(response.get(0).getValue(), "response value is null");

            if (isStarted(response.get(0).getValue())) {
                startStatus.call();
                return openSession;
            }
            if (isStopped(response.get(0).getValue())) {
                stopStatus.call();
                return openSession;
            }
            throw new IllegalStateException("unhandled status");
        };
    }

    /*
     * TODO
     */
    static boolean isStarted(String value) {
        return verifyMessage(value, START);
    }

    /*
     * TODO
     */
    static boolean isStopped(String value) {
        return verifyMessage(value, STOP);
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
