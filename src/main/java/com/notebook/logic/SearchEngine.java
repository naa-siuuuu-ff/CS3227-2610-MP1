package com.notebook.logic;

import com.notebook.model.Note;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class SearchEngine {

    public List<Note> search(Collection<Note> notes, String query) {
        if (notes == null || notes.isEmpty()) {
            return Collections.emptyList();
        }
        if (query == null || query.isBlank()) {
            return List.copyOf(notes);
        }

        String trimmed = query.trim().toLowerCase();

        if (trimmed.startsWith("#")) {
            String targetTag = trimmed.substring(1);
            return notes.stream()
                    .filter(n -> n.getTags().stream().anyMatch(t -> t.equalsIgnoreCase(targetTag)))
                    .collect(Collectors.toUnmodifiableList());
        }

        return notes.stream()
                .filter(n -> matchesKeyword(n, trimmed))
                .collect(Collectors.toUnmodifiableList());
    }

    private boolean matchesKeyword(Note note, String keyword) {
        if (note.getTitle() != null && note.getTitle().toLowerCase().contains(keyword)) {
            return true;
        }
        if (note.getContent() != null && note.getContent().toLowerCase().contains(keyword)) {
            return true;
        }
        return note.getTags().stream().anyMatch(tag -> tag.toLowerCase().contains(keyword));
    }
}