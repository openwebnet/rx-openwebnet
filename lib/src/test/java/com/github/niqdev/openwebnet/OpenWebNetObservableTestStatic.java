package com.github.niqdev.openwebnet;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import rx.Observable;
import rx.observers.TestSubscriber;

import static org.mockito.Mockito.*;

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
    public void testDoHandshake() throws Exception {
        // not working as expected: stub static method are not called

        TestSubscriber<OpenContext> tester = new TestSubscriber<>();
        final OpenContext mockedContext = mock(OpenContext.class);

        PowerMockito.when(OpenWebNetObservable.doHandshake(OpenWebNet.Channel.COMMAND)).thenCallRealMethod();
        PowerMockito.when(OpenWebNetObservable.read()).thenReturn(openContext -> {
            System.out.println("read");
            return Observable.just("READ-MOCK");
        });
        PowerMockito.when(OpenWebNetObservable.expectedAck(mockedContext)).then(invocation -> {
            System.out.println("expectedAck");
            return Observable.just(mockedContext);
        });
        PowerMockito.when(OpenWebNetObservable.class, "write", "FRAME").then(invocation -> {
            System.out.println("write");
            return Observable.just(mockedContext);
        });

        Observable.just(mockedContext)
            .flatMap(OpenWebNetObservable.doHandshake(OpenWebNet.Channel.COMMAND))
            .toBlocking()
            .subscribe(tester);

        //tester.assertValue(mockedContext);
        //tester.assertCompleted();
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

}
