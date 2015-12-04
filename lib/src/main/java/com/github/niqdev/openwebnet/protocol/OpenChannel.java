package com.github.niqdev.openwebnet.protocol;

public enum OpenChannel {

    COMMAND("*99*0##"),
    EVENT("*99*1##");

    private final String value;

    OpenChannel(String value) {
        this.value = value;
    }

    public String value() {
        return value;
    }

}
