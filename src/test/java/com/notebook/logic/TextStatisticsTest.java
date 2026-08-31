package com.notebook.logic;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TextStatisticsTest {

    @ParameterizedTest
    @NullAndEmptySource
    @DisplayName("Returns zeroed statistics for null or empty content")
    void compute_nullOrEmpty_returnsZeroes(String input) {
        TextStatistics stats = TextStatistics.compute(input);

        assertEquals(0, stats.wordCount());
        assertEquals(0, stats.charCount());
        assertEquals(0, stats.charCountNoSpaces());
        assertEquals(0, stats.lineCount());
        assertEquals(0, stats.readingTimeMinutes());
    }

    @ParameterizedTest
    @ValueSource(strings = { "   ", "\t", "\n", " \t \r\n " })
    @DisplayName("Counts whitespace characters but reports zero words")
    void compute_onlyWhitespace_reportsZeroWords(String input) {
        TextStatistics stats = TextStatistics.compute(input);

        assertEquals(0, stats.wordCount());
        assertEquals(input.length(), stats.charCount());
        assertEquals(0, stats.charCountNoSpaces());
        assertEquals(0, stats.readingTimeMinutes());
    }

    @Test
    @DisplayName("Accurately counts words separated by variable whitespace")
    void compute_regularText_countsWordsAndCharacters() {
        String text = "Markdown   desktop\tnotebook application.";
        TextStatistics stats = TextStatistics.compute(text);

        assertEquals(4, stats.wordCount());
        assertEquals(40, stats.charCount());
        assertEquals(35, stats.charCountNoSpaces());
        assertEquals(1, stats.lineCount());
        assertEquals(1, stats.readingTimeMinutes());
    }

    @Test
    @DisplayName("Accurately counts lines across various newline delimiters")
    void compute_multiLineText_countsLinesCorrectly() {
        String unixText = "Line 1\nLine 2\nLine 3";
        assertEquals(3, TextStatistics.compute(unixText).lineCount());

        String windowsText = "Line 1\r\nLine 2\r\nLine 3\r\n";
        assertEquals(4, TextStatistics.compute(windowsText).lineCount());
    }

    @Test
    @DisplayName("Calculates reading time assuming 200 words per minute")
    void compute_readingTime_calculatesCeilingMinutes() {
        // 201 words should round up to 2 minutes
        String longText = ("word ").repeat(201);
        TextStatistics stats = TextStatistics.compute(longText);

        assertEquals(201, stats.wordCount());
        assertEquals(2, stats.readingTimeMinutes());
    }
}