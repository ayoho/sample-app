package it.io.ayoho.endpoints;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.UnsupportedEncodingException;
import java.security.Key;

import javax.crypto.spec.SecretKeySpec;
import javax.ws.rs.client.Invocation.Builder;

import org.jose4j.jwk.HttpsJwks;
import org.jose4j.jwt.consumer.JwtConsumer;
import org.jose4j.jwt.consumer.JwtConsumerBuilder;
import org.jose4j.jwt.consumer.JwtContext;
import org.jose4j.jwx.JsonWebStructure;
import org.jose4j.keys.resolvers.HttpsJwksVerificationKeyResolver;
import org.junit.jupiter.api.Test;

import it.io.ayoho.common.CommonIT;

public class JwtEndpointIT extends CommonIT {

    private static final String HS256_KEY = "this is a sufficiently long secret passphrase";
    private static final String DEFAULT_JWT_BUILDER_ID = "defaultJWT";
    private static final String HS256_JWT_BUILDER_ID = "jwtBuilderHs256";

    private final String httpBase = "http://localhost:9080";
    private final String httpsBase = "https://localhost:9443";
    private final String issuerBase = httpsBase + "/jwt/";

    @Test
    public void test_get_defaultBuilder() {
        webTarget = client.target(baseUrl + "/jwt");
        Builder request = webTarget.request();
        response = request.get();
        String responseContent = itUtils.assertStatusCode(200, response, "GET request to the default JWT builder endpoint", String.class);

        // Retrieve the key to verify the JWT from the server's JWK endpoint
        HttpsJwks httpsJkws = new HttpsJwks(httpBase + "/jwt/ibm/api/" + DEFAULT_JWT_BUILDER_ID + "/jwk");
        HttpsJwksVerificationKeyResolver httpsJwksKeyResolver = new HttpsJwksVerificationKeyResolver(httpsJkws);

        // Parse the JWT in the response
        JwtConsumer consumer = new JwtConsumerBuilder()
            .setVerificationKeyResolver(httpsJwksKeyResolver)
            .setExpectedIssuer(issuerBase + DEFAULT_JWT_BUILDER_ID)
            .build();
        try {
            JwtContext jwtContext = consumer.process(responseContent);
            JsonWebStructure jws = jwtContext.getJoseObjects().get(0);
            assertEquals("RS256", jws.getAlgorithmHeaderValue(), "JWT was not signed with the expected signature algorithm.");
        } catch (Exception e) {
            fail("Should have cleanly parsed the JWT but got an exception: " + e);
        }
    }

    @Test
    public void test_get_hs256Builder() throws UnsupportedEncodingException {
        webTarget = client.target(baseUrl + "/jwt/" + HS256_JWT_BUILDER_ID);
        Builder request = webTarget.request();
        response = request.get();
        String responseContent = itUtils.assertStatusCode(200, response, "GET request to the HS256 JWT builder endpoint", String.class);

        // Parse the JWT in the response
        JwtConsumer consumer = new JwtConsumerBuilder()
            .setVerificationKey(getHmacKey(HS256_KEY))
            .setExpectedIssuer(issuerBase + HS256_JWT_BUILDER_ID)
            .build();
        try {
            JwtContext jwtContext = consumer.process(responseContent);
            JsonWebStructure jws = jwtContext.getJoseObjects().get(0);
            assertEquals("HS256", jws.getAlgorithmHeaderValue(), "JWT was not signed with the expected signature algorithm.");
        } catch (Exception e) {
            fail("Should have cleanly parsed the JWT but got an exception: " + e);
        }
    }

    private Key getHmacKey(String keyString) throws UnsupportedEncodingException {
        byte[] secretBytes = keyString.getBytes("UTF-8");
        return new SecretKeySpec(secretBytes, "HmacSHA256");
    }

}
