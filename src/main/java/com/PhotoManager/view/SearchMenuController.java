package com.PhotoManager.view;

import com.PhotoManager.GUInterface;
import com.PhotoManager.model.Directory;
import com.PhotoManager.model.Image;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.io.File;
import java.util.HashSet;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SearchMenuController {
    private GUInterface mainApp;
    private Logger logger = Logger.getLogger(SearchMenuController.class.getName());

    @FXML
    private Button btnOpen;

    @FXML
    private Button btnBack;

    @FXML
    private ListView<Image> lvImagesFound;

    private String tag;

    /**
     * Initialize this SearchMenuController and add functions to attributes.
     */
    @FXML
    private void initialize() {
        lvImagesFound.setCellFactory(param -> new ListCell<Image>() {
            @Override
            protected void updateItem(Image image, boolean empty) {
                super.updateItem(image, empty);

                if (empty || image == null || image.getFilePath() == null) {
                    setText(null);
                } else {
                    setText(image.getFilePath().getAbsolutePath());
                }
            }
        });

    }

    /**
     * Handles functionality for the Open button press.
     */
    @FXML
    private void handleOpen() {
        Image selectedImage = lvImagesFound.getSelectionModel().getSelectedItem();
        if (selectedImage != null) {
            Directory containingDirectory = mainApp.getController().getRootDirectory().getDirectory(
                    new File(selectedImage.getFilePath().getAbsolutePath().substring(0, selectedImage.getFilePath().
                            getAbsolutePath().lastIndexOf(File.separator))));
            mainApp.showDirectoryMenu(containingDirectory);
            mainApp.showImageMenu(selectedImage);
        }
    }

    /**
     * Handles functionality for the Back button press.
     */
    @FXML
    private void handleBack() {
        mainApp.showTagMenu();
    }

    /**
     * Sets the mainApp for this controller.
     *
     * @param mainApp The mainApp to be used to set this controllers mainApp.
     */
    public void setMainApp(GUInterface mainApp) {
        this.mainApp = mainApp;
    }

    /**
     * Returns the searched tag.
     *
     * @return The searched tag.
     */
    public String getTag() {
        return tag;
    }

    /**
     * Sets the tag to be searched.
     *
     * @param tag The tag to be searched for.
     */
    public void setTag(String tag) {
        this.tag = tag;
        HashSet<Image> images = mainApp.getController().findImagesByTag(tag, mainApp.getController()
                .getCurrentDirectory());

        if (images == null) {
            lvImagesFound.setItems(null);
        } else {
            logger.log(Level.FINE, images.toString());
            logger.addHandler(new ConsoleHandler());
            lvImagesFound.setItems(FXCollections.observableArrayList(images));
        }

    }
}
