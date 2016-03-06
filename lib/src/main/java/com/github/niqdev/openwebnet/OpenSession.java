package com.github.niqdev.openwebnet;

import com.github.niqdev.openwebnet.message.OpenMessage;
import com.google.common.base.MoreObjects;

import java.util.ArrayList;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 *
 */
public class OpenSession {

    private final OpenMessage request;
    private final List<OpenMessage> response = new ArrayList<>();

    private OpenSession(OpenMessage request) {
        checkNotNull(request, "request can't be null");
        this.request = request;
    }

    public static OpenSession newSession(OpenMessage request) {
        return new OpenSession(request);
    }

    public OpenSession addAllResponse(List<OpenMessage> response) {
        checkNotNull(response, "response can't be null");
        this.response.addAll(response);
        return this;
    }

    public OpenSession addResponse(OpenMessage response) {
        checkNotNull(response, "response can't be null");
        this.response.add(response);
        return this;
    }

    public OpenMessage getRequest() {
        return request;
    }

    public List<OpenMessage> getResponse() {
        return response;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
            .add("request", request.getValue())
            .add("response", response)
            .toString();
    }
}
