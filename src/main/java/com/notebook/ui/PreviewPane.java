package com.notebook.ui;

import com.notebook.viewmodel.MainViewModel;
import javafx.scene.layout.StackPane;
import javafx.scene.web.WebView;

public class PreviewPane extends StackPane {
    private final WebView webView;

    private static final String HTML_SHELL = """
            <!DOCTYPE html>
            <html>
            <head>
                <style>
                    body { font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif; padding: 16px; font-size: 14px; line-height: 1.6; }
                code { background: #f0f0f0; padding: 2px 4px; border-radius: 3px; font-family: monospace; }
                pre { background: #f4f4f4; padding: 10px; border-radius: 4px; overflow-x: auto; }
                table { border-collapse: collapse; width: 100%; margin: 12px 0; }
                th, td { border: 1px solid #ddd; padding: 6px 12px; text-align: left; }
                th { background-color: #f8f8f8; }
            </style>
        </head>
        <body>{{BODY}}</body>
        </html>
        """;

    public PreviewPane(MainViewModel viewModel) {
        this.webView = new WebView();
        getChildren().add(webView);

        viewModel.htmlPreviewProperty().addListener((obs, oldVal, newVal) -> renderHtml(newVal));
        renderHtml(viewModel.htmlPreviewProperty().get());
    }

    private void renderHtml(String rawHtml) {
        String content = HTML_SHELL.replace("{{BODY}}", rawHtml != null ? rawHtml : "");
        webView.getEngine().loadContent(content);
    }
}