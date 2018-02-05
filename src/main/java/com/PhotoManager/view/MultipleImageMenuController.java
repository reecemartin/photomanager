package com.PhotoManager.view;

import com.PhotoManager.GUInterface;
import com.PhotoManager.model.Image;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class MultipleImageMenuController extends ImageMenuController {
    @FXML
    private Button btnOpen;

    @FXML
    private ListView<Image> lvImages;

    private List<Image> listOfImages;

    @FXML
    protected void initialize() {
        super.initialize();
        lvImages.setCellFactory(param -> new ListCell<Image>() {
            @Override
            protected void updateItem(Image item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || item == null || item.getImageName() == null) {
                    setText(null);
                } else {
                    setText(item.getFilePath().getAbsolutePath());
                }
            }
        });
        lvImages.setOnMouseClicked(event -> btnOpen.setDisable(false));
    }

    /**
     * Sets the mainApp for this controller.
     *
     * @param mainApp The mainApp to be used to set this controllers mainApp.
     */
    @Override
    public void setMainApp(GUInterface mainApp) {
        this.mainApp = mainApp;
        displayTags();
    }

    /**
     * Handles functionality for the open button press.
     */
    @FXML
    private void handleOpen() {
        Image selected = lvImages.getSelectionModel().getSelectedItem();
        if (selected != null) {
            mainApp.showImageMenu(selected);
        }
    }

    /**
     * Takes in a list of images and initializes this multipleImageMenuController as is.
     *
     * @param listOfImages a list of images to display in this menu
     */
    public void setImages(List<Image> listOfImages) {
        if (listOfImages.size() > 0) {
            lvImages.setItems(FXCollections.observableList(listOfImages));
            this.listOfImages = listOfImages;
            displayAddedTags();
            getLblPath().setText(mainApp.getController().getCurrentDirectory().getFile().getAbsolutePath());
        }
    }

    /**
     * Displays the tags added to all images in listOfImages in the tags listView.
     */
    @Override
    protected void displayAddedTags() {
        HashSet<String> addedTags = this.mainApp.
                getController().
                getCommonTags(
                        new ArrayList<>(listOfImages));
        for (String tag : addedTags) {
            if (getLvTags().getItems().contains(tag)) {
                getLvTags().getItems().set(getLvTags().getItems().indexOf(tag), "(Added) " + tag);
            }
        }
    }

    /**
     * Adds the given tag to every menu in listOfImages.
     *
     * @param newTag The tag to be added to an image.
     */
    @Override
    protected void addTag(String newTag) {
        boolean added = true;
        for (Image image : listOfImages) {
            if (!mainApp.getController().addTag(image, newTag)) {
                added = false;
            }
        }

        if (added) {
            mainApp.showAlertInformation(new String[]{
                    "Information", "Tag Added", "The tag " + newTag + " has been added to all images."
            });

            getTxtNewTag().setText("");

            handleBack();
            mainApp.showMultipleImageMenu(listOfImages);
        } else {
            mainApp.showAlertError(new String[]{
                    "Error", "Tag Failed to Add", "The tag " + newTag + " cannot be added to some images. Try another tag."
            });

            getTxtNewTag().setText("");
        }
    }

    /**
     * Removes the given tag from every image in listOfImages.
     *
     * @param tag The tag to be removed.
     */
    @Override
    protected void removeTag(String tag) {
        boolean removed = true;
        for (Image image : listOfImages) {
            if (!mainApp.getController().removeTag(tag, image)) {
                removed = false;
            }
        }
        if (removed) {
            mainApp.showAlertInformation(new String[]{
                    "Tag Removed", "Tag Successfully Removed", "The tag " + tag + " has been removed from all image. "
            });
            handleBack();
            mainApp.showMultipleImageMenu(listOfImages);
        } else {
            mainApp.showAlertError(new String[]{
                    "Tag Remove Failed", "Tag Failed to be Removed", "The tag " + tag + " cannot be removed from some images. Try another tag."
            });

            getTxtNewTag().setText("");
        }
    }

    /**
     * Handles functionality for the back button press
     */
    @Override
    protected void handleBack() {
        mainApp.showDirectoryMenu(mainApp.getController().getCurrentDirectory());
    }
}
