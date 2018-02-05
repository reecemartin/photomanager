
package com.PhotoManager.view;

import com.PhotoManager.GUInterface;
import javafx.collections.FXCollections;
import javafx.collections.ObservableSet;
import javafx.collections.SetChangeListener;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TagMenuController {
    private GUInterface mainApp;
    private Logger logger = Logger.getLogger(DirectoryMenuController.class.getName());

    @FXML
    private ListView<String> lvTags;

    @FXML
    private Button btnBack;

    @FXML
    private Button btnSearch;

    @FXML
    private Button btnAddTag;

    @FXML
    private Button btnRemoveTag;

    public TagMenuController() {
    }

    /**
     * Initialize this TagMenuController and add functions to attributes.
     */
    @FXML
    private void initialize() {
        lvTags.setOnMouseClicked(event -> {
            btnRemoveTag.setDisable(false);
            btnSearch.setDisable(false);
        });
    }

    /**
     * Displays all the available tags in a listview.
     */
    private void displayTags() {
        ObservableSet<String> set = FXCollections.observableSet(mainApp.getController()
                .getAvailableTags());
        logger.log(Level.FINE, set.toString());
        logger.addHandler(new ConsoleHandler());
        lvTags.getItems().addAll(set);
        logger.log(Level.FINE, lvTags.getItems().toString());
        logger.addHandler(new ConsoleHandler());
        set.addListener((SetChangeListener.Change<? extends String> c) -> {
            if (c.wasAdded()) {
                lvTags.getItems().add(c.getElementAdded());
            }
            if (c.wasRemoved()) {
                lvTags.getItems().remove(c.getElementRemoved());
            }
        });
    }

    /**
     * Sets the mainApp for this controller.
     *
     * @param mainApp The mainApp to be used to set this controllers mainApp.
     */
    public void setMainApp(GUInterface mainApp) {
        this.mainApp = mainApp;
        displayTags();
    }

    /**
     * Handles functionality for the Back button press.
     */
    @FXML
    public void handleBack() {
        mainApp.showDirectoryMenu(mainApp.getController().getCurrentDirectory());
    }

    /**
     * Handles functionality for the Search button press.
     */
    @FXML
    public void handleSearch() {
        String tag = lvTags.getSelectionModel().getSelectedItem();
        if (tag != null) {
            mainApp.showSearchMenu(tag);
        }
    }

    /**
     * Handles functionality for the Remove Tag button press.
     */
    @FXML
    public void handleRemoveTag() {
        String tag = lvTags.getSelectionModel().getSelectedItem();
        if (tag != null) {
            if (mainApp.showAlertConfirmation(new String[]{
                    "Remove Tag", "Are You Sure?", "This tag will be removed from your available tags"
            })) {
                mainApp.getController().removeTag(tag);
                mainApp.showTagMenu();
            }
        }
    }

    /**
     * Handles functionality for the Add Tag button press.
     */
    @FXML
    public void handleAddTag() {
        String newTag = mainApp.showDialogTextInput(new String[]{
                "Add Tag", "Enter a Tag to Add", "New Tag: "
        });
        try {
            mainApp.getController().addTag(newTag);
            mainApp.showAlertInformation(new String[]{
                    "Information", "New Tag Added", "The tag " + newTag + " has been added to the system."
            });
            mainApp.showTagMenu();
        } catch (IllegalArgumentException e) {
            mainApp.showAlertError(new String[]{
                    "Error", "Tag Failed to Add", "The tag " + newTag + " cannot be added. Try another tag."
            });
        }
    }

}