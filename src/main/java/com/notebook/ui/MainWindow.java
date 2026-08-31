package com.notebook.ui;

import com.notebook.viewmodel.MainViewModel;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;

public class MainWindow extends BorderPane {

    public MainWindow(MainViewModel viewModel) {
        NavigationPane navigationPane = new NavigationPane(viewModel);

        TextArea editor = new TextArea();
        editor.setWrapText(true);
        editor.textProperty().bindBidirectional(viewModel.editorContentProperty());

        PreviewPane previewPane = new PreviewPane(viewModel);

        SplitPane splitPane = new SplitPane();
        splitPane.getItems().addAll(navigationPane, editor, previewPane);
        splitPane.setDividerPositions(0.20, 0.60);

        setCenter(splitPane);
    }
}