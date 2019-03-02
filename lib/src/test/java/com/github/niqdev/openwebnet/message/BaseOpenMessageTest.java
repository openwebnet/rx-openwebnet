package com.github.niqdev.openwebnet.message;

import org.junit.Test;

import static com.github.niqdev.openwebnet.ThrowableCaptor.captureThrowable;
import static com.github.niqdev.openwebnet.message.BaseOpenMessage.checkBus;
import static com.github.niqdev.openwebnet.message.BaseOpenMessage.isValidBus;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.*;

public class BaseOpenMessageTest {

    @Test
    public void testCheckBus() {
        assertEquals("should be a valid value", "01", checkBus("01"));
        assertEquals("should be a valid value", "09", checkBus("09"));
        assertEquals("should be a valid value", "11", checkBus("11"));
        assertEquals("should be a valid value", "15", checkBus("15"));
    }

    @Test
    public void testCheckBusInvalid() {
        assertThat(captureThrowable(() -> checkBus(null)))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("invalid integer format");

        assertThat(captureThrowable(() -> checkBus("#")))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("invalid integer format");

        assertThat(captureThrowable(() -> checkBus("0")))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("invalid length [2]");

        assertThat(captureThrowable(() -> checkBus("010")))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("invalid length [2]");

        assertThat(captureThrowable(() -> checkBus("20")))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("invalid i3 [0-1]");

        assertThat(captureThrowable(() -> checkBus("00")))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("invalid i4 [1-9]");

        assertThat(captureThrowable(() -> checkBus("10")))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("invalid i4 [1-5]");

        assertThat(captureThrowable(() -> checkBus("16")))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("invalid i4 [1-5]");
    }

    @Test
    public void testIsValidBus() {
        assertTrue("should be a valid value", isValidBus("01"));
        assertTrue("should be a valid value", isValidBus("09"));
        assertTrue("should be a valid value", isValidBus("11"));
        assertTrue("should be a valid value", isValidBus("15"));

        assertFalse("should be an invalid value", isValidBus(null));
        assertFalse("should be an invalid value", isValidBus("#"));
        assertFalse("should be an invalid value", isValidBus("0"));
        assertFalse("should be an invalid value", isValidBus("010"));
        assertFalse("should be an invalid value", isValidBus("20"));
        assertFalse("should be an invalid value", isValidBus("00"));
        assertFalse("should be an invalid value", isValidBus("10"));
        assertFalse("should be an invalid value", isValidBus("16"));
    }

}
