package com.notebook.ui;

import com.notebook.viewmodel.MainViewModel;
import javafx.animation.AnimationTimer;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.web.WebView;

import java.util.Locale;

public class PreviewPane extends StackPane {
    private static final double FRICTION = 0.95;
    private static final double VELOCITY_STOP_THRESHOLD = 0.2;

    private final WebView webView;
    private double velocityY = 0.0;
    private double pendingDeltaY = 0.0;
    private AnimationTimer momentumTimer;

    private static final String HTML_SHELL = """
            <!DOCTYPE html>
            <html lang="en">
            <head>
                <meta charset="UTF-8">
                <style>
                    * {
                        box-sizing: border-box;
                    }
                    html, body {
                        scroll-behavior: auto !important;
                    }
                    body {
                        font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Helvetica, Arial, sans-serif;
                        font-size: 14px;
                        line-height: 1.6;
                        color: #24292f;
                        background-color: #ffffff;
                        padding: 24px 32px;
                        margin: 0;
                    }
                    h1, h2, h3, h4, h5, h6 {
                        margin-top: 24px;
                        margin-bottom: 16px;
                        font-weight: 600;
                        line-height: 1.25;
                        color: #1f2328;
                    }
                    h1 {
                        font-size: 1.8em;
                        padding-bottom: 0.3em;
                        border-bottom: 1px solid #d0d7de;
                    }
                    h2 {
                        font-size: 1.4em;
                        padding-bottom: 0.3em;
                        border-bottom: 1px solid #d0d7de;
                    }
                    h3 { font-size: 1.2em; }
                    h4 { font-size: 1.0em; }
                    p, ul, ol, dl, table, pre {
                        margin-top: 0;
                        margin-bottom: 16px;
                    }
                    ul, ol {
                        padding-left: 2em;
                    }
                    li + li {
                        margin-top: 0.25em;
                    }
                    blockquote {
                        margin: 0 0 16px 0;
                        padding: 0 1em;
                        color: #57606a;
                        border-left: 0.25em solid #d0d7de;
                    }
                    hr {
                        height: 0.25em;
                        padding: 0;
                        margin: 24px 0;
                        background-color: #d0d7de;
                        border: 0;
                    }
                    code {
                        font-family: "JetBrains Mono", SFMono-Regular, Consolas, Menlo, monospace;
                        font-size: 85%;
                        background-color: #eff1f3;
                        padding: 0.2em 0.4em;
                        border-radius: 4px;
                    }
                    pre {
                        font-family: "JetBrains Mono", SFMono-Regular, Consolas, Menlo, monospace;
                        font-size: 85%;
                        padding: 16px;
                        overflow: auto;
                        line-height: 1.45;
                        background-color: #f6f8fa;
                        border-radius: 6px;
                    }
                    pre code {
                        background-color: transparent;
                        padding: 0;
                        font-size: 100%;
                    }
                    table {
                        border-spacing: 0;
                        border-collapse: collapse;
                        width: 100%;
                        margin-bottom: 16px;
                    }
                    table th, table td {
                        padding: 6px 13px;
                        border: 1px solid #d0d7de;
                    }
                    table th {
                        font-weight: 600;
                        background-color: #f6f8fa;
                    }
                    table tr:nth-child(2n) {
                        background-color: #f6f8fa;
                    }
                    a {
                        color: #0969da;
                        text-decoration: none;
                    }
                    a:hover {
                        text-decoration: underline;
                    }
                </style>
            </head>
            <body>{{BODY}}</body>
            </html>
            """;

    public PreviewPane(MainViewModel viewModel) {
        this.webView = new WebView();
        configurePhysicsScrolling();
        getChildren().add(webView);

        viewModel.htmlPreviewProperty().addListener((obs, oldVal, newVal) -> renderHtml(newVal));
        renderHtml(viewModel.htmlPreviewProperty().get());
    }

    private void configurePhysicsScrolling() {
        momentumTimer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                // Apply active user scroll events first
                if (pendingDeltaY != 0.0) {
                    executeScroll(-pendingDeltaY);
                    pendingDeltaY = 0.0;
                    return;
                }

                // Apply decaying momentum when user lifts fingers
                if (Math.abs(velocityY) > VELOCITY_STOP_THRESHOLD) {
                    executeScroll(-velocityY);
                    velocityY *= FRICTION;
                } else {
                    velocityY = 0.0;
                    stop();
                }
            }
        };

        webView.addEventFilter(ScrollEvent.SCROLL, event -> {
            // Drop synthetic OS inertia completely; we handle physics locally
            if (event.isInertia()) {
                event.consume();
                return;
            }

            double deltaY = event.getDeltaY();
            if (deltaY != 0.0) {
                pendingDeltaY += deltaY;
                velocityY = deltaY;
                momentumTimer.start();
            }

            event.consume();
        });
    }

    private void executeScroll(double pixels) {
        String script = String.format(Locale.US, "window.scrollBy(0, %.2f);", pixels);
        webView.getEngine().executeScript(script);
    }

    private void renderHtml(String rawHtml) {
        String content = HTML_SHELL.replace("{{BODY}}", rawHtml != null ? rawHtml : "");
        webView.getEngine().loadContent(content);
    }
}