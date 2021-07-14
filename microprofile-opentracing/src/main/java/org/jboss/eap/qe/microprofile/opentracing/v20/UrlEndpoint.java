package org.jboss.eap.qe.microprofile.opentracing.v20;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

@Path("url")
@Produces({ "text/plain" })
public class UrlEndpoint {

    @GET
    public String get(final @QueryParam("url") String url) {
        ClientBuilder builder = ClientBuilder.newBuilder();
        builder = org.eclipse.microprofile.opentracing.ClientTracingRegistrar.configure(builder);

        Client client = builder.build();

        WebTarget target = client.target(url);

        Response response = target.request()
                .get();

        return response.readEntity(String.class);
    }
}
