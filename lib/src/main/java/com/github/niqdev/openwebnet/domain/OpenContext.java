package com.github.niqdev.openwebnet.domain;

import lombok.Getter;

import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

/**
 *
 */
public class OpenContext {

    @Getter private final SocketChannel client;
    private final ByteBuffer buffer = ByteBuffer.allocate(1024);

    public OpenContext(SocketChannel client) {
        this.client = client;
    }

    public ByteBuffer getEmptyBuffer() {
        buffer.flip();
        buffer.clear();
        return buffer;
    }
}
