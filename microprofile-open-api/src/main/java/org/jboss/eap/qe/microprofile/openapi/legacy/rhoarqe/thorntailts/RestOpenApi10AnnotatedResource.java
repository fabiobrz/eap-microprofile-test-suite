package org.jboss.eap.qe.microprofile.openapi.legacy.rhoarqe.thorntailts;

import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

@Path("/rest")
public class RestOpenApi10AnnotatedResource {
    @GET
    @Path("/{pathParam}")
    @Operation(summary = "Annotated operation summary", operationId = "annotatedOp")
    @APIResponse(content = @Content(mediaType = "application/json", schema = @Schema(implementation = MyResponse.class)))
    public Response annotatedOperation(@PathParam("pathParam") String pathParam, @QueryParam("queryParam") String queryParam) {
        return Response.ok().entity(new MyResponse(pathParam, queryParam)).build();
    }
}
