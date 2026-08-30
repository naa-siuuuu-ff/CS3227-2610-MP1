package com.notebook.storage;

import com.notebook.model.Note;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class FileSystemStorageTest {

    @TempDir
    Path tempFolder;

    private FileSystemStorage storage;

    @BeforeEach
    void setUp() {
        storage = new FileSystemStorage(tempFolder);
    }

    @Test
    void saveAndFindById_successfulRoundTrip() {
        Note note = new Note("intro", "Intro Note", "# Title\nHello world #test", java.time.Instant.now());
        storage.save(note);

        Optional<Note> retrieved = storage.findById("intro");
        assertTrue(retrieved.isPresent());
        assertEquals("# Title\nHello world #test", retrieved.get().getContent());
        assertTrue(retrieved.get().getTags().contains("test"));
    }

    @Test
    void findAll_retrievesAllSavedMarkdownFiles() {
        storage.save(new Note("note1", "Content 1"));
        storage.save(new Note("note2", "Content 2"));

        List<Note> notes = storage.findAll();
        assertEquals(2, notes.size());
    }

    @Test
    void delete_removesFileFromDisk() {
        storage.save(new Note("to-delete", "Bye"));
        assertTrue(storage.exists("to-delete"));

        storage.delete("to-delete");
        assertFalse(storage.exists("to-delete"));
        assertTrue(storage.findById("to-delete").isEmpty());
    }
}