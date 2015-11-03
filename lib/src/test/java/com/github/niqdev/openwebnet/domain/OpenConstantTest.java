package com.github.niqdev.openwebnet.domain;

import org.junit.Test;

import static com.github.niqdev.openwebnet.domain.OpenConstant.*;
import static org.junit.Assert.assertEquals;

public class OpenConstantTest {

    @Test
    public void testValue() {
        assertEquals("invalid constant ACK", ACK.val(), "*#*1##");
        assertEquals("invalid constant NACK", NACK.val(), "*#*0##");
        assertEquals("invalid constant CHANNEL_COMMAND", CHANNEL_COMMAND.val(), "*99*0##");
        assertEquals("invalid constant CHANNEL_EVENT", CHANNEL_EVENT.val(), "*99*1##");
        assertEquals("invalid constant FRAME_END", FRAME_END.val(), "##");
    }

    @Test
    public void testToString() {
        assertEquals("invalid toString", ACK.toString(), "[ACK|*#*1##]");
    }

}
