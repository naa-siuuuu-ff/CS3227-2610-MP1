package com.notebook.render;

import com.notebook.logic.MarkdownRenderer;
import org.commonmark.Extension;
import org.commonmark.ext.gfm.strikethrough.StrikethroughExtension;
import org.commonmark.ext.gfm.tables.TablesExtension;
import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;

import java.util.List;

//markdown rendering`
public class CommonMarkRenderer implements MarkdownRenderer {
    private final Parser parser;
    private final HtmlRenderer renderer;

    public CommonMarkRenderer() {
        List<Extension> extensions = List.of(
                TablesExtension.create(),
                StrikethroughExtension.create());
        this.parser = Parser.builder().extensions(extensions).build();
        this.renderer = HtmlRenderer.builder().extensions(extensions).build();
    }

    @Override
    public String renderToHtml(String markdownSource) {
        if (markdownSource == null || markdownSource.isBlank()) {
            return "";
        }
        Node document = parser.parse(markdownSource);
        return renderer.render(document);
    }
}