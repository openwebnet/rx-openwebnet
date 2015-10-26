package com.github.niqdev;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

import static com.github.niqdev.SocketClient.LOCALHOST;

/**
 * @author niqdev
 */
public class SimpleClient {

    private static final Logger log = LoggerFactory.getLogger(SimpleClient.class);

    public static void main(String[] args) {
        log.debug("send: {0}", args[0]);

        SocketClient client = new OpenWebNetClient(LOCALHOST, 20000);
        try {
            client.send(args[0]);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
