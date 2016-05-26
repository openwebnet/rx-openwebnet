package com.github.niqdev.openwebnet.message;

import com.github.niqdev.openwebnet.OpenSession;
import com.google.common.collect.Lists;
import org.junit.Test;
import rx.Observable;
import rx.functions.Action0;
import rx.functions.Action1;

import static com.github.niqdev.openwebnet.ThrowableCaptor.captureThrowable;
import static com.github.niqdev.openwebnet.message.Heating.TemperatureScale.*;
import static com.github.niqdev.openwebnet.message.Heating.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class HeatingTest {

    @Test
    public void testRequestTemperature() {
        assertEquals("should be a valid value", "*#4*0*0##", requestTemperature("0").getValue());
        assertEquals("should be a valid value", "*#4*001*0##", requestTemperature("001").getValue());
        assertEquals("should be a valid value", "*#4*899*0##", requestTemperature("899").getValue());
    }

    @Test
    public void testRequestTemperatureWithScale() {
        assertEquals("should be a valid value", "*#4*0*0##", requestTemperature("0", CELSIUS).getValue());
        assertEquals("should be a valid value", "*#4*001*0##", requestTemperature("001", FAHRENHEIT).getValue());
        assertEquals("should be a valid value", "*#4*899*0##", requestTemperature("899", KELVIN).getValue());
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

    @Test
    public void testHandleTemperature() {
        Action1 onSuccessMock = mock(Action1.class);
        Action0 onErrorMock = mock(Action0.class);

        OpenSession openSession = OpenSession.newSession(requestTemperature("0"));
        openSession.addAllResponse(Lists.newArrayList(() -> "*#4*0*0*0225##", () -> OpenMessage.ACK));
        Observable.just(openSession)
            .map(Heating.handleTemperature(onSuccessMock, onErrorMock))
            .subscribe();

        verify(onSuccessMock).call(new Double(22.5));
        verify(onErrorMock, never()).call();
    }

    @Test
    public void testHandleTemperatureInvalid() {
        Action1 onSuccessMock = mock(Action1.class);
        Action0 onErrorMock = mock(Action0.class);

        OpenSession openSession = OpenSession.newSession(requestTemperature("0"));
        openSession.addAllResponse(Lists.newArrayList(() -> OpenMessage.ACK));
        Observable.just(openSession)
            .map(Heating.handleTemperature(onSuccessMock, onErrorMock))
            .subscribe();

        verify(onSuccessMock, never()).call(new Double(22.5));
        verify(onErrorMock).call();
    }

    @Test
    public void testIsValidTemperature() {
        assertTrue("should be valid", isValidTemperature("*#4*0*0*0225##"));
        assertTrue("should be valid", isValidTemperature("*#4*00*0*0225##"));
        assertTrue("should be valid", isValidTemperature("*#4*899*0*0225##"));
        assertTrue("should be valid", isValidTemperature("*#4*XXXXXXXX##"));
        assertTrue("should be valid", isValidTemperature("*#4*XXXXXXXXXX##"));

        assertFalse("should be invalid", isValidTemperature("*#4*XXXXXXX##"));
        assertFalse("should be invalid", isValidTemperature("*#4*XXXXXXXXXXX##"));
        assertFalse("should be invalid", isValidTemperature("*#4*0*0##"));
        assertFalse("should be invalid", isValidTemperature("*#4*899*0##"));
        assertFalse("should be invalid", isValidTemperature(null));
        assertFalse("should be invalid", isValidTemperature(""));
    }

    @Test
    public void testGetTemperatureCelsius() {
        Heating request = Heating.requestTemperature("012");

        assertEquals("invalid temperature", new Double(22.5), getTemperature(request, "*#4*012*0*0225##"));
        assertEquals("invalid temperature", new Double(22.0), getTemperature(request, "*#4*012*0*0220##"));

        Heating requestCelsius = Heating.requestTemperature("012", CELSIUS);
        assertEquals("invalid temperature", new Double(22.5), getTemperature(requestCelsius, "*#4*012*0*0225##"));
        assertEquals("invalid temperature", new Double(22.0), getTemperature(requestCelsius, "*#4*012*0*0220##"));
    }

    @Test
    public void testGetTemperatureFahrenheit() {
        Heating request = Heating.requestTemperature("0", FAHRENHEIT);

        assertEquals("invalid temperature", new Double(32.0), getTemperature(request, "*#4*0*0*0000##"));
        assertEquals("invalid temperature", new Double(122.18), getTemperature(request, "*#4*0*0*0501##"));
    }

    @Test
    public void testGetTemperatureKelvin() {
        Heating request = Heating.requestTemperature("899", KELVIN);

        assertEquals("invalid temperature", new Double(273.15), getTemperature(request, "*#4*899*0*0000##"));
        assertEquals("invalid temperature", new Double(311.35), getTemperature(request, "*#4*899*0*0382##"));
    }

    @Test
    public void testGetTemperatureInvalid() {
        assertThat(captureThrowable(() -> getTemperature(requestTemperature("012"), "*#4*0*0*0225##")))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("invalid temperature length");

        assertThat(captureThrowable(() -> getTemperature(Lighting.requestTurnOn("21"), "*#4*0*0*0225##")))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("invalid request type");

        assertThat(captureThrowable(() -> getTemperature(Automation.requestStatus("10"), "*#4*0*0*0225##")))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("invalid request type");
    }

    @Test
    public void testToFahrenheit() {
        assertEquals("bad conversion", new Double(32.0), toFahrenheit(0.0));
        assertEquals("bad conversion", new Double(68.0), toFahrenheit(20.0));
        assertEquals("bad conversion", new Double(72.5), toFahrenheit(22.5));
        assertEquals("bad conversion", new Double(100.76), toFahrenheit(38.2));
        assertEquals("bad conversion", new Double(122.18), toFahrenheit(50.1));
        assertEquals("bad conversion", new Double(10.04), toFahrenheit(-12.2));
        assertEquals("bad conversion", new Double(9.86), toFahrenheit(-12.3));
        assertEquals("bad conversion", new Double(-62.14), toFahrenheit(-52.3));
    }

    @Test
    public void testToKelvin() {
        assertEquals("bad conversion", new Double(273.15), toKelvin(0.0));
        assertEquals("bad conversion", new Double(293.15), toKelvin(20.0));
        assertEquals("bad conversion", new Double(295.65), toKelvin(22.5));
        assertEquals("bad conversion", new Double(311.35), toKelvin(38.2));
        assertEquals("bad conversion", new Double(323.25), toKelvin(50.1));
        assertEquals("bad conversion", new Double(260.95), toKelvin(-12.2));
        assertEquals("bad conversion", new Double(0.0), toKelvin(-273.15));
    }

}
