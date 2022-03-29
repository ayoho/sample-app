package it.io.ayoho.persistence.models;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.json.JsonObject;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.Response;

import org.apache.cxf.jaxrs.provider.jsrjsonp.JsrJsonpProvider;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import io.ayoho.persistence.models.Note;
import it.io.ayoho.persistence.models.common.CommonModelsIT;

public class NoteIT extends CommonModelsIT {

    private static final String NOTE_TEXT = "This is an example note.";

    @BeforeAll
    public static void oneTimeSetup() {
        port = System.getProperty("http.port");
        baseUrl = "http://localhost:" + port + "/";
        path = "notes";
        primaryKeyColumn = Note.COLUMN_NAME_ID;
    }

    @BeforeEach
    public void setup() {
        form = new Form();
        client = ClientBuilder.newClient();
        client.register(JsrJsonpProvider.class);

        submissionForm = new HashMap<String, String>();
        submissionForm.put(Note.COLUMN_NAME_TEXT, NOTE_TEXT);
    }

    @Test
    public void testCreate_sameEntryTwice() {
        Response postResponse = postRequest(submissionForm);
        assertStatusCode(CREATED_CODE, postResponse,
            "Creating a note", JsonObject.class);

        postResponse = postRequest(submissionForm);
        assertStatusCode(CREATED_CODE, postResponse,
            "Trying to create a note that already exists", String.class);
    }

    @Test
    public void testCreate_missingText() {
        String keyToRemove = Note.COLUMN_NAME_TEXT;
        submissionForm.remove(keyToRemove);

        Response postResponse = postRequest(submissionForm);
        assertStatusCode(BAD_REQUEST_CODE, postResponse,
            "Creating a note without the " + keyToRemove + " parameter", String.class);
        assertNoEntriesPresent();
    }

    @Test
    public void testCreate_emptyText() {
        String link = "";
        submissionForm.put(Note.COLUMN_NAME_TEXT, link);

        Response postResponse = postRequest(submissionForm);
        JsonObject createdEntry = assertStatusCode(CREATED_CODE, postResponse,
            "Creating a note with an empty " + Note.COLUMN_NAME_TEXT + " parameter", JsonObject.class);
        assertEntryMatchesSubmittedData(createdEntry);
    }

    @Test
    public void testUpdate_emptyText() {
        JsonObject createdEntry = createNewEntry(submissionForm);
        int entryId = createdEntry.getInt(primaryKeyColumn);

        String updatedText = "";
        submissionForm.clear();
        submissionForm.put(Note.COLUMN_NAME_TEXT, updatedText);

        Response updateResponse = updateRequest(submissionForm, entryId);
        assertStatusCode(NO_CONTENT_CODE, updateResponse,
            "Updating a note with an empty " + Note.COLUMN_NAME_TEXT + " parameter", String.class);

        JsonObject retrievedEntry = findEntry(entryId);
        assertData(retrievedEntry, updatedText);
    }

    @Test
    public void testUpdate_text() {
        JsonObject createdEntry = createNewEntry(submissionForm);
        int entryId = createdEntry.getInt(primaryKeyColumn);

        String updatedText = "New idea";
        submissionForm.clear();
        submissionForm.put(Note.COLUMN_NAME_TEXT, updatedText);

        Response updateResponse = updateRequest(submissionForm, entryId);
        assertStatusCode(NO_CONTENT_CODE, updateResponse,
            "Updating a note", String.class);

        JsonObject retrievedEntry = findEntry(entryId);
        assertData(retrievedEntry, updatedText);
    }

    @Override
    protected String getEntryId(JsonObject entry) {
        return String.valueOf(entry.getInt(primaryKeyColumn));
    }

    @Override
    protected List<JsonObject> createMultipleUniqueEntries() {
        List<JsonObject> entries = new ArrayList<>();

        JsonObject createdEntry1 = createNewEntry(submissionForm);
        entries.add(createdEntry1);

        submissionForm.put(Note.COLUMN_NAME_TEXT, "Just wanted to jot down a note about something.");
        JsonObject createdEntry2 = createNewEntry(submissionForm);
        entries.add(createdEntry2);

        submissionForm.put(Note.COLUMN_NAME_TEXT, "Why do we drive on parkways and park on driveways?");
        JsonObject createdEntry3 = createNewEntry(submissionForm);
        entries.add(createdEntry3);

        return entries;
    }

    @Override
    protected void updateSubmissionFormForPut() {
        String updatedText = "We've gotten some new information, something's changed";
        submissionForm.put(Note.COLUMN_NAME_TEXT, updatedText);
    }

    /**
     *  Asserts note fields equal the provided values.
     */
    protected void assertData(JsonObject note, String expectedText) {
        assertEquals(expectedText, note.getString(Note.COLUMN_NAME_TEXT), "Text entry did not match expected value. Full note data was: " + note);
    }

}
