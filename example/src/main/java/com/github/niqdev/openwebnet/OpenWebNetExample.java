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

        OpenWebNetObservable.rawCommand(CONFIG, "*1*1*21##")
            .subscribe(
                openFrames -> { openFrames.stream().forEach(frame -> { log.debug("FRAME {}", frame); }); },
                throwable -> { log.error("ERROR {}", throwable); },
                () -> { log.debug("COMPLETE"); }
            );

        log.debug("OpenWebNetExample: " + Thread.currentThread().getName());
        //TimeUnit.SECONDS.sleep(1);
    }

}
