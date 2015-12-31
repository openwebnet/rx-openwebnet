package com.github.niqdev.openwebnet.message;

import com.github.niqdev.openwebnet.domain.Who;

public abstract class BaseOpenMessage implements OpenMessage {

    protected static final String FORMAT_REQUEST = "*%d*%d*%d##";
    protected static final String FORMAT_STATUS = "*#%d*%d##";

    private final Who who;
    private final String value;

    protected BaseOpenMessage(Who who, String value) {
        this.who = who;
        this.value = value;
    }

    protected Who getWho() {
        return who;
    }

    @Override
    public String getValue() {
        return value;
    }
}
