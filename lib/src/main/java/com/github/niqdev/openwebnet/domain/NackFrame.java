package com.github.niqdev.openwebnet.domain;

/**
 *
 */
public class NackFrame implements OpenFrame {

    private static final String NACK = "*#*0##";

    @Override
    public String value() {
        return NACK;
    }
}
