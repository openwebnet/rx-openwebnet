package com.github.niqdev.openwebnet.message;

import com.github.niqdev.openwebnet.OpenSession;
import com.google.common.collect.Lists;
import org.junit.Test;
import rx.Observable;
import rx.functions.Action0;

import static com.github.niqdev.openwebnet.ThrowableCaptor.captureThrowable;
import static com.github.niqdev.openwebnet.message.Automation.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class AutomationTest {

    @Test
    public void testRequestStop() {
        assertEquals("should be a valid value", "*2*0*21##", requestStop("21").getValue());
        assertEquals("should be a valid value", "*2*0*0##", requestStop("0").getValue());
        assertEquals("should be a valid value", "*2*0*9999##", requestStop("9999").getValue());
        assertEquals("should be a valid value", "*2*0*01##", requestStop("01").getValue());
        assertEquals("should be a valid value", "*2*0*1##", requestStop("1").getValue());
        assertEquals("should be a valid value", "*2*0*09##", requestStop("09").getValue());
        assertEquals("should be a valid value", "*2*0*9##", requestStop("9").getValue());
    }

    @Test
    public void testRequestStopInvalid() {
        assertThat(captureThrowable(() -> requestStop("")))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("invalid integer format");

        assertThat(captureThrowable(() -> requestStop("a1")))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("invalid integer format");

        assertThat(captureThrowable(() -> requestStop("-1")))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("value must be between 0 and 9999");

        assertThat(captureThrowable(() -> requestStop("10000")))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("value must be between 0 and 9999");
    }

    @Test
    public void testRequestMoveUp() {
        assertEquals("should be a valid value", "*2*1*21##", requestMoveUp("21").getValue());
        assertEquals("should be a valid value", "*2*1*0##", requestMoveUp("0").getValue());
        assertEquals("should be a valid value", "*2*1*9999##", requestMoveUp("9999").getValue());
        assertEquals("should be a valid value", "*2*1*01##", requestMoveUp("01").getValue());
        assertEquals("should be a valid value", "*2*1*1##", requestMoveUp("1").getValue());
        assertEquals("should be a valid value", "*2*1*09##", requestMoveUp("09").getValue());
        assertEquals("should be a valid value", "*2*1*9##", requestMoveUp("9").getValue());
    }

    @Test
    public void testRequestMoveUpInvalid() {
        assertThat(captureThrowable(() -> requestMoveUp("")))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("invalid integer format");

        assertThat(captureThrowable(() -> requestMoveUp("a1")))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("invalid integer format");

        assertThat(captureThrowable(() -> requestMoveUp("-1")))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("value must be between 0 and 9999");

        assertThat(captureThrowable(() -> requestMoveUp("10000")))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("value must be between 0 and 9999");
    }

    @Test
    public void testRequestDown() {
        assertEquals("should be a valid value", "*2*2*21##", requestMoveDown("21").getValue());
        assertEquals("should be a valid value", "*2*2*0##", requestMoveDown("0").getValue());
        assertEquals("should be a valid value", "*2*2*9999##", requestMoveDown("9999").getValue());
        assertEquals("should be a valid value", "*2*2*01##", requestMoveDown("01").getValue());
        assertEquals("should be a valid value", "*2*2*1##", requestMoveDown("1").getValue());
        assertEquals("should be a valid value", "*2*2*09##", requestMoveDown("09").getValue());
        assertEquals("should be a valid value", "*2*2*9##", requestMoveDown("9").getValue());
    }

    @Test
    public void testRequestDownInvalid() {
        assertThat(captureThrowable(() -> requestMoveDown("")))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("invalid integer format");

        assertThat(captureThrowable(() -> requestMoveDown("a1")))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("invalid integer format");

        assertThat(captureThrowable(() -> requestMoveDown("-1")))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("value must be between 0 and 9999");

        assertThat(captureThrowable(() -> requestMoveDown("10000")))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("value must be between 0 and 9999");
    }

    @Test
    public void testHandleResponse() {
        Action0 onSuccessMock = mock(Action0.class);
        Action0 onFailMock = mock(Action0.class);

        OpenSession openSession = OpenSession.newSession(() -> "*2*REQUEST");
        openSession.addAllResponse(Lists.newArrayList(() -> "*#*1##"));
        Observable.just(openSession)
            .map(Automation.handleResponse(onSuccessMock, onFailMock))
            .subscribe();

        verify(onSuccessMock).call();
        verify(onFailMock, never()).call();
    }

    @Test
    public void testRequestStatus() {
        assertEquals("should be a valid value", "*#2*21##", requestStatus("21").getValue());
        assertEquals("should be a valid value", "*#2*0##", requestStatus("0").getValue());
        assertEquals("should be a valid value", "*#2*9999##", requestStatus("9999").getValue());
        assertEquals("should be a valid value", "*#2*01##", requestStatus("01").getValue());
        assertEquals("should be a valid value", "*#2*1##", requestStatus("1").getValue());
        assertEquals("should be a valid value", "*#2*09##", requestStatus("09").getValue());
        assertEquals("should be a valid value", "*#2*9##", requestStatus("9").getValue());
    }

    @Test
    public void testRequestStatusInvalid() {
        assertThat(captureThrowable(() -> requestStatus("")))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("invalid integer format");

        assertThat(captureThrowable(() -> requestStatus("a1")))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("invalid integer format");

        assertThat(captureThrowable(() -> requestStatus("-1")))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("value must be between 0 and 9999");

        assertThat(captureThrowable(() -> requestStatus("10000")))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("value must be between 0 and 9999");
    }

    @Test
    public void testIsStop() {
        assertTrue(isStop("*2*0*21##"));
        assertTrue(isStop("*2*0*X##"));
        assertTrue(isStop("*2*0*XXXX##"));
    }

    @Test
    public void testIsStopInvalid() {
        assertFalse(isStop("*2*1*21##"));
        assertFalse(isStop(null));
        assertFalse(isStop(""));
        assertFalse(isStop("*2*0*21"));
        assertFalse(isStop("*2*0*##"));
        assertFalse(isStop("*2*0*XXXXX##"));
    }

    @Test
    public void testIsUp() {
        assertTrue(isUp("*2*1*21##"));
        assertTrue(isUp("*2*1*X##"));
        assertTrue(isUp("*2*1*XXXX##"));
    }

    @Test
    public void testIsUpInvalid() {
        assertFalse(isUp("*2*0*21##"));
        assertFalse(isUp(null));
        assertFalse(isUp(""));
        assertFalse(isUp("*2*1*21"));
        assertFalse(isUp("*2*1*##"));
        assertFalse(isUp("*2*1*XXXXX##"));
    }

    @Test
    public void testIsDown() {
        assertTrue(isDown("*2*2*21##"));
        assertTrue(isDown("*2*2*X##"));
        assertTrue(isDown("*2*2*XXXX##"));
    }

    @Test
    public void testIsDownInvalid() {
        assertFalse(isDown("*2*1*21##"));
        assertFalse(isDown(null));
        assertFalse(isDown(""));
        assertFalse(isDown("*2*2*21"));
        assertFalse(isDown("*2*2*##"));
        assertFalse(isDown("*2*2*XXXXX##"));
    }

    @Test
    public void testHandleStatusStop() {
        Action0 stopAction = mock(Action0.class);
        Action0 upAction = mock(Action0.class);
        Action0 downAction = mock(Action0.class);

        OpenSession openSession = OpenSession.newSession(() -> "*#2*REQUEST");
        openSession.addAllResponse(Lists.newArrayList(() -> "*2*0*21##", () -> "*#*1##"));
        Observable.just(openSession)
                .map(Automation.handleStatus(stopAction, upAction, downAction))
                .subscribe();

        verify(stopAction).call();
        verify(upAction, never()).call();
        verify(downAction, never()).call();
    }


    @Test
    public void testHandleStatusUp() {
        Action0 stopAction = mock(Action0.class);
        Action0 upAction = mock(Action0.class);
        Action0 downAction = mock(Action0.class);

        OpenSession openSession = OpenSession.newSession(() -> "*#2*REQUEST");
        openSession.addAllResponse(Lists.newArrayList(() -> "*2*1*21##", () -> "*#*1##"));
        Observable.just(openSession)
            .map(Automation.handleStatus(stopAction, upAction, downAction))
            .subscribe();

        verify(upAction).call();
        verify(stopAction, never()).call();
        verify(downAction, never()).call();
    }

    @Test
    public void testHandleStatusDown() {
        Action0 stopAction = mock(Action0.class);
        Action0 upAction = mock(Action0.class);
        Action0 downAction = mock(Action0.class);

        OpenSession openSession = OpenSession.newSession(() -> "*#2*REQUEST");
        openSession.addAllResponse(Lists.newArrayList(() -> "*2*2*21##", () -> "*#*1##"));
        Observable.just(openSession)
            .map(Automation.handleStatus(stopAction, upAction, downAction))
            .subscribe();

        verify(downAction).call();
        verify(stopAction, never()).call();
        verify(upAction, never()).call();
    }

}
