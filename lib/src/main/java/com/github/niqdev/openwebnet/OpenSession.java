package com.github.niqdev.openwebnet;

import com.github.niqdev.openwebnet.message.OpenMessage;

import static com.google.common.base.Preconditions.*;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.*;

import static com.github.niqdev.openwebnet.OpenWebNet.OpenGateway;

/**
 *
 */
public class OpenSession {

    private final Channel channel;
    private final OpenMessage request;
    private final List<OpenMessage> response = new ArrayList<>();
    private final ByteBuffer buffer = ByteBuffer.allocate(1024);
    private SocketChannel client;

    private OpenSession(Channel channel, OpenMessage request) {
        requireNonNull(channel, "channel can't be null");
        requireNonNull(request, "request can't be null");
        requireNonNull(request.getValue(), "request value can't be null");

        this.channel = channel;
        this.request = request;
    }

    public Channel getChannel() {
        return channel;
    }

    public static OpenSession newCommand(OpenMessage request) {
        return new OpenSession(Channel.COMMAND, request);
    }

    public static OpenSession newEvent(OpenMessage request) {
        return new OpenSession(Channel.EVENT, request);
    }

    public OpenMessage getRequest() {
        return request;
    }

    public List<OpenMessage> getResponse() {
        return response;
    }

    public void addResponse(OpenMessage message) {
        requireNonNull(message, "message can't be null");
        requireNonNull(message.getValue(), "message value can't be null");

        response.add(message);
    }

    public ByteBuffer getEmptyBuffer() {
        buffer.flip();
        buffer.clear();
        return buffer;
    }

    /*
     * On Android {@link java.nio.channels.AsynchronousSocketChannel}
     * throws java.lang.ClassNotFoundException.
     * So use {@link java.nio.channels.SocketChannel}
     */
    public void connect(OpenGateway gateway) throws IOException {
        requireNonNull(gateway, "gateway can't be null");

        client = SocketChannel.open();
        client.connect(new InetSocketAddress(gateway.getHost(), gateway.getPort()));
    }

    public void disconnect() throws IOException {
        if (client.isConnected()) {
            client.close();
        }
    }

    public SocketChannel getClient() {
        requireNonNull(client, "client is null");
        checkArgument(client.isConnected(), "client is not connected");

        return client;
    }

    /**
     *
     */
    public enum Channel {

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
