package com.PhotoManager;

import com.PhotoManager.model.Directory;
import com.PhotoManager.model.Image;
import com.PhotoManager.view.*;

import com.PhotoManager.view.*;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

/**
 * Controls the elements in displaying a graphical user interface to the user.
 */
public class GUInterface extends Application {
    private Stage mainStage;
    private BorderPane rootLayout;
    private Controller controller;
    private static final File resourceFile = new File(GUInterface.class.getResource("")
            .getPath()).getParentFile().getParentFile();

    /**
     * Main method of this program. Launches this GUI application when it is called.
     *
     * @param args main method arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

    /**
     * Returns this.controller.
     *
     * @return the controller object stored in this.controller
     */
    public Controller getController() {
        return controller;
    }

    /**
     * Sets this.controller to the specified controller object.
     *
     * @param controller the controller object to set this.controller to
     */
    public void setController(Controller controller) {
        this.controller = controller;
    }

    /**
     * Returns this.mainStage.
     *
     * @return the Stage object stored in this.mainStage
     */
    public Stage getMainStage() {
        return mainStage;
    }

    /**
     * Starts this application, initializing the start up menu.
     *
     * @param main the Stage object to display this application in.
     */
    public void start(Stage main) {
        this.mainStage = main;
        getMainStage().setTitle("Photo Manager");

        initRootLayout();

        showStartupMenu();
    }

    /**
     * Initializes the root layout.
     */
    private void initRootLayout() {
        try {
            // Load root layout from fxml file.
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(new File(resourceFile, "fxml/RootLayout.fxml").toURI().toURL());
            rootLayout = loader.load();

            // Show the scene containing the root layout.
            Scene scene = new Scene(rootLayout);
            mainStage.setScene(scene);
            mainStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Shows the startup menu.
     */
    private void showStartupMenu() {
        try {
            // Load startup menu.
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(new File(resourceFile, "fxml/StartupMenu.fxml").toURI().toURL());
            AnchorPane startupMenu = loader.load();

            // Set directory menu into the center of root layout.
            rootLayout.setCenter(startupMenu);

            StartupMenuController controller = loader.getController();
            controller.setMainApp(this);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Shows the directory menu with the specified directory filling up its contents.
     *
     * @param directory the directory to populate the directory menu with
     */
    public void showDirectoryMenu(Directory directory) {
        try {
            // Load directory menu.
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(new File(resourceFile, "fxml/DirectoryMenu.fxml").toURI().toURL());
            AnchorPane directoryMenu = loader.load();

            // Set directory menu into the center of root layout.
            rootLayout.setCenter(directoryMenu);

            DirectoryMenuController controller = loader.getController();
            controller.setMainApp(this);
            controller.setCurDirectory(directory);
            this.controller.setCurrentDirectory(directory);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Shows the image menu with the specified image filling up its contents.
     *
     * @param image the image to populate the image menu with
     */
    public void showImageMenu(Image image) {
        try {
            // Load image menu.
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(new File(resourceFile, "fxml/ImageMenu.fxml").toURI().toURL());
            AnchorPane imageMenu = loader.load();

            // Set image menu into the center of root layout.
            rootLayout.setCenter(imageMenu);
            image.setSuggestedTag(controller.getSuggestedTag(image));
            ImageMenuController controller = loader.getController();
            controller.setMainApp(this);
            controller.setImage(image);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Shows the image menu with the specified images filling up its contents.
     *
     * @param images the images to populate the image menu with
     */
    public void showMultipleImageMenu(List<Image> images) {
        try {
            // Load image menu.
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(new File(resourceFile, "fxml/MultipleImageMenu.fxml").toURI()
                    .toURL());
            AnchorPane imageMenu = loader.load();

            // Set image menu into the center of root layout.
            rootLayout.setCenter(imageMenu);
            MultipleImageMenuController controller = loader.getController();
            controller.setMainApp(this);
            controller.setImages(images);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * Shows the tag menu to manage tags.
     */
    public void showTagMenu() {
        try {
            // Load startup menu.
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(new File(resourceFile, "fxml/TagMenu.fxml").toURI().toURL());
            AnchorPane historyMenu = loader.load();

            // Set directory menu into the center of root layout.
            rootLayout.setCenter(historyMenu);
            TagMenuController controller = loader.getController();
            controller.setMainApp(this);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Shows the search menu with the specified tag's search results filling up its contents.
     *
     * @param tag the tag to search for
     */
    public void showSearchMenu(String tag) {
        try {
            // Load search menu.
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(new File(resourceFile, "fxml/SearchMenu.fxml").toURI().toURL());
            AnchorPane startupMenu = loader.load();

            // Set directory menu into the center of root layout.
            rootLayout.setCenter(startupMenu);

            SearchMenuController controller = loader.getController();
            controller.setMainApp(this);
            controller.setTag(tag);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Shows the history menu with the specified image's histories filling up its contents
     *
     * @param image the image to populate history menu with
     */
    public void showHistoryMenu(Image image) {
        try {
            // Load startup menu.
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(new File(resourceFile, "fxml/HistoryMenu.fxml").toURI().toURL());
            AnchorPane historyMenu = loader.load();

            // Set directory menu into the center of root layout.
            rootLayout.setCenter(historyMenu);
            HistoryMenuController controller = loader.getController();
            controller.setMainApp(this);
            controller.setImage(image);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Shows the all history menu with all images' histories filling up its contents
     */
    public void showAllHistoryMenu() {
        try {
            // Load startup menu.
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(new File(resourceFile, "fxml/AllHistoryMenu.fxml").toURI().toURL());
            AnchorPane historyMenu = loader.load();

            // Set directory menu into the center of root layout.
            rootLayout.setCenter(historyMenu);
            AllHistoryMenuController controller = loader.getController();
            controller.setMainApp(this);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean showAlertConfirmation(String[] message) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(message[0]);
        alert.setHeaderText(message[1]);
        alert.setContentText(message[2]);

        Optional<ButtonType> result = alert.showAndWait();
        return result.isPresent() && result.get() == ButtonType.OK;
    }

    public void showAlertError(String[] message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(message[0]);
        alert.setHeaderText(message[1]);
        alert.setContentText(message[2]);

        alert.showAndWait();
    }

    public void showAlertInformation(String[] message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(message[0]);
        alert.setHeaderText(message[1]);
        alert.setContentText(message[2]);

        alert.showAndWait();
    }

    public String showDialogTextInput(String[] message) {
        TextInputDialog dialog = new TextInputDialog("");
        dialog.setTitle(message[0]);
        dialog.setHeaderText(message[1]);
        dialog.setContentText(message[2]);

        Optional<String> result = dialog.showAndWait();
        return result.orElse("");
    }


    @Override
    public void stop() {
        if (controller != null)
            controller.updateConfig();
    }
}
