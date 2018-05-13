package com.PhotoManager;

import com.PhotoManager.model.*;

import com.PhotoManager.model.*;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Controller class which controls all interactions between the GUInterface and classes in the
 * model package.
 */
public class Controller {
    /**
     * Integer to Month Conversion
     */
    private static final HashMap<String, String> MONTHS = new HashMap<String, String>() {{
        put("01", "Jan");
        put("02", "Feb");
        put("03", "Mar");
        put("04", "Apr");
        put("05", "May");
        put("06", "Jun");
        put("07", "Jul");
        put("08", "Aug");
        put("09", "Sept");
        put("10", "Oct");
        put("11", "Nov");
        put("12", "Dec");
    }};

    private ConfigManager cachedData;

    /**
     * Top of the Image Collection
     * Only things in or under this will be included as part of the Image Collection.
     */
    private Directory rootDirectory;
    private Directory currentDirectory;

    private ObjectClassifier classifier;
    private TagManager tagManager;

    /**
     * Creates a Controller Object with a root Directory (starting point).
     * <br>Controls actions between UI and backend operations.
     *
     * @param rootFilePath File path to root Directory of the program
     */
    public Controller(File rootFilePath) {
        cachedData = new ConfigManager();
        //imageHistories = new HashMap<>();

        classifier = new ObjectClassifier();

        tagManager = new TagManager(cachedData.getCachedTags());
        rootDirectory = new Directory(this, null, rootFilePath, cachedData);
        setCurrentDirectory(getRootDirectory());
    }

    /**
     * Return the Directory at which the Controller is currently operating in.
     *
     * @return currentDirectory
     */
    public Directory getCurrentDirectory() {
        return currentDirectory;
    }

    /**
     * Sets the Directory Controller is at to currentDirectory and write the updates of the previous Directory
     * to config.txt.
     *
     * @param currentDirectory Directory object representing the current directory
     */
    public void setCurrentDirectory(Directory currentDirectory) {
        assert currentDirectory != null;
        this.currentDirectory = currentDirectory;
        currentDirectory.updateImagesOSPathAll();
        updateAvailableTags();
        updateConfig();
    }

    /**
     * Updates the available set of tags with every tag associated with Images in this directory.
     */
    private void updateAvailableTags() {
        for (Image img : getCurrentDirectory().getImages()) {
            for (String imageTag : img.getTags()) {
                tagManager.addTagToAvailableTags(imageTag);
            }
        }
    }

    /**
     * Return the (user-chosen) top-most Directory root node.
     *
     * @return rootDirectory
     */
    public Directory getRootDirectory() {
        return rootDirectory;
    }

    /**
     * Sets the (user-chosen) top-most Directory root node to new Root.
     *
     * @param newRoot Directory object representing the root directory
     */
    private void setRootDirectory(Directory newRoot) {
        assert newRoot != null;
        rootDirectory = newRoot;
    }

    /**
     * Sets the current Directory to one level up the Directory tree.
     * <br>If it's not possible to go further up in the system storage return false.
     * <br>If it's possible to go one level up a Directory return true.
     * <br>If the Directory above does not yet exist, create a new one.
     *
     * @return Whether or not the currentDirectory went up a directory.
     */
    public boolean goUpDirectory() {
        if (getCurrentDirectory().getParentDirectory() == null) {
            // Current Directory == Root Directory
            File parentFilePath = getCurrentDirectory().getFile().getParentFile();

            if (parentFilePath == null) {
                // At top most directory
                return false;
            } else {
                setCurrentDirectory(new Directory(this, parentFilePath, getCurrentDirectory(),
                        cachedData));
                setRootDirectory(getCurrentDirectory());

                return true;
            }
        } else {
            setCurrentDirectory(currentDirectory.getParentDirectory());
            return true;
        }
    }

    /**
     * Creates a new Directory and adds it to currentDirectory (as a sub-directory).
     *
     * @param newDirectoryName Name of a new directory
     */
    public void createSubDirectory(String newDirectoryName) {
        assert !newDirectoryName.equals("");
        String pathLoc = currentDirectory.getFile().getAbsolutePath() + File.separator +
                newDirectoryName;

        boolean created = new File(pathLoc).mkdirs();
        if (created)
            currentDirectory.updateDirectoryChanges();
    }

    /**
     * Return the corresponding sub-directory(or root directory) at or below the currentDirectory
     * according to the filepath, or null otherwise.
     *
     * @param filePath File path of a Directory to search for
     * @return Directory at filePath
     */
    public Directory search(File filePath) {
        String rootPath = rootDirectory.getFile().getPath();
        String directoryPath = filePath.getPath();

        if (directoryPath.contains(rootPath)) {
            return rootDirectory.getDirectory(filePath.getAbsoluteFile());
        }

        return null;
    }

    //
    // Tag Manager Methods
    //

    /**
     * Return all available tags and their associated Image objects.
     *
     * @return HashSet of Strings representing the available tags
     */
    public HashSet<String> getAvailableTags() {
        return tagManager.getAvailableTags();
    }

    /**
     * Return true if the tag is successfully added to the Image object, an
     * IllegalArgumentException if there contained Illegal characters, or false otherwise.
     *
     * @param image Image to add tag to
     * @param tag   Tag to add to Image
     * @return boolean representing if the tag was added successfully
     * @throws IllegalArgumentException If an invalid character was in the tag
     */
    public boolean addTag(Image image, String tag) throws IllegalArgumentException {
        String oldName = image.getImageName();
        if (tagManager.addTagToImage(image, tag)) {
            image.addTag(tag);
            logHistory(image, oldName);
            return true;
        } else {
            return false;
        }
    }

    /**
     * Return if the tag was successfully added, throws IllegalArgumentException if an invalid
     * character was used.
     * <br>Adds tag to available tags in tagManager.
     *
     * @param tag Tag to add to available tags
     * @return boolean representing if the tag was added successfully
     * @throws IllegalArgumentException If an invalid character was in the tag
     */
    public boolean addTag(String tag) throws IllegalArgumentException {
        return tagManager.addTagToAvailableTags(tag);
    }

    /**
     * Adds the tags contained in &lt;image&gt; to the available tags.
     *
     * @param image Image to add tags of
     */
    public void addInitialTags(Image image) {
        for (String tag : image.getTags()) {
            try {
                tagManager.addTagToImage(image, tag);
            } catch (IllegalArgumentException e) {
                // Ignore tag
            }
        }
    }

    /**
     * Return true if the tag was successfully removed; false otherwise.
     * <br>Removes the specified tag from all Image objects that contain it and only removes it from
     * the tag collection when it is not contained
     * in any image.
     *
     * @param tag Tag to remove from all Images
     * @return Whether or not the Image contained the tag
     */
    public boolean removeTag(String tag) {
        HashSet<Image> imageSet = tagManager.getImagesByTag(tag);

        if (imageSet == null) {
            return false;
        } else {
            tagManager.removeTagFromAvailableTags(tag);
            return true;
        }
    }

    /**
     * Return true if the tag was successfully removed; false otherwise.
     * <br>Removes specified tag from Image.
     *
     * @param tag   Tag to remove from Image
     * @param image Image to have tag removed from
     * @return Whether or not the Image contained the tag
     */
    public boolean removeTag(String tag, Image image) {
        //if(!(image.getTags().size() == 1 && image.getImageName().indexOf(tag) == 1)) {
        String oldName = image.getImageName();
        if (tagManager.removeTagFromImage(image, tag)) {
            logHistory(image, oldName);
            return true;
        } else {
            return false;
        }
        //}
        //return false;
    }

    /**
     * Return all Image objects which contains the specified tag
     *
     * @param tag      Tag to search all Images for
     * @param toSearch Parent directory to search within | null if searching from root
     * @return ArrayList of Images
     */
    public HashSet<Image> findImagesByTag(String tag, Directory toSearch) {
        HashSet<Image> images = tagManager.getImagesByTag(tag);
        if (images == null) {
            return null;
        }
        if (toSearch == null) {
            return images;
        } else {
            // Only return images under the directory toSearch
            for (Image img : images) {
                if (!img.getFilePath().getPath().contains(toSearch.getFile().getPath())) {
                    images.remove(img);
                }
            }
            return images;
        }
    }

    /**
     * Return a HashSet&lt;String&gt; of all the tags which are common between each Image in images.
     *
     * @param images Image objects to look through
     * @return HashSet&lt;String&gt; of tags that are common between each Image in images.
     */
    public HashSet<String> getCommonTags(ArrayList<Image> images) {
        return tagManager.getCommonTags(images);
    }

    /**
     * Move the Image file to the Directory targetDirectory.
     *
     * @param targetDirectory Image file to move.
     * @param targetImage     Directory to move target to.
     * @return Whether or not the move to the Directory was successful
     */
    public boolean moveFileToDirectory(File targetDirectory, File targetImage) {
        Directory newDirectory = search(targetDirectory);
        if (newDirectory == null) {
            return false;
        }

        String newFilePath = newDirectory.getFile() + File.separator + targetImage.getName();
        Image image = currentDirectory.getImage(targetImage);
        if (image == null) {
            return false;
        }

        if (!currentDirectory.equals(newDirectory)) {
            currentDirectory.removeFile(image.getFilePath());
            image.setFilePath(new File(newFilePath), newDirectory);
            newDirectory.addImage(image);
        } else {
            image.setFilePath(new File(newFilePath), newDirectory);
        }

        return true;
    }

    /**
     * Changes the name of the Image to nameToRevertTo and updates the Image's tags as well.
     *
     * @param image          Image to revert the name of
     * @param nameToRevertTo file Name to revert the image to
     */
    public void revertImageName(Image image, String nameToRevertTo) {
        String oldName = image.getImageName();

        HashSet<String> oldTags = image.getTags();

        image.forceFilePath(new File(image.getFilePath().getParent() + File.separator +
                nameToRevertTo));

        HashSet<String> tagsToRevertTo = image.getTags();
        HashSet<String> maintainedTags = new HashSet<>(oldTags); // Tags that exist in both versions
        maintainedTags.retainAll(tagsToRevertTo);

        oldTags.removeAll(maintainedTags); // Tags to remove
        for (String tag : oldTags) {
            removeTag(tag, image);
        }

        tagsToRevertTo.removeAll(maintainedTags); // Tags to add
        for (String tag : tagsToRevertTo) {
            addTag(image, tag);
        }

        logHistory(image, oldName);
    }

    /**
     * Return ArrayList of all the name changes to the Image at filePath in the format:
     * [oldName, newName, timeStamp]
     *
     * @param image the Image object
     * @return ArrayList of all the name changes the Image at filePath undertook.
     */
    public ArrayList<String[]> getImageHistory(Image image) {
        return image.getHistory();
    }

    /**
     * Returns an array list containing the file histories of every file.
     *
     * @return an array list containing all file histories
     */
    public ArrayList<String[]> getAllImageHistories() {
        ArrayList<String[]> listOfHistories = new ArrayList<>();

        for (Image img : rootDirectory.getImagesInSubDirectories()) {
            listOfHistories.addAll(compoundHistoryData(img.getFilePath(), img.getHistory()));
        }

        HashMap<File, ArrayList<String[]>> histories = cachedData.getUnusedHistories();

        for (File key : histories.keySet()) {
            listOfHistories.addAll(compoundHistoryData(key, histories.get(key)));
        }

        return listOfHistories;
    }

    /**
     * Return a compounded version of changes with filePath at the end of each change.
     *
     * @param filePath File path to the targeted Image
     * @param changes  Name changes that occurred to this Image
     * @return a compounded version of changes with filepath at the end of each change
     */
    private ArrayList<String[]> compoundHistoryData(File filePath, ArrayList<String[]> changes) {
        ArrayList<String[]> allChanges = new ArrayList<>();
        for (String[] imageHistory : changes) {
            ArrayList<String> history = new ArrayList<>();
            history.addAll(Arrays.asList(imageHistory));
            String imagePath = filePath.getAbsolutePath();
            history.add(imagePath);
            allChanges.add(history.toArray(new String[0]));
        }
        return allChanges;
    }

    /**
     * Add a log to the Image's history when it's name is changed.
     *
     * @param image   Image to log history for
     * @param oldName Old name of the image
     */
    private void logHistory(Image image, String oldName) {
        // Formats the time stamp in a understandable way
        String time = new SimpleDateFormat("MM, dd, yyyy, HH:mm:ss")
                .format(Calendar.getInstance().getTime());
        int monthsEnd = time.indexOf(',');
        time = MONTHS.get(time.substring(0, monthsEnd)) + time.substring(monthsEnd);

        // Stores the history
        String[] log = {oldName, image.getImageName(), time};

        image.addHistory(log);
    }

    /**
     * Finds and returns the suggested tag for image.
     *
     * @param image the image to find a suggested tag for
     * @return the suggested tag for image
     */
    String getSuggestedTag(Image image) {
        if (image.getSuggestedTag().equals("")) {
            try {
                String loc = image.getFilePath().getCanonicalPath().toLowerCase();
                if (loc.endsWith(".jpg") || loc.endsWith(".jpeg"))
                    return classifier.suggestTag(image.getFilePath().getCanonicalPath());
            } catch (IOException iox) {
                iox.printStackTrace();
            }
        } else {
            return image.getSuggestedTag();
        }
        return "";
    }

    /**
     * Update the config file with the new tags that are available and new imageHistories.
     */
    public void updateConfig() {
        cachedData.writeData(tagManager.getAvailableTags(), rootDirectory
                .getImagesInSubDirectories());
    }
}