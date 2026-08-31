package com.notebook;

import com.notebook.logic.NotebookManager;
import com.notebook.logic.NoteRepository;
import com.notebook.logic.MarkdownRenderer;
import com.notebook.logic.SearchEngine;
import com.notebook.render.CommonMarkRenderer;
import com.notebook.storage.FileSystemStorage;
import com.notebook.ui.MainWindow;
import com.notebook.viewmodel.MainViewModel;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.nio.file.Path;
import java.nio.file.Paths;

public class MainApp extends Application {

    private static final Path DEFAULT_STORAGE_PATH = Paths.get(
            System.getProperty("user.home"), ".markdown-notebook", "notes");

    private MainViewModel viewModel;

    @Override
    public void start(Stage primaryStage) {
        // Outbound Adapters
        NoteRepository repository = new FileSystemStorage(DEFAULT_STORAGE_PATH);
        MarkdownRenderer renderer = new CommonMarkRenderer();
        SearchEngine searchEngine = new SearchEngine();

        // Core Domain Orchestrator
        NotebookManager manager = new NotebookManager(repository, renderer, searchEngine);

        // ViewModel (Presentation Port Adapter)
        this.viewModel = new MainViewModel(manager);

        // UI Assembly
        MainWindow mainWindow = new MainWindow(viewModel);
        Scene scene = new Scene(mainWindow, 1100, 720);

        primaryStage.setTitle("Markdown Desktop Notebook");
        primaryStage.setMinWidth(800);
        primaryStage.setMinHeight(500);
        primaryStage.setScene(scene);

        primaryStage.setOnCloseRequest(event -> flushUnsavedChanges());
        primaryStage.show();
    }

    @Override
    public void stop() {
        flushUnsavedChanges();
    }

    private void flushUnsavedChanges() {
        if (viewModel != null && viewModel.isDirtyProperty().get()) {
            viewModel.saveCurrentNote();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}