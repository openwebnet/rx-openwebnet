package com.github.niqdev.openwebnet;

import com.github.niqdev.openwebnet.domain.OpenConfig;
import com.github.niqdev.openwebnet.rx.OpenWebNetObservable;
import rx.Observable;
import rx.schedulers.Schedulers;

/**
 *
 */
public class OpenWebNetExample {

    /**
     * ISSUE: org.slf4j.Logger is blocking (sync)!
     */
    //private static final Logger log = LoggerFactory.getLogger(OpenWebNetExample.class);

    private static final String LOCALHOST = "localhost";
    private static final String LOCALHOST_ANDROID = "10.0.2.2";
    private static final String HOST = "192.168.1.41";
    private static final int PORT = 20000;

    public static void main(String[] args) {
        System.out.println("MAIN-before: " + Thread.currentThread().getName());
        //exampleFlowAsync();
        OpenWebNetObservable.exampleFlowAsyncClass()
                .subscribe(s -> {
                    System.out.println("RESULT " + Thread.currentThread().getName() + "|" + s);
                }, throwable -> {
                    System.out.println("ERROR " + Thread.currentThread().getName() + "|" + throwable);
                });
        //runDemo();
        System.out.println("MAIN-after: " + Thread.currentThread().getName());
    }

    /*
        MAIN-before: main
        sub-before: main
        sub-after: main
        MAIN-after: main
        CREATE RxNewThreadScheduler-1
        HANDSHAKE RxNewThreadScheduler-1|#1
        SEND RxNewThreadScheduler-1|#2
    */
    private static void exampleFlowAsync() {
        System.out.println("sub-before: " + Thread.currentThread().getName());

        Observable.OnSubscribe<String> onSubscribe = subscriber -> {
            System.out.println("CREATE " + Thread.currentThread().getName());

            subscriber.onNext("#1");
            //subscriber.onError(new Exception("ERROR"));
            subscriber.onCompleted();
            //subscriber.unsubscribe();
        };

        Observable.create(onSubscribe)
            .subscribeOn(Schedulers.newThread())
            .flatMap(s -> {
                System.out.println("HANDSHAKE " + Thread.currentThread().getName() + "|" + s);
                return newStep("#2");
            })
            .flatMap(s -> {
                System.out.println("SEND " + Thread.currentThread().getName() + "|" + s);
                return newStep("#3");
            })
            .doOnError(throwable -> {
                System.out.println("ERROR " + Thread.currentThread().getName() + "|" + throwable);
            })
            .subscribe(s -> {
                System.out.println("RESULT " + Thread.currentThread().getName() + "|" + s);
            });

        System.out.println("sub-after: " + Thread.currentThread().getName());
    }

    private static Observable<String> newStep(String value) {
        return Observable.just(value);
    }

    /*
        request command
        *1*1*21##
        response command
        *#*1##

        request status
        *#1*21##
        response status
        *1*0*21##*#*1##
    */
    private static void runDemo() {
        System.out.println("BEFORE: " + Thread.currentThread().getName());
        OpenWebNetObservable
            .rawCommand(LOCALHOST, PORT, "*1*1*21##")
            .subscribeOn(Schedulers.newThread())
            .doOnError(throwable -> {
                System.out.println("ERROR " + Thread.currentThread().getName() + "|" + throwable);
            })
            .subscribe(openFrames -> {
                openFrames.stream().forEach(frame -> {
                    System.out.println("FRAME " + Thread.currentThread().getName() + "|" + frame);
                });
            });
        System.out.println("AFTER: " + Thread.currentThread().getName());
    }

}
