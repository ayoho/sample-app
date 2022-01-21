package it.io.ayoho.common.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;

import javax.ws.rs.core.Response;

public class ITUtils {

    public <T> T assertStatusCode(int expectedStatus, Response response, String failurePrefix, Class<T> responseEntityClass) {
        int responseStatus = response.getStatus();
        if (expectedStatus != responseStatus) {
            // Change to read the entity as a string so that it should always be able to be read, even if we fail
            String responseString = response.readEntity(String.class);
            assertEquals(expectedStatus, responseStatus,
                failurePrefix + " should return the HTTP response code " + expectedStatus + ". Response: " + responseString);
        }
        T responseEntity = (T) response.readEntity(responseEntityClass);
        return responseEntity;
    }

}
