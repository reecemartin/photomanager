package com.PhotoManager.model;

import com.PhotoManager.GUInterface;

import java.io.*;
import java.util.*;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A manager to regulate the getting and setting of contents for the config file.
 */
public class ConfigManager {

    /**
     * File path to the config file.
     **/
    private final static File CONFIG_FILE = new File(new File(GUInterface.class
            .getResource("").getPath()).getParentFile().getParentFile(), "config.txt");

    private HashMap<File, ArrayList<String[]>> cachedHistories;

    private HashSet<String> cachedTags;

    private Logger logger = Logger.getLogger(Directory.class.getName());

    public ConfigManager() {
        initializeCachedData();
    }

    /**
     * Return Iterator of all tags that are cached in the config.txt.
     *
     * @return Iterator of all tags that are cached in the config.txt
     */
    public Iterator<String> getCachedTags() {
        return cachedTags.iterator();
    }

    /**
     * Return Image history if it exists at the filePath &lt;filePath&gt;.
     *
     * @param filePath File path to an Image file
     * @return Image history of Image at &lt;filePath&gt; | null
     */
    public ArrayList<String[]> getImageHistory(File filePath) {
        if (cachedHistories != null && cachedHistories.containsKey(filePath)) {
            ArrayList<String[]> temp = cachedHistories.get(filePath);
            cachedHistories.remove(filePath);
            return temp;
        } else {
            return null;
        }
    }

    /**
     * Return HashMap&lt;File, ArrayList&lt;String[]&gt;&gt; of all unused File histories.
     *
     * @return HashMap&lt;File, ArrayList&lt;String[]&gt;&gt; of all unused File histories
     */
    public HashMap<File, ArrayList<String[]>> getUnusedHistories() {
        return new HashMap<>(cachedHistories);
    }

    /**
     * Return the cached histories in config.txt for each Image that still exists in a
     * HashMap&lt;File, ArrayList&lt;String[]&gt;&gt;.
     */
    private void initializeCachedData() {
        // Check if the file exists.
        cachedTags = new HashSet<>();
        cachedHistories = new HashMap<>();
        if (CONFIG_FILE.exists()) {
            ArrayList<String[]> histories;
            try {
                BufferedReader file = new BufferedReader(new FileReader(CONFIG_FILE));
                String line = file.readLine();
                if (line != null) {
                    if (!line.equals("")) {
                        cachedTags.addAll(Arrays.asList(line.split(", ")));
                    }
                    line = file.readLine(); // First Image's history
                    // While there is another PhotoManager.model.Image's history to look at
                    while (line != null) {
                        histories = new ArrayList<>();
                        String[] lineElements = line.split("\\|");
                        File filePath = new File(lineElements[0]);
                        // Only return the PhotoManager.model.Image history if that Image still exists.
                        if (filePath.exists()) {
                            cachedHistories.put(filePath, histories);

                            for (int i = 1; i < lineElements.length; i++) {
                                histories.add(lineElements[i].split(";"));
                            }
                        }
                        line = file.readLine();
                    }
                }

            } catch (IOException e) {
                logger.log(Level.WARNING, e.toString());
                logger.addHandler(new ConsoleHandler());
            }
        }
    }

    /**
     * Write tags and fileHistory to the config file in the format:
     * <br>filePath1|oldName,newName,timeStamp|oldName,newName,timeStamp..
     * <br>filePath2|..
     *
     * @param images ArrayList of String of all Images
     */
    public void writeData(HashSet<String> availableTags, ArrayList<Image> images) {
        try {
            FileWriter writer = new FileWriter(CONFIG_FILE);

            writer.write(availableTags.toString().substring(1, availableTags.toString().length()
                    - 1) + "\n");

            for (Image image : images) {
                File imageFile = image.getFilePath();

                ArrayList<String[]> imageHistory = image.getHistory();

                if (imageHistory.size() != 0) {
                    writer.write(compileFileLine(imageFile, imageHistory));
                }
            }

            for (File key : cachedHistories.keySet()) {
                writer.write(compileFileLine(key, cachedHistories.get(key)));
            }

            writer.close();
        } catch (java.io.IOException e) {
            logger.log(Level.WARNING, "Error: " + e);
            logger.addHandler(new ConsoleHandler());
        }
    }

    /**
     * Compile the changes in changes in the format:
     * <br>imageFile|oldName,newName,time stamp|oldName,newName,time stamp
     *
     * @param imageFile File path of an Image
     * @param changes   Array of changes that occurred for the imageFile
     * @return Formatted changes for imageFile
     */
    private String compileFileLine(File imageFile, ArrayList<String[]> changes) {
        String dataLine = imageFile + "|"; // Line to write to file

        for (String[] fileChange : changes) {
            for (int i = 0; i < 3; i++) {
                dataLine += fileChange[i];

                if (i != 2) {
                    dataLine += ";";
                } else {
                    dataLine += "|";
                }
            }
        }

        return dataLine.substring(0, dataLine.length() - 1) + "\n";

    }
}
