package com.github.niqdev.openwebnet.message;

import com.github.niqdev.openwebnet.OpenSession;
import com.google.common.collect.Lists;
import org.junit.Test;
import rx.Observable;
import rx.functions.Action0;

import static com.github.niqdev.openwebnet.ThrowableCaptor.captureThrowable;
import static com.github.niqdev.openwebnet.message.Scene.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * TODO translation for Scenario - Scene??
 */
public class SceneTest {

    @Test
    public void testRequestStart() {
        assertEquals("invalid message", "*17*1*0##", requestStart("0").getValue());
        assertEquals("invalid message", "*17*1*0##", requestStart("0", Scene.Version.MH200N).getValue());
        assertEquals("invalid message", "*17*1*0##", requestStart("0", Scene.Version.MH202).getValue());

        assertEquals("invalid message", "*17*1*9999##", requestStart("9999").getValue());
        assertEquals("invalid message", "*17*1*300##", requestStart("300", Scene.Version.MH200N).getValue());
        assertEquals("invalid message", "*17*1*9999##", requestStart("9999", Scene.Version.MH202).getValue());
    }

    @Test
    public void testRequestStartInvalid() {
        assertThat(captureThrowable(() -> requestStart(null)))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("invalid integer format");

        assertThat(captureThrowable(() -> requestStart("")))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("invalid integer format");

        assertThat(captureThrowable(() -> requestStart("a")))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("invalid integer format");

        assertThat(captureThrowable(() -> requestStart("-1")))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("value must be between 0 and 9999");

        assertThat(captureThrowable(() -> requestStart("10000")))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("value must be between 0 and 9999");

        assertThat(captureThrowable(() -> requestStart("301", Scene.Version.MH200N)))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("value must be between 0 and 300");
    }

    @Test
    public void testRequestStop() {
        assertEquals("invalid message", "*17*2*0##", requestStop("0").getValue());
        assertEquals("invalid message", "*17*2*0##", requestStop("0", Scene.Version.MH200N).getValue());
        assertEquals("invalid message", "*17*2*0##", requestStop("0", Scene.Version.MH202).getValue());

        assertEquals("invalid message", "*17*2*9999##", requestStop("9999").getValue());
        assertEquals("invalid message", "*17*2*300##", requestStop("300", Scene.Version.MH200N).getValue());
        assertEquals("invalid message", "*17*2*9999##", requestStop("9999", Scene.Version.MH202).getValue());
    }

    @Test
    public void testRequestStopInvalid() {
        assertThat(captureThrowable(() -> requestStop(null)))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("invalid integer format");

        assertThat(captureThrowable(() -> requestStop("")))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("invalid integer format");

        assertThat(captureThrowable(() -> requestStop("a")))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("invalid integer format");

        assertThat(captureThrowable(() -> requestStop("-1")))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("value must be between 0 and 9999");

        assertThat(captureThrowable(() -> requestStop("10000")))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("value must be between 0 and 9999");

        assertThat(captureThrowable(() -> requestStop("301", Scene.Version.MH200N)))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("value must be between 0 and 300");
    }

    @Test
    public void testHandleResponse() {
        Action0 onSuccessMock = mock(Action0.class);
        Action0 onFailMock = mock(Action0.class);

        OpenSession openSession = OpenSession.newSession(() -> "*17*REQUEST");
        openSession.addAllResponse(Lists.newArrayList(() -> OpenMessage.ACK));
        Observable.just(openSession)
            .map(handleResponse(onSuccessMock, onFailMock))
            .subscribe();

        verify(onSuccessMock).call();
        verify(onFailMock, never()).call();
    }

    @Test
    public void testRequestStatus() {
        assertEquals("invalid message", "*#17*0##", requestStatus("0").getValue());
        assertEquals("invalid message", "*#17*0##", requestStatus("0", Version.MH200N).getValue());
        assertEquals("invalid message", "*#17*0##", requestStatus("0", Version.MH202).getValue());

        assertEquals("invalid message", "*#17*9999##", requestStatus("9999").getValue());
        assertEquals("invalid message", "*#17*300##", requestStatus("300", Version.MH200N).getValue());
        assertEquals("invalid message", "*#17*9999##", requestStatus("9999", Version.MH202).getValue());
    }

    @Test
    public void testRequestStatusInvalid() {
        assertThat(captureThrowable(() -> requestStatus(null)))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("invalid integer format");

        assertThat(captureThrowable(() -> requestStatus("")))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("invalid integer format");

        assertThat(captureThrowable(() -> requestStatus("a")))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("invalid integer format");

        assertThat(captureThrowable(() -> requestStatus("-1")))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("value must be between 0 and 9999");

        assertThat(captureThrowable(() -> requestStatus("10000")))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("value must be between 0 and 9999");

        assertThat(captureThrowable(() -> requestStatus("301", Scene.Version.MH200N)))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("value must be between 0 and 300");
    }

    @Test
    public void testIsStarted() {
        assertTrue(isStarted("*17*1*0##"));
        assertTrue(isStarted("*17*1*X##"));
        assertTrue(isStarted("*17*1*XXXX##"));
    }

    @Test
    public void testIsStartedInvalid() {
        assertFalse(isStarted("*17*2*0##"));
        assertFalse(isStarted(null));
        assertFalse(isStarted(""));
        assertFalse(isStarted("*17*1*21"));
        assertFalse(isStarted("*17*1*##"));
        assertFalse(isStarted("*17*1*XXXXX##"));
    }

    @Test
    public void testIsStopped() {
        assertTrue(isStopped("*17*2*0##"));
        assertTrue(isStopped("*17*2*X##"));
        assertTrue(isStopped("*17*2*XXXX##"));
    }

    @Test
    public void testIsStoppedInvalid() {
        assertFalse(isStopped("*17*1*0##"));
        assertFalse(isStopped(null));
        assertFalse(isStopped(""));
        assertFalse(isStopped("*17*2*21"));
        assertFalse(isStopped("*17*2*##"));
        assertFalse(isStopped("*17*2*XXXXX##"));
    }

    @Test
    public void testHandleStatusStart() {
        Action0 startStatusMock = mock(Action0.class);
        Action0 stopStatusMock = mock(Action0.class);

        OpenSession openSession = OpenSession.newSession(() -> "*#17*REQUEST");
        openSession.addAllResponse(Lists.newArrayList(() -> "*17*1*0##", () -> "*#*1##"));
        Observable.just(openSession)
            .map(handleStatus(startStatusMock, stopStatusMock))
            .subscribe();

        verify(startStatusMock).call();
        verify(stopStatusMock, never()).call();
    }

    @Test
    public void testHandleStatusStop() {
        Action0 startStatusMock = mock(Action0.class);
        Action0 stopStatusMock = mock(Action0.class);

        OpenSession openSession = OpenSession.newSession(() -> "*#17*REQUEST");
        openSession.addAllResponse(Lists.newArrayList(() -> "*17*2*0##", () -> "*#*1##"));
        Observable.just(openSession)
            .map(handleStatus(startStatusMock, stopStatusMock))
            .subscribe();

        verify(stopStatusMock).call();
        verify(startStatusMock, never()).call();
    }

}
