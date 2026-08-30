package com.notebook.storage;

import com.notebook.logic.NoteRepository;
import com.notebook.model.Note;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

// Persistent file storage systempackage com.notebook.storage;
public class FileSystemStorage implements NoteRepository {
    private final Path rootDir;

    public FileSystemStorage(Path rootDir) {
        this.rootDir = rootDir;
        try {
            if (!Files.exists(rootDir)) {
                Files.createDirectories(rootDir);
            }
        } catch (IOException e) {
            throw new UncheckedIOException("Unable to initialize root notebook directory: " + rootDir, e);
        }
    }

    @Override
    public List<Note> findAll() {
        try (Stream<Path> stream = Files.walk(rootDir)) {
            return stream.filter(Files::isRegularFile)
                    .filter(p -> p.toString().endsWith(".md"))
                    .map(this::readNote)
                    .toList();
        } catch (IOException e) {
            throw new UncheckedIOException("Failed to list notes from " + rootDir, e);
        }
    }

    @Override
    public Optional<Note> findById(String noteId) {
        Path targetPath = resolvePath(noteId);
        if (!Files.exists(targetPath) || !Files.isRegularFile(targetPath)) {
            return Optional.empty();
        }
        return Optional.of(readNote(targetPath));
    }

    @Override
    public void save(Note note) {
        Path targetPath = resolvePath(note.getId());
        try {
            if (targetPath.getParent() != null) {
                Files.createDirectories(targetPath.getParent());
            }
            Files.writeString(
                    targetPath,
                    note.getContent(),
                    StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE,
                    StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            throw new UncheckedIOException("Failed to write note: " + note.getId(), e);
        }
    }

    @Override
    public void delete(String noteId) {
        Path targetPath = resolvePath(noteId);
        try {
            Files.deleteIfExists(targetPath);
        } catch (IOException e) {
            throw new UncheckedIOException("Failed to delete note: " + noteId, e);
        }
    }

    @Override
    public boolean exists(String noteId) {
        return Files.exists(resolvePath(noteId));
    }

    private Path resolvePath(String noteId) {
        String filename = noteId.endsWith(".md") ? noteId : noteId + ".md";
        return rootDir.resolve(filename).normalize();
    }

    private Note readNote(Path path) {
        try {
            String content = Files.readString(path, StandardCharsets.UTF_8);
            String fileName = path.getFileName().toString();
            String id = fileName.substring(0, fileName.lastIndexOf('.'));
            return new Note(id, content);
        } catch (IOException e) {
            throw new UncheckedIOException("Failed to read note file: " + path, e);
        }
    }
}