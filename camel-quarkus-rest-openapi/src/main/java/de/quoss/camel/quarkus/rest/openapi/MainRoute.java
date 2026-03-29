package de.quoss.camel.quarkus.rest.openapi;

import org.apache.camel.builder.RouteBuilder;

public class MainRoute extends RouteBuilder {
    
    @Override
    public void configure() {
        
        rest().openApi().missingOperation("ignore");
        
        from("netty-http:http://0.0.0.0:8081")
                .to("rest-openapi:findPetsByStatus");
        
        from("direct:findPetsByStatus")
                .log("Message received: ${body}");
        
    }
    
}
