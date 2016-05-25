package com.github.niqdev.openwebnet.message;

import org.junit.Test;

import static com.github.niqdev.openwebnet.ThrowableCaptor.captureThrowable;
import static com.github.niqdev.openwebnet.message.Heating.TemperatureScale.*;
import static com.github.niqdev.openwebnet.message.Heating.requestTemperature;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;

public class HeatingTest {

    @Test
    public void testRequestTemperature() {
        assertEquals("should be a valid value", "*4*0*0##", requestTemperature("0").getValue());
        assertEquals("should be a valid value", "*4*001*0##", requestTemperature("001").getValue());
        assertEquals("should be a valid value", "*4*899*0##", requestTemperature("899").getValue());
    }

    @Test
    public void testRequestTemperatureWithScale() {
        assertEquals("should be a valid value", "*4*0*0##", requestTemperature("0", CELSIUS).getValue());
        assertEquals("should be a valid value", "*4*001*0##", requestTemperature("001", FAHRENHEIT).getValue());
        assertEquals("should be a valid value", "*4*899*0##", requestTemperature("899", KELVIN).getValue());
    }

    @Test
    public void testRequestTemperatureInvalid() {
        assertThat(captureThrowable(() -> requestTemperature(null)))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("invalid integer format");

        assertThat(captureThrowable(() -> requestTemperature("")))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("invalid integer format");

        assertThat(captureThrowable(() -> requestTemperature("XXX")))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("invalid integer format");

        assertThat(captureThrowable(() -> requestTemperature("-1")))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("value must be between 0 and 899");

        assertThat(captureThrowable(() -> requestTemperature("900")))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("value must be between 0 and 899");

        assertThat(captureThrowable(() -> requestTemperature("1", null)))
            .isInstanceOf(NullPointerException.class)
            .hasMessage("invalid null scale");
    }

}
