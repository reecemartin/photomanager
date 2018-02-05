package com.PhotoManager.model;

import com.PhotoManager.Controller;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.ConsoleHandler;

/**
 * A collection of actions to be performed on Directories.
 */
public class Directory {

    private ConfigManager config;
    private Controller controller;
    private Directory parentDirectory;

    private ArrayList<Image> imageArr;
    private ArrayList<Directory> directoryArr;
    /**
     * Images to have their OS path updated.
     */
    private ArrayList<Image> updateQueue;

    private File filePath;
    private Logger logger = Logger.getLogger(Directory.class.getName());


    /**
     * Possible file extensions an Image Object can have.
     */
    private final static HashSet<String> IMAGE_EXTENSIONS = new HashSet<String>() {{
        add(".gif");
        add(".png");
        add(".jpg");
        add(".bmp");
        add(".tiff");
        add(".jpeg");
    }};

    /**
     * Constructs a new Directory at &lt;parentDirectory&gt;.
     *
     * @param controller      Instance of Controller class.
     * @param parentDirectory Instance of Directory's parent.
     * @param filePath        FilePath to this Directory in the OS's filesystem.
     */
    public Directory(Controller controller, Directory parentDirectory, File filePath,
                     ConfigManager config) {
        assert controller != null && filePath != null;

        this.config = config;
        this.controller = controller;
        setParentDirectory(parentDirectory);

        imageArr = new ArrayList<>();
        directoryArr = new ArrayList<>();
        updateQueue = new ArrayList<>();

        this.filePath = filePath;

        updateDirectoryChanges();
    }

    /**
     * Constructs a new root Directory from a child Directory.
     *
     * @param controller     Instance of Controller class.
     * @param filePath       FilePath to this Directory in the OS's filesystem.
     * @param childDirectory Instance of one of Directory's children.
     */
    public Directory(Controller controller, File filePath, Directory childDirectory,
                     ConfigManager config) {
        assert controller != null && filePath != null;

        this.config = config;
        this.controller = controller;

        imageArr = new ArrayList<>();
        directoryArr = new ArrayList<>();
        updateQueue = new ArrayList<>();

        this.filePath = filePath;

        childDirectory.setParentDirectory(this);
        directoryArr.add(childDirectory);

        updateDirectoryChanges();
    }

    /**
     * Updates this Directory for any changed files/folders
     */
    public void updateDirectoryChanges() {
        ArrayList<File> directoryFiles;
        if (getFile().exists()) {
            directoryFiles = new ArrayList<>(Arrays.asList(getFile().listFiles()));
        } else {
            directoryFiles = new ArrayList<>();
        }
        // Check for images
        for (Image image : getImages()) {
            File imageFile = image.getFilePath();
            if (directoryFiles.contains(imageFile)) {
                // If file still exists
                directoryFiles.remove(imageFile);
            } else {
                // If file doesn't exist anymore
                directoryFiles.remove(imageFile);
                imageArr.remove(image);
            }
        }

        // Check for folders
        for (Directory directory : getDirectories()) {
            File directoryFile = directory.getFile();
            if (directoryFiles.contains(directoryFile)) {
                // If file still exists
                directoryFiles.remove(directoryFile);
            } else {
                // If file doesn't exist anymore
                directoryFiles.remove(directoryFile);
                directoryArr.remove(directory);
            }
        }

        // Remaining Files in directoryFiles are just files that are not currently added.
        addImagesAndDirectories(directoryFiles);

    }

    /**
     * Adds all the Images and Directories in files into their respective ObservableLists
     *
     * @param files ObservableList&lt;File&gt; of Directories or Images
     */
    private void addImagesAndDirectories(ArrayList<File> files) {
        for (File filePath : files) {
            if (isFileAnImage(filePath)) {
                Image newImage = new Image(this, filePath, config.getImageHistory(filePath));
                imageArr.add(newImage);
                controller.addInitialTags(newImage);
            } else if (isFileADirectory(filePath)) {
                try {
                    Directory newDirectory = new Directory(controller, this, filePath, config);
                    directoryArr.add(newDirectory);
                } catch (NullPointerException e) {
                    logger.log(Level.WARNING, "No access to: " + filePath);
                    logger.addHandler(new ConsoleHandler());
                }
            }
        }
    }

    /**
     * Return the file path of this Directory.
     *
     * @return File path of this Directory.
     */
    public File getFile() {
        return filePath;
    }

    /**
     * Get the Name of this Directory
     *
     * @return Name of Tree
     */
    public String getDirectoryName() {
        return filePath.getName();
    }

    /**
     * Return this Directory's parent Directory if it exists.
     *
     * @return This Directory's parent Directory | null
     */
    public Directory getParentDirectory() {
        return parentDirectory;
    }

    /**
     * Set the parent Directory of this Directory.
     *
     * @param newParentDirectory new Directory to be this Directory's parent
     */
    private void setParentDirectory(Directory newParentDirectory) {
        parentDirectory = newParentDirectory;
    }

    /**
     * Return Image at filePath if it exists.
     *
     * @param filePath filePath of a Image.
     * @return Image at filePath | null
     */
    public Image getImage(File filePath) {
        if (isFileAnImage(filePath)) {
            for (Image img : imageArr) {
                if (img.getFilePath().equals(filePath)) {
                    return img;
                }
            }
        }
        return null;
    }

    /**
     * Return all Images in this Directory.
     *
     * @return ObservableList&lt;Image&gt; of all Images in this Directory.
     */
    public ArrayList<Image> getImages() {
        return new ArrayList<>(imageArr);
    }

    /**
     * Return all Images in this Directory and its sub-Directories.
     *
     * @return ObservableList&lt;Image&gt; of all Images in this Directory and its sub-Directories
     */
    public ArrayList<Image> getImagesInSubDirectories() {
        ArrayList<Image> temp = getImages();
        for (Directory subDirectory : directoryArr) {
            // Gets all images in this and any trees extending from this tree
            temp.addAll(subDirectory.getImagesInSubDirectories());
        }
        return temp;
    }

    /**
     * Return Directory at filePath if it exists.
     *
     * @param filePath filePath of a Directory.
     * @return Directory at filePath | null
     */
    public Directory getDirectory(File filePath) {
        if (isFileAnImage(filePath)) {
            filePath = filePath.getParentFile();
        }

        if (isFileADirectory(filePath)) {
            // If a Directory
            if (getFile().equals(filePath)) {
                // If root path
                return this;
            } else {
                // If a sub-Directory
                String tempPath = filePath.getPath();

                for (Directory dir : directoryArr) {
                    // Go through all directories in this directory
                    if (tempPath.contains(dir.getFile().getAbsolutePath())) {
                        // If this directory is part of filePath's file path then recurse
                        return dir.getDirectory(filePath);
                    }
                }
                // Directory exists but isn't recorded in this Directory so update the records in
                // this Directory
                updateDirectoryChanges();
                // Hit that replay
                getDirectory(filePath);
            }
        }
        return null;
    }

    /**
     * Return all directories under this Directory.
     *
     * @return ObservableList&lt;Directory&gt; of all directories under this Directory
     */
    public ArrayList<Directory> getDirectories() {
        return new ArrayList<>(directoryArr);
    }

    /**
     * Returns whether or not filePath is a Directory or not
     *
     * @param filePath filePath to questioned Directory
     * @return true if filePath is a Directory and false if filePath is not a Directory
     */
    private boolean isFileADirectory(File filePath) {
        return filePath.exists() && !filePath.isFile();
    }

    /**
     * Returns whether or not filePath is an Image or not
     *
     * @param filePath filePath to questioned Image
     * @return true if filePath is a Image and false if filePath is not a Image
     */
    private boolean isFileAnImage(File filePath) {
        return filePath.exists() && filePath.isFile() &&
                filePath.getAbsolutePath().contains(".") &&
                IMAGE_EXTENSIONS.contains(
                        filePath.getAbsolutePath().substring(
                                filePath.getAbsolutePath().lastIndexOf(".")
                        ).toLowerCase());
    }

    /**
     * Adds Image image to this Directory.
     *
     * @param image Image to be added to this Directory
     */
    public void addImage(Image image) {
        imageArr.add(image);
    }

    /**
     * Removes Image from this Directory.
     *
     * @param file filePath to the file you want removed from this Directory
     */
    public void removeFile(File file) {
        if (isFileAnImage(file)) {
            imageArr.remove(getImage(file));
        } else if (isFileADirectory(file)) {
            // Is a directory
            directoryArr.remove(getDirectory(file));
        }
    }

    /**
     * Adds an Image to the update Queue.
     *
     * @param image Image to be added to updateQueue
     */
    void addToUpdateQueue(Image image) {
        updateQueue.add(image);
    }

    /**
     * Updates all the images in the updateQueue's file paths in the OS' filesystem.
     */
    private void updateImagesOSPath() {
        for (Image image : updateQueue) {
            image.updateOSPath();
        }

        updateQueue.clear();
    }

    /**
     * Updates all images file paths in the OS' filesystem in every sub-directory's updateQueues.
     */
    public void updateImagesOSPathAll() {
        updateImagesOSPath();
        for (Directory subDirectory : directoryArr) subDirectory.updateImagesOSPathAll();
    }

    /**
     * Opens this Image's filepath.
     *
     * @throws IOException In case this file path doesn't not exist.
     */
    public void openInCurrentDirectory() throws IOException {
        String os = System.getProperty("os.name").toLowerCase();
        if (os.contains("win")) {
            Runtime.getRuntime().exec("explorer.exe " + getFile().getCanonicalPath());
        } else if (os.contains("nux") | os.contains("mac")) {
            Runtime.getRuntime().exec(new String[]{"/usr/bin/open",
                    getFile().getCanonicalPath()});
        }
    }
}