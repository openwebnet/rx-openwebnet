package com.github.niqdev.openwebnet.protocol;

import com.github.niqdev.openwebnet.message.OpenMessage;

import java.util.List;

public class OpenSessionCommand implements OpenSession {

    @Override
    public OpenChannel getChannel() {
        return OpenChannel.COMMAND;
    }

    @Override
    public OpenMessage getRequest() {
        return null;
    }

    @Override
    public List<OpenMessage> getResponse() {
        return null;
    }
}
