package com.notebook.logic;

public record TextStatistics(
        int wordCount,
        int charCount,
        int charCountNoSpaces,
        int lineCount,
        int readingTimeMinutes) {
    private static final int WORDS_PER_MINUTE = 200;

    public static TextStatistics compute(String text) {
        if (text == null || text.isEmpty()) {
            return new TextStatistics(0, 0, 0, 0, 0);
        }

        int charCount = text.length();
        int charCountNoSpaces = text.replaceAll("\\s", "").length();

        String trimmed = text.trim();
        int wordCount = trimmed.isEmpty() ? 0 : trimmed.split("\\s+").length;

        // Split by all common line terminators (\r\n, \n, \r)
        int lineCount = text.split("\r\n|\r|\n", -1).length;

        int readingTime = wordCount == 0 ? 0 : (int) Math.ceil((double) wordCount / WORDS_PER_MINUTE);

        return new TextStatistics(wordCount, charCount, charCountNoSpaces, lineCount, readingTime);
    }
}