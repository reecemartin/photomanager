package com.PhotoManager.view;

import com.PhotoManager.GUInterface;
import com.PhotoManager.model.Image;
import javafx.scene.text.Text;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.util.List;

public class HistoryMenuController {
    private GUInterface mainApp;
    private Image image;
    private List<String[]> histories;

    @FXML
    private Button btnRevert;

    @FXML
    private Button btnBack;

    @FXML
    private Label lblPath;

    @FXML
    private TableView<String[]> tblHistories;

    public HistoryMenuController() {

    }

    /**
     * Initialize this HistoryMenuController.
     */
    @FXML
    protected void initialize() {
        getTblHistories().setOnMouseClicked(event -> btnRevert.setDisable(false));
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
     * Sets the Image for which the current history belongs to.
     *
     * @param image The image for which the history belongs to.
     */
    public void setImage(Image image) {
        this.image = image;
        if (image != null) {
            lblPath.setText(image.getFilePath().getAbsolutePath());
            setHistories(getMainApp().getController().getImageHistory(image));
        }
    }

    /**
     * Populates the history table with the histories given.
     *
     * @param histories The histories to populate the table with
     */
    protected void setUpTableColumns(ObservableList<String[]> histories) {
        if (!histories.isEmpty()) {
            getTblHistories().setItems(histories);
            TableColumn<String[], String> colTimestamp = new TableColumn<>(histories.get(0)[2]);
            colTimestamp.setCellValueFactory(param -> new SimpleStringProperty((param.getValue()[2])));
            colTimestamp.setPrefWidth(130);
            colTimestamp.setText("Timestamp");
            colTimestamp.setCellFactory(param -> {
                TableCell<String[], String> cell = new TableCell<>();
                Text text = new Text();
                cell.setGraphic(text);
                cell.setPrefHeight(Control.USE_COMPUTED_SIZE);
                text.wrappingWidthProperty().bind(colTimestamp.widthProperty());
                text.textProperty().bind(cell.itemProperty());
                return cell;
            });
            getTblHistories().getColumns().add(colTimestamp);
            TableColumn<String[], String> colOldName = new TableColumn<>(histories.get(0)[0]);
            colOldName.setCellValueFactory(param -> new SimpleStringProperty((param.getValue()[0])));
            colOldName.setPrefWidth(250);
            colOldName.setText("Old Name");
            colOldName.setCellFactory(param -> {
                TableCell<String[], String> cell = new TableCell<>();
                Text text = new Text();
                cell.setGraphic(text);
                cell.setPrefHeight(Control.USE_COMPUTED_SIZE);
                text.wrappingWidthProperty().bind(colOldName.widthProperty());
                text.textProperty().bind(cell.itemProperty());
                return cell;
            });
            getTblHistories().getColumns().add(colOldName);
            TableColumn<String[], String> colNewName = new TableColumn<>(histories.get(0)[1]);
            colNewName.setCellValueFactory(param -> new SimpleStringProperty((param.getValue()[1])));
            colNewName.setPrefWidth(250);
            colNewName.setText("New Name");
            colNewName.setCellFactory(param -> {
                TableCell<String[], String> cell = new TableCell<>();
                Text text = new Text();
                cell.setGraphic(text);
                cell.setPrefHeight(Control.USE_COMPUTED_SIZE);
                text.wrappingWidthProperty().bind(colNewName.widthProperty());
                text.textProperty().bind(cell.itemProperty());
                return cell;
            });
            getTblHistories().getColumns().add(colNewName);

            colTimestamp.setEditable(false);
            colNewName.setEditable(false);
            colOldName.setEditable(false);
        }
    }

    /**
     * Handles functionality for the Back button press.
     */
    @FXML
    protected void handleBack() {
        getMainApp().showImageMenu(image);
    }

    /**
     * Handles functionality for the Revert button press.
     */
    @FXML
    private void handleRevert() {
        String[] historySelected = getTblHistories().getSelectionModel().getSelectedItem();
        if (historySelected != null && getMainApp().showAlertConfirmation(new String[]{
                "Confirmation", "Revert Image Name", "Are you sure you want to revert this image's name?"
        })) {
            String nameToRevertTo = historySelected[0];
            getMainApp().getController().revertImageName(image, nameToRevertTo);
            handleBack();
        }
    }

    GUInterface getMainApp() {
        return mainApp;
    }

    void setHistories(List<String[]> histories) {
        this.histories = histories;
        setUpTableColumns(FXCollections.observableList(histories));
    }

    TableView<String[]> getTblHistories() {
        return tblHistories;
    }
}
