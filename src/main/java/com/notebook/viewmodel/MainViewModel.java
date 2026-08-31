package com.notebook.viewmodel;

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

// presentation state and properties
public class MainViewModel {
    private final NotebookManager manager;

    private final ObservableList<Note> notes = FXCollections.observableArrayList();
    private final ObjectProperty<Note> activeNote = new SimpleObjectProperty<>();
    private final StringProperty editorContent = new SimpleStringProperty("");
    private final StringProperty htmlPreview = new SimpleStringProperty("");
    private final BooleanProperty isDirty = new SimpleBooleanProperty(false);

    private final PauseTransition debounceTimer = new PauseTransition(Duration.millis(250));

    public MainViewModel(NotebookManager manager) {
        this.manager = manager;
        setupDebounce();
        setupEditorBinding();
        loadAllNotes();
    }

    private void setupDebounce() {
        debounceTimer.setOnFinished(event -> {
            String markdown = editorContent.get();
            htmlPreview.set(manager.renderMarkdown(markdown));
            if (activeNote.get() != null && isDirty.get()) {
                saveCurrentNote();
            }
        });
    }

    private void setupEditorBinding() {
        editorContent.addListener((obs, oldVal, newVal) -> {
            Note current = activeNote.get();
            if (current != null) {
                boolean changed = !newVal.equals(current.getContent());
                isDirty.set(changed);
                debounceTimer.playFromStart();
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

    public void loadAllNotes() {
        notes.setAll(manager.getAllNotes());
        if (!notes.isEmpty() && activeNote.get() == null) {
            activeNote.set(notes.get(0));
        }
    }

    public void createNewNote(String title) {
        Note created = manager.createNote(title, "");
        notes.add(0, created);
        activeNote.set(created);
    }

    public void deleteCurrentNote() {
        Note current = activeNote.get();
        if (current == null)
            return;

        manager.deleteNote(current.getId());
        notes.remove(current);
        activeNote.set(notes.isEmpty() ? null : notes.get(0));
    }

    public void saveCurrentNote() {
        Note current = activeNote.get();
        if (current == null)
            return;

        Note updated = manager.updateNoteContent(current.getId(), editorContent.get());
        int index = notes.indexOf(current);
        if (index != -1) {
            notes.set(index, updated);
        }
        activeNote.set(updated);
        isDirty.set(false);
    }

    public ObservableList<Note> getNotes() {
        return notes;
    }

    public ObjectProperty<Note> activeNoteProperty() {
        return activeNote;
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