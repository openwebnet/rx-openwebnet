package com.github.niqdev.openwebnet.message;

public interface OpenMessage {

    String ACK = "*#*1##";
    String NACK = "*#*0##";
    String FRAME_START = "*";
    String FRAME_END = "##";

    String getValue();

}
