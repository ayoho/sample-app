package io.ayoho.persistence.models;

import java.io.Serializable;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

@Entity
@Table(name = "Note")
@NamedQuery(name = "Note.findAll", query = "SELECT n FROM Note n")
public class Note implements Serializable {

    private static final long serialVersionUID = 1L;

    public static final String COLUMN_NAME_ID = "id";
    public static final String COLUMN_NAME_TEXT = "text";

    @GeneratedValue(strategy = GenerationType.AUTO)
    @Id
    @Column(name = COLUMN_NAME_ID)
    private int id;

    @Column(name = COLUMN_NAME_TEXT)
    private String text;

    public Note() {
    }

    public Note(String text) {
        setText(text);
    }

    public int getId() {
        return id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        if (text == null) {
            throw new IllegalArgumentException("Note text cannot be null.");
        }
        this.text = text;
    }

    public JsonObject build() {
        JsonObjectBuilder builder = Json.createObjectBuilder();
        builder.add(COLUMN_NAME_ID, id);
        builder.add(COLUMN_NAME_TEXT, text);
        return builder.build();
    }

    @Override
    public String toString() {
        return "Note{" +
            COLUMN_NAME_ID + "=" + id + ", " +
            COLUMN_NAME_TEXT + "=" + text +
        "}";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + id;
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Note other = (Note) obj;
        if (id != other.getId())
            return false;
        return true;
    }

}
