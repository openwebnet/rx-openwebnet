package com.github.niqdev.openwebnet.domain;

/**
 *
 */
public class AckFrame implements OpenFrame {

    private static final String ACK = "*#*1##";

    @Override
    public String value() {
        return ACK;
    }
}
