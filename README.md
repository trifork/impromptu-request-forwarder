# impromptu-request-forwarder

A client library for calling Java servlets that are co-located in the same servlet container as the caller. Caller and callee does not have to be part of the same webapplication. 

This library is a better alternative to using a http-client for calling co-located servlets, both in terms of eliminating a risk of deadlock (due to thread starvation), and also due to performance overhead (no tpc/ip stack involved). 

The project includes a jersey connector, making it possible to set up a jaxrs REST client based on this library. 

## Getting started

Setting up Jersey REST client: 

Maven dependency: 

    <dependency>
        <groupId>com.trifork.impromptu</groupId>
        <artifactId>impromptu-request-forwarder-jersey-connector</artifactId>
        <version>1.0.0</version>
    </dependency>

Creating a jersey client with the forwarding connector. Note that the connector needs a reference to a ServletContext instance:

        Client client = ClientBuilder.newClient(new ClientConfig().connectorProvider((jaxRsClient, config) ->  new ForwardingClientConnector(req.getServletContext())));
        
        WebTarget target = client.target("http://localhost:8080/path/to/neighbouring/servlet");
        
        // etc..        

