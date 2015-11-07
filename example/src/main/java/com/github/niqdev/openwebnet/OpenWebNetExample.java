package com.github.niqdev.openwebnet;

import com.github.niqdev.openwebnet.rx.OpenWebNetObservable;

import static com.github.niqdev.openwebnet.rx.OpenWebNetObservable.logDebug;

/**
 *
 */
public class OpenWebNetExample {

    private static final String LOCALHOST = "localhost";
    private static final String LOCALHOST_ANDROID = "10.0.2.2";
    private static final String HOST = "192.168.1.41";
    private static final int PORT = 20000;

    public static void main(String[] args) {
        logDebug("BEFORE-main");
        runDemo();
        logDebug("BEFORE-after");
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
        logDebug("BEFORE-demo");

        OpenWebNetObservable
            .rawCommand(LOCALHOST, PORT, "*1*1*21##")
            .subscribe(openFrames -> {
                openFrames.stream().forEach(frame -> {
                    logDebug("FRAME | " + frame);
                });
            }, throwable -> {
                logDebug("ERROR-subscribe " + throwable);
            });

        logDebug("AFTER-demo");
    }

}
