package com.github.niqdev.openwebnet;

import com.github.niqdev.openwebnet.message.OpenMessage;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class OpenSession {

    private final Channel channel;
    private final OpenMessage request;
    private List<OpenMessage> response = new ArrayList<>();

    private OpenSession(Channel channel, OpenMessage request) {
        this.channel = channel;
        this.request = request;
    }

    public Channel getChannel() {
        return channel;
    }

    public static OpenSession newCommand(OpenMessage request) {
        return new OpenSession(Channel.COMMAND, request);
    }

    public OpenMessage getRequest() {
        return request;
    }

    public List<OpenMessage> getResponse() {
        return response;
    }

    public void addResponse(OpenMessage message) {
        response.add(message);
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
