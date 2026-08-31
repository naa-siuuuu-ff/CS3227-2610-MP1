package com.notebook.ui;

import com.notebook.viewmodel.MainViewModel;
import javafx.scene.layout.StackPane;
import javafx.scene.web.WebView;

public class PreviewPane extends StackPane {
    private final WebView webView;

    public PreviewPane(MainViewModel viewModel) {
        this.webView = new WebView();
        getChildren().add(webView);

        viewModel.htmlPreviewProperty().addListener((obs, oldHtml, newHtml) -> {
            renderHtml(newHtml);
        });

        renderHtml(viewModel.htmlPreviewProperty().get());
    }

    private void renderHtml(String html) {
        String wrapped = """
                <!DOCTYPE html>
                <html>
                <head>
                    <style>
                        body { font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif; padding: 16px; font-size: 14px; line-height: 1.6; }
                        pre { background: #f4f4f4; padding: 8px; border-radius: 4px; overflow-x: auto; }
                        code { font-family: monospace; }
                        table { border-collapse: collapse; width: 100%; margin: 12px 0; }
                        th, td { border: 1px solid #ddd; padding: 6px 12px; text-align: left; }
                        th { background-color: #f8f8f8; }
                    </style>
                </head>
                <body>%s</body>
                </html>
                """
                .formatted(html != null ? html : "");

        webView.getEngine().loadContent(wrapped);
    }
}