package com.github.niqdev.openwebnet.domain;

import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;

/**
 *
 */
public class OpenContext {

    private final AsynchronousSocketChannel client;
    private final ByteBuffer buffer = ByteBuffer.allocate(1024);

    public OpenContext(AsynchronousSocketChannel client) {
        this.client = client;
    }

    public AsynchronousSocketChannel getClient() {
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
