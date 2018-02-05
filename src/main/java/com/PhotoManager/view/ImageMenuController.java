package com.PhotoManager.view;

import com.PhotoManager.GUInterface;
import com.PhotoManager.model.Directory;
import com.PhotoManager.model.Image;
import javafx.collections.FXCollections;
import javafx.collections.ObservableSet;
import javafx.collections.SetChangeListener;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ImageMenuController {
    GUInterface mainApp;
    private Logger logger = Logger.getLogger(ImageMenuController.class.getName());

    @FXML
    private Label lblPath;

    @FXML
    private VBox boxImageActions;

    @FXML
    private AnchorPane paneDisplay;

    @FXML
    private ListView<String> lvTags;

    @FXML
    private Button btnNewTag;

    @FXML
    private Button btnMove;

    @FXML
    private Button btnHistory;

    @FXML
    private Button btnBack;

    @FXML
    private Button btnAddTag;

    @FXML
    private Button btnRemoveTag;

    @FXML
    private TextField txtNewTag;

    private Image image;

    private ImageView imageView;

    public ImageMenuController() {

    }

    /**
     * Initialize this ImageMenuController and add functions to attributes, and set the state of the tag buttons to on.
     */
    @FXML
    protected void initialize() {
        lvTags.setOnMouseClicked(event -> {
            btnAddTag.setDisable(false);
            btnRemoveTag.setDisable(false);
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
     * Sets the current image to a given image.
     *
     * @param newImage The image to set the current image to.
     */
    public void setImage(Image newImage) {
        image = newImage;
        txtNewTag.setText("");

        displayImage();
        if (image != null) {
            if (!image.getOSFilePath().equals(image.getFilePath())) {
                logger.log(Level.FINE, Boolean.toString(image.getOSFilePath().equals(image.getFilePath())));
                logger.addHandler(new ConsoleHandler());

                refreshImage();
            }
            displayAddedTags();
            displaySuggestedTag();
        }
    }

    /**
     * Displays the current image in the imageview window.
     */
    private void displayImage() {
        imageView = new ImageView();
        if (image != null) {
            try {
                javafx.scene.image.Image imageOb = new javafx.scene.image.Image(new
                        FileInputStream(image.getOSFilePath()));
                imageView.setImage(imageOb);
                if (imageOb.getHeight() > imageOb.getWidth())
                    imageView.fitHeightProperty().bind(paneDisplay.heightProperty());
                else
                    imageView.fitWidthProperty().bind(paneDisplay.widthProperty());
                imageView.setPreserveRatio(true);
                paneDisplay.getChildren().add(imageView);
                getLblPath().setText(image.getFilePath().getAbsolutePath());
            } catch (FileNotFoundException ex) {
                ex.printStackTrace();
            }
        } else {
            paneDisplay.getChildren().removeAll();
            getLblPath().setText("Path of Image");
        }
    }

    /**
     * Displays all the available tags in lvTags.
     */
    void displayTags() {
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
     * Displays the suggested tag for this image at the top of lvTags.
     */
    private void displaySuggestedTag() {
        if (!image.getSuggestedTag().equals("") && !image.getTags().contains(image.getSuggestedTag()))
            lvTags.getItems().add(0, "Smart Suggestion: " + image.getSuggestedTag());
    }

    /**
     * Changes the tag names in lvTags to (Added) + tag if it has been added to image.
     */
    protected void displayAddedTags() {
        for (String tag : image.getTags()) {
            if (lvTags.getItems().contains(tag)) {
                lvTags.getItems().set(lvTags.getItems().indexOf(tag), "(Added) " + tag);
            }
        }
        logger.log(Level.FINE, lvTags.getItems().toString());
        logger.addHandler(new ConsoleHandler());
    }

    /**
     * Handles functionality for the Add Tag button press.
     */
    @FXML
    private void handleAddTag() {
        String tag = lvTags.getSelectionModel().getSelectedItem();
        if (tag != null) {
            tag = tag.replace("(Added) ", "");
            tag = tag.replace("Smart Suggestion: ", "");
            addTag(tag);
        }
    }

    /**
     * Handles functionality for the Remove Tag button press.
     */
    @FXML
    private void handleRemoveTag() {
        String tag = lvTags.getSelectionModel().getSelectedItem();
        if (tag != null) {
            tag = tag.replace("(Added) ", "");
            removeTag(tag);
        }
    }

    /**
     * Removes a given tag from an image.
     *
     * @param tag The tag to be removed.
     */
    protected void removeTag(String tag) {
        boolean removed = mainApp.getController().removeTag(tag, image);

        if (removed) {
            mainApp.showAlertInformation(new String[]{
                    "Tag Removed", "Tag Successfully Removed", "The tag " + tag + " has been removed from this image. "
            });
            handleBack();
            mainApp.showImageMenu(image);
        } else {
            mainApp.showAlertError(new String[]{
                    "Tag Remove Failed", "Tag Failed to be Removed", "The tag " + tag + " cannot be removed. Try another tag."
            });

            txtNewTag.setText("");
        }
    }

    /**
     * Handles functionality for the New Tag button press.
     */
    @FXML
    private void handleNewTag() {
        assert txtNewTag.getText() != null;
        String newTag = txtNewTag.getText();
        addTag(newTag);
    }

    /**
     * Handles adding a tag to an image.
     *
     * @param newTag The tag to be added to an image.
     */
    protected void addTag(String newTag) {
        try {
            boolean added = mainApp.getController().addTag(image, newTag);

            if (added) {
                mainApp.showAlertInformation(new String[]{
                        "Information", "Tag Added", "The tag " + newTag + " has been added."
                });

                txtNewTag.setText("");

                handleBack();
                mainApp.showImageMenu(image);
            } else {
                mainApp.showAlertError(new String[]{
                        "Error", "Tag Failed to Add", "The tag " + newTag + " cannot be added. Try another tag."
                });

                txtNewTag.setText("");
            }
        } catch (IllegalArgumentException e) {
            mainApp.showAlertError(new String[]{
                    "Error", "Tag Failed to Add", "The tag " + newTag + " contains an illegal character. Try another tag"
            });

            txtNewTag.setText("");
        }
    }

    /**
     * Handles changes to txtNewTag (the field where user enters new tags).
     */
    @FXML
    private void txtNewTagChanged() {
        if (txtNewTag.getText().equals("")) {
            btnNewTag.setDisable(true);
        } else {
            btnNewTag.setDisable(false);
        }
    }

    /**
     * Handles functionality for the Move button press.
     */
    @FXML
    private void handleMove() {
        // Make a pop up box to prompt the user for a path
        VBox popUpVBox = new VBox();
        Label lblMessage = new Label();
        lblMessage.setText("Enter a path to move this file to:");
        lblMessage.setFont(new Font(15));
        HBox boxPath = new HBox();
        TextField txtPath = new TextField();
        Button btnPath = new Button();
        btnPath.setText("Choose");
        boxPath.getChildren().add(txtPath);
        boxPath.getChildren().add(btnPath);
        Button btnMove = new Button();
        btnMove.setText("Move");
        Button btnCancel = new Button();
        btnCancel.setText("Cancel");
        HBox boxButtons = new HBox();
        boxButtons.getChildren().add(btnMove);
        boxButtons.getChildren().add(btnCancel);
        popUpVBox.getChildren().add(lblMessage);
        popUpVBox.getChildren().add(boxPath);
        popUpVBox.getChildren().add(boxButtons);
        popUpVBox.setStyle("-fx-background-color: white; -fx-padding: 10; -fx-border-color: black; " +
                "-fx-border-width: 3; ");

        AnchorPane popUpPane = new AnchorPane();
        popUpPane.getChildren().add(popUpVBox);
        Stage popupStage = new Stage(StageStyle.TRANSPARENT);
        popupStage.initOwner(mainApp.getMainStage());
        popupStage.initModality(Modality.APPLICATION_MODAL);
        popupStage.setScene(new Scene(popUpPane, Color.TRANSPARENT));

        // If user presses the path button, open up a directory chooser for the user to select a directory
        btnPath.setOnAction(event -> {
            DirectoryChooser directoryChooser = new DirectoryChooser();
            directoryChooser.setTitle("Select Folder to Move to");
            File selectedFile = directoryChooser.showDialog(mainApp.getMainStage());
            if (selectedFile != null) {
                txtPath.setText(selectedFile.getAbsolutePath());
            }
        });

        // If the user presses the move button, move the file
        btnMove.setOnAction(event -> {
            String path = txtPath.getText();
            Directory curDirectory = image.getCurrentDirectory();
            boolean moved = mainApp.getController().moveFileToDirectory(new File(path), image.getFilePath());
            if (moved) {
                popupStage.hide();

                // Go back to the Directory
                Directory targetDir = mainApp.getController().search(new File(path));
                handleBack();
                mainApp.getController().setCurrentDirectory(targetDir);
                mainApp.showDirectoryMenu(targetDir);
                mainApp.getController().setCurrentDirectory(curDirectory);
                handleBack();
            } else {
                lblMessage.setText("Try a different path...");
                txtPath.setText("");
            }
        });
        btnCancel.setOnAction(event -> popupStage.hide());

        popupStage.show();
    }

    /**
     * Handles functionality for the Back button press.
     */
    @FXML
    protected void handleBack() {
        imageView.setImage(null);
        System.gc();
        mainApp.showDirectoryMenu(mainApp.getController().getCurrentDirectory());
    }

    /**
     * Refreshes the attributes of the image after a change.
     */
    private void refreshImage() {
        imageView.setImage(null);
        System.gc();
        mainApp.showDirectoryMenu(mainApp.getController().getCurrentDirectory());
        mainApp.showImageMenu(image);
    }

    /**
     * Handles functionality for the History button press.
     */
    @FXML
    private void handleHistory() {
        imageView.setImage(null);
        System.gc();
        mainApp.showHistoryMenu(image);
    }

    /**
     * Returns lblPath, the label that shows the path of the image.
     *
     * @return lblPath
     */
    Label getLblPath() {
        return lblPath;
    }

    /**
     * Returns lvTags, the list that displays all the tags.
     *
     * @return lvTags
     */
    ListView<String> getLvTags() {
        return lvTags;
    }

    /**
     * Returns txtNewTag, the textField that gets user input for a new tag.
     *
     * @return txtNewTag
     */
    TextField getTxtNewTag() {
        return txtNewTag;
    }
}
