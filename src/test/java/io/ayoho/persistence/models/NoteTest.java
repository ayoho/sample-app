package io.ayoho.persistence.models;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.fail;

import javax.json.JsonObject;

import org.junit.jupiter.api.Test;

import io.ayoho.test.utils.TestUtils;

public class NoteTest {

    private TestUtils utils = new TestUtils();

    private final String text = "Text of the note";

    @Test
    public void test_constructor_nullText() {
        String text = null;
        try {
            Note note = new Note(text);
            fail("Should have thrown an exception but did not. Got: " + note);
        } catch (IllegalArgumentException e) {
            // Expected
        }
    }

    @Test
    public void test_constructor_nonNullText() {
        Note note = new Note(text);

        assertNotNull(note.getId(), "Should have found an id value but didn't.");
        assertEquals(text, note.getText(), "Did not find the expected text.");
    }

    @Test
    public void test_constructor_emptyText() {
        String text = "";
        Note note = new Note(text);

        assertNotNull(note.getId(), "Should have found an id value but didn't.");
        assertEquals(text, note.getText(), "Did not find the expected text.");
    }

    @Test
    public void test_setText_null() {
        Note note = new Note(text);

        String newText = null;
        try {
            note.setText(newText);
            fail("Should have thrown an exception but did not. Got: " + note);
        } catch (IllegalArgumentException e) {
            // Expected
        }

        assertEquals(text, note.getText(), "Text should not have been updated.");
    }

    @Test
    public void test_setText_empty() {
        Note note = new Note(text);

        String newText = "";
        note.setText(newText);

        assertEquals(newText, note.getText(), "Did not find the expected text.");
    }

    @Test
    public void test_setText_nonEmpty() {
        Note note = new Note(text);

        String newText = "This is some new text.";
        note.setText(newText);

        assertEquals(newText, note.getText(), "Did not find the expected text.");
    }

    @Test
    public void test_build() {
        Note note = new Note(text);

        JsonObject builtNote = note.build();
        utils.assertJsonContainsKeys(builtNote, Note.COLUMN_NAME_ID,
                                                Note.COLUMN_NAME_TEXT);
        assertEquals(2, builtNote.size(), "Note did not have the expected number of entries: " + builtNote);

        assertEquals(note.getId(), builtNote.getInt(Note.COLUMN_NAME_ID));
        assertEquals(note.getText(), builtNote.getString(Note.COLUMN_NAME_TEXT));
    }

    @Test
    public void test_equals_identical() {
        Note note1 = new Note(text);
        Note note2 = new Note(text);

        assertEquals(note1, note2, "Two separately instantiated notes should be equal if unique IDs haven't been generated for them");
    }

    @Test
    public void test_equals_differentText() {
        Note note1 = new Note("one");
        Note note2 = new Note("two");

        assertEquals(note1, note2, "Two separately instantiated notes, even with different text, should be equal if unique IDs haven't been generated for them");
    }

}
