package com.notebook.render;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CommonMarkRendererTest {

    private CommonMarkRenderer renderer;

    @BeforeEach
    void setUp() {
        renderer = new CommonMarkRenderer();
    }

    @Test
    void renderToHtml_standardMarkdown_rendersHeadersAndParagraphs() {
        String input = "# Main Title\n\nThis is a paragraph with **bold** text.";
        String html = renderer.renderToHtml(input);

        assertTrue(html.contains("<h1>Main Title</h1>"));
        assertTrue(html.contains("<strong>bold</strong>"));
    }

    @Test
    void renderToHtml_gfmTableExtension_rendersHtmlTable() {
        String tableMarkdown = """
                | Header 1 | Header 2 |
                | -------- | -------- |
                | Cell A   | Cell B   |
                """;
        String html = renderer.renderToHtml(tableMarkdown);

        assertTrue(html.contains("<table>"));
        assertTrue(html.contains("<th>Header 1</th>"));
        assertTrue(html.contains("<td>Cell A</td>"));
    }

    @Test
    void renderToHtml_emptyInput_returnsEmptyString() {
        assertEquals("", renderer.renderToHtml(""));
        assertEquals("", renderer.renderToHtml(null));
    }
}