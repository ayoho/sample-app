package io.ayoho.test.utils;

import static org.junit.jupiter.api.Assertions.assertTrue;

import javax.json.JsonObject;

public class TestUtils {

    public void assertJsonContainsKeys(JsonObject json, String... expectedKeys) {
        for (String expectedKey : expectedKeys) {
            assertTrue(json.containsKey(expectedKey), "Did not find an entry for \"" + expectedKey + "\": " + json);
        }
    }

}