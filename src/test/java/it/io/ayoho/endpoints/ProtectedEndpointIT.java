package it.io.ayoho.endpoints;

import javax.ws.rs.client.Invocation.Builder;

import org.junit.jupiter.api.Test;

import it.io.ayoho.common.CommonIT;

public class ProtectedEndpointIT extends CommonIT {

    @Test
    public void test_heartbeat_returns403() throws Exception {
        webTarget = client.target(baseUrl + "/protected/heartbeat");
        Builder request = webTarget.request();
        response = request.get();
        itUtils.assertStatusCode(403, response, "Request to /heartbeat endpoint", String.class);
    }

}
