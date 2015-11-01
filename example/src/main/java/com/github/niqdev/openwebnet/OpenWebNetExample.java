package com.github.niqdev.openwebnet;

import com.github.niqdev.openwebnet.domain.OpenConfig;
import com.github.niqdev.openwebnet.rx.OpenWebNetObservable;
import com.google.common.base.Splitter;
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
    private static final OpenConfig CONFIG = new OpenConfig(LOCALHOST, PORT);

    public static void main(String[] args) {

        // request command
        // *1*1*21##
        // response command
        // *#*1##

        // request status
        // *#1*21##
        // response status
        // *1*0*21##*#*1##

        /*
        Iterable<String> frames = Splitter.on("##")
                .trimResults()
                .omitEmptyStrings()
                .split("*1*0*21##*#*1##");
        for (String frame: frames) {
            System.out.println(frame);
        }
        */

        OpenWebNetObservable.rawCommand(CONFIG, "*1*1*21##")
            .observeOn(Schedulers.io())
            .subscribe(openFrame -> {
                log.debug(openFrame.val());
            });

        // TODO non-blocking (async)
        log.debug("OpenWebNetExample: " + Thread.currentThread().getName());
        //TimeUnit.SECONDS.sleep(1);
    }

}
