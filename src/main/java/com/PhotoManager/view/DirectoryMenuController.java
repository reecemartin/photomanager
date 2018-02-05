package com.PhotoManager.view;

import com.PhotoManager.GUInterface;
import com.PhotoManager.model.Directory;
import com.PhotoManager.model.Image;
import javafx.scene.text.Text;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.*;

import java.io.File;
import java.io.IOException;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DirectoryMenuController {
    private GUInterface mainApp;
    private Logger logger = Logger.getLogger(DirectoryMenuController.class.getName());

    private Directory curDirectory;

    @FXML
    private Label lblPath;

    @FXML
    private ListView<Directory> lvDirectories;

    @FXML
    private ListView<Image> lvImages;

    @FXML
    private Button btnOpen;

    @FXML
    private Button btnOpenOS;

    @FXML
    private Button btnAddFolder;

    @FXML
    private Button btnBack;

    @FXML
    private Button btnManageTags;

    @FXML
    private Button btnMove;

    @FXML
    private Button btnShowAll;

    public DirectoryMenuController() {
    }

    /**
     * Initialize this DirectoryMenuController and add functions to attributes.
     */
    @FXML
    private void initialize() {
        lvImages.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        lvDirectories.setCellFactory(param -> new ListCell<Directory>() {
            @Override
            protected void updateItem(Directory item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || item == null || item.getDirectoryName() == null) {
                    setText(null);
                } else {
                    Text text = new Text(item.getDirectoryName());
                    text.setWrappingWidth(lvDirectories.getPrefWidth());
                    setGraphic(text);
                }
            }
        });
        lvImages.setCellFactory(param -> new ListCell<Image>() {
            @Override
            protected void updateItem(Image item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || item == null || item.getImageName() == null) {
                    setText(null);
                } else {
                    Text text;
                    if (item.getCurrentDirectory() == curDirectory)
                        text = new Text(item.getImageName());
                    else
                        text = new Text(item.getFilePath().getAbsolutePath().replace(curDirectory.getFile().getAbsolutePath() + File.separator, ""));
                    text.setWrappingWidth(lvImages.getPrefWidth());
                    setGraphic(text);
                }
            }
        });
        lvDirectories.focusedProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> focusChanged(lvDirectories, newValue));
        lvImages.focusedProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> focusChanged(lvImages, newValue));
    }

    /**
     * Changes the DirectoryMenu given the focuse state (value) of the listview.
     *
     * @param listView The listview whose focus changed.
     * @param value    The boolean representing the current focus state.
     */
    private void focusChanged(ListView listView, boolean value) {
        if (value) {
            btnOpen.setDisable(false);
            if (listView == lvDirectories) {
                lvImages.getSelectionModel().clearSelection();
                btnMove.setDisable(true);
            } else {
                lvDirectories.getSelectionModel().clearSelection();
                btnMove.setDisable(false);
            }
        }
    }

    /**
     * Sets the mainApp for this controller.
     *
     * @param mainApp The mainApp to be used to set this controllers mainApp.
     */
    public void setMainApp(GUInterface mainApp) {
        this.mainApp = mainApp;
    }

    public void setCurDirectory(Directory d) {
        curDirectory = d;
        if (curDirectory != null) {
            displayImages();
            displayDirectories();
            lblPath.setText(curDirectory.getFile().toString());

        } else {
            lvDirectories.getItems().removeAll();
            lvImages.getItems().removeAll();
        }

    }

    /**
     * Displays the list of images within the current directory.
     */
    private void displayImages() {
        logger.log(Level.FINE, curDirectory.getImages().toString());
        logger.addHandler(new ConsoleHandler());
        lvImages.setItems(FXCollections.observableList(curDirectory.getImages()));
    }

    /**
     * Displays the subdirectories of the current directory.
     */
    private void displayDirectories() {
        lvDirectories.setItems(FXCollections.observableList(curDirectory.getDirectories()));
    }

    /**
     * Handles functionality for the Add Folder button press.
     */
    @FXML
    public void handleAddFolder() {
        String newDirectoryName = mainApp.showDialogTextInput(new String[]{
                "Text Input", "Enter New Directory Name", "Name:"
        });
        mainApp.getController().createSubDirectory(newDirectoryName);
        mainApp.showDirectoryMenu(curDirectory);
    }

    /**
     * Handles functionality for the Back button press.
     */
    @FXML
    public void handleBack() {
        if (curDirectory.getParentDirectory() != null || mainApp.showAlertConfirmation(new String[]{
                        "Confirmation", "Move root directory", "Would you like to move up the root directory?"
                }
        )) {

            if (mainApp.getController().goUpDirectory()) {
                mainApp.showDirectoryMenu(curDirectory.getParentDirectory());
                mainApp.getController().updateConfig();
            } else {
                mainApp.showAlertError(new String[]{
                                "Error", "Cannot move root directory", "You have reached the root of the drive!"
                        }
                );
            }
        }
    }

    /**
     * Handles functionality for the Manage Tag button press.
     */
    @FXML
    public void handleManageTags() {
        mainApp.showTagMenu();
    }

    /**
     * Handles functionality for the Move button press.
     */
    @FXML
    public void handleMove() {
        if (lvImages.getSelectionModel().getSelectedItem() != null) {
            Image image = lvImages.getSelectionModel().getSelectedItem();

            // Make a pop up box to prompt the user for a path
            VBox popUpVBox = new VBox();
            Label lblMessage = new Label();
            lblMessage.setText("Enter a path to move this file to:");
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
            btnMove.setOnAction((ActionEvent event) -> {
                String path = txtPath.getText();
                logger.log(Level.FINE, image.getFilePath().getAbsolutePath());
                logger.addHandler(new ConsoleHandler());
                boolean moved = mainApp.getController().moveFileToDirectory(new File(path), image.getFilePath());
                if (moved) {
                    popupStage.hide();

                    //handleBack();
                    Directory targetDir = mainApp.getController().search(new File(path));
                    mainApp.getController().setCurrentDirectory(targetDir);
                    mainApp.showDirectoryMenu(targetDir);
                    mainApp.getController().setCurrentDirectory(curDirectory);
                    mainApp.showDirectoryMenu(curDirectory);
                } else {
                    lblMessage.setText("Try a different path...");
                    txtPath.setText("");
                }
            });

            btnCancel.setOnAction(event -> popupStage.hide());

            popupStage.show();
        }
    }

    /**
     * Handles functionality for the Open button press.
     */
    @FXML
    public void handleOpen() {
        ObservableList<Image> selectedImages = lvImages.getSelectionModel().getSelectedItems();
        Directory selectedDirectory = lvDirectories.getSelectionModel().getSelectedItem();
        // Open the menu corresponding to either a file or directory selection
        if (selectedDirectory != null || selectedImages != null) {
            if (selectedDirectory == null) {
                if (selectedImages.size() == 1)
                    mainApp.showImageMenu(selectedImages.get(0));
                else if (selectedImages.size() > 1)
                    mainApp.showMultipleImageMenu(selectedImages);
            } else {
                mainApp.getController().setCurrentDirectory(selectedDirectory);
                mainApp.showDirectoryMenu(selectedDirectory);
            }
        }
    }

    /**
     * Handles functionality for the Open in File Explorer button press.
     */
    @FXML
    public void handleOpenOS() {
        try {
            curDirectory.openInCurrentDirectory();
        } catch (IOException iox) {
            iox.printStackTrace();
        }
    }

    /**
     * Handles functionality for the Show All button press.
     */
    @FXML
    public void handleShowAll() {
        if (curDirectory != null) {
            logger.log(Level.FINE, curDirectory.getImagesInSubDirectories().toString());
            logger.addHandler(new ConsoleHandler());
            lvImages.setItems(FXCollections.observableArrayList(curDirectory.getImagesInSubDirectories()));
        }
    }

    /**
     * Handles functionality for the All Histories button press.
     */
    @FXML
    public void handleAllHistories() {
        if (curDirectory != null) {
            mainApp.showAllHistoryMenu();
        }
    }

}
