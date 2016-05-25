package com.github.niqdev.openwebnet;

import com.github.niqdev.openwebnet.message.OpenMessage;
import com.google.common.base.MoreObjects;

import java.util.ArrayList;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * OpenWebNet session has both request and response messages.
 *
 */
public class OpenSession {

    private final OpenMessage request;
    private final List<OpenMessage> response = new ArrayList<>();

    private OpenSession(OpenMessage request) {
        checkNotNull(request, "request can't be null");
        this.request = request;
    }

    /**
     * Helper method to create a new session.
     *
     * @param request
     * @return session
     */
    public static OpenSession newSession(OpenMessage request) {
        return new OpenSession(request);
    }

    /**
     * Update the response messages.
     *
     * @param response
     * @return session
     */
    public OpenSession addAllResponse(List<OpenMessage> response) {
        checkNotNull(response, "response can't be null");
        this.response.addAll(response);
        return this;
    }

    /**
     * Update the response messages.
     *
     * @param response
     * @return session
     */
    public OpenSession addResponse(OpenMessage response) {
        checkNotNull(response, "response can't be null");
        this.response.add(response);
        return this;
    }

    /**
     * Returns the initial request message.
     *
     * @return request
     */
    public OpenMessage getRequest() {
        return request;
    }

    /**
     * Returns the response messages.
     *
     * @return response
     */
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
