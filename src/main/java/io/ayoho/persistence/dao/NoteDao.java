package io.ayoho.persistence.dao;

import java.util.List;

import javax.enterprise.context.RequestScoped;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import io.ayoho.persistence.models.Note;

@RequestScoped
public class NoteDao {

    @PersistenceContext(name = "jpa-unit")
    private EntityManager em;

    public void createNote(Note note) {
        em.persist(note);
    }

    public Note readNote(int noteId) {
        return em.find(Note.class, noteId);
    }

    public void updateNote(Note note) {
        em.merge(note);
    }

    public void deleteNote(Note note) {
        em.remove(note);
    }

    public List<Note> readAllNotes() {
        return em.createNamedQuery("Note.findAll", Note.class).getResultList();
    }

}
