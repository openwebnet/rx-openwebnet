package com.github.niqdev.openwebnet.rx;

import com.github.niqdev.openwebnet.domain.OpenFrame;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.Lists;
import org.junit.Test;
import rx.Observable;
import rx.Subscriber;
import rx.observers.TestSubscriber;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * RUN gradle :lib:test --debug
 */
public class OpenWebNetObservableTest {

    private static class PrintSubscriber extends Subscriber<Object> {
        private final String name;

        public PrintSubscriber(String name) {
            this.name = name;
        }

        @Override
        public void onCompleted() {
            System.out.println(name + ": Completed");
        }

        @Override
        public void onError(Throwable e) {
            System.out.println(name + ": Error: " + e);
        }

        @Override
        public void onNext(Object v) {
            System.out.println(name + ": " + v);
        }
    }

    @Test
    public void testParseFrames() {
        TestSubscriber<List<OpenFrame>> tester = new TestSubscriber<>();

        Observable.just("*1*0*21##*#*1##")
                .map(OpenWebNetObservable.parseFrames())
                //.subscribe(new PrintSubscriber("parseFrames"))
                .subscribe(tester);

        // TODO newArrayList
        List<OpenFrame> openFrames = Lists.newArrayList(new OpenFrame("*1*0*21##"), new OpenFrame("*#*1##"));

        // guava RegularImmutableList
        tester.assertValue(FluentIterable.from(openFrames).toList());

        //[[[*1*0*21##], [*#*1##]]]
        //[[[*1*0*21##], [*#*1##]]]
    }
}
