package com.notebook.logic;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TextFormatterTest {

    @Test
    @DisplayName("Wraps active selection with prefix and suffix")
    void wrapSelection_activeSelection_wrapsSelectedText() {
        String text = "Hello world!";
        // Select "world" (index 6 to 11)
        TextFormatter.FormatResult result = TextFormatter.wrapSelection(text, 6, 11, "**", "**");

        assertEquals("Hello **world**!", result.newText());
        assertEquals(6, result.newSelectionStart());
        assertEquals(15, result.newSelectionEnd());
    }

    @Test
    @DisplayName("Inserts markers and sets cursor between them when selection is empty")
    void wrapSelection_emptySelection_placesCursorInBetween() {
        String text = "Note: ";
        TextFormatter.FormatResult result = TextFormatter.wrapSelection(text, 6, 6, "`", "`");

        assertEquals("Note: ``", result.newText());
        assertEquals(7, result.newSelectionStart());
        assertEquals(7, result.newSelectionEnd());
    }

    @Test
    @DisplayName("Handles null input text safely")
    void wrapSelection_nullText_treatedAsEmpty() {
        TextFormatter.FormatResult result = TextFormatter.wrapSelection(null, 0, 0, "*", "*");

        assertEquals("**", result.newText());
        assertEquals(1, result.newSelectionStart());
        assertEquals(1, result.newSelectionEnd());
    }

    @Test
    @DisplayName("Clamps out-of-bounds selection indices")
    void wrapSelection_outOfBoundsIndices_clampedSafely() {
        String text = "Sample";
        TextFormatter.FormatResult result = TextFormatter.wrapSelection(text, -5, 20, "_", "_");

        assertEquals("_Sample_", result.newText());
        assertEquals(0, result.newSelectionStart());
        assertEquals(8, result.newSelectionEnd());
    }

    @Test
    @DisplayName("Prefixes entire current line with markdown token")
    void prefixLines_singleLine_addsPrefixToLineStart() {
        String text = "First line\nSecond line\nThird line";
        // Cursor placed inside "Second line" (index 15)
        TextFormatter.FormatResult result = TextFormatter.prefixLines(text, 15, 15, "> ");

        assertEquals("First line\n> Second line\nThird line", result.newText());
        assertEquals(11, result.newSelectionStart());
        assertEquals(24, result.newSelectionEnd());
    }

    @Test
    @DisplayName("Prefixes multiple lines across a multi-line selection")
    void prefixLines_multiLineSelection_prefixesAllSelectedLines() {
        String text = "Item A\nItem B\nItem C";
        // Select across Item A and Item B
        TextFormatter.FormatResult result = TextFormatter.prefixLines(text, 2, 9, "- [ ] ");

        assertEquals("- [ ] Item A\n- [ ] Item B\nItem C", result.newText());
    }
}