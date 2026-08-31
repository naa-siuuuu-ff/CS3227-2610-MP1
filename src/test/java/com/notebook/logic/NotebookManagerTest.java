package com.notebook.logic;

import com.notebook.model.Note;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class NotebookManagerTest {

    private NotebookManager manager;
    private InMemoryNoteRepository stubRepository;

    @BeforeEach
    void setUp() {
        stubRepository = new InMemoryNoteRepository();
        MarkdownRenderer stubRenderer = markdown -> "<p>" + markdown + "</p>";
        manager = new NotebookManager(stubRepository, stubRenderer);
    }

    @Test
    void createNote_persistsAndReturnsValidNote() {
        Note note = manager.createNote("Sprint Plan", "#plan Initial content");

        assertNotNull(note.getId());
        assertEquals("Sprint Plan", note.getTitle());
        assertTrue(stubRepository.exists(note.getId()));
    }

    @Test
    void updateNoteContent_updatesTimestampAndContent() {
        Note initial = manager.createNote("Draft", "Initial");
        Note updated = manager.updateNoteContent(initial.getId(), "Updated content");

        assertEquals("Updated content", updated.getContent());
        assertEquals(updated.getContent(), stubRepository.findById(initial.getId()).get().getContent());
    }

    @Test
    void deleteNote_removesFromStorage() {
        Note note = manager.createNote("To Delete", "Content");
        manager.deleteNote(note.getId());

        assertFalse(stubRepository.exists(note.getId()));
        assertTrue(manager.getNote(note.getId()).isEmpty());
    }

    @Test
    void renderMarkdown_delegatesToRenderer() {
        String result = manager.renderMarkdown("**bold**");
        assertEquals("<p>**bold**</p>", result);
    }

    private static class InMemoryNoteRepository implements NoteRepository {
        private final Map<String, Note> storage = new HashMap<>();

        @Override
        public List<Note> findAll() {
            return new ArrayList<>(storage.values());
        }

        @Override
        public Optional<Note> findById(String id) {
            return Optional.ofNullable(storage.get(id));
        }

        @Override
        public void save(Note note) {
            storage.put(note.getId(), note);
        }

        @Override
        public void delete(String id) {
            storage.remove(id);
        }

        @Override
        public boolean exists(String id) {
            return storage.containsKey(id);
        }
    }
}