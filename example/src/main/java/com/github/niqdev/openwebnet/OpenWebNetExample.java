package com.github.niqdev.openwebnet;

import com.github.niqdev.openwebnet.domain.OpenConfig;
import com.github.niqdev.openwebnet.rx.OpenWebNetObservable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

        //"*1*1*21##"

        OpenWebNetObservable.rawCommand(CONFIG, args[0])
            .subscribe(openFrame -> {
                System.out.println(openFrame.val());
            });

        // TODO non-blocking (async)
        log.debug("OpenWebNetExample: " + Thread.currentThread().getName());
        //TimeUnit.SECONDS.sleep(1);
    }

}
