package com.github.niqdev.openwebnet;

import com.github.niqdev.openwebnet.message.OpenMessage;

import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.requireNonNull;

/**
 *
 */
public class OpenSession {

    private final OpenMessage request;
    private final List<OpenMessage> response = new ArrayList<>();

    private OpenSession(OpenMessage request) {
        requireNonNull(request, "request can't be null");
        this.request = request;
    }

    public static OpenSession newSession(OpenMessage request) {
        return new OpenSession(request);
    }

    public OpenSession addAllResponse(List<OpenMessage> response) {
        requireNonNull(response, "response can't be null");
        this.response.addAll(response);
        return this;
    }

    public OpenSession addResponse(OpenMessage response) {
        requireNonNull(response, "response can't be null");
        this.response.add(response);
        return this;
    }

    public OpenMessage getRequest() {
        return request;
    }

    public List<OpenMessage> getResponse() {
        return response;
    }

}
