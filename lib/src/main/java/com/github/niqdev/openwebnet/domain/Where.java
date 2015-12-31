package com.github.niqdev.openwebnet.domain;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static java.lang.String.format;

// TODO remove
public class Where {

    private final String value;

    private Where(String value) {
        this.value = value;
    }

    public String value() {
        return value;
    }

    public static Where general() {
        return new Where("0");
    }

    public static Where room(Integer value) {
        checkRange(0, 10, value);
        return new Where(format("%d", value));
    }

    public static Where group(Integer value) {
        checkRange(0, 10, value);
        return new Where(format("#%d", value));
    }

    public static Where lightPoint(Integer value) {
        checkRange(10, 100, value);
        return new Where(format("%d", value));
    }

    private static void checkRange(Integer from, Integer to, Integer value) {
        checkNotNull(value, "invalid null value");
        checkArgument(value > from && value < to,
            format("value must be between %d and %d", from, to));
    }

}
