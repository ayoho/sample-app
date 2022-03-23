package it.io.ayoho.persistence.models.common;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashMap;
import java.util.List;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonValue;
import javax.json.JsonValue.ValueType;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.cxf.jaxrs.provider.jsrjsonp.JsrJsonpProvider;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import it.io.ayoho.common.utils.ITUtils;

public abstract class CommonModelsIT {

    public static final int OK_CODE = Status.OK.getStatusCode();
    public static final int CREATED_CODE = Status.CREATED.getStatusCode();
    public static final int NO_CONTENT_CODE = Status.NO_CONTENT.getStatusCode();
    public static final int BAD_REQUEST_CODE = Status.BAD_REQUEST.getStatusCode();
    public static final int UNAUTHORIZED_CODE = Status.UNAUTHORIZED.getStatusCode();
    public static final int FORBIDDEN_CODE = Status.FORBIDDEN.getStatusCode();
    public static final int NOT_FOUND_CODE = Status.NOT_FOUND.getStatusCode();
    public static final int METHOD_NOT_ALLOWED_CODE = Status.METHOD_NOT_ALLOWED.getStatusCode();
    public static final int CONFLICT_CODE = Status.CONFLICT.getStatusCode();

    private WebTarget webTarget;

    protected Form form;
    protected Client client;
    protected Response response;
    protected HashMap<String, String> submissionForm;

    protected static String baseUrl;
    protected static String port;
    protected static String path;

    protected static String primaryKeyColumn;

    protected ITUtils itUtils = new ITUtils();

    abstract protected String getEntryId(JsonObject entry);

    abstract protected List<JsonObject> createMultipleUniqueEntries();

    abstract protected void updateSubmissionFormForPut();

    @AfterEach
    public void testCleanup() {
        Client client = ClientBuilder.newClient();
        client.register(JsrJsonpProvider.class);

        deleteAllEntries();

        if (response != null) {
            response.close();
        }
        client.close();
    }

    @Test
    abstract public void testCreate_sameEntryTwice();

    @Test
    public void testCreate_noFormParams() {
        submissionForm.clear();
        Response postResponse = postRequest(submissionForm);
        assertStatusCode(BAD_REQUEST_CODE, postResponse,
            "Creating an entry without any form parameters", String.class);
        assertNoEntriesPresent();
    }

    @Test
    public void testCreate_successful() {
        Response postResponse = postRequest(submissionForm);
        JsonObject createdEntry = assertStatusCode(CREATED_CODE, postResponse,
            "Creating a new entry", JsonObject.class);
        assertEntryMatchesSubmittedData(createdEntry);
    }

    @Test
    public void testRead_entryDoesNotExist() {
        JsonObject individualEntry = getIndividualEntry(-1);
        assertTrue(individualEntry.isEmpty(),
            "Reading an entry that does not exist should return an empty object but got " + individualEntry);
        assertNoEntriesPresent();
    }

    @Test
    public void testRead_individualEntry() {
        JsonObject createdEntry = createNewEntry(submissionForm);
        String entryId = getEntryId(createdEntry);

        JsonObject retrievedEntry = getIndividualEntry(entryId);
        assertEquals(createdEntry, retrievedEntry, "Retrieved entry did not match the created one");
    }

    @Test
    public void testRead_all_noEntries() {
        JsonArray retrievedEntries = getRequest();
        assertNotNull(retrievedEntries, "Retrieved JSON array should not have been null but was");
        assertTrue(retrievedEntries.isEmpty(), "Retrieved JSON array should have been empty but was " + retrievedEntries);
    }

    @Test
    public void testRead_all_oneEntry() {
        JsonObject createdEntry = createNewEntry(submissionForm);

        JsonArray retrievedEntries = getRequest();
        assertNotNull(retrievedEntries, "Retrieved JSON array should not have been null but was");
        assertFalse(retrievedEntries.isEmpty(), "Retrieved JSON array should not have been empty but was");
        assertEquals(1, retrievedEntries.size(), "Did not find the expected number of entries. Entries were: " + retrievedEntries);
        JsonObject retrievedEntry = retrievedEntries.getJsonObject(0);
        assertEquals(createdEntry, retrievedEntry, "Retrieved entry did not match the created one");
    }

    @Test
    public void testRead_all_multipleEntries() {
        List<JsonObject> createdEntries = createMultipleUniqueEntries();

        JsonArray retrievedEntries = getRequest();
        assertNotNull(retrievedEntries, "Retrieved JSON array should not have been null but was");
        assertFalse(retrievedEntries.isEmpty(), "Retrieved JSON array should not have been empty but was");
        assertEquals(createdEntries.size(), retrievedEntries.size(), "Did not find the expected number of entries. Entries were: " + retrievedEntries);
        for (int i = 0; i < createdEntries.size(); i++) {
            JsonObject createdEntry = createdEntries.get(i);
            JsonObject retrievedEntry = retrievedEntries.getJsonObject(i);
            assertEquals(createdEntry, retrievedEntry, "Entry[" + i + "] did not match the respective created item");
        }
    }

    @Test
    public void testUpdate_entryDoesNotExist() {
        int expectedStatusCode = NOT_FOUND_CODE;
        Response updateResponse = updateRequest(submissionForm, -1);
        assertStatusCode(expectedStatusCode, updateResponse,
            "Trying to update an entry that doesn't exist", String.class);
        assertNoEntriesPresent();
    }

    @Test
    public void testUpdate_noFormParams() {
        JsonObject createdEntry = createNewEntry(submissionForm);
        String entryId = getEntryId(createdEntry);

        submissionForm.clear();

        Response updateResponse = updateRequest(submissionForm, entryId);
        assertStatusCode(NO_CONTENT_CODE, updateResponse,
            "Updating an entry without any form parameters", String.class);

        JsonObject retrievedEntry = findEntry(entryId);
        assertEquals(createdEntry, retrievedEntry, "Entry should not have been modified at all");
    }

    @Test
    public void testDelete_entryDoesNotExist() {
        Response deleteResponse = deleteRequest(-1);
        assertStatusCode(NOT_FOUND_CODE, deleteResponse,
            "Trying to delete an entry that doesn't exist", String.class);
    }

    @Test
    public void testDelete_successful() {
        JsonObject createdEntry = createNewEntry(submissionForm);
        String entryId = getEntryId(createdEntry);

        JsonObject retrievedEntry = getIndividualEntry(entryId);
        assertEquals(createdEntry, retrievedEntry, "Retrieved entry should have matched the original entry");

        Response deleteResponse = deleteRequest(entryId);
        assertStatusCode(OK_CODE, deleteResponse,
            "Deleting an entry by the original author", String.class);

        retrievedEntry = getIndividualEntry(entryId);
        assertTrue(retrievedEntry.isEmpty(), "Retrieved entry should have been empty but was " + retrievedEntry);
    }

    @Test
    public void testCRUD() {
        int entryCount = getRequest().size();

        JsonObject createdEntry = createNewEntry(submissionForm);
        String entryId = getEntryId(createdEntry);

        JsonObject retrievedEntry = getIndividualEntry(entryId);
        assertEquals(createdEntry, retrievedEntry, "Retrieved entry did not match the original created entry");

        updateSubmissionFormForPut();

        Response updateResponse = updateRequest(submissionForm, entryId);
        assertStatusCode(NO_CONTENT_CODE, updateResponse,
            "Updating an entry", String.class);

        retrievedEntry = findEntry(entryId);
        assertUpdatedData(createdEntry, retrievedEntry);

        Response deleteResponse = deleteRequest(entryId);
        assertStatusCode(OK_CODE, deleteResponse,
            "Deleting an entry", String.class);

        assertEquals(entryCount, getRequest().size(),
            "Total number of entries stored should be the same after testing CRUD operations.");
    }

    /**
     * Runs a test for updating a single value in some object. Ensures the new value gets set,
     * then runs another update to unset the value and ensures the value is removed.
     */
    protected void runTest_update_singleValue(String entryUnderTest, String newValue) {
        JsonObject feature = createNewEntry(submissionForm);
        String featureId = getEntryId(feature);

        // Set a new value
        submissionForm.clear();
        submissionForm.put(entryUnderTest, newValue);

        Response updateResponse = updateRequest(submissionForm, featureId);
        assertStatusCode(NO_CONTENT_CODE, updateResponse,
            "Updating the \"" + entryUnderTest + "\" of a feature", String.class);

        JsonObject updatedFeature = findEntry(featureId);
        assertTrue(updatedFeature.containsKey(entryUnderTest), "Feature should have had a \"" + entryUnderTest + "\" entry but didn't. Feature data: " + updatedFeature);
        assertUpdatedData(feature, updatedFeature);
        JsonValue updatedValue = updatedFeature.get(entryUnderTest);

        // Unset the value
        submissionForm.clear();
        submissionForm.put(entryUnderTest, "");

        updateResponse = updateRequest(submissionForm, featureId);
        assertStatusCode(NO_CONTENT_CODE, updateResponse,
            "Updating a feature to remove \"" + entryUnderTest + "\"", String.class);

        JsonObject reUpdatedFeature = findEntry(featureId);
        if (updatedValue.getValueType() == ValueType.TRUE || updatedValue.getValueType() == ValueType.FALSE) {
            // An empty string for the new value will be parsed as "false"
            assertEquals(false, reUpdatedFeature.getBoolean(entryUnderTest), "Boolean value for \"" + entryUnderTest + "\" should have been set to false. Feature data: " + reUpdatedFeature);
        } else {
            assertFalse(reUpdatedFeature.containsKey(entryUnderTest), "Feature should not have had a \"" + entryUnderTest + "\" entry but did. Feature data: " + reUpdatedFeature);
        }
        assertUpdatedData(feature, reUpdatedFeature);
    }

    /**
     *  Makes a POST request to the /{path} endpoint.
     */
    protected Response postRequest(HashMap<String, String> formDataMap) {
        formDataMap.forEach((formField, data) -> {
            form.param(formField, data);
        });
        webTarget = client.target(baseUrl + path);
        Builder request = webTarget.request();
        response = request.post(Entity.form(form));
        form = new Form();
        return response;
    }

    /**
     *  Makes a PUT request to the /{path}/{id} endpoint.
     */
    protected Response updateRequest(HashMap<String, String> formDataMap, int id) {
        return updateRequest(formDataMap, String.valueOf(id));
    }

    /**
     *  Makes a PUT request to the /{path}/{id} endpoint.
     */
    protected Response updateRequest(HashMap<String, String> formDataMap, String id) {
        formDataMap.forEach((formField, data) -> {
            form.param(formField, data);
        });
        webTarget = client.target(baseUrl + path + "/" + id);
        Builder request = webTarget.request();
        response = request.put(Entity.form(form));
        form = new Form();
        return response;
    }

    /**
     *  Makes a DELETE request to /{path}/{id} endpoint and return the response code.
     */
    protected Response deleteRequest(int id) {
        return deleteRequest(String.valueOf(id));
    }

    /**
     *  Makes a DELETE request to /{path}/{id} endpoint and return the response code.
     */
    protected Response deleteRequest(String id) {
        webTarget = client.target(baseUrl + path + "/" + id);
        Builder request = webTarget.request();
        return request.delete();
    }

    /**
     *  Makes a GET request to the /{path} endpoint and returns result in a JsonArray.
     */
    protected JsonArray getRequest() {
        webTarget = client.target(baseUrl + path);
        response = webTarget.request().get();
        return response.readEntity(JsonArray.class);
    }

    /**
     *  Makes a GET request to the /{path}/{id} endpoint and returns a JsonObject.
     */
    protected JsonObject getIndividualEntry(int id) {
        return getIndividualEntry(String.valueOf(id));
    }

    /**
     *  Makes a GET request to the /{path}/{id} endpoint and returns a JsonObject.
     */
    protected JsonObject getIndividualEntry(String id) {
        webTarget = client.target(baseUrl + path + "/" + id);
        response = webTarget.request().get();
        return response.readEntity(JsonObject.class);
    }

    /**
     *  Makes a GET request to the /{path} endpoint and returns the entry with the provided id if it exists.
     */
    protected JsonObject findEntry(int id) {
        return findEntry(String.valueOf(id));
    }

    /**
     *  Makes a GET request to the /{path} endpoint and returns the entry with the provided id if it exists.
     */
    protected JsonObject findEntry(String id) {
        JsonArray entries = getRequest();
        for (int i = 0; i < entries.size(); i++) {
            JsonObject testEntry = entries.getJsonObject(i);
            String testId = testEntry.get(primaryKeyColumn).toString();
            testId = testId.replaceAll("^\"", "").replaceAll("\"$", "");
            if (id.equals(testId)) {
                return testEntry;
            }
        }
        return null;
    }

    protected JsonObject createNewEntry(HashMap<String, String> formDataMap) {
        Response postResponse = postRequest(formDataMap);
        JsonObject entry = assertStatusCode(CREATED_CODE, postResponse,
            "Creating a new entry", JsonObject.class);
        return entry;
    }

    private void deleteAllEntries() {
        JsonArray allEntries = getRequest();
        for (int i = 0; i < allEntries.size(); i++) {
            JsonObject entry = allEntries.getJsonObject(i);
            deleteEntry(entry);
        }
    }

    private void deleteEntry(JsonObject entry) {
        String entryId = entry.get(primaryKeyColumn).toString();
        // Some entries have IDs that are strings, others are ints
        entryId = entryId.replaceAll("^\"", "").replaceAll("\"$", "");
        try {
            response = deleteRequest(entryId);
            assertStatusCode(OK_CODE, response, "Deleting entry " + entryId + " under /" + path, String.class);
        } catch (Exception e) {
            System.err.println("Failed to delete entry " + entryId + " under /" + path + " during cleanup: " + e);
        }
    }

    protected <T> T assertStatusCode(int expectedStatus, Response response, String failurePrefix, Class<T> responseEntityClass) {
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

    protected void assertNoEntriesPresent() {
        JsonArray retrievedEntries = getRequest();
        assertNotNull(retrievedEntries, "Retrieved JSON array should not have been null but was");
        assertTrue(retrievedEntries.isEmpty(), "Retrieved JSON array should have been empty but was " + retrievedEntries);
    }

    protected void assertUpdatedData(JsonObject originalEntry, JsonObject entryToValidate) {
        // Verify all of the entries in the original entry
        for (String key : originalEntry.keySet()) {
            if (!submissionForm.containsKey(key)) {
                // Entry wasn't changed, so ensure it matches the original value
                assertEquals(originalEntry.get(key), entryToValidate.get(key), "\"" + key + "\" value should not have been updated, but its value doesn't match the original value. JSON data to validate was " + entryToValidate);
            }
        }
        // Ensure any values POSTed or PUT have the corresponding value
        assertEntryMatchesSubmittedData(entryToValidate);
    }

    protected void assertEntryMatchesSubmittedData(JsonObject entryToValidate) {
        assertEntryMatchesSubmittedData(submissionForm, entryToValidate);
    }

    protected void assertEntryMatchesSubmittedData(HashMap<String, String> formDataMap, JsonObject entryToValidate) {
        for (String submittedKey : formDataMap.keySet()) {
            assertEntryValueMatchesSubmittedValue(entryToValidate, submittedKey);
        }
    }

    protected void assertEntryValueMatchesSubmittedValue(JsonObject entryToValidate, String key) {
        JsonValue valueToValidate = entryToValidate.get(key);
        ValueType entryValueType = (valueToValidate == null) ? ValueType.NULL : valueToValidate.getValueType();
        String rawSubmittedValue = submissionForm.get(key);
        if (rawSubmittedValue.isEmpty()) {
            if (entryValueType != ValueType.NULL && (entryValueType == ValueType.TRUE || entryValueType == ValueType.FALSE)) {
                // An empty value for a boolean parameter should be interpreted as "false"
                assertEquals(JsonValue.FALSE, valueToValidate, "Entry for \"" + key + "\" should have been false but wasn't. JSON data to validate was: " + entryToValidate);
                return;
            }
        }
        JsonValue submittedValueAsJsonValue = getSubmittedValueAsJsonValue(entryValueType, rawSubmittedValue);
        assertEquals(submittedValueAsJsonValue, valueToValidate, "Value for \"" + key + "\" entry did not match the submitted value. JSON data to validate was " + entryToValidate);
    }

    private JsonValue getSubmittedValueAsJsonValue(ValueType expectedValueType, String rawSubmittedValue) {
        if (expectedValueType == ValueType.NUMBER) {
            return Json.createValue(Long.valueOf(rawSubmittedValue));
        } else if (expectedValueType == ValueType.TRUE) {
            return JsonValue.TRUE;
        } else if (expectedValueType == ValueType.FALSE) {
            return JsonValue.FALSE;
        } else {
            return Json.createValue(rawSubmittedValue);
        }
    }

}
