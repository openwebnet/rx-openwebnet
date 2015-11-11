package com.github.niqdev.openwebnet;

import com.github.niqdev.openwebnet.rx.OpenWebNetObservable;
import com.github.niqdev.openwebnet.rx.OpenWebNetUtils;

import static com.github.niqdev.openwebnet.rx.OpenWebNetObservable.logDebug;
import static com.github.niqdev.openwebnet.rx.OpenWebNetUtils.LOCALHOST;
import static com.github.niqdev.openwebnet.rx.OpenWebNetUtils.PORT;

/**
 *
 */
public class OpenWebNetExample {

    public static void main(String[] args) {
        logDebug("BEFORE-main");
        //runExample();
        runExampleAsync();
        logDebug("BEFORE-after");
    }

    private static void runExample() {
        logDebug("BEFORE-demo");

        OpenWebNetObservable.rawCommand(LOCALHOST, PORT, "*#1*21##")
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
