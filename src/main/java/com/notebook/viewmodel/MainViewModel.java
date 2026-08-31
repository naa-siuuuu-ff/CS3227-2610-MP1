package com.notebook.viewmodel;

import java.util.Objects;

import com.notebook.logic.NotebookManager;
import com.notebook.model.Note;
import javafx.animation.PauseTransition;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.util.Duration;

public class MainViewModel {
    private final NotebookManager manager;

    private final ObservableList<Note> displayedNotes = FXCollections.observableArrayList();
    private final ObjectProperty<Note> activeNote = new SimpleObjectProperty<>();
    private final StringProperty searchQuery = new SimpleStringProperty("");
    private final StringProperty editorContent = new SimpleStringProperty("");
    private final StringProperty htmlPreview = new SimpleStringProperty("");
    private final BooleanProperty isDirty = new SimpleBooleanProperty(false);

    private final PauseTransition renderDebounce = new PauseTransition(Duration.millis(250));

    public MainViewModel(NotebookManager manager) {
        this.manager = manager;
        setupDebounce();
        setupListeners();
        applyFilter();
    }

    private void setupDebounce() {
        renderDebounce.setOnFinished(e -> {
            String md = editorContent.get();
            htmlPreview.set(manager.renderMarkdown(md));
            if (activeNote.get() != null && isDirty.get()) {
                saveCurrentNote();
            }
        });
    }

    private void setupListeners() {
        searchQuery.addListener((obs, oldVal, newVal) -> applyFilter());

        editorContent.addListener((obs, oldVal, newVal) -> {
            Note current = activeNote.get();
            if (current != null) {
                boolean modified = !Objects.equals(newVal, current.getContent());
                isDirty.set(modified);
                renderDebounce.playFromStart();
            }
        });

        activeNote.addListener((obs, oldNote, newNote) -> {
            if (newNote != null) {
                editorContent.set(newNote.getContent());
                htmlPreview.set(manager.renderMarkdown(newNote.getContent()));
                isDirty.set(false);
            } else {
                editorContent.set("");
                htmlPreview.set("");
                isDirty.set(false);
            }
        });
    }

    public void applyFilter() {
        displayedNotes.setAll(manager.searchNotes(searchQuery.get()));
        if (!displayedNotes.isEmpty() && activeNote.get() == null) {
            activeNote.set(displayedNotes.get(0));
        }
    }

    public void createNewNote(String title) {
        Note created = manager.createNote(title, "");
        applyFilter();
        activeNote.set(created);
    }

    public void deleteCurrentNote() {
        Note current = activeNote.get();
        if (current == null)
            return;

        manager.deleteNote(current.getId());
        applyFilter();
        activeNote.set(displayedNotes.isEmpty() ? null : displayedNotes.get(0));
    }

    public void saveCurrentNote() {
        Note current = activeNote.get();
        if (current == null)
            return;

        Note updated = manager.updateNoteContent(current.getId(), editorContent.get());
        activeNote.set(updated);
        applyFilter();
        isDirty.set(false);
    }

    public ObservableList<Note> getDisplayedNotes() {
        return displayedNotes;
    }

    public ObjectProperty<Note> activeNoteProperty() {
        return activeNote;
    }

    public StringProperty searchQueryProperty() {
        return searchQuery;
    }

    public StringProperty editorContentProperty() {
        return editorContent;
    }

    public ReadOnlyStringProperty htmlPreviewProperty() {
        return htmlPreview;
    }

    public ReadOnlyBooleanProperty isDirtyProperty() {
        return isDirty;
    }
}