package com.github.niqdev.openwebnet.message;

public class ResponseOpenMessage implements OpenMessage {

    private final String value;

    public ResponseOpenMessage(String value) {
        this.value = value;
    }

    @Override
    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return String.format("{response=%s}", value);
    }
}
