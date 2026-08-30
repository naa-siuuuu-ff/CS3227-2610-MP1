package com.notebook.model;

import java.time.Instant;
import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Note {
    private static final Pattern TAG_PATTERN = Pattern.compile("#([a-zA-Z0-9_-]+)");

    private final String id;
    private String title;
    private String content;
    private final Set<String> tags;
    private Instant lastModified;

    public Note(String id, String content) {
        this(id, id, content, Instant.now());
    }

    public Note(String id, String title, String content, Instant lastModified) {
        this.id = Objects.requireNonNull(id, "id cannot be null");
        this.title = Objects.requireNonNull(title, "title cannot be null");
        this.content = Objects.requireNonNull(content, "content cannot be null");
        this.lastModified = Objects.requireNonNull(lastModified, "lastModified cannot be null");
        this.tags = new HashSet<>();
        refreshTags();
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = Objects.requireNonNull(title, "title cannot be null");
        this.lastModified = Instant.now();
    }

    public String getContent() {
        return content;
    }

    public void updateContent(String newContent) {
        this.content = Objects.requireNonNull(newContent, "content cannot be null");
        this.lastModified = Instant.now();
        refreshTags();
    }

    public Set<String> getTags() {
        return Collections.unmodifiableSet(tags);
    }

    public Instant getLastModified() {
        return lastModified;
    }

    private void refreshTags() {
        tags.clear();
        Matcher matcher = TAG_PATTERN.matcher(content);
        while (matcher.find()) {
            tags.add(matcher.group(1).toLowerCase());
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof Note note))
            return false;
        return id.equals(note.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}