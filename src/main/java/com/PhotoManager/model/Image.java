package com.PhotoManager.model;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A collection of actions to be performed on Image files.
 */
public class Image {
    private Directory currentDirectory;
    private Directory oldDirectory;
    private File filePath;
    private File OSFilePath;
    private HashSet<String> tags;
    private String suggestedTag = "";
    private Logger logger = Logger.getLogger(Image.class.getName());
    private ArrayList<String[]> nameHistory;

    /**
     * Constructs a new Image.
     *
     * @param currentDirectory Current Directory that PhotoManager.model.Image is in.
     * @param filePath         FilePath to this Image in the OS's filesystem.
     */
    public Image(Directory currentDirectory, File filePath, ArrayList<String[]> nameHistory) {
        this.currentDirectory = currentDirectory;
        this.oldDirectory = currentDirectory;
        this.filePath = filePath;
        this.OSFilePath = filePath;
        this.tags = new HashSet<>();
        this.logger.addHandler(new ConsoleHandler());
        if (nameHistory != null) {
            this.nameHistory = nameHistory;
        } else {
            this.nameHistory = new ArrayList<>();
        }

        addInitialTags();
    }

    /**
     * Finds and adds all the initial tags on the Image to tags.
     */
    private void addInitialTags() {
        // tag names excluding the first and last index
        if (getImageName().contains("@")) {
            ArrayList<String> initialTags = new ArrayList<>(Arrays.asList(getImageName().
                    substring(0, getImageName().lastIndexOf(".")).split("@")));

            initialTags.remove(0);

            for (int i = 0; i < initialTags.size(); i++) {
                initialTags.set(i, initialTags.get(i).trim());
            }

            tags.addAll(initialTags);
        }
    }

    /**
     * Return a HashSet&lt;String&gt; of all tags on this Image.
     *
     * @return Return HashSet&lt;String&gt; of on this Image.
     */
    public HashSet<String> getTags() {
        return new HashSet<>(tags);
    }

    /**
     * Adds a tag to this Image's filePath and adds the tag to the list of
     * currently used tags.
     *
     * @param tag String to be added to this PhotoManager.model.Image as a tag.
     */
    public void addTag(String tag) {
        tags.add(tag);

        addOrRemoveTag(" @" + tag, true);
    }

    /**
     * Removes tag from this Image's filePath and the list of currently used
     * tags if it exists.
     *
     * @param tag String to be removed from this Image if it exists.
     */
    public void removeTag(String tag) {
        tags.remove(tag);
        addOrRemoveTag(" @" + tag, false);
    }

    /**
     * If add is true, add changedElement to the end of the name of this image.
     * <br>If add is false, remove changedElement from the name of this image.
     *
     * @param changedElement String to add or remove from the Image name
     * @param add            Boolean to decide whether or not to add or remove changedElement to the Image name
     */
    private void addOrRemoveTag(String changedElement, boolean add) {
        String filePath = getFilePath().getAbsolutePath();
        logger.log(Level.FINE, filePath);
        logger.addHandler(new ConsoleHandler());
        String imageName = getImageName();
        logger.log(Level.FINE, imageName);
        logger.addHandler(new ConsoleHandler());

        if (add) {
            String directoryPath = filePath.substring(0, filePath.lastIndexOf(File.separator) + 1);
            String newName;

            newName = imageName.substring(0, imageName.lastIndexOf(".")) + changedElement + imageName.substring(imageName.lastIndexOf("."));

            logger.log(Level.FINE, newName);
            logger.addHandler(new ConsoleHandler());
            String newPath = directoryPath + newName;
            logger.log(Level.FINE, newPath);
            logger.addHandler(new ConsoleHandler());
            setFilePath(new File(newPath));
        } else {
            // Remove changedElement from filePath
            int indexOfElement = filePath.indexOf(changedElement);

            if (indexOfElement != -1) {
                setFilePath(new File(filePath.substring(0, indexOfElement) +
                        filePath.substring(indexOfElement + changedElement.length())));
            }
        }
    }

    /**
     * Removes all tags from this Image.
     */
    public void removeAllTags() {
        tags.clear();

        setFilePath(new File(getImageName().split(" @")[0]));
    }

    /**
     * Sets the filePath of this Image to newPath.
     *
     * @param newPath new file path to this Image.
     */
    public void setFilePath(File newPath) {
        filePath = newPath;
        getCurrentDirectory().addToUpdateQueue(this);
    }

    /**
     * Sets the filePath of this Image to newPath.
     *
     * @param newPath      new file path to this Image.
     * @param newDirectory new currentDirectory
     */
    public void setFilePath(File newPath, Directory newDirectory) {
        filePath = newPath;
        currentDirectory = newDirectory;
        getCurrentDirectory().addToUpdateQueue(this);
    }

    /**
     * Moves/renames Image at OSFilePath to be filePath.
     */
    void updateOSPath() {
        System.out.println(getTags());
        if (getOSFilePath() != getFilePath()) {
            try {
                Files.move(Paths.get(getOSFilePath().getAbsolutePath()), Paths.get(getFilePath().getAbsolutePath()));
                OSFilePath = getFilePath();
            } catch (IOException e) {
                e.printStackTrace();
            }

            oldDirectory.updateDirectoryChanges();
            currentDirectory.updateDirectoryChanges();

            OSFilePath = getFilePath();
            oldDirectory = currentDirectory;
        }
    }

    /**
     * Force the change of this Image's file path to newFilePath.
     *
     * @param newFilePath New File path to this Image.
     */
    public void forceFilePath(File newFilePath) {
        tags.clear();

        setFilePath(newFilePath);

        // Adds all the tags in oldName to this Image
        addInitialTags();
    }

    /**
     * Return this Image's name.
     * <br>ie. for file path C:\Tim.jpg -&gt; returns Tim.jpg.
     *
     * @return String of this Image's name
     */
    public String getImageName() {
        return getFilePath().getName();
    }

    /**
     * Return the Directory that this Image is currently in.
     *
     * @return currentDirectory of this Image.
     */
    public Directory getCurrentDirectory() {
        return currentDirectory;
    }

    /**
     * Return this Image's name change history.
     *
     * @return ArrayList&lt;String[]&gt; of this Image's name change history
     */
    public ArrayList<String[]> getHistory() {
        return this.nameHistory;
    }

    /**
     * Add a name change history to this Image.
     *
     * @param newHistory String[] of name change history to add to this Image.
     */
    public void addHistory(String[] newHistory) {
        nameHistory.add(newHistory);
    }

    /**
     * Return file path of this Image
     *
     * @return filePath of this Image
     */
    public File getFilePath() {
        return filePath;
    }

    /**
     * Return OS' file path to this Image
     *
     * @return OS' file path to this Image
     */
    public File getOSFilePath() {
        return OSFilePath;
    }

    /**
     * Return tag suggested by the ObjectClassifier class.
     *
     * @return String of tag suggested by the ObjectClassifier class
     */
    public String getSuggestedTag() {
        return suggestedTag;
    }

    /**
     * Set/Cache the suggested tag for this image.
     *
     * @param suggestedTag String of tag to set/cache for this Image
     */
    public void setSuggestedTag(String suggestedTag) {
        this.suggestedTag = suggestedTag;
    }
}