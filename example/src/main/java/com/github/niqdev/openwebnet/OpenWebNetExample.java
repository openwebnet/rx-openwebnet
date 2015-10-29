package com.github.niqdev.openwebnet;

import com.github.niqdev.openwebnet.domain.OpenConfig;
import com.github.niqdev.openwebnet.rx.OpenWebNetObservable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rx.schedulers.Schedulers;

/**
 *
 */
public class OpenWebNetExample {

    private static final Logger log = LoggerFactory.getLogger(OpenWebNetExample.class);

    private static final String LOCALHOST = "localhost";
    private static final String HOST = "192.168.1.41";
    private static final int PORT = 20000;

    public static void main(String[] args) {

        OpenWebNetObservable
                .send(config(), () -> {
                    return "*1*1*21##";
                })
                //.observeOn(Schedulers.io())
                .subscribe(openFrame -> {
                    System.out.println(openFrame.value());
                });
        // TODO non-blocking
        log.debug("after: " + Thread.currentThread().getName());
    }

    // TODO refactor
    private static OpenConfig config() {
        return new OpenConfig() {
            @Override
            public String host() {
                return LOCALHOST;
            }

            @Override
            public int port() {
                return PORT;
            }
        };
    }

}
