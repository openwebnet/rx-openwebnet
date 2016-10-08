package com.github.niqdev.openwebnet.message;

import com.github.niqdev.openwebnet.OpenSession;
import com.google.common.collect.Lists;
import org.junit.Test;
import rx.Observable;
import rx.functions.Action0;

import static com.github.niqdev.openwebnet.ThrowableCaptor.captureThrowable;
import static com.github.niqdev.openwebnet.message.Lighting.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class LightingTest {

    @Test
    public void testRequestTurnOn() {
        assertEquals("should be a valid value", "*1*1*21##", requestTurnOn("21").getValue());
        assertEquals("should be a valid value", "*1*1*0##", requestTurnOn("0").getValue());
        assertEquals("should be a valid value", "*1*1*9999##", requestTurnOn("9999").getValue());
        assertEquals("should be a valid value", "*1*1*01##", requestTurnOn("01").getValue());
        assertEquals("should be a valid value", "*1*1*1##", requestTurnOn("1").getValue());
        assertEquals("should be a valid value", "*1*1*09##", requestTurnOn("09").getValue());
        assertEquals("should be a valid value", "*1*1*9##", requestTurnOn("9").getValue());
    }

    @Test
    public void testRequestTurnOnWithType() {
        assertEquals("should be a valid value", "*1*1*0##", requestTurnOn("0", Type.GENERAL).getValue());
        assertEquals("should be a valid value", "*1*1*9##", requestTurnOn("9", Type.AREA).getValue());
        assertEquals("should be a valid value", "*1*1*#21##", requestTurnOn("21", Type.GROUP).getValue());
        assertEquals("should be a valid value", "*1*1*21##", requestTurnOn("21", Type.POINT_TO_POINT).getValue());
    }

    @Test
    public void testRequestTurnOnInvalid() {
        assertThat(captureThrowable(() -> requestTurnOn(null)))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("invalid integer format");

        assertThat(captureThrowable(() -> requestTurnOn("")))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("invalid integer format");

        assertThat(captureThrowable(() -> requestTurnOn("a1")))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("invalid integer format");

        assertThat(captureThrowable(() -> requestTurnOn("-1")))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("value must be between 0 and 9999");

        assertThat(captureThrowable(() -> requestTurnOn("10000")))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("value must be between 0 and 9999");
    }

    @Test
    public void testRequestTurnOff() {
        assertEquals("should be a valid value", "*1*0*21##", requestTurnOff("21").getValue());
        assertEquals("should be a valid value", "*1*0*0##", requestTurnOff("0").getValue());
        assertEquals("should be a valid value", "*1*0*9999##", requestTurnOff("9999").getValue());
        assertEquals("should be a valid value", "*1*0*01##", requestTurnOff("01").getValue());
        assertEquals("should be a valid value", "*1*0*1##", requestTurnOff("1").getValue());
        assertEquals("should be a valid value", "*1*0*09##", requestTurnOff("09").getValue());
        assertEquals("should be a valid value", "*1*0*9##", requestTurnOff("9").getValue());
    }

    @Test
    public void testRequestTurnOffWithType() {
        assertEquals("should be a valid value", "*1*0*0##", requestTurnOff("0", Type.GENERAL).getValue());
        assertEquals("should be a valid value", "*1*0*9##", requestTurnOff("9", Type.AREA).getValue());
        assertEquals("should be a valid value", "*1*0*#21##", requestTurnOff("21", Type.GROUP).getValue());
        assertEquals("should be a valid value", "*1*0*21##", requestTurnOff("21", Type.POINT_TO_POINT).getValue());
    }

    @Test
    public void testRequestTurnOffInvalid() {
        assertThat(captureThrowable(() -> requestTurnOff(null)))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("invalid integer format");

        assertThat(captureThrowable(() -> requestTurnOff("")))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("invalid integer format");

        assertThat(captureThrowable(() -> requestTurnOff("a1")))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("invalid integer format");

        assertThat(captureThrowable(() -> requestTurnOff("-1")))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("value must be between 0 and 9999");

        assertThat(captureThrowable(() -> requestTurnOff("10000")))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("value must be between 0 and 9999");
    }

    @Test
    public void testHandleResponse() {
        Action0 onSuccessMock = mock(Action0.class);
        Action0 onFailMock = mock(Action0.class);

        OpenSession openSession = OpenSession.newSession(() -> "*1*REQUEST");
        openSession.addAllResponse(Lists.newArrayList(() -> "*#*1##"));
        Observable.just(openSession)
            .map(Lighting.handleResponse(onSuccessMock, onFailMock))
            .subscribe();

        verify(onSuccessMock).call();
        verify(onFailMock, never()).call();
    }

    @Test
    public void testRequestStatus() {
        assertEquals("should be a valid value", "*#1*21##", requestStatus("21").getValue());
        assertEquals("should be a valid value", "*#1*0##", requestStatus("0").getValue());
        assertEquals("should be a valid value", "*#1*9999##", requestStatus("9999").getValue());
        assertEquals("should be a valid value", "*#1*01##", requestStatus("01").getValue());
        assertEquals("should be a valid value", "*#1*1##", requestStatus("1").getValue());
        assertEquals("should be a valid value", "*#1*09##", requestStatus("09").getValue());
        assertEquals("should be a valid value", "*#1*9##", requestStatus("9").getValue());
    }

    @Test
    public void testRequestStatusInvalid() {
        assertThat(captureThrowable(() -> requestStatus(null)))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("invalid integer format");

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
    public void testIsOn() {
        assertTrue(isOn("*1*1*21##"));
        assertTrue(isOn("*1*1*X##"));
        assertTrue(isOn("*1*1*XXXX##"));
    }

    @Test
    public void testIsOnInvalid() {
        assertFalse(isOn("*1*0*21##"));
        assertFalse(isOn(null));
        assertFalse(isOn(""));
        assertFalse(isOn("*1*1*21"));
        assertFalse(isOn("*1*1*##"));
        assertFalse(isOn("*1*1*XXXXX##"));
    }

    @Test
    public void testIsOff() {
        assertTrue(isOff("*1*0*21##"));
        assertTrue(isOff("*1*0*X##"));
        assertTrue(isOff("*1*0*XXXX##"));
    }

    @Test
    public void testIsOffInvalid() {
        assertFalse(isOff("*1*1*21##"));
        assertFalse(isOff(null));
        assertFalse(isOff(""));
        assertFalse(isOff("*1*0*21"));
        assertFalse(isOff("*1*0*##"));
        assertFalse(isOff("*1*0*XXXXX##"));
    }

    @Test
    public void testHandleStatusOn() {
        Action0 onStatusMock = mock(Action0.class);
        Action0 offStatusMock = mock(Action0.class);

        OpenSession openSession = OpenSession.newSession(() -> "*#1*REQUEST");
        openSession.addAllResponse(Lists.newArrayList(() -> "*1*1*21##", () -> "*#*1##"));
        Observable.just(openSession)
            .map(Lighting.handleStatus(onStatusMock, offStatusMock))
            .subscribe();

        verify(onStatusMock).call();
        verify(offStatusMock, never()).call();
    }

    @Test
    public void testHandleStatusOff() {
        Action0 onStatusMock = mock(Action0.class);
        Action0 offStatusMock = mock(Action0.class);

        OpenSession openSession = OpenSession.newSession(() -> "*#1*REQUEST");
        openSession.addAllResponse(Lists.newArrayList(() -> "*1*0*21##", () -> "*#*1##"));
        Observable.just(openSession)
            .map(Lighting.handleStatus(onStatusMock, offStatusMock))
            .subscribe();

        verify(offStatusMock).call();
        verify(onStatusMock, never()).call();
    }

    @Test
    public void testCheckRangeType() {
        checkRangeType("0", Type.GENERAL);
        checkRangeType("00", Type.AREA);
        checkRangeType("1", Type.AREA);
        checkRangeType("9", Type.AREA);
        checkRangeType("100", Type.AREA);
        checkRangeType("1", Type.GROUP);
        checkRangeType("255", Type.GROUP);
        checkRangeType("0", Type.POINT_TO_POINT);
        checkRangeType("9999", Type.POINT_TO_POINT);
    }

    @Test
    public void testCheckRangeType_invalidArgument() {
        assertThat(captureThrowable(() -> checkRangeType(null, Type.POINT_TO_POINT)))
            .isInstanceOf(NullPointerException.class)
            .hasMessage("invalid null value: [where]");

        assertThat(captureThrowable(() -> checkRangeType("1", null)))
            .isInstanceOf(NullPointerException.class)
            .hasMessage("invalid null value: [type]");

        assertThat(captureThrowable(() -> checkRangeType("", Type.POINT_TO_POINT)))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("invalid length [1-4]");

        assertThat(captureThrowable(() -> checkRangeType("XXXXX", Type.POINT_TO_POINT)))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("invalid length [1-4]");
    }

    @Test
    public void testCheckRangeType_invalidGeneral() {
        assertThat(captureThrowable(() -> checkRangeType("1", Type.GENERAL)))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("allowed value [0]");

        assertThat(captureThrowable(() -> checkRangeType("-1", Type.GENERAL)))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("allowed value [0]");

        assertThat(captureThrowable(() -> checkRangeType("x", Type.GENERAL)))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("allowed value [0]");

        assertThat(captureThrowable(() -> checkRangeType("#", Type.GENERAL)))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("allowed value [0]");
    }

    @Test
    public void testCheckRangeType_invalidArea() {
        assertThat(captureThrowable(() -> checkRangeType("0", Type.AREA)))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("allowed value [00, 1−9, 100]");

        assertThat(captureThrowable(() -> checkRangeType("01", Type.AREA)))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("allowed value [00, 1−9, 100]");

        assertThat(captureThrowable(() -> checkRangeType("10", Type.AREA)))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("allowed value [00, 1−9, 100]");

        assertThat(captureThrowable(() -> checkRangeType("99", Type.AREA)))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("allowed value [00, 1−9, 100]");

        assertThat(captureThrowable(() -> checkRangeType("101", Type.AREA)))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("allowed value [00, 1−9, 100]");

        assertThat(captureThrowable(() -> checkRangeType("-1", Type.AREA)))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("allowed value [00, 1−9, 100]");

        assertThat(captureThrowable(() -> checkRangeType("x", Type.AREA)))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("invalid integer format");

        assertThat(captureThrowable(() -> checkRangeType("#", Type.AREA)))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("invalid integer format");
    }

    @Test
    public void testCheckRangeType_invalidGroup() {
        assertThat(captureThrowable(() -> checkRangeType("-1", Type.GROUP)))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("value must be between 1 and 255");

        assertThat(captureThrowable(() -> checkRangeType("x", Type.GROUP)))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("invalid integer format");

        assertThat(captureThrowable(() -> checkRangeType("#", Type.GROUP)))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("invalid integer format");

        assertThat(captureThrowable(() -> checkRangeType("0", Type.GROUP)))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("value must be between 1 and 255");

        // TODO this shouldn't pass
        //assertThat(captureThrowable(() -> checkRangeType("001", Type.GROUP)))
        //    .isInstanceOf(IllegalArgumentException.class)
        //    .hasMessage("value must be between 1 and 255");

        assertThat(captureThrowable(() -> checkRangeType("256", Type.GROUP)))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("value must be between 1 and 255");
    }

    @Test
    public void testCheckRangeType_invalidPointToPoint() {
        assertThat(captureThrowable(() -> checkRangeType("-1", Type.POINT_TO_POINT)))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("value must be between 0 and 9999");

        assertThat(captureThrowable(() -> checkRangeType("10000", Type.POINT_TO_POINT)))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("invalid length [1-4]");
    }

}
