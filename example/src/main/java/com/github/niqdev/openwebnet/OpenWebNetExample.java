package com.github.niqdev.openwebnet;

import com.github.niqdev.openwebnet.rx.OpenWebNetObservable;
import com.github.niqdev.openwebnet.rx.OpenWebNetUtils;

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
        //runExample();
        runExampleAsync();
        logDebug("BEFORE-after");
    }

    private static void runExample() {
        logDebug("BEFORE-demo");

        OpenWebNetObservable
            .rawCommand(LOCALHOST, PORT, "*#1*21##")
            .subscribe(openFrames -> {
                openFrames.stream().forEach(frame -> {
                    logDebug("FRAME: " + frame.getValue());
                });
            }, throwable -> {
                logDebug("ERROR-subscribe " + throwable);
            });

        logDebug("AFTER-demo");
    }

    private static void runExampleAsync() {
        logDebug("BEFORE-demo");

        OpenWebNetUtils.rawCommandAsync(LOCALHOST, PORT, "*#1*21##")
            .subscribe(openFrames -> {
                openFrames.stream().forEach(frame -> {
                    logDebug("FRAME: " + frame.getValue());
                });
            }, throwable -> {
                logDebug("ERROR-subscribe " + throwable);
            });

        logDebug("AFTER-demo");
    }

}
