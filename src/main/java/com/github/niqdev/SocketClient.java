package com.github.niqdev;

import java.io.IOException;

/**
 * @author niqdev
 */
public interface SocketClient<I, O> {

    String LOCALHOST = "localhost";

    O send(I input) throws IOException;
}
