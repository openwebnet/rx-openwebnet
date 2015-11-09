package com.github.niqdev.openwebnet.domain;

/**
 *
 */
public class OpenFrame {

    private final String value;

    public OpenFrame(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return String.format("%s", this.getValue());
    }
}
