package com.github.niqdev.openwebnet.protocol;

import com.github.niqdev.openwebnet.message.OpenMessage;

import java.util.List;

public interface OpenSession {

    /**
     *
     */
    enum OpenChannel {

        COMMAND("*99*0##"),
        EVENT("*99*1##");

        private final String value;

        OpenChannel(String value) {
            this.value = value;
        }

        public String value() {
            return value;
        }

    }

    OpenChannel getChannel();

    OpenMessage getRequest();

    List<OpenMessage> getResponse();
    
}
