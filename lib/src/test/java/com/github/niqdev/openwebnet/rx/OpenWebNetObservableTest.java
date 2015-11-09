package com.github.niqdev.openwebnet.rx;

import com.github.niqdev.openwebnet.domain.OpenFrame;
import com.google.common.collect.Lists;
import org.junit.Ignore;
import org.junit.Test;
import rx.Observable;
import rx.observers.TestSubscriber;

import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * RUN
 * gradle :lib:test --debug
 */
//@RunWith(PowerMockRunner.class)
//@PrepareForTest(OpenWebNetObservable.class)
public class OpenWebNetObservableTest {

    @Test @Ignore
    public void testLogDebug() {
        throw new UnsupportedOperationException("not implemented yet");
    }

    @Test @Ignore
    public void testRawCommand() {
        throw new UnsupportedOperationException("not implemented yet");
    }

    @Test @Ignore
    public void testSendFrame() {
        throw new UnsupportedOperationException("not implemented yet");
    }

    @Test @Ignore
    public void testConnect() {
        throw new UnsupportedOperationException("not implemented yet");
    }

    @Test @Ignore
    public void testHandshake() {
        throw new UnsupportedOperationException("not implemented yet");
    }

    @Test @Ignore
    public void testSend() {
        throw new UnsupportedOperationException("not implemented yet");
    }

    @Test @Ignore
    public void testRead() {
        throw new UnsupportedOperationException("not implemented yet");
    }

    @Test @Ignore
    public void testExpectedAck() {
        throw new UnsupportedOperationException("not implemented yet");
    }

    @Test @Ignore
    public void testWrite() {
        throw new UnsupportedOperationException("not implemented yet");
    }

    @Test
    public void testParseFrames() {
        TestSubscriber<List<OpenFrame>> tester = new TestSubscriber<>();

        Observable.just("*1*0*21##*#*1##")
            .flatMap(OpenWebNetObservable.parseFrames())
            .subscribe(tester);

        List<OpenFrame> expectedOpenFrames =
            Lists.newArrayList(new OpenFrame("*1*0*21##"), new OpenFrame("*#*1##"));

        tester.assertCompleted();

        assertEquals("invalid frame", expectedOpenFrames.get(0).getValue(), "*1*0*21##");
        assertEquals("invalid frame", expectedOpenFrames.get(1).getValue(), "*#*1##");
    }
}
