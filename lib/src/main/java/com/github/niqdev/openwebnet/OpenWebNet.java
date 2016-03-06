package com.github.niqdev.openwebnet;

import com.github.niqdev.openwebnet.message.OpenMessage;
import rx.Observable;

import java.util.List;

import static com.github.niqdev.openwebnet.OpenWebNetObservable.*;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Reactive OpenWebNet client.
 *
 * @author niqdev
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
        checkNotNull(request, "request can't be null");
        return connect(gateway)
            .flatMap(doHandshake(Channel.COMMAND))
            .flatMap(doRequest(request));
    }

    public Observable<List<OpenSession>> send(List<OpenMessage> requests) {
        checkNotNull(requests, "requests can't be null");
        return connect(gateway)
            .flatMap(doHandshake(Channel.COMMAND))
            .flatMap(doRequests(requests));
    }

    public Observable<List<OpenSession>> listen(OpenMessage... requests) {
        //Channel.EVENT
        throw new UnsupportedOperationException("not implemented yet");
    }

    /**
     *
     */
    public interface OpenGateway {

        int DEFAULT_PORT = 20000;

        String getHost();

        int getPort();
    }

    public static OpenGateway gateway(final String host, final int port) {
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

    /**
     *
     */
    enum Channel {

        COMMAND("*99*0##"),
        EVENT("*99*1##");

        private final String value;

        Channel(String value) {
            this.value = value;
        }

        public String value() {
            return value;
        }

    }

}
