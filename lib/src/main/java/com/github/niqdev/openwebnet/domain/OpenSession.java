package com.github.niqdev.openwebnet.domain;

import java.util.List;

public class OpenSession {

    private final OpenFrame request;
    private List<OpenFrame> response;

    public OpenSession(OpenFrame request) {
        this.request = request;
    }

    public OpenFrame getRequest() {
        return request;
    }

    public List<OpenFrame> getResponse() {
        return response;
    }

    public void setResponse(List<OpenFrame> response) {
        this.response = response;
    }
}
