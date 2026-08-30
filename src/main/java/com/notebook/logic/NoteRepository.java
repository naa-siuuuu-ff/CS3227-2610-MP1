package com.notebook.logic;

import com.notebook.model.Note;
import java.util.List;
import java.util.Optional;

public interface NoteRepository {
    List<Note> findAll();

    Optional<Note> findById(String noteId);

    void save(Note note);

    void delete(String noteId);

    boolean exists(String noteId);
}