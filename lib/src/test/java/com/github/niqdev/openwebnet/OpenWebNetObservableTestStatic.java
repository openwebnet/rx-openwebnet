package com.github.niqdev.openwebnet;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import rx.Observable;
import rx.functions.Func1;
import rx.observers.TestSubscriber;

import static com.github.niqdev.openwebnet.OpenWebNet.Channel.COMMAND;
import static org.mockito.Mockito.mock;

/**
 * RUN
 * gradle :lib:test --debug
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest(OpenWebNetObservable.class)
public class OpenWebNetObservableTestStatic {

    @Before
    public void setup() {
        PowerMockito.mockStatic(OpenWebNetObservable.class);
    }

    @Test
    @Ignore
    public void testConnect() {
        throw new UnsupportedOperationException("not implemented yet");
    }

    @Test
    public void testDoHandshakeVersion1() throws Exception {
        // not working as expected: stub static method are not called

        TestSubscriber<OpenContext> tester = new TestSubscriber<>();
        final OpenContext mockedContext = mock(OpenContext.class);

        PowerMockito.when(OpenWebNetObservable.doHandshake(COMMAND)).thenCallRealMethod();
        PowerMockito.when(OpenWebNetObservable.read()).thenReturn(openContext -> {
            System.out.println("read");
            return Observable.just("READ");
        });
        PowerMockito.when(OpenWebNetObservable.expectedAck(mockedContext)).then(invocation -> {
            System.out.println("expectedAck");
            return Observable.just(mockedContext);
        });
        PowerMockito.when(OpenWebNetObservable.class, "write", "WRITE").then(invocation -> {
            System.out.println("write");
            return Observable.just(mockedContext);
        });

        Observable.just(mockedContext)
            .flatMap(OpenWebNetObservable.doHandshake(COMMAND))
            .toBlocking()
            .subscribe(tester);

        // error
        //tester.assertValue(mockedContext);
        //tester.assertCompleted();
    }

    @Test
    public void testDoHandshakeVersion2() throws Exception {
        // not working as expected

        final OpenContext mockedContext = mock(OpenContext.class);

        PowerMockito.when(OpenWebNetObservable.doHandshake(COMMAND))
            .thenCallRealMethod();
        PowerMockito.when(OpenWebNetObservable.read())
            .thenReturn(readMock());
        PowerMockito.when(OpenWebNetObservable.expectedAck(mockedContext))
            .thenReturn(expectedAckMock(mockedContext));
        PowerMockito.when(OpenWebNetObservable.class, "write", "MESSAGE")
            .thenReturn(writeMock("WRITE"));

        OpenWebNetObservable.doHandshake(COMMAND);

        // expects to test invocation order:
        // read, expectedAck, write, read, expectedAck
    }

    @Test
    @Ignore
    public void testDoRequests() {
        throw new UnsupportedOperationException("not implemented yet");
    }

    @Test
    @Ignore
    public void testDoRequest() {
        throw new UnsupportedOperationException("not implemented yet");
    }

    private static Func1<OpenContext, Observable<String>> readMock() {
        return context -> {
            System.out.println("read");
            return Observable.just("READ");
        };
    }

    private static Func1<String, Observable<OpenContext>> expectedAckMock(OpenContext context) {
        return value -> {
            System.out.println("expectedAck");
            return Observable.just(context);
        };
    }

    private static Func1<OpenContext, Observable<OpenContext>> writeMock(String value) {
        return context -> {
            System.out.println("write");
            return Observable.just(context);
        };
    }

}
