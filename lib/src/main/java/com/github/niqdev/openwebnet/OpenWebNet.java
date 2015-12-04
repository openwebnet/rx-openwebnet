package com.github.niqdev.openwebnet;

import com.github.niqdev.openwebnet.message.OpenMessage;
import com.github.niqdev.openwebnet.protocol.OpenSession;
import rx.Observable;

import java.util.List;

/*
 *  frame = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, *, #}
 *  starts with '*'
 *  ends with '##'
 *  separator among the tags '*'
 *  *tag1*tag2*tag3*...*tagN##
 *
 *  tag = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, #}
 *  tag can't have the couple '##'
 */
public class OpenWebNet {

    private final OpenGateway gateway;

    private OpenWebNet(OpenGateway gateway) {
        this.gateway = gateway;
    }

    public static OpenWebNet newClient(OpenGateway gateway) {
        return new OpenWebNet(gateway);
    }

    public Observable<OpenSession> send(OpenMessage request) {
        throw new UnsupportedOperationException("not implemented yet");
    }

    public Observable<List<OpenSession>> send(List<OpenMessage> request) {
        throw new UnsupportedOperationException("not implemented yet");
    }

    public void listen() {
        throw new UnsupportedOperationException("not implemented yet");
    }

    /**
     * TODO documentation
     */
    public interface OpenGateway {

        int DEFAULT_PORT = 2000;

        /**
         *
         */
        String getHost();

        /**
         *
         */
        int getPort();
    }

    public static OpenGateway gateway(String host, int port) {
        // TODO validate ip
        // TODO validate port
        return new OpenGateway() {

            @Override
            public String getHost() {
                return host;
            }

            @Override
            public int getPort() {
                return port;
            }
        };
    }

    public static OpenGateway defaultGateway(String host) {
        return gateway(host, OpenGateway.DEFAULT_PORT);
    }

}
