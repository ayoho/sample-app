package it.io.ayoho.common;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

import org.apache.cxf.jaxrs.provider.jsrjsonp.JsrJsonpProvider;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

import it.io.ayoho.common.utils.ITUtils;

public class CommonIT {

    protected Client client;
    protected WebTarget webTarget;
    protected Response response;

    protected String baseUrl = "http://localhost:9080/app";

    protected ITUtils itUtils = new ITUtils();

    @BeforeEach
    public void testSetup() {
        client = ClientBuilder.newClient();
        client.register(JsrJsonpProvider.class);
    }

    @AfterEach
    public void testCleanup() {
        if (response != null) {
            response.close();
        }
        client.close();
    }

}
