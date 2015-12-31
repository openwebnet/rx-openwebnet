package com.github.niqdev.openwebnet.message;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static java.lang.String.format;

public abstract class BaseOpenMessage implements OpenMessage {

    protected static final String FORMAT_REQUEST = "*%d*%d*%d##";
    protected static final String FORMAT_STATUS = "*#%d*%d##";
    protected static final String FORMAT_PREFIX_STATUS = "*%d*%d*";

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
        checkArgument(value > from && value < to,
            format("value must be between %d and %d", from, to));
    }
}
