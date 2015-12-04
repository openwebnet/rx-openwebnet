package com.github.niqdev.openwebnet;

import static com.github.niqdev.openwebnet.OpenWebNet.defaultGateway;

/**
 *
 */
public class OpenWebNetExample {

    public static void main(String[] args) {
        OpenWebNet
            .newClient(defaultGateway("192.168.0.1"))
            .send(() -> "")
            .subscribe(openSession -> openSession
                .getResponse().stream().forEach(System.out::println));
    }

    /*
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
    */

}
