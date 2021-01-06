package com.trifork.impromptu;

import java.io.InputStream;

import javax.servlet.http.HttpServletResponse;

public class InvocationResult {

    private HttpServletResponse response;
    private InputStream responseBody;

    public InvocationResult(HttpServletResponse response, InputStream responseBody) {
        this.response = response;
        this.responseBody = responseBody;
    }

    public HttpServletResponse getResponse() {
        return response;
    }
    
    public InputStream getResponseBody() {
        return responseBody;
    }
    
}
