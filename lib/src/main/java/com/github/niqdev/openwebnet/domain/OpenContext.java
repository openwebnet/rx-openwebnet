package com.github.niqdev.openwebnet.domain;

import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

/**
 *
 */
public class OpenContext {

    private final SocketChannel client;
    private final ByteBuffer buffer = ByteBuffer.allocate(1024);

    public OpenContext(SocketChannel client) {
        this.client = client;
    }

    public SocketChannel getClient() {
        return client;
    }

    public ByteBuffer getBuffer() {
        return buffer;
    }

    public ByteBuffer getEmptyBuffer() {
        buffer.flip();
        buffer.clear();
        return buffer;
    }
}
