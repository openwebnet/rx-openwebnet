package com.github.niqdev.openwebnet.message;

import com.github.niqdev.openwebnet.OpenSession;
import com.google.common.collect.Lists;
import org.junit.Test;
import rx.Observable;
import rx.functions.Action0;
import rx.functions.Action1;

import java.util.List;

import static com.github.niqdev.openwebnet.ThrowableCaptor.captureThrowable;
import static com.github.niqdev.openwebnet.message.EnergyManagement.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class EnergyManagementTest {

    @Test
    public void testRequestInstantaneous() {
        assertEquals("invalid message", "*#18*51*113##", requestInstantaneousPower("1", Version.MODEL_F523).getValue());
        assertEquals("invalid message", "*#18*5255*113##", requestInstantaneousPower("255", Version.MODEL_F523).getValue());

        assertEquals("invalid message", "*#18*71#0*113##", requestInstantaneousPower("1", Version.MODEL_F523_A).getValue());
        assertEquals("invalid message", "*#18*7255#0*113##", requestInstantaneousPower("255", Version.MODEL_F523_A).getValue());
    }

    @Test
    public void testRequestDaily() {
        assertEquals("invalid message", "*#18*51*54##", requestDailyPower("1", Version.MODEL_F523).getValue());
        assertEquals("invalid message", "*#18*5255*54##", requestDailyPower("255", Version.MODEL_F523).getValue());

        assertEquals("invalid message", "*#18*71#0*54##", requestDailyPower("1", Version.MODEL_F523_A).getValue());
        assertEquals("invalid message", "*#18*7255#0*54##", requestDailyPower("255", Version.MODEL_F523_A).getValue());
    }

    @Test
    public void testRequestMonthly() {
        assertEquals("invalid message", "*#18*51*53##", requestMonthlyPower("1", Version.MODEL_F523).getValue());
        assertEquals("invalid message", "*#18*5255*53##", requestMonthlyPower("255", Version.MODEL_F523).getValue());

        assertEquals("invalid message", "*#18*71#0*53##", requestMonthlyPower("1", Version.MODEL_F523_A).getValue());
        assertEquals("invalid message", "*#18*7255#0*53##", requestMonthlyPower("255", Version.MODEL_F523_A).getValue());
    }

    @Test
    public void testRequestPowerInvalidVersion() {
        assertThat(captureThrowable(() -> requestInstantaneousPower("1", null)))
            .isInstanceOf(NullPointerException.class)
            .hasMessage("invalid null version");

        assertThat(captureThrowable(() -> requestDailyPower("1", null)))
            .isInstanceOf(NullPointerException.class)
            .hasMessage("invalid null version");

        assertThat(captureThrowable(() -> requestMonthlyPower("1", null)))
            .isInstanceOf(NullPointerException.class)
            .hasMessage("invalid null version");
    }

    @Test
    public void testRequestPowerInvalidWhere() {
        assertThat(captureThrowable(() -> requestInstantaneousPower(null, Version.MODEL_F523)))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("invalid integer format");
        assertThat(captureThrowable(() -> requestDailyPower(null, Version.MODEL_F523)))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("invalid integer format");
        assertThat(captureThrowable(() -> requestMonthlyPower(null, Version.MODEL_F523)))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("invalid integer format");

        assertThat(captureThrowable(() -> requestInstantaneousPower("", Version.MODEL_F523)))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("invalid integer format");
        assertThat(captureThrowable(() -> requestDailyPower("", Version.MODEL_F523)))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("invalid integer format");
        assertThat(captureThrowable(() -> requestMonthlyPower("", Version.MODEL_F523)))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("invalid integer format");

        assertThat(captureThrowable(() -> requestInstantaneousPower("XXX", Version.MODEL_F523)))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("invalid integer format");
        assertThat(captureThrowable(() -> requestDailyPower("XXX", Version.MODEL_F523)))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("invalid integer format");
        assertThat(captureThrowable(() -> requestMonthlyPower("XXX", Version.MODEL_F523)))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("invalid integer format");

        assertThat(captureThrowable(() -> requestInstantaneousPower("0", Version.MODEL_F523)))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("value must be between 1 and 255");
        assertThat(captureThrowable(() -> requestDailyPower("0", Version.MODEL_F523)))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("value must be between 1 and 255");
        assertThat(captureThrowable(() -> requestMonthlyPower("0", Version.MODEL_F523)))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("value must be between 1 and 255");

        assertThat(captureThrowable(() -> requestInstantaneousPower("256", Version.MODEL_F523)))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("value must be between 1 and 255");
        assertThat(captureThrowable(() -> requestDailyPower("256", Version.MODEL_F523)))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("value must be between 1 and 255");
        assertThat(captureThrowable(() -> requestMonthlyPower("256", Version.MODEL_F523)))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("value must be between 1 and 255");
    }

    @Test
    public void testHandlePowers() {
        Action1 onSuccessMock = mock(Action1.class);
        Action0 onErrorMock = mock(Action0.class);

        List<OpenSession> openSessions = Lists.newArrayList(
            OpenSession.newSession(requestInstantaneousPower("1", Version.MODEL_F523)),
            OpenSession.newSession(requestDailyPower("1", Version.MODEL_F523_A)),
            OpenSession.newSession(requestMonthlyPower("255", Version.MODEL_F520))
        );
        openSessions.get(0).addAllResponse(Lists.newArrayList(() -> "*#18*51*113*888##", () -> OpenMessage.ACK));
        openSessions.get(1).addAllResponse(Lists.newArrayList(() -> "*#18*71#0*54*1234##", () -> OpenMessage.ACK));
        openSessions.get(2).addAllResponse(Lists.newArrayList(() -> "*#18*5255*53*999999##", () -> OpenMessage.ACK));
        Observable.just(openSessions)
            .map(EnergyManagement.handlePowers(onSuccessMock, onErrorMock))
            .subscribe();

        verify(onSuccessMock).call(Lists.newArrayList("888", "1234", "999999"));
        verify(onErrorMock, never()).call();
    }

    @Test
    public void testHandleInvalidPowers() {
        Action1 onSuccessMock = mock(Action1.class);
        Action0 onErrorMock = mock(Action0.class);

        List<OpenSession> openSessions = Lists.newArrayList(
            OpenSession.newSession(requestDailyPower("1", Version.MODEL_F523_A)),
            OpenSession.newSession(requestInstantaneousPower("1", Version.MODEL_F523)),
            OpenSession.newSession(requestMonthlyPower("255", Version.MODEL_F520))
        );
        openSessions.get(1).addAllResponse(Lists.newArrayList(() -> "*#18*51*113*888##", () -> OpenMessage.ACK));
        // invalids
        openSessions.get(0).addAllResponse(Lists.newArrayList(() -> OpenMessage.ACK, () -> "1*54*3897##", () -> OpenMessage.ACK));
        openSessions.get(2).addAllResponse(Lists.newArrayList(() -> OpenMessage.ACK));
        Observable.just(openSessions)
            .map(EnergyManagement.handlePowers(onSuccessMock, onErrorMock))
            .subscribe();

        verify(onSuccessMock).call(Lists.newArrayList("", "888", ""));
        verify(onErrorMock, never()).call();
    }

    @Test
    public void testIsValidPower() {
        assertTrue("should be valid", isValidPower("*#18*51*54*X##"));
        assertTrue("should be valid", isValidPower("*#18*7255#0*113*XXXXXXX##"));
        assertTrue("should be valid", isValidPower("*#18*XXXXXXX##"));

        assertFalse("should be invalid", isValidPower("*#18*51*54*##"));
        assertFalse("should be invalid", isValidPower("*#18*7255#0*113*XXXXXXXX##"));
        assertFalse("should be invalid", isValidPower("*#18*51*54*XXX"));
        assertFalse("should be invalid", isValidPower("*#19*51*54*X##"));
        assertFalse("should be invalid", isValidPower(null));
        assertFalse("should be invalid", isValidPower(""));
    }

    @Test
    public void testGetPower() {
        assertEquals("invalid power", "888",
            getPower(requestInstantaneousPower("1", Version.MODEL_F523), "*#18*51*113*888##"));
        assertEquals("invalid power", "888",
            getPower(requestInstantaneousPower("1", Version.MODEL_F523_A), "*#18*71#0*113*888##"));
    }

    @Test
    public void testGetInvalidPower() {
        // TODO
        assertEquals("invalid power: wrong version", "",
            getPower(requestInstantaneousPower("1", Version.MODEL_F523_A), "*#18*51*113*888##"));
        assertEquals("invalid power: not integer value", "",
            getPower(requestInstantaneousPower("1", Version.MODEL_F523), "*#18*51*113*XXX##"));
        assertEquals("invalid power: missing value", "",
            getPower(requestInstantaneousPower("1", Version.MODEL_F523), "*#18*51*113*##"));
        assertEquals("invalid power: value too long", "",
            getPower(requestInstantaneousPower("1", Version.MODEL_F523), "*#18*51*113*XXXXXXXX##"));
    }

}
