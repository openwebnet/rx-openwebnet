package com.github.niqdev.openwebnet.domain;

/**
 *
 */
public enum OpenConstant {

    ACK("*#*1##"),
    NACK("*#*0##"),
    CHANNEL_COMMAND("*99*0##"),
    CHANNEL_EVENT("*99*1##"),
    // TODO
    FRAME_END("##");

    private final OpenFrame frame;

    private OpenConstant(String value) {
        //this.frame = new OpenFrame(value);
        throw new UnsupportedOperationException("not implemented yet");
    }

    /*
    public String val() {
        return frame.getValue();
    }

    @Override
    public String toString() {
        return String.format("[%s|%s]", name(), val());
    }
    */
}
