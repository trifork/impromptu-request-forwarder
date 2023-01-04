package com.trifork.impromptu.jersey;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.concurrent.Future;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.ProcessingException;
import javax.ws.rs.core.Response;

import org.glassfish.jersey.client.ClientRequest;
import org.glassfish.jersey.client.ClientResponse;
import org.glassfish.jersey.client.spi.AsyncConnectorCallback;
import org.glassfish.jersey.client.spi.Connector;
import org.glassfish.jersey.message.internal.Statuses;
import com.trifork.impromptu.HeaderValueResolver;
import com.trifork.impromptu.InternalDelegator;
import com.trifork.impromptu.InvocationResult;

public class ForwardingClientConnector implements Connector {

    private ServletContext servletContext;

    public ForwardingClientConnector(ServletContext servletContext) {
        this.servletContext = servletContext;
    }

    @Override
    public ClientResponse apply(ClientRequest clientRequest) {

        InternalDelegator delegator = new InternalDelegator(servletContext);
        
        ByteArrayOutputStream os = new ByteArrayOutputStream();

        if (clientRequest.getEntity() != null) {
            clientRequest.setStreamProvider(contentLength -> os);
            try {
                clientRequest.writeEntity();
            } catch (IOException e) {
                throw new ProcessingException("Failed to write request entity", e);
            }
        }
        
        ByteArrayInputStream is = new ByteArrayInputStream(os.toByteArray());

        HeaderValueResolver headerValueResolver = new HeaderValueResolver() {

            @Override
            public List<String> getValue(String key) {
                return clientRequest.getStringHeaders().get(key);
            }

            @Override
            public Enumeration<String> getKeys() {
                return Collections.enumeration(clientRequest.getStringHeaders().keySet());
            }
            
        };
        
        InvocationResult invocationResult;
        try {
            String query = clientRequest.getUri().getQuery();
            String pathAndQuery = clientRequest.getUri().getPath();
            if (query != null && query.length() > 0) {
                pathAndQuery += "?" + query;
            }
            invocationResult = delegator.doRequest(pathAndQuery, is, clientRequest.getMethod(), headerValueResolver);
        } catch (ServletException | IOException e) {
            throw new ProcessingException("Failure forwarding request", e);
        }

        return toJerseyResponse(clientRequest, invocationResult);    
    }

    @Override
    public Future<?> apply(ClientRequest request, AsyncConnectorCallback callback) {
        throw new UnsupportedOperationException("Not Implemented");
    }

    @Override
    public String getName() {
        return "Servlet Forwarding Client";
    }

    @Override
    public void close() {
        // Nothing to do 
    }

    private ClientResponse toJerseyResponse(ClientRequest clientRequest, InvocationResult invocationResult) {
        HttpServletResponse response = invocationResult.getResponse();

        final Response.StatusType responseStatus = Statuses.from(response.getStatus());
        final ClientResponse jerseyResponse = new ClientResponse(responseStatus, clientRequest);
        response.getHeaderNames().forEach(key -> response.getHeaders(key).forEach(value -> jerseyResponse.header(key, value)));
        jerseyResponse.setEntityStream(invocationResult.getResponseBody());

        return jerseyResponse;
    }

}
