package com.PhotoManager.view;

import com.PhotoManager.GUInterface;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ObservableList;
import javafx.scene.control.Control;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.text.Text;

import java.util.ArrayList;

public class AllHistoryMenuController extends HistoryMenuController {
    @Override
    protected void initialize() {
    }

    /**
     * Sets the main app for this AllHistoryMenuController, and sets up all the histories.
     *
     * @param mainApp The mainApp to be used to set this controllers mainApp.
     */
    @Override
    public void setMainApp(GUInterface mainApp) {
        super.setMainApp(mainApp);
        ArrayList<String[]> histories = mainApp.getController().getAllImageHistories();
        setHistories(histories);
    }

    /**
     * Populates the history table with the histories given, adding the current name of each image as a column.
     *
     * @param histories the histories to populate the table with
     */
    @Override
    protected void setUpTableColumns(ObservableList<String[]> histories) {
        super.setUpTableColumns(histories);
        for (int i = 1; i < getTblHistories().getColumns().size(); i++) {
            getTblHistories().getColumns().get(i).setPrefWidth(165);
        }
        if (histories.size() > 0) {
            TableColumn<String[], String> colCurName = new TableColumn<>(histories.get(0)[3]);
            colCurName.setCellValueFactory(param -> new SimpleStringProperty((param.getValue()[3])));
            colCurName.setPrefWidth(207);
            colCurName.setText("Current Name");
            colCurName.setCellFactory(param -> {
                TableCell<String[], String> cell = new TableCell<>();
                Text text = new Text();
                cell.setGraphic(text);
                cell.setPrefHeight(Control.USE_COMPUTED_SIZE);
                text.wrappingWidthProperty().bind(colCurName.widthProperty());
                text.textProperty().bind(cell.itemProperty());
                return cell;
            });
            getTblHistories().getColumns().add(1, colCurName);
        }
    }

    @Override
    protected void handleBack() {
        getMainApp().showDirectoryMenu(getMainApp().getController().getCurrentDirectory());
    }
}
