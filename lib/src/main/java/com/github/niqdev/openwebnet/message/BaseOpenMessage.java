package com.github.niqdev.openwebnet.message;

import com.github.niqdev.openwebnet.OpenSession;
import rx.functions.Action0;
import rx.functions.Func1;

import java.util.List;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static java.lang.String.format;

abstract class BaseOpenMessage implements OpenMessage {

    protected static final String FORMAT_PREFIX_REQUEST_WHO = "*%d*";
    protected static final String FORMAT_PREFIX_DIMENSION_WHO = "*#%d*";
    protected static final String FORMAT_PREFIX_STATUS_WHO = "*#%d*";
    protected static final String FORMAT_REQUEST = "*%d*%d*%s##";
    protected static final String FORMAT_DIMENSION = "*#%d*%s*%d##";
    protected static final String FORMAT_STATUS = "*#%d*%s##";
    protected static final String FORMAT_PREFIX_RESPONSE = "*%d*%d*";
    protected static final String FORMAT_PREFIX_DIMENSION = "*#%d*";

    protected static final int WHERE_MIN_VALUE = 0;
    protected static final int WHERE_MAX_VALUE = 9999;

    private final String value;

    protected BaseOpenMessage(String value) {
        this.value = value;
    }

    @Override
    public String getValue() {
        return value;
    }

    protected static void checkRange(Integer from, Integer to, Integer value) {
        checkNotNull(value, "invalid null value");
        checkArgument(isInRange(from, to, value), format("value must be between %d and %d", from, to));
    }

    protected static boolean isInRange(Integer from, Integer to, Integer value) {
        checkNotNull(value, "invalid null value");
        return value >= from && value <= to;
    }

    /*
     * See also org.apache.commons.lang.StringUtils.isNumeric
     */
    protected static int checkIsInteger(String value) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("invalid integer format");
        }
    }

    protected static void isValidPrefixType(OpenMessage request, String format, int who) {
        checkNotNull(request, "request is null");
        checkNotNull(request.getValue(), "request value is null");
        boolean isValidWho = request.getValue().startsWith(format(format, who));
        checkArgument(isValidWho, "invalid request of type " + who);
    }

    protected static Func1<OpenSession, OpenSession> handleResponse(Action0 onSuccess, Action0 onFail, int who) {
        return openSession -> {
            isValidPrefixType(openSession.getRequest(), FORMAT_PREFIX_REQUEST_WHO, who);
            List<OpenMessage> response = openSession.getResponse();
            checkNotNull(response, "response is null");
            checkArgument(response.size() == 1, "invalid response");
            final String responseValue = response.get(0).getValue();
            checkNotNull(responseValue, "response value is null");

            if (ACK.equals(responseValue)) {
                onSuccess.call();
            } else {
                onFail.call();
            }
            return openSession;
        };
    }
}
