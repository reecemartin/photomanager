package com.PhotoManager.view;

import com.PhotoManager.Controller;
import com.PhotoManager.GUInterface;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.DirectoryChooser;

import java.io.File;


public class StartupMenuController {
    private GUInterface mainApp;

    @FXML
    private TextField txtPath;
    @FXML
    private Button btnChoosePath;
    @FXML
    private Button btnStart;

    /*
    * Constructs the controller.
    */
    public StartupMenuController() {
    }

    /**
     * Handles functionality for the Choose button press.
     */
    public void handleChoosePath() {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Photo Collection Chooser");
        File selectedFile = directoryChooser.showDialog(mainApp.getMainStage());
        if (selectedFile != null) {
            txtPath.setText(selectedFile.getAbsolutePath());
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

    /**
     * Handles functionality for the Start button press.
     */
    @FXML
    private void handleStart() {
        assert !txtPath.getText().equals("");
        File rootFile = new File(txtPath.getText());
        if (rootFile.exists()) {
            Controller controller = new Controller(rootFile);
            mainApp.setController(controller);
            mainApp.showDirectoryMenu(controller.getCurrentDirectory());
        } else {
            mainApp.showAlertError(new String[]{
                    "Error", "Invalid Path Entered", "Please enter a valid path name, or select a path with Choose..."
            });
        }
    }

}
