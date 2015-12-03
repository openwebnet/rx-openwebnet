package com.github.niqdev.openwebnet.domain;

import org.junit.Test;

import static com.github.niqdev.openwebnet.ThrowableCaptor.captureThrowable;
import static com.github.niqdev.openwebnet.domain.Where.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class WhereTest {

    @Test
    public void testGeneral() {
        assertEquals("should be a valid value", "0", general().value());
        assertNotEquals("should not be a valid value", 0, general().value());
    }

    @Test
    public void testRoom() {
        assertEquals("should be a valid value", "2", room(2).value());
        assertNotEquals("should not be a valid value", 2, room(2).value());

        assertThat(captureThrowable(() -> room(null)))
            .isInstanceOf(NullPointerException.class)
            .hasMessage("invalid null value");

        assertThat(captureThrowable(() -> room(0)))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("value must be between 0 and 10");

        assertThat(captureThrowable(() -> room(10)))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("value must be between 0 and 10");
    }

    @Test
    public void tesGroup() {
        assertEquals("should be a valid value", "#2", group(2).value());
        assertNotEquals("should not be a valid value", "2", group(2).value());
        assertNotEquals("should not be a valid value", 2, group(2).value());

        assertThat(captureThrowable(() -> group(null)))
            .isInstanceOf(NullPointerException.class)
            .hasMessage("invalid null value");

        assertThat(captureThrowable(() -> group(0)))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("value must be between 0 and 10");

        assertThat(captureThrowable(() -> group(10)))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("value must be between 0 and 10");
    }

    @Test
    public void tesLightPoint() {
        assertEquals("should be a valid value", "8", lightPoint(8).value());
        assertNotEquals("should not be a valid value", 8, lightPoint(8).value());

        assertThat(captureThrowable(() -> lightPoint(null)))
            .isInstanceOf(NullPointerException.class)
            .hasMessage("invalid null value");

        assertThat(captureThrowable(() -> lightPoint(10)))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("value must be between 0 and 10");

        assertThat(captureThrowable(() -> lightPoint(100)))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("value must be between 0 and 10");
    }

}
