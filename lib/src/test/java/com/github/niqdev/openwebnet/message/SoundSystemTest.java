package com.github.niqdev.openwebnet.message;

import com.github.niqdev.openwebnet.OpenSession;
import com.google.common.collect.Lists;
import org.junit.Test;
import rx.Observable;
import rx.functions.Action0;

import static com.github.niqdev.openwebnet.ThrowableCaptor.captureThrowable;
import static com.github.niqdev.openwebnet.message.SoundSystem.*;
import static java.util.Arrays.asList;
import static junit.framework.TestCase.assertFalse;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

public class SoundSystemTest {

    @Test
    public void testRequestTurnOn() {
        assertEquals("invalid message", "*16*3*0##", requestTurnOn("0", Type.AMPLIFIER_GENERAL, Source.STEREO_CHANNEL).getValue());
        assertEquals("invalid message", "*16*0*0##", requestTurnOn("0", Type.AMPLIFIER_GENERAL, Source.BASE_BAND).getValue());

        assertEquals("invalid message", "*16*3*#0##", requestTurnOn("0", Type.AMPLIFIER_GROUP, Source.STEREO_CHANNEL).getValue());
        assertEquals("invalid message", "*16*3*#9##", requestTurnOn("9", Type.AMPLIFIER_GROUP, Source.STEREO_CHANNEL).getValue());
        assertEquals("invalid message", "*16*0*#0##", requestTurnOn("0", Type.AMPLIFIER_GROUP, Source.BASE_BAND).getValue());
        assertEquals("invalid message", "*16*0*#9##", requestTurnOn("9", Type.AMPLIFIER_GROUP, Source.BASE_BAND).getValue());

        assertEquals("invalid message", "*16*3*01##", requestTurnOn("01", Type.AMPLIFIER_P2P, Source.STEREO_CHANNEL).getValue());
        assertEquals("invalid message", "*16*3*09##", requestTurnOn("09", Type.AMPLIFIER_P2P, Source.STEREO_CHANNEL).getValue());
        assertEquals("invalid message", "*16*3*99##", requestTurnOn("99", Type.AMPLIFIER_P2P, Source.STEREO_CHANNEL).getValue());
        assertEquals("invalid message", "*16*0*01##", requestTurnOn("01", Type.AMPLIFIER_P2P, Source.BASE_BAND).getValue());
        assertEquals("invalid message", "*16*0*09##", requestTurnOn("09", Type.AMPLIFIER_P2P, Source.BASE_BAND).getValue());
        assertEquals("invalid message", "*16*0*99##", requestTurnOn("99", Type.AMPLIFIER_P2P, Source.BASE_BAND).getValue());

        assertEquals("invalid message", "*16*3*100##", requestTurnOn("100", Type.SOURCE_GENERAL, Source.STEREO_CHANNEL).getValue());
        assertEquals("invalid message", "*16*0*100##", requestTurnOn("100", Type.SOURCE_GENERAL, Source.BASE_BAND).getValue());

        assertEquals("invalid message", "*16*3*101##", requestTurnOn("101", Type.SOURCE_P2P, Source.STEREO_CHANNEL).getValue());
        assertEquals("invalid message", "*16*3*109##", requestTurnOn("109", Type.SOURCE_P2P, Source.STEREO_CHANNEL).getValue());
        assertEquals("invalid message", "*16*0*101##", requestTurnOn("101", Type.SOURCE_P2P, Source.BASE_BAND).getValue());
        assertEquals("invalid message", "*16*0*109##", requestTurnOn("109", Type.SOURCE_P2P, Source.BASE_BAND).getValue());
    }

    @Test
    public void testRequestTurnOnInvalid() {
        assertThat(captureThrowable(() -> requestTurnOn(null, null, null)))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("invalid where|type");

        assertThat(captureThrowable(() -> requestTurnOn("", Type.AMPLIFIER_P2P, Source.STEREO_CHANNEL)))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("invalid where|type");

        assertThat(captureThrowable(() -> requestTurnOn("a", Type.AMPLIFIER_P2P, Source.STEREO_CHANNEL)))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("invalid where|type");

        assertThat(captureThrowable(() -> requestTurnOn("1", null, Source.STEREO_CHANNEL)))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("invalid where|type");

        assertThat(captureThrowable(() -> requestTurnOn("1", Type.AMPLIFIER_P2P, null)))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("invalid where|type");
    }

    @Test
    public void testRequestTurnOnInvalid_AmplifierGeneral() {
        assertThat(captureThrowable(() -> requestTurnOn("-1", Type.AMPLIFIER_GENERAL, Source.STEREO_CHANNEL)))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("invalid where|type");

        assertThat(captureThrowable(() -> requestTurnOn("1", Type.AMPLIFIER_GENERAL, Source.STEREO_CHANNEL)))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("invalid where|type");

        assertThat(captureThrowable(() -> requestTurnOn("a", Type.AMPLIFIER_GROUP, Source.STEREO_CHANNEL)))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("invalid integer format");
    }

    @Test
    public void testRequestTurnOnInvalid_AmplifierGroup() {
        assertThat(captureThrowable(() -> requestTurnOn("00", Type.AMPLIFIER_GROUP, Source.STEREO_CHANNEL)))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("invalid where|type");

        assertThat(captureThrowable(() -> requestTurnOn("01", Type.AMPLIFIER_GROUP, Source.STEREO_CHANNEL)))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("invalid where|type");

        assertThat(captureThrowable(() -> requestTurnOn("09", Type.AMPLIFIER_GROUP, Source.STEREO_CHANNEL)))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("invalid where|type");

        assertThat(captureThrowable(() -> requestTurnOn("10", Type.AMPLIFIER_GROUP, Source.STEREO_CHANNEL)))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("invalid where|type");

        assertThat(captureThrowable(() -> requestTurnOn("#1", Type.AMPLIFIER_GROUP, Source.STEREO_CHANNEL)))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("invalid where|type");

        assertThat(captureThrowable(() -> requestTurnOn("#", Type.AMPLIFIER_GROUP, Source.STEREO_CHANNEL)))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("invalid integer format");
    }

    @Test
    public void testRequestTurnOnInvalid_AmplifierP2P() {
        assertThat(captureThrowable(() -> requestTurnOn("1", Type.AMPLIFIER_P2P, Source.STEREO_CHANNEL)))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("invalid where|type");

        assertThat(captureThrowable(() -> requestTurnOn("9", Type.AMPLIFIER_P2P, Source.STEREO_CHANNEL)))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("invalid where|type");

        assertThat(captureThrowable(() -> requestTurnOn("100", Type.AMPLIFIER_P2P, Source.STEREO_CHANNEL)))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("invalid where|type");

        assertThat(captureThrowable(() -> requestTurnOn("001", Type.AMPLIFIER_P2P, Source.STEREO_CHANNEL)))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("invalid where|type");
    }

    @Test
    public void testRequestTurnOnInvalid_SourceGeneral() {
        assertThat(captureThrowable(() -> requestTurnOn("0100", Type.SOURCE_GENERAL, Source.STEREO_CHANNEL)))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("invalid where|type");

        assertThat(captureThrowable(() -> requestTurnOn("99", Type.SOURCE_GENERAL, Source.STEREO_CHANNEL)))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("invalid where|type");

        assertThat(captureThrowable(() -> requestTurnOn("101", Type.SOURCE_GENERAL, Source.STEREO_CHANNEL)))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("invalid where|type");
    }

    @Test
    public void testRequestTurnOnInvalid_SourceP2P() {
        assertThat(captureThrowable(() -> requestTurnOn("100", Type.SOURCE_P2P, Source.STEREO_CHANNEL)))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("invalid where|type");

        assertThat(captureThrowable(() -> requestTurnOn("110", Type.SOURCE_P2P, Source.STEREO_CHANNEL)))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("invalid where|type");

        assertThat(captureThrowable(() -> requestTurnOn("0101", Type.SOURCE_P2P, Source.STEREO_CHANNEL)))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("invalid where|type");
    }

    @Test
    public void testRequestTurnOff() {
        assertEquals("invalid message", "*16*13*0##", requestTurnOff("0", Type.AMPLIFIER_GENERAL, Source.STEREO_CHANNEL).getValue());
        assertEquals("invalid message", "*16*10*0##", requestTurnOff("0", Type.AMPLIFIER_GENERAL, Source.BASE_BAND).getValue());

        assertEquals("invalid message", "*16*13*#0##", requestTurnOff("0", Type.AMPLIFIER_GROUP, Source.STEREO_CHANNEL).getValue());
        assertEquals("invalid message", "*16*13*#9##", requestTurnOff("9", Type.AMPLIFIER_GROUP, Source.STEREO_CHANNEL).getValue());
        assertEquals("invalid message", "*16*10*#0##", requestTurnOff("0", Type.AMPLIFIER_GROUP, Source.BASE_BAND).getValue());
        assertEquals("invalid message", "*16*10*#9##", requestTurnOff("9", Type.AMPLIFIER_GROUP, Source.BASE_BAND).getValue());

        assertEquals("invalid message", "*16*13*01##", requestTurnOff("01", Type.AMPLIFIER_P2P, Source.STEREO_CHANNEL).getValue());
        assertEquals("invalid message", "*16*13*09##", requestTurnOff("09", Type.AMPLIFIER_P2P, Source.STEREO_CHANNEL).getValue());
        assertEquals("invalid message", "*16*13*99##", requestTurnOff("99", Type.AMPLIFIER_P2P, Source.STEREO_CHANNEL).getValue());
        assertEquals("invalid message", "*16*10*01##", requestTurnOff("01", Type.AMPLIFIER_P2P, Source.BASE_BAND).getValue());
        assertEquals("invalid message", "*16*10*09##", requestTurnOff("09", Type.AMPLIFIER_P2P, Source.BASE_BAND).getValue());
        assertEquals("invalid message", "*16*10*99##", requestTurnOff("99", Type.AMPLIFIER_P2P, Source.BASE_BAND).getValue());

        assertEquals("invalid message", "*16*13*100##", requestTurnOff("100", Type.SOURCE_GENERAL, Source.STEREO_CHANNEL).getValue());
        assertEquals("invalid message", "*16*10*100##", requestTurnOff("100", Type.SOURCE_GENERAL, Source.BASE_BAND).getValue());

        assertEquals("invalid message", "*16*13*101##", requestTurnOff("101", Type.SOURCE_P2P, Source.STEREO_CHANNEL).getValue());
        assertEquals("invalid message", "*16*13*109##", requestTurnOff("109", Type.SOURCE_P2P, Source.STEREO_CHANNEL).getValue());
        assertEquals("invalid message", "*16*10*101##", requestTurnOff("101", Type.SOURCE_P2P, Source.BASE_BAND).getValue());
        assertEquals("invalid message", "*16*10*109##", requestTurnOff("109", Type.SOURCE_P2P, Source.BASE_BAND).getValue());
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
        assertEquals("invalid message", "*#16*0*5##", requestStatus("0", Type.AMPLIFIER_GENERAL).getValue());

        assertEquals("invalid message", "*#16*#0*5##", requestStatus("0", Type.AMPLIFIER_GROUP).getValue());
        assertEquals("invalid message", "*#16*#9*5##", requestStatus("9", Type.AMPLIFIER_GROUP).getValue());

        assertEquals("invalid message", "*#16*01*5##", requestStatus("01", Type.AMPLIFIER_P2P).getValue());
        assertEquals("invalid message", "*#16*09*5##", requestStatus("09", Type.AMPLIFIER_P2P).getValue());
        assertEquals("invalid message", "*#16*99*5##", requestStatus("99", Type.AMPLIFIER_P2P).getValue());

        assertEquals("invalid message", "*#16*100*5##", requestStatus("100", Type.SOURCE_GENERAL).getValue());

        assertEquals("invalid message", "*#16*101*5##", requestStatus("101", Type.SOURCE_P2P).getValue());
        assertEquals("invalid message", "*#16*109*5##", requestStatus("109", Type.SOURCE_P2P).getValue());
    }

    @Test
    public void testRequestStatusInvalid() {
        assertThat(captureThrowable(() -> requestStatus(null, Type.AMPLIFIER_P2P)))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("invalid where|type");

        assertThat(captureThrowable(() -> requestStatus("", Type.AMPLIFIER_P2P)))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("invalid where|type");

        assertThat(captureThrowable(() -> requestStatus("a", Type.AMPLIFIER_P2P)))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("invalid where|type");

        assertThat(captureThrowable(() -> requestStatus("88", null)))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("invalid where|type");

        assertThat(captureThrowable(() -> requestStatus("#", Type.AMPLIFIER_GROUP)))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("invalid integer format");

        assertThat(captureThrowable(() -> requestStatus("#1", Type.AMPLIFIER_GROUP)))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("invalid where|type");

        assertThat(captureThrowable(() -> requestStatus("-1", Type.AMPLIFIER_P2P)))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("invalid where|type");
    }

    @Test
    public void testBuildWhereValue() {
        final String VALUE = "XXX";
        assertEquals(buildWhereValue(VALUE, Type.AMPLIFIER_GENERAL), VALUE);
        assertEquals(buildWhereValue(VALUE, Type.AMPLIFIER_P2P), VALUE);
        assertEquals(buildWhereValue(VALUE, Type.SOURCE_GENERAL), VALUE);
        assertEquals(buildWhereValue(VALUE, Type.SOURCE_P2P), VALUE);
        assertEquals(buildWhereValue(VALUE, Type.AMPLIFIER_GROUP), "#" + VALUE);

    }

    @Test
    public void testHandleStatusOn() {
        Action0 onStatusMock = mock(Action0.class);
        Action0 offStatusMock = mock(Action0.class);

        OpenSession openSession = OpenSession.newSession(() -> "*#16*REQUEST");
        openSession.addAllResponse(Lists.newArrayList(() -> "*16*3*01##", () -> "*#*1##"));
        Observable.just(openSession)
            .map(SoundSystem.handleStatus(onStatusMock, offStatusMock))
            .subscribe();

        verify(onStatusMock).call();
        verify(offStatusMock, never()).call();
    }

    @Test
    public void testIsOn() {
        final String WHERE = "88";
        OpenMessage request = requestStatus(WHERE, Type.AMPLIFIER_P2P);

        assertFalse("should be off", isOn(request, Lists.newArrayList()));
        assertFalse("should be off", isOn(request, asList(() -> "")));
        assertFalse("should be off", isOn(request, asList(() -> ACK)));

        assertFalse("should be off", isOn(request, asList(requestStatus("0", Type.AMPLIFIER_GENERAL), () -> ACK)));

        assertTrue("should be on", isOn(request, asList(requestTurnOn(WHERE, Type.AMPLIFIER_P2P, Source.STEREO_CHANNEL))));
        assertTrue("should be on", isOn(request, asList(requestTurnOn(WHERE, Type.AMPLIFIER_P2P, Source.STEREO_CHANNEL), () -> "")));

        assertTrue("should be on", isOn(request, asList(requestTurnOn(WHERE, Type.AMPLIFIER_P2P, Source.BASE_BAND))));
        assertTrue("should be on", isOn(request, asList(requestTurnOn(WHERE, Type.AMPLIFIER_P2P, Source.BASE_BAND), () -> "")));
    }

    @Test
    public void testRequestVolumeUp() {
        throw new UnsupportedOperationException("not implemented");
    }

    @Test
    public void testRequestVolumeDown() {
        throw new UnsupportedOperationException("not implemented");
    }

    @Test
    public void testRequestStationUp() {
        throw new UnsupportedOperationException("not implemented");
    }

    @Test
    public void testRequestStationDown() {
        throw new UnsupportedOperationException("not implemented");
    }

}
