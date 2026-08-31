package com.notebook.ui;

import com.notebook.model.Note;
import com.notebook.viewmodel.MainViewModel;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class NavigationPane extends VBox {
    private final MainViewModel viewModel;
    private final ListView<Note> noteListView;

    public NavigationPane(MainViewModel viewModel) {
        this.viewModel = viewModel;
        this.noteListView = new ListView<>();
        initLayout();
        bindViewModel();
    }

    private void initLayout() {
        setPadding(new Insets(8));
        setSpacing(8);

        TextField searchField = new TextField();
        searchField.setPromptText("Search title, text, or #tag...");
        searchField.textProperty().bindBidirectional(viewModel.searchQueryProperty());

        Button newBtn = new Button("+ Note");
        Button deleteBtn = new Button("Delete");
        newBtn.setMaxWidth(Double.MAX_VALUE);
        deleteBtn.setMaxWidth(Double.MAX_VALUE);

        newBtn.setOnAction(e -> promptNewNote());
        deleteBtn.setOnAction(e -> viewModel.deleteCurrentNote());

        HBox actions = new HBox(8, newBtn, deleteBtn);
        HBox.setHgrow(newBtn, Priority.ALWAYS);
        HBox.setHgrow(deleteBtn, Priority.ALWAYS);

        noteListView.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(Note item, boolean empty) {
                super.updateItem(item, empty);
                setText((empty || item == null) ? null : item.getTitle());
            }
        });

        VBox.setVgrow(noteListView, Priority.ALWAYS);
        getChildren().addAll(searchField, actions, noteListView);
    }

    private void bindViewModel() {
        noteListView.setItems(viewModel.getDisplayedNotes());

        noteListView.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null && !newVal.equals(viewModel.activeNoteProperty().get())) {
                viewModel.activeNoteProperty().set(newVal);
            }
        });

        viewModel.activeNoteProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null && !newVal.equals(noteListView.getSelectionModel().getSelectedItem())) {
                noteListView.getSelectionModel().select(newVal);
            }
        });
    }

    private void promptNewNote() {
        TextInputDialog dialog = new TextInputDialog("Untitled Note");
        dialog.setHeaderText(null);
        dialog.setTitle("New Note");
        dialog.setContentText("Title:");
        dialog.showAndWait().ifPresent(viewModel::createNewNote);
    }
}