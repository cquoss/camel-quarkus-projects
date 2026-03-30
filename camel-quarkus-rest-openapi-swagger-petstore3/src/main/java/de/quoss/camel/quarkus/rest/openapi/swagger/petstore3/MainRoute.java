package de.quoss.camel.quarkus.rest.openapi.swagger.petstore3;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.netty.http.NettyHttpOperationFailedException;

public class MainRoute extends RouteBuilder {
    
    @Override
    public void configure() {
        
        onException(NettyHttpOperationFailedException.class)
                .handled(true)
                .log("Exception caught, message body: ${body}");
        
        from("netty-http:http://0.0.0.0:8081")
                .to("rest-openapi:findPetsByStatus");
        
    }
    
}
