package io.ayoho.endpoints.persistence;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.transaction.Transactional;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import io.ayoho.endpoints.utils.EndpointUtils;
import io.ayoho.persistence.dao.NoteDao;
import io.ayoho.persistence.models.Note;

@RequestScoped
@Path("notes")
public class NoteEndpoint {

    @Inject
    private NoteDao noteDAO;

    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    @Transactional
    public Response addNewNote(@FormParam(Note.COLUMN_NAME_TEXT) String text) {
        Note newNote = null;
        try {
            newNote = new Note(text);
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                .entity(EndpointUtils.buildJsonError("Failed to create a new note: " + e)).build();
        }
        noteDAO.createNote(newNote);
        return Response.status(Response.Status.CREATED)
            .entity(newNote.build())
            .build();
    }

    @PUT
    @Path("{" + Note.COLUMN_NAME_ID + "}")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Transactional
    public Response updateNote(
            @FormParam(Note.COLUMN_NAME_TEXT) String text,
            @PathParam(Note.COLUMN_NAME_ID) int id) {
        Note prevNote = noteDAO.readNote(id);
        if (prevNote == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("Note " + id + " does not exist.").build();
        }
        try {
            if (text != null) {
                prevNote.setText(text);
            }
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                .entity(EndpointUtils.buildJsonError("Failed to update note " + id + ": " + e)).build();
        }

        noteDAO.updateNote(prevNote);
        return Response.status(Response.Status.NO_CONTENT).build();
    }

    @DELETE
    @Path("{" + Note.COLUMN_NAME_ID + "}")
    @Transactional
    public Response deleteNote(@PathParam(Note.COLUMN_NAME_ID) int id) {
        Note note = noteDAO.readNote(id);
        if (note == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("Note " + id + " does not exist.").build();
        }
        noteDAO.deleteNote(note);
        return Response.status(Response.Status.OK).build();
    }

    @GET
    @Path("{" + Note.COLUMN_NAME_ID + "}")
    @Produces(MediaType.APPLICATION_JSON)
    @Transactional
    public JsonObject getNote(@PathParam(Note.COLUMN_NAME_ID) int noteId) {
        JsonObjectBuilder builder = Json.createObjectBuilder();
        Note note = noteDAO.readNote(noteId);
        if (note != null) {
            builder = Json.createObjectBuilder(note.build());
        }
        return builder.build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Transactional
    public JsonArray getNotes() {
        JsonArrayBuilder finalArray = Json.createArrayBuilder();
        for (Note note : noteDAO.readAllNotes()) {
            finalArray.add(note.build());
        }
        return finalArray.build();
    }

}
