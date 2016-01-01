package com.github.niqdev.openwebnet.message;

import org.junit.Test;

import static com.github.niqdev.openwebnet.ThrowableCaptor.captureThrowable;
import static com.github.niqdev.openwebnet.message.Who.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.*;

public class WhoTest {

    @Test
    public void testIsValidName() {
        assertTrue("should be a valid name", isValidName("LIGHTING"));
        assertFalse("should not be a valid name", isValidName("lighting"));
        assertFalse("should not be a valid name", isValidName(null));
    }

    @Test
    public void testIsValidValue() {
        assertTrue("should be a valid value", isValidValue(1));
        assertFalse("should not be a valid value", isValidValue(-1));
        assertFalse("should not be a valid value", isValidValue(100));
        assertFalse("should not be a valid value", isValidValue(null));
    }

    @Test
    public void testFromName() {
        assertEquals("should retrieve element by name", LIGHTING, fromName("LIGHTING"));

        assertThat(captureThrowable(() -> fromName("lighting")))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("invalid name");

        assertThat(captureThrowable(() -> fromName(null)))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("invalid name");
    }

    @Test
    public void testFromValue() {
        assertEquals("should retrieve element by value", LIGHTING, fromValue(1));

        assertThat(captureThrowable(() -> fromValue(-1)))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("invalid value");

        assertThat(captureThrowable(() -> fromValue(null)))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("invalid value");
    }

}
