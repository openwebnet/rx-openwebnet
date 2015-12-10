package com.github.niqdev.openwebnet;

import rx.schedulers.Schedulers;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.github.niqdev.openwebnet.OpenWebNet.defaultGateway;
import static com.github.niqdev.openwebnet.OpenWebNet.gateway;
import static java.util.Arrays.asList;

/**
 *
 */
public class OpenWebNetExample {

    private static final String LOCALHOST = "localhost";
    private static final String LOCALHOST_ANDROID = "10.0.2.2";
    private static final String HOST = "192.168.1.41";
    private static final int PORT = 20000;

    private static final ExecutorService executor = Executors.newSingleThreadExecutor();

    public static void main(String[] args) {
        //example1();
        example2();
    }

    private static void example1() {
        OpenWebNet
            .newClient(defaultGateway(LOCALHOST))
            .send(() -> "*#1*21##")
            .subscribe(System.out::println);
    }

    private static void example2() {
        System.out.println("before " + Thread.currentThread().getName());
        OpenWebNet
            .newClient(gateway(LOCALHOST, PORT))
            .send(asList(() -> "*#1*21##", () -> "*#1*22##"))
            .subscribeOn(Schedulers.from(executor))
            .doOnError(throwable -> System.out.println("ERROR " + throwable))
            .finallyDo(() -> executor.shutdown())
            .subscribe(System.out::println, throwable -> {});
        System.out.println("after " + Thread.currentThread().getName());
    }

}
