package com.PhotoManager.model;

import java.util.*;

/**
 * Handles all available tags.
 */
public class TagManager {
    private HashMap<String, HashSet<Image>> availableTags;
    private HashSet<String> tagsToNotShow;

    private static final ArrayList<String> INVALID_CHARACTERS = new ArrayList<String>() {{
        add("*");
        add("/");
        add("\\");
        add(":");
        add("?");
        add("\"");
        add("<");
        add(">");
        add("|");
    }};

    /**
     * Creates a TagManager object with all cachedTags.
     *
     * @param cachedTags Iterator full of cached tags
     */
    public TagManager(Iterator<String> cachedTags) {
        availableTags = new HashMap<>();
        while (cachedTags.hasNext()) {
            this.availableTags.put(cachedTags.next(), new HashSet<>());
        }
        this.tagsToNotShow = new HashSet<>();
    }

    /**
     * Return a copy of all available tags that do not contain tagsToNotShow.
     *
     * @return HashSet&lt;String&gt; of all available tags
     */
    public HashSet<String> getAvailableTags() {
        HashSet<String> availableTags = new HashSet<>(Arrays.asList(this.availableTags.keySet()
                .toArray(new String[this.availableTags.size()])));
        availableTags.removeAll(tagsToNotShow);
        return availableTags;
    }

    /**
     * Return true if tag was successfully added or if it's no longer hidden; false otherwise.
     *
     * @param tag Tag to add to availableTags
     * @return Whether or not the tag was successfully added
     * @throws IllegalArgumentException Throw if illegal character is inputted
     */
    public boolean addTagToAvailableTags(String tag) {
        if (!availableTags.containsKey(tag)) {
            for (String invalidCharacter : INVALID_CHARACTERS) {
                if (tag.contains(invalidCharacter)) {
                    // Invalid characters
                    throw new IllegalArgumentException("Illegal Input");
                }
            }
            availableTags.put(tag, new HashSet<>());
            return true;

        } else if (tagsToNotShow.contains(tag)) {
            // Unhide the tag from the user
            tagsToNotShow.remove(tag);
            return true;
        }
        // Already in
        return false;
    }

    /**
     * Remove tag exists, if it's empty remove it, otherwise don't show it when accessing
     * availableTags.
     *
     * @param tag Tag to remove from availableTags
     */
    public void removeTagFromAvailableTags(String tag) {
        if (availableTags.containsKey(tag)) {
            if (availableTags.get(tag).size() == 0) {
                availableTags.remove(tag);
            } else {
                tagsToNotShow.add(tag);
            }
        }
    }

    /**
     * Return true if successfully added tag to img and availableTags, throws
     * IllegalArgumentException if tag contains an illegal character; false otherwise.
     *
     * @param img Image to add tag to
     * @param tag Tag to add to Image and availableTags
     * @return true if successfully added tag to img and availableTags; false otherwise
     * @throws IllegalArgumentException If tag contains an illegal character
     */
    public boolean addTagToImage(Image img, String tag) throws IllegalArgumentException {
        if (availableTags.containsKey(tag)) {
            if (availableTags.get(tag).contains(img)) {
                return false;
            } else {
                availableTags.get(tag).add(img);
                return true;
            }
        } else {
            if (addTagToAvailableTags(tag)) {
                addTagToImage(img, tag);
            } else {
                return false;
            }
        }

        return true; // Never makes it here.
    }

    /**
     * Return true if tag is successfully removed from img; false otherwise.
     *
     * @param img img to remove tag to
     * @param tag tag to remove to img and availableTags
     * @return True if tag is successfully removed from img
     */
    public boolean removeTagFromImage(Image img, String tag) {
        if (availableTags.containsKey(tag) && availableTags.get(tag).contains(img)) {
            availableTags.get(tag).remove(img);
            img.removeTag(tag);
            return true;
        }
        return false;
    }

    /**
     * Return HashSet&lt;Image&gt; of all Images that contain tag.
     *
     * @param tag Tag to check for in Images
     * @return HashSet&lt;Image&gt; of all Images that contain tag
     */
    public HashSet<Image> getImagesByTag(String tag) {
        if (availableTags.containsKey(tag)) {
            return new HashSet<>(availableTags.get(tag));
        } else {
            return null;
        }
    }

    /**
     * Return HashSet&lt;String&gt; of all tags that each Image in images contains.
     *
     * @param images Images to check for the same tag
     * @return HashSet&lt;String&gt; of all tags that each Image in images contains
     */
    public HashSet<String> getCommonTags(ArrayList<Image> images) {
        HashSet<String> commonTags = new HashSet<>();
        HashSet<String> uncommonTags = new HashSet<>();

        for (Image img : images) {
            for (String tag : img.getTags()) {
                if (!commonTags.contains(tag) && !uncommonTags.contains(tag)) {
                    // If the tag hasn't been checked yet
                    if (getImagesByTag(tag).containsAll(images)) {
                        // If the tag is in all input Images
                        commonTags.add(tag);
                    } else {
                        // If the tag isn't in all input images
                        uncommonTags.add(tag);
                    }
                }
            }
        }
        return commonTags;
    }
}
