package com.notebook.model;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class FolderNode {
    private final String name;
    private final Path path;
    private final List<FolderNode> subFolders = new ArrayList<>();
    private final List<String> noteIds = new ArrayList<>();

    public FolderNode(String name, Path path) {
        this.name = Objects.requireNonNull(name, "Folder name cannot be null");
        this.path = Objects.requireNonNull(path, "Path cannot be null");
    }

    public String getName() {
        return name;
    }

    public Path getPath() {
        return path;
    }

    public List<FolderNode> getSubFolders() {
        return Collections.unmodifiableList(subFolders);
    }

    public List<String> getNoteIds() {
        return Collections.unmodifiableList(noteIds);
    }

    public void addSubFolder(FolderNode folder) {
        this.subFolders.add(folder);
    }

    public void addNoteId(String noteId) {
        this.noteIds.add(noteId);
    }
}