package it.io.ayoho.endpoints;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import javax.ws.rs.client.Invocation.Builder;

import org.junit.jupiter.api.Test;

import it.io.ayoho.common.CommonIT;

public class SimpleEndpointIT extends CommonIT {

    @Test
    public void test_hello() throws Exception {
        webTarget = client.target(baseUrl + "/simple/hello");
        Builder request = webTarget.request();
        response = request.get();
        String responseContent = itUtils.assertStatusCode(200, response, "Request to /simple/hello endpoint", String.class);
        assertEquals("Hello, world!", responseContent, "Did not get the expected content in the response.");
    }

    @Test
    public void test_heartbeat() throws Exception {
        webTarget = client.target(baseUrl + "/simple/heartbeat");
        Builder request = webTarget.request();
        response = request.get();
        String responseContent = itUtils.assertStatusCode(200, response, "Request to /simple/hello endpoint", String.class);
        assertTrue(responseContent.startsWith("Heartbeat:"), "Content did not begin with the expected string. Response content was [" + responseContent + "].");
    }

}
