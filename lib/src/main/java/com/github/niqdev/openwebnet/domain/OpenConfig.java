package com.github.niqdev.openwebnet.domain;

import lombok.Getter;

/**
 *
 */
public class OpenConfig {

    @Getter private final String host;
    @Getter private final int port;

    public OpenConfig(String host, int port) {
        this.host = host;
        this.port = port;
    }
}
