package com.github.niqdev;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;

/**
 * Created by niqdev on 25/10/15.
 */
public class OpenWebNetClient extends AbstractSocketClient<String, String> {

    public OpenWebNetClient(String host, int port) {
        super(host, port);
    }

    @Override
    protected String handleData(Reader reader, Writer writer, String input) throws IOException {
        String response = "";
        handshake(reader, writer);
        write(writer, input);
        response = read(reader);
        return response;
    }

    private void handshake(Reader reader, Writer writer) throws IOException {
        String ack1 = read(reader);
        System.out.println(ack1);
        write(writer, "*99*0##");
        String ack2 = read(reader);
        System.out.println(ack2);
    }

    private String read(Reader reader) throws IOException {
        char[] inputChar = new char[1];
        StringBuilder value = new StringBuilder();
        while (reader.read(inputChar, 0, 1) != -1) {
            value.append(inputChar);
        }
        return value.toString();
    }

    private void write(Writer writer, String value) throws IOException {
        writer.write(value);
        writer.flush();
    }

}
