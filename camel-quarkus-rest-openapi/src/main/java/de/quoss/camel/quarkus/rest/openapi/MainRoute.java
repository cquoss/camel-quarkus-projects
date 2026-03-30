package de.quoss.camel.quarkus.rest.openapi;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.netty.http.NettyHttpOperationFailedException;

public class MainRoute extends RouteBuilder {
    
    @Override
    public void configure() {
        
        /*
         *   TODO bunch of to-dos here:
         *
         *   - Non functional requirements:
         *     + Production grade error handling
         *     + Production grade logging
         *     + Health / metrics
         *     + Performance tests
         *   - Provide at least some mvp functionality the consumers
         */
        
        rest().openApi()
                .specification("classpath:openapi.yaml")
                .missingOperation("ignore")
                .end()
                .clientRequestValidation(true)
                .clientResponseValidation(true);

        onException(NettyHttpOperationFailedException.class)
                .handled(true)
                .log("Exception caught, message body: ${body}");
        
        from("netty-http:http://0.0.0.0:8081")
                // TODO check: setting requestValidationEnabled to true does not seem to have any effect
                .to("rest-openapi:classpath:openapi.yaml#updatePet");
        
        from("direct:findPetsByStatus")
                .log("find-pets-by-status - message received: ${body}");
        
        from("direct:updatePet")
                .log("update-pet - message received: ${body}");
        
    }
    
}
