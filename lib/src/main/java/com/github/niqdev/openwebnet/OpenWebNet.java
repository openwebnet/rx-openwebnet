package com.github.niqdev.openwebnet;

import com.github.niqdev.openwebnet.message.OpenMessage;
import rx.Observable;

import java.util.List;

import static com.github.niqdev.openwebnet.OpenWebNetObservable.*;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * OpenWebNet client.
 *
 * @author niqdev
 */
public class OpenWebNet {

    private final OpenGateway gateway;

    private OpenWebNet(OpenGateway gateway) {
        this.gateway = gateway;
    }

    /**
     * Helper method to create a new client.
     *
     * @param gateway The gateway
     * @return client
     */
    public static OpenWebNet newClient(OpenGateway gateway) {
        return new OpenWebNet(gateway);
    }

    /**
     * Open a connection and send a {@link Channel#COMMAND} message.
     *
     * @param request The message to be sent
     * @return {@code Observable<OpenSession>}
     */
    public Observable<OpenSession> send(OpenMessage request) {
        checkNotNull(request, "request can't be null");
        return connect(gateway)
            .flatMap(doHandshake(Channel.COMMAND))
            .flatMap(doRequest(request));
    }

    /**
     * Open a connection and send a list of {@link Channel#COMMAND} messages.
     *
     * @param requests The messages to be sent
     * @return {@code Observable<OpenSession>}
     */
    public Observable<List<OpenSession>> send(List<OpenMessage> requests) {
        checkNotNull(requests, "requests can't be null");
        return connect(gateway)
            .flatMap(doHandshake(Channel.COMMAND))
            .flatMap(doRequests(requests));
    }

    /**
     * Not implemented yet.
     *
     * @throws UnsupportedOperationException
     */
    public Observable<List<OpenSession>> listen(OpenMessage... requests) {
        //Channel.EVENT
        throw new UnsupportedOperationException("not implemented yet");
    }

    /**
     * OpenWebNet gateway.
     */
    public interface OpenGateway {

        /**
         * Default gateway port is 20000.
         */
        int DEFAULT_PORT = 20000;

        /**
         * Returns the gateway ip address.
         *
         * @return host
         */
        String getHost();

        /**
         * Returns the gateway port.
         *
         * @return port
         */
        int getPort();
    }

    /**
     * Helper method to create a new gateway.
     *
     * @param host Gateway ip address
     * @param port Gateway port
     * @return gateway
     */
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

    /**
     * Helper method to create a new gateway on port {@link OpenGateway#DEFAULT_PORT}.
     *
     * @param host Gateway ip address
     * @return gateway
     */
    public static OpenGateway defaultGateway(String host) {
        return gateway(host, OpenGateway.DEFAULT_PORT);
    }

    /**
     * OpenWebNet channel types.
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
