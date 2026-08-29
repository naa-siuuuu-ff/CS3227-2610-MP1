package com.notebook;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

public class MainApp extends Application {

    @Override
    public void start(Stage primaryStage) {
        WebView webView = new WebView();

        // Spike test: render basic HTML to confirm WebKit/JavaFX Web works
        String testHtml = """
                <!DOCTYPE html>
                <html>
                <body style="font-family: sans-serif; padding: 20px;">
                    <h1 style="color: #2b579a;">Spike: JavaFX WebView Active</h1>
                    <p>If you can read this, your JavaFX and WebKit native dependencies are functioning correctly.</p>
                </body>
                </html>
                """;

        webView.getEngine().loadContent(testHtml);

        StackPane root = new StackPane(webView);
        Scene scene = new Scene(root, 600, 400);

        primaryStage.setTitle("MP1 De-risking Spike");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}