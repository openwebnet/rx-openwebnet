package com.github.niqdev.openwebnet.message;

import com.github.niqdev.openwebnet.OpenSession;
import com.google.common.collect.Lists;
import org.junit.Ignore;
import org.junit.Test;
import rx.Observable;
import rx.functions.Action0;

import static com.github.niqdev.openwebnet.ThrowableCaptor.captureThrowable;
import static com.github.niqdev.openwebnet.message.SoundSystem.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

public class SoundSystemTest {

    @Test
    public void testRequestTurnOn() {
        assertEquals("invalid message", "*16*3*0##", requestTurnOn("0").getValue());

        assertEquals("invalid message", "*16*3*#0##", requestTurnOn("#0").getValue());
        assertEquals("invalid message", "*16*3*#9##", requestTurnOn("#9").getValue());

        assertEquals("invalid message", "*16*3*01##", requestTurnOn("01").getValue());
        assertEquals("invalid message", "*16*3*1##", requestTurnOn("1").getValue());
        assertEquals("invalid message", "*16*3*09##", requestTurnOn("09").getValue());
        assertEquals("invalid message", "*16*3*99##", requestTurnOn("99").getValue());

        assertEquals("invalid message", "*16*3*101##", requestTurnOn("101").getValue());
        assertEquals("invalid message", "*16*3*109##", requestTurnOn("109").getValue());

        // should't be valid
        assertEquals("invalid message", "*16*3*001##", requestTurnOn("001").getValue());
        assertEquals("invalid message", "*16*3*000##", requestTurnOn("000").getValue());
    }

    @Test
    public void testRequestTurnOnInvalid() {
        assertThat(captureThrowable(() -> requestTurnOn(null)))
            .isInstanceOf(NullPointerException.class)
            .hasMessage("invalid null value");

        assertThat(captureThrowable(() -> requestTurnOn("")))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("invalid integer format");

        assertThat(captureThrowable(() -> requestTurnOn("a")))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("invalid integer format");

        assertThat(captureThrowable(() -> requestTurnOn("#00")))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("invalid length");

        assertThat(captureThrowable(() -> requestTurnOn("0000")))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("invalid length");

        assertThat(captureThrowable(() -> requestTurnOn("-1")))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("invalid where range");

        assertThat(captureThrowable(() -> requestTurnOn("100")))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("invalid where range");

        assertThat(captureThrowable(() -> requestTurnOn("110")))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("invalid where range");
    }

    @Test
    public void testRequestTurnOff() {
        assertEquals("invalid message", "*16*13*0##", requestTurnOff("0").getValue());

        assertEquals("invalid message", "*16*13*#0##", requestTurnOff("#0").getValue());
        assertEquals("invalid message", "*16*13*#9##", requestTurnOff("#9").getValue());

        assertEquals("invalid message", "*16*13*01##", requestTurnOff("01").getValue());
        assertEquals("invalid message", "*16*13*1##", requestTurnOff("1").getValue());
        assertEquals("invalid message", "*16*13*09##", requestTurnOff("09").getValue());
        assertEquals("invalid message", "*16*13*99##", requestTurnOff("99").getValue());

        assertEquals("invalid message", "*16*13*101##", requestTurnOff("101").getValue());
        assertEquals("invalid message", "*16*13*109##", requestTurnOff("109").getValue());

        // should't be valid
        assertEquals("invalid message", "*16*13*001##", requestTurnOff("001").getValue());
        assertEquals("invalid message", "*16*13*000##", requestTurnOff("000").getValue());
    }

    @Test
    public void testRequestTurnOffInvalid() {
        assertThat(captureThrowable(() -> requestTurnOff(null)))
            .isInstanceOf(NullPointerException.class)
            .hasMessage("invalid null value");

        assertThat(captureThrowable(() -> requestTurnOff("")))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("invalid integer format");

        assertThat(captureThrowable(() -> requestTurnOff("a")))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("invalid integer format");

        assertThat(captureThrowable(() -> requestTurnOff("#00")))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("invalid length");

        assertThat(captureThrowable(() -> requestTurnOff("0000")))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("invalid length");

        assertThat(captureThrowable(() -> requestTurnOff("-1")))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("invalid where range");

        assertThat(captureThrowable(() -> requestTurnOff("100")))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("invalid where range");

        assertThat(captureThrowable(() -> requestTurnOff("110")))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("invalid where range");
    }

    @Test
    public void testHandleResponse() {
        Action0 onSuccessMock = mock(Action0.class);
        Action0 onFailMock = mock(Action0.class);

        OpenSession openSession = OpenSession.newSession(() -> "*16*REQUEST");
        openSession.addAllResponse(Lists.newArrayList(() -> OpenMessage.ACK));
        Observable.just(openSession)
            .map(handleResponse(onSuccessMock, onFailMock))
            .subscribe();

        verify(onSuccessMock).call();
        verify(onFailMock, never()).call();
    }

    @Test
    public void testRequestStatus() {
        assertEquals("invalid message", "*#16*0*5##", requestStatus("0").getValue());

        assertEquals("invalid message", "*#16*#0*5##", requestStatus("#0").getValue());
        assertEquals("invalid message", "*#16*#9*5##", requestStatus("#9").getValue());

        assertEquals("invalid message", "*#16*01*5##", requestStatus("01").getValue());
        assertEquals("invalid message", "*#16*1*5##", requestStatus("1").getValue());
        assertEquals("invalid message", "*#16*09*5##", requestStatus("09").getValue());
        assertEquals("invalid message", "*#16*99*5##", requestStatus("99").getValue());

        assertEquals("invalid message", "*#16*101*5##", requestStatus("101").getValue());
        assertEquals("invalid message", "*#16*109*5##", requestStatus("109").getValue());

        // should't be valid
        assertEquals("invalid message", "*#16*001*5##", requestStatus("001").getValue());
        assertEquals("invalid message", "*#16*000*5##", requestStatus("000").getValue());
    }

    @Test
    public void testRequestStatusInvalid() {
        assertThat(captureThrowable(() -> requestStatus(null)))
            .isInstanceOf(NullPointerException.class)
            .hasMessage("invalid null value");

        assertThat(captureThrowable(() -> requestStatus("")))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("invalid integer format");

        assertThat(captureThrowable(() -> requestStatus("a")))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("invalid integer format");

        assertThat(captureThrowable(() -> requestStatus("#00")))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("invalid length");

        assertThat(captureThrowable(() -> requestStatus("0000")))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("invalid length");

        assertThat(captureThrowable(() -> requestStatus("-1")))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("invalid where range");

        assertThat(captureThrowable(() -> requestStatus("100")))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("invalid where range");

        assertThat(captureThrowable(() -> requestStatus("110")))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("invalid where range");
    }

    @Ignore
    @Test
    public void testIsOn() {
        // TODO 16 or 22

//        final String WHERE = "88";
//        OpenMessage request = requestStatus(WHERE);
//
//        assertFalse("should be off", isOn(request, Lists.newArrayList()));
//        assertFalse("should be off", isOn(request, asList(() -> "")));
//        assertFalse("should be off", isOn(request, asList(() -> ACK)));
//
//        assertTrue("should be on", isOn(request, asList(requestTurnOn(WHERE))));
//        assertTrue("should be on", isOn(request, asList(requestTurnOn(WHERE), () -> "")));
    }

}
