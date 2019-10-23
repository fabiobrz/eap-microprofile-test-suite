package org.jboss.eap.qe.microprofile.openapi.legacy.javaeesamples;

import org.eclipse.microprofile.openapi.annotations.Operation;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import static javax.ws.rs.core.MediaType.TEXT_PLAIN;

@Path("/hello")
public class RestHelloResource {

    @GET
    @Operation(
        operationId = "hello world",
        description = "This is a well know Hello World service. It will output a variant of the expected 'Hello, world!' phrase.")
    @Produces(TEXT_PLAIN)
    public String helloWorld() {
        return "Hello World!";
    }

}
