package com.notebook;

import javafx.animation.PauseTransition;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;

public class MainApp extends Application {

    private final Parser parser = Parser.builder().build();
    private final HtmlRenderer renderer = HtmlRenderer.builder().build();

    @Override
    public void start(Stage stage) {
        // Left Pane: Navigation stub
        VBox navPane = new VBox(8);
        navPane.setPadding(new Insets(10));
        Label navTitle = new Label("Notes");
        navTitle.setStyle("-fx-font-weight: bold;");
        ListView<String> noteList = new ListView<>(
                FXCollections.observableArrayList("Welcome.md", "Syntax_Demo.md", "Tasks.md"));
        VBox.setVgrow(noteList, Priority.ALWAYS);
        navPane.getChildren().addAll(navTitle, noteList);

        // Center Pane: Editor
        TextArea editor = new TextArea();
        editor.setWrapText(true);
        editor.setText("# Smoke Test\n\nEdit this **markdown** to test the *live debounce* preview.");

        // Right Pane: WebView
        WebView webView = new WebView();

        // Debounced preview pipeline (~250ms)
        PauseTransition debounce = new PauseTransition(Duration.millis(250));
        debounce.setOnFinished(e -> updatePreview(webView, editor.getText()));

        editor.textProperty().addListener((obs, oldText, newText) -> debounce.playFromStart());

        // Initial render
        updatePreview(webView, editor.getText());

        // Assembly
        SplitPane splitPane = new SplitPane();
        splitPane.getItems().addAll(navPane, editor, webView);
        splitPane.setDividerPositions(0.20, 0.60);

        BorderPane root = new BorderPane(splitPane);
        Scene scene = new Scene(root, 1100, 700);

        stage.setTitle("JavaFX Spike Verification");
        stage.setScene(scene);
        stage.show();
    }

    private void updatePreview(WebView webView, String markdown) {
        String body = renderer.render(parser.parse(markdown != null ? markdown : ""));
        String template = """
                <!DOCTYPE html>
                <html>
            <head>
                <style>
                    body { font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif; padding: 16px; font-size: 14px; line-height: 1.6; }
                    code { background: #f0f0f0; padding: 2px 4px; border-radius: 3px; font-family: monospace; }
                    pre { background: #f4f4f4; padding: 10px; border-radius: 4px; overflow-x: auto; }
                    table { border-collapse: collapse; width: 100%; margin: 12px 0; }
                    th, td { border: 1px solid #ddd; padding: 6px 12px; text-align: left; }
                </style>
            </head>
            <body>{{BODY}}</body>
            </html>
            """;

        String html = template.replace("{{BODY}}", body != null ? body : "");
        webView.getEngine().loadContent(html);
    }

    public static void main(String[] args) {
        launch(args);
    }
}