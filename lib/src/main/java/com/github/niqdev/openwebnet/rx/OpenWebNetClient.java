package com.github.niqdev.openwebnet.rx;

import com.github.niqdev.openwebnet.domain.OpenSession;
import rx.Observable;

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
public class OpenWebNetClient implements OpenWebNet {

    private final String host;
    private final int port;

    private OpenWebNetClient(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public OpenWebNetClient newClient(String host, int port) {
        return new OpenWebNetClient(host, port);
    }

    @Override
    public Observable<OpenSession> writeCommand(Integer who, Integer what, Integer where) {
        //Preconditions.checkArgument();
        throw new UnsupportedOperationException("not implemented yet");
    }

    @Override
    public Observable<OpenSession> requestStatus(Integer who, Integer where) {
        throw new UnsupportedOperationException("not implemented yet");
    }

    @Override
    public Observable<OpenSession> requestDimension(Integer who, Integer where, Integer dimension) {
        throw new UnsupportedOperationException("not implemented yet");
    }

    @Override
    public Observable<OpenSession> writeDimension(Integer who, Integer where, Integer dimension, Integer... value) {
        throw new UnsupportedOperationException("not implemented yet");
    }
}
