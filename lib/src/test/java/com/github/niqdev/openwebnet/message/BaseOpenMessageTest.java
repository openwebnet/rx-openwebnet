package com.github.niqdev.openwebnet.message;

import org.junit.Test;

import static com.github.niqdev.openwebnet.ThrowableCaptor.captureThrowable;
import static com.github.niqdev.openwebnet.message.BaseOpenMessage.checkBus;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;

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

}
