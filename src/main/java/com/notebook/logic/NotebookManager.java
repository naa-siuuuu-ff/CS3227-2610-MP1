package com.notebook.logic;

import com.notebook.model.Note;
import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

public class NotebookManager {
    private final NoteRepository repository;
    private final MarkdownRenderer renderer;

    public NotebookManager(NoteRepository repository, MarkdownRenderer renderer) {
        this.repository = Objects.requireNonNull(repository, "Repository must not be null");
        this.renderer = Objects.requireNonNull(renderer, "Renderer must not be null");
    }

    public List<Note> getAllNotes() {
        return repository.findAll();
    }

    public Optional<Note> getNote(String id) {
        if (id == null || id.isBlank()) {
            return Optional.empty();
        }
        return repository.findById(id);
    }

    public Note createNote(String title, String initialContent) {
        String noteId = UUID.randomUUID().toString();
        String safeTitle = (title == null || title.isBlank()) ? "Untitled" : title.trim();
        String content = (initialContent == null) ? "" : initialContent;

        Note newNote = new Note(noteId, safeTitle, content, Instant.now());
        repository.save(newNote);
        return newNote;
    }

    public Note updateNoteContent(String id, String newContent) {
        Note existing = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Note not found with ID: " + id));

        Note updated = new Note(
                existing.getId(),
                existing.getTitle(),
                newContent != null ? newContent : "",
                Instant.now());
        repository.save(updated);
        return updated;
    }

    public void deleteNote(String id) {
        if (id != null && !id.isBlank()) {
            repository.delete(id);
        }
    }

    public String renderMarkdown(String markdownSource) {
        if (markdownSource == null || markdownSource.isEmpty()) {
            return "";
        }
        return renderer.renderToHtml(markdownSource);
    }

    public List<Note> findByTag(String tag) {
        if (tag == null || tag.isBlank()) {
            return Collections.emptyList();
        }
        String cleanTag = tag.startsWith("#") ? tag.substring(1).toLowerCase() : tag.toLowerCase();
        return repository.findAll().stream()
                .filter(note -> note.getTags().stream()
                        .anyMatch(t -> t.equalsIgnoreCase(cleanTag)))
                .collect(Collectors.toUnmodifiableList());
    }
}