package io.ayoho.endpoints;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

@Path("/simple")
public class SimpleEndpoint {
    
    @GET
    @Path("/hello")
    public String hello() {
        return "Hello, world!";
    }

    @GET
    @Path("/heartbeat")
    public String heartbeat() {
        return "Heartbeat: " + System.currentTimeMillis();
    }

}
