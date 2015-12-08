package com.github.niqdev.openwebnet.message;

import com.github.niqdev.openwebnet.domain.Who;

// TODO
public abstract class BaseOpenMessage implements OpenMessage {

    private final Who who;

    protected BaseOpenMessage(Who who) {
        this.who = who;
    }
}
