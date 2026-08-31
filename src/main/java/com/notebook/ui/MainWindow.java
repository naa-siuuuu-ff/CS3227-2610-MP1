package com.notebook.ui;

import com.notebook.viewmodel.MainViewModel;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;

public class MainWindow extends BorderPane {

    public MainWindow(MainViewModel viewModel) {
        NavigationPane navigationPane = new NavigationPane(viewModel);

        TextArea editor = new TextArea();
        editor.setWrapText(true);
        editor.textProperty().bindBidirectional(viewModel.editorContentProperty());

        PreviewPane previewPane = new PreviewPane(viewModel);

        Label statsLabel = new Label("Words: 0 | Chars: 0 | Lines: 0 | Read: 0 min");
        statsLabel.setStyle("-fx-text-fill: #64748b; -fx-font-size: 11px; -fx-padding: 4px 12px;");

        viewModel.statisticsProperty().addListener((obs, oldStats, newStats) -> {
            if (newStats != null) {
                statsLabel.setText(String.format(
                        "Words: %d | Chars: %d | Lines: %d | Est. Reading: %d min",
                        newStats.wordCount(),
                        newStats.charCount(),
                        newStats.lineCount(),
                        newStats.readingTimeMinutes()));
            }
        });

        setBottom(statsLabel);

        SplitPane splitPane = new SplitPane();
        splitPane.getItems().addAll(navigationPane, editor, previewPane);
        splitPane.setDividerPositions(0.25, 0.62);

        setCenter(splitPane);
    }
}