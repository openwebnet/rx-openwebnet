package com.github.niqdev.openwebnet.message;

/**
 * Interface for all OpenWebNet messages.
 */
public interface OpenMessage {

    String ACK = "*#*1##";
    String NACK = "*#*0##";
    String FRAME_START = "*";
    String FRAME_END = "##";

    /**
     * Raw message value.
     *
     * @return value
     */
    String getValue();

}
