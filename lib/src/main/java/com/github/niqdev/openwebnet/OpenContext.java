package com.github.niqdev.openwebnet;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

import static com.github.niqdev.openwebnet.OpenWebNet.OpenGateway;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * OpenWebNet context helper class.
 */
class OpenContext {

    private final OpenGateway gateway;
    private SocketChannel client;
    private final ByteBuffer buffer = ByteBuffer.allocate(1024);

    private OpenContext(OpenGateway gateway) {
        checkNotNull(gateway, "gateway can't be null");
        this.gateway = gateway;
    }

    public static OpenContext setup(OpenGateway gateway) {
        return new OpenContext(gateway);
    }

    /*
     * On Android {@link java.nio.channels.AsynchronousSocketChannel}
     * throws java.lang.ClassNotFoundException.
     * So use {@link java.nio.channels.SocketChannel}
     */
    public void connect() throws IOException {
        client = SocketChannel.open();
        client.connect(new InetSocketAddress(gateway.getHost(), gateway.getPort()));
    }

    public void disconnect() throws IOException {
        if (client.isConnected()) {
            client.close();
        }
    }

    public SocketChannel getClient() {
        checkNotNull(client, "client can't be null");
        checkArgument(client.isConnected(), "client is not connected");
        return client;
    }

    public ByteBuffer getEmptyBuffer() {
        buffer.flip();
        buffer.clear();
        return buffer;
    }

}
