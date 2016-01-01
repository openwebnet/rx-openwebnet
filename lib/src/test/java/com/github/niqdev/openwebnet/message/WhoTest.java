package com.github.niqdev.openwebnet.message;

import org.junit.Ignore;
import org.junit.Test;

import static com.github.niqdev.openwebnet.ThrowableCaptor.captureThrowable;
import static com.github.niqdev.openwebnet.message.Who.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.*;

// TODO refactor compatible java 7
public class WhoTest {

    @Test
    @Ignore
    public void testIsValidName() {
        assertTrue("should be a valid name", isValidName("LIGHTING"));
        assertFalse("should not be a valid name", isValidName("lighting"));
        assertFalse("should not be a valid name", isValidName(null));
    }

    @Test
    @Ignore
    public void testIsValidValue() {
        assertTrue("should be a valid value", isValidValue(1));
        assertFalse("should not be a valid value", isValidValue(-1));
        assertFalse("should not be a valid value", isValidValue(100));
        assertFalse("should not be a valid value", isValidValue(null));
    }

    @Test
    @Ignore
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
    @Ignore
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
