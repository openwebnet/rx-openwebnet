package com.github.niqdev.openwebnet.domain;

import lombok.Getter;

/**
 *
 */
public class OpenFrame {

    @Getter private final String value;

    public OpenFrame(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return String.format("[%s]", this.getValue());
    }
}
