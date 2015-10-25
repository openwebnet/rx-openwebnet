package com.github.niqdev;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * @author niqdev
 */
public abstract class AbstractSocketClient<I, O> implements SocketClient<I, O> {

    private static final Logger log = LoggerFactory.getLogger(AbstractSocketClient.class);

    public static final String LOCALHOST = "localhost";
    private static final int TIMEOUT = 10*1000; // 10 seconds

    private final String host;
    private final int port;
    private final int timeout;

    public AbstractSocketClient(String host, int port) {
        this.host = host;
        this.port = port;
        this.timeout = TIMEOUT;
    }

    protected abstract O handleData(Reader reader, Writer writer, I input) throws IOException;

    @Override
    public O send(I input) throws IOException {
        try (Socket socket = initSocket();
             BufferedReader reader = initReader(socket);
             PrintWriter writer = initWriter(socket)) {

            return handleData(reader, writer, input);

        } catch (IOException e) {
            log.error("client connection error", e);
            throw e;
        }
    }

    private Socket initSocket() throws IOException {
        Socket socket = new Socket();
        socket.connect(new InetSocketAddress(getHost(), getPort()), getTimeout());
        return socket;
    }

    private BufferedReader initReader(Socket socket) throws IOException {
        return new BufferedReader(new InputStreamReader(socket.getInputStream()));
    }

    private PrintWriter initWriter(Socket socket) throws IOException {
        return new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true);
    }

    protected String getHost() {
        return host;
    }
    protected int getPort() {
        return port;
    }
    protected int getTimeout() {
        return timeout;
    }

}
