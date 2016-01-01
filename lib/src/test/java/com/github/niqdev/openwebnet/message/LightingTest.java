package com.github.niqdev.openwebnet.message;

import org.junit.Test;

import static com.github.niqdev.openwebnet.ThrowableCaptor.captureThrowable;
import static com.github.niqdev.openwebnet.message.Lighting.requestTurnOff;
import static com.github.niqdev.openwebnet.message.Lighting.requestTurnOn;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;

public class LightingTest {

    @Test
    public void testRequestTurnOn() {
        assertEquals("should be a valid value", "*1*1*21##", requestTurnOn(21).getValue());
        assertEquals("should be a valid value", "*1*1*1##", requestTurnOn(1).getValue());
        assertEquals("should be a valid value", "*1*1*9999##", requestTurnOn(9999).getValue());
    }

    // TODO
    @Test
    public void testRequestTurnOnInvalid() {
        assertThat(captureThrowable(() -> requestTurnOn(null)))
            .isInstanceOf(NullPointerException.class)
            .hasMessage("invalid null value");
    }

    @Test
    public void testRequestTurnOff() {
        assertEquals("should be a valid value", "*1*0*21##", requestTurnOff(21).getValue());
        assertEquals("should be a valid value", "*1*0*1##", requestTurnOff(1).getValue());
        assertEquals("should be a valid value", "*1*0*9999##", requestTurnOff(9999).getValue());
    }

}
