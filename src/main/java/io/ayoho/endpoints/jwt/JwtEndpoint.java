package io.ayoho.endpoints.jwt;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.ibm.websphere.security.jwt.InvalidBuilderException;
import com.ibm.websphere.security.jwt.JwtBuilder;
import com.ibm.websphere.security.jwt.JwtToken;

@Path("/jwt")
public class JwtEndpoint {

    @GET
    public Response buildDefaultJwt() {
        JwtBuilder builder;
        try {
            builder = JwtBuilder.create();
        } catch (InvalidBuilderException e) {
            return Response.status(400).entity("Didn't find a default JwtBuilder: " + e).build();
        }
        return buildResponseWithJwt(builder);
    }

    @GET
    @Path("/{builderId}")
    public Response buildJwtWithBuilder(@PathParam("builderId") String builderId) {
        JwtBuilder builder;
        try {
            builder = JwtBuilder.create(builderId);
        } catch (InvalidBuilderException e) {
            return Response.status(400).entity("Didn't find a JwtBuilder config with ID [" + builderId + "]: " + e).build();
        }
        return buildResponseWithJwt(builder);
    }

    private Response buildResponseWithJwt(JwtBuilder builder) {
        try {
            JwtToken jwt = builder.buildJwt();
            return Response.ok(jwt.compact()).build();
        } catch (Exception e) {
            return Response.serverError().entity("Encountered an exception building a JWT: " + e).build();
        }
    }

}
