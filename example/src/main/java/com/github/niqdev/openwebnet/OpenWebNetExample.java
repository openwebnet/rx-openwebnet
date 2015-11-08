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
        turn-on *1*1*21##
        turn-off *1*0*21##
        response command
        *#*1##

        request status
        is-on *#1*21##
        response status
        (off) *1*0*21##*#*1##
    */

    //sudo route -n add -host 192.168.1.41 192.168.1.89
    //netstat -rn
    //echo *1*1*21## | nc 192.168.1.41 20000
    //while true; do ((echo "ACK";) | nc -l 20000) done
    private static void runDemo() {
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

}
