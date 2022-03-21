package io.ayoho.endpoints;

import java.security.Principal;

import javax.annotation.security.DenyAll;
import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.enterprise.context.RequestScoped;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.UriInfo;

import io.ayoho.endpoints.utils.EndpointUtils;

@Path("/protected")
@DenyAll
@RequestScoped
public class ProtectedEndpoint {

    EndpointUtils endpointUtils = new EndpointUtils();

    /**
     * Does not require authentication.
     */
    @GET
    @Path("/heartbeat")
    @PermitAll
    public String heartbeat() {
        return "Heartbeat: " + System.currentTimeMillis();
    }

    /**
     * Should always return a 403.
     */
    @GET
    @Path("/no-roles")
    public String noRoles(@Context SecurityContext sec) {
        return "No roles allowed - should always return a 403";
    }

    @GET
    @Path("/echo")
    @RolesAllowed("Echoer")
    public String echoInput(@Context SecurityContext sec, @QueryParam("input") String input) {
        Principal user = sec.getUserPrincipal();
        return input + ", user="+user.getName();
    }

    @GET
    @Path("/dump")
    @RolesAllowed("Echoer")
    @Produces(MediaType.TEXT_PLAIN)
    public String dump(@Context UriInfo uriInfo, @Context HttpHeaders headers, @Context SecurityContext sec) {
        String result = endpointUtils.getRequestDump(uriInfo, headers, sec);
        return result;
    }

}
