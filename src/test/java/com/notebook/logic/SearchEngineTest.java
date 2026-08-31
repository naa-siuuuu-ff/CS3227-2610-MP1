package com.notebook.logic;

import com.notebook.model.Note;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SearchEngineTest {

    private SearchEngine searchEngine;
    private Note note1;
    private Note note2;
    private Note note3;
    private List<Note> sampleNotes;

    @BeforeEach
    void setUp() {
        searchEngine = new SearchEngine();

        note1 = new Note(
                "note-1",
                "Meeting Minutes",
                "Discussing release goals and #devops pipeline updates.",
                Instant.now());

        note2 = new Note(
                "note-2",
                "Backend Architecture",
                "Designing the core domain using pure Java and #hexagonal architecture.",
                Instant.now());

        note3 = new Note(
                "note-3",
                "Shopping List",
                "Milk, coffee beans, and green tea.",
                Instant.now());

        sampleNotes = List.of(note1, note2, note3);
    }

    @Test
    @DisplayName("Returns empty list when collection is null or empty")
    void search_nullOrEmptyNotes_returnsEmptyList() {
        assertTrue(searchEngine.search(null, "query").isEmpty());
        assertTrue(searchEngine.search(Collections.emptyList(), "query").isEmpty());
    }

    @Test
    @DisplayName("Returns all notes when query is null, empty, or whitespace")
    void search_nullOrBlankQuery_returnsAllNotes() {
        List<Note> nullQueryResult = searchEngine.search(sampleNotes, null);
        assertEquals(3, nullQueryResult.size());

        List<Note> emptyQueryResult = searchEngine.search(sampleNotes, "");
        assertEquals(3, emptyQueryResult.size());

        List<Note> blankQueryResult = searchEngine.search(sampleNotes, "    ");
        assertEquals(3, blankQueryResult.size());
    }

    @Test
    @DisplayName("Matches notes using exact tag query (#tag) ignoring tag case")
    void search_validTagQuery_matchesExactTagCaseInsensitively() {
        List<Note> resultsLower = searchEngine.search(sampleNotes, "#devops");
        assertEquals(1, resultsLower.size());
        assertTrue(resultsLower.contains(note1));

        List<Note> resultsUpper = searchEngine.search(sampleNotes, "#HEXAGONAL");
        assertEquals(1, resultsUpper.size());
        assertTrue(resultsUpper.contains(note2));
    }

    @Test
    @DisplayName("Tag query requires exact tag name match and excludes partial tag names")
    void search_partialTagQuery_returnsEmpty() {
        // "dev" is a prefix of "devops", but tag queries test exact match via
        // equalsIgnoreCase
        List<Note> results = searchEngine.search(sampleNotes, "#dev");
        assertTrue(results.isEmpty());
    }

    @Test
    @DisplayName("Tag query containing only '#' character returns empty list")
    void search_tagQueryOnlyHash_returnsEmpty() {
        List<Note> results = searchEngine.search(sampleNotes, "#");
        assertTrue(results.isEmpty());
    }

    @Test
    @DisplayName("Keyword query matches note titles case-insensitively")
    void search_keywordMatchingTitle_returnsMatchingNotes() {
        List<Note> results = searchEngine.search(sampleNotes, "architecture");
        assertEquals(1, results.size());
        assertEquals(note2, results.get(0));

        List<Note> caseInsensitiveResults = searchEngine.search(sampleNotes, "MEETING");
        assertEquals(1, caseInsensitiveResults.size());
        assertEquals(note1, caseInsensitiveResults.get(0));
    }

    @Test
    @DisplayName("Keyword query matches note content case-insensitively")
    void search_keywordMatchingContent_returnsMatchingNotes() {
        List<Note> results = searchEngine.search(sampleNotes, "coffee");
        assertEquals(1, results.size());
        assertEquals(note3, results.get(0));
    }

    @Test
    @DisplayName("Keyword query matches tags as substrings when not using '#' prefix")
    void search_keywordMatchingTagSubstring_returnsMatchingNotes() {
        // Without '#' prefix, "ops" matches the "devops" tag via
        // tag.toLowerCase().contains(keyword)
        List<Note> results = searchEngine.search(sampleNotes, "ops");
        assertEquals(1, results.size());
        assertEquals(note1, results.get(0));
    }

    @Test
    @DisplayName("Trims whitespace surrounding keyword queries")
    void search_queryWithWhitespace_isTrimmed() {
        List<Note> results = searchEngine.search(sampleNotes, "   tea   ");
        assertEquals(1, results.size());
        assertEquals(note3, results.get(0));
    }

    @Test
    @DisplayName("Returns empty list when keyword matches no title, content, or tag")
    void search_unmatchedKeyword_returnsEmptyList() {
        List<Note> results = searchEngine.search(sampleNotes, "nonexistent");
        assertTrue(results.isEmpty());
    }

    @Test
    @DisplayName("Returns an unmodifiable list of notes")
    void search_resultList_isUnmodifiable() {
        List<Note> results = searchEngine.search(sampleNotes, "tea");
        Note dummyNote = new Note("note-4", "Dummy Content");

        assertThrows(UnsupportedOperationException.class, () -> results.add(dummyNote));
    }
}