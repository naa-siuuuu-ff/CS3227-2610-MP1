package com.notebook.logic;

public final class TextFormatter {

    private TextFormatter() {
        // Utility class
    }

    public record FormatResult(String newText, int newSelectionStart, int newSelectionEnd) {
    }

    public static FormatResult wrapSelection(String text, int start, int end, String prefix, String suffix) {
        String safeText = text == null ? "" : text;
        int safeStart = Math.max(0, Math.min(start, safeText.length()));
        int safeEnd = Math.max(safeStart, Math.min(end, safeText.length()));

        String before = safeText.substring(0, safeStart);
        String selected = safeText.substring(safeStart, safeEnd);
        String after = safeText.substring(safeEnd);

        if (selected.isEmpty()) {
            // No selection: insert prefix and suffix, place cursor between them
            String replacement = prefix + suffix;
            String newText = before + replacement + after;
            int cursorPosition = safeStart + prefix.length();
            return new FormatResult(newText, cursorPosition, cursorPosition);
        }

        // Selection exists: wrap it and select the wrapped content
        String replacement = prefix + selected + suffix;
        String newText = before + replacement + after;
        return new FormatResult(newText, safeStart, safeStart + replacement.length());
    }

    public static FormatResult prefixLines(String text, int start, int end, String linePrefix) {
        String safeText = text == null ? "" : text;
        int safeStart = Math.max(0, Math.min(start, safeText.length()));
        int safeEnd = Math.max(safeStart, Math.min(end, safeText.length()));

        // Find the start of the first selected line
        int lineStart = safeText.lastIndexOf('\n', safeStart - 1);
        lineStart = (lineStart == -1) ? 0 : lineStart + 1;

        // Find the end of the last selected line
        int lineEnd = safeText.indexOf('\n', safeEnd);
        lineEnd = (lineEnd == -1) ? safeText.length() : lineEnd;

        String before = safeText.substring(0, lineStart);
        String targetSegment = safeText.substring(lineStart, lineEnd);
        String after = safeText.substring(lineEnd);

        String[] lines = targetSegment.split("\n", -1);
        StringBuilder formatted = new StringBuilder();
        for (int i = 0; i < lines.length; i++) {
            formatted.append(linePrefix).append(lines[i]);
            if (i < lines.length - 1) {
                formatted.append("\n");
            }
        }

        String newText = before + formatted + after;
        return new FormatResult(newText, lineStart, lineStart + formatted.length());
    }
}