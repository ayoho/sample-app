package it.io.ayoho.common.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonValue;
import javax.json.JsonValue.ValueType;
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

    public void verifyError(JsonObject errorJson, String errorMsgRegex) {
        assertTrue(errorJson.containsKey("error"), "Did not find \"error\" key in JSON data: " + errorJson);
        String errorMsg = errorJson.getString("error");
        assertTrue(Pattern.matches(errorMsgRegex, errorMsg), "Error string did not match regex. Expected regex was [" + errorMsgRegex + "]. Error message was [" + errorMsg + "]");
    }

    public String getStringApproximationOfEntry(JsonObject jsonData, String key) {
        if (!jsonData.containsKey(key)) {
            return null;
        }
        ValueType type = getEntryValueType(jsonData, key);

        if (type == ValueType.ARRAY) {
            return convertJsonArrayToListString(jsonData.getJsonArray(key));
        } else if (type == ValueType.NUMBER) {
            return String.valueOf(jsonData.getJsonNumber(key).longValueExact());
        } else if (type == ValueType.STRING) {
            return jsonData.getString(key);
        } else {
            return jsonData.get(key).toString();
        }
    }

    public ValueType getEntryValueType(JsonObject jsonData, String key) {
        if (!jsonData.containsKey(key)) {
            return null;
        }
        JsonValue jsonValue = jsonData.get(key);
        return jsonValue.getValueType();
    }

    /**
     * Useful for getting rid of the " characters the JsonArray adds to string entries.
     */
    public String convertJsonArrayToListString(JsonArray array) {
        List<Object> list = new ArrayList<>();
        for (int i = 0; i < array.size(); i++) {
            JsonValue value = array.get(i);
            if (value.getValueType() == ValueType.STRING) {
                list.add(array.getString(i));
            } else if (value.getValueType() == ValueType.NUMBER) {
                list.add(array.getJsonNumber(i).longValueExact());
            } else {
                list.add(value.toString());
            }
        }
        return list.toString();
    }

}
