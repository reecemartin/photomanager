package com.PhotoManager;

import com.PhotoManager.Controller;
import com.PhotoManager.GUInterface;
import com.PhotoManager.model.Directory;
import com.PhotoManager.model.Image;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.*;

public class ControllerTest {

    private Controller c;
    private Directory d;
    private Image i;

    @BeforeEach
    void setUp() {
        c = new Controller(new File(new File(GUInterface.class.getResource("").getPath())
                .getParentFile().getParentFile().getParentFile() + File.separator + "test-classes", "test"));
        d = c.getRootDirectory();
        i = d.getDirectories().get(0).getImages().get(0);
    }

    @Test
    public void testDirectory() {
        // If initially the current directory is the root directory
        assertTrue(c.getRootDirectory() == c.getCurrentDirectory());

        Directory child = d.getDirectories().get(0);

        // Move from the root to child directory
        c.setCurrentDirectory(child);

        // If the root isn't the child directory
        assertFalse(c.getRootDirectory() == child);

        // If the root isn't the current
        assertFalse(c.getRootDirectory() == c.getCurrentDirectory());

        // If the current directory is the child
        assertTrue(c.getCurrentDirectory() == child);
    }

    @Test
    public void testInvalidTagCharacters() {
        String msg = "Illegal Input";
        assertThrows(IllegalArgumentException.class, () -> c.addTag("he\\llo"));
        assertThrows(IllegalArgumentException.class, () -> c.addTag("he|llo"));
        assertThrows(IllegalArgumentException.class, () -> c.addTag("he<llo"));
        assertThrows(IllegalArgumentException.class, () -> c.addTag("he>llo"));
        assertThrows(IllegalArgumentException.class, () -> c.addTag("he?llo"));
        assertThrows(IllegalArgumentException.class, () -> c.addTag("he:llo"));
        assertThrows(IllegalArgumentException.class, () -> c.addTag("he*llo"));
        assertThrows(IllegalArgumentException.class, () -> c.addTag("hello*"));
        assertThrows(IllegalArgumentException.class, () -> c.addTag("*hello"));
    }

    @Test
    public void testValidTagCharacters() {
        assertTrue(c.addTag("abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ!@#$%^&()" +
                "-_=+[]{}';,.`~"));
    }

    @Test
    public void testAddTag() {
        c.addTag("wow");

        assertTrue(c.getAvailableTags().contains("wow"));

        assertFalse(i.getTags().contains("wow"));
    }

    @Test
    public void testAddTagToImage() {
        c.addTag(i, "wow");

        // Image has the tag
        assertTrue(i.getTags().contains("wow"));
    }

    @Test
    public void testFindImagesByTag() {
        testAddTagToImage();

        assertTrue(c.findImagesByTag("wow", null).contains(i));
        assertTrue(c.findImagesByTag("wow", d).contains(i));

        assertNull(c.findImagesByTag("wowie", null));
        assertNull(c.findImagesByTag("wowie", d));
    }

    @Test
    public void testTagInAvailable() {
        testAddTagToImage();

        assertTrue(c.getAvailableTags().contains("wow"));

        c.addTag("how");

        assertTrue(c.getAvailableTags().contains("how"));
    }

    @Test
    public void testTagInHistory() {
        testAddTagToImage();

        // Single tag in history
        assertEquals("test.jpg", i.getHistory().get(0)[0]);
        assertEquals("test @wow.jpg", i.getHistory().get(0)[1]);

        // Multiple tags in history
        c.addTag(i, "no");
        assertEquals("test @wow.jpg", i.getHistory().get(1)[0]);
        assertEquals("test @wow @no.jpg", i.getHistory().get(1)[1]);

        // Not adding duplicate tag history
        c.addTag(i, "no");
        assertEquals(2, c.getImageHistory(i).size());

        // Not adding history for when the tag isn't added on this image
        c.addTag("what");
        assertEquals(2, c.getImageHistory(i).size());
    }

    @Test
    public void testNoInitialTags() {
        int expected = c.getAvailableTags().size();
        c.addInitialTags(i);
        assertEquals(expected, c.getAvailableTags().size());
    }

    @Test
    public void testAddInitialTags() {
        Image i2 = new Image(d, new File("test" + File.separatorChar + "folder" + File
                .separatorChar + "test1 @1.jpg"), null);
        int expected = c.getAvailableTags().size() + 1;
        c.addInitialTags(i2);
        assertEquals(expected, c.getAvailableTags().size());
        assertTrue(c.getAvailableTags().contains("1"));
    }

    @Test
    public void testGoUpDirectory() {
        // Go to the child directory d
        c.setCurrentDirectory(d);

        assertEquals(d, c.getCurrentDirectory());

        // Go up from d to the root directory
        c.goUpDirectory();

        assertEquals(c.getRootDirectory(), c.getCurrentDirectory());
    }

    @Test
    void testInvalidSearch() {
        assertEquals(null, c.search(new File("test" + File.separatorChar + "dumb")));
    }

    @Test
    public void testRemoveTagFromImage() {
        testAddTagToImage();

        c.removeTag("wow", i);

        assertFalse(i.getTags().contains("wow"));
    }

    @Test
    public void testRemoveTagFromAllImages() {
        testRemoveTagFromAvailable();

        assertFalse(i.getTags().contains("wow"));
    }

    @Test
    public void testRemoveTagFromInvalidImage() {
        testAddTag();

        // Removing tag from an image without the tag
        assertFalse(c.removeTag("wow", i));

        // Making sure the tag is still available
        assertTrue(c.getAvailableTags().contains("wow"));
    }

    @Test
    public void testRemoveTagFromAvailable() {
        testAddTag();

        c.removeTag("hurroThere");

        // Making sure the tag is removed from available
        assertFalse(c.getAvailableTags().contains("hurroThere"));
    }

    @Test
    public void testRemoveInvalidTag() {
        testAddTag();

        assertFalse(c.removeTag("wo"));
        assertFalse(c.removeTag(""));

        assertTrue(c.getAvailableTags().contains("wow"));
    }

    @Test
    public void testGetCommonTagsExist() {
        Image i2 = new Image(d, new File("test" + File.separatorChar + "folder" + File
                .separatorChar + "test1.jpg"), null);

        c.addTag(i, "1");
        c.addTag(i2, "2");
        c.addTag("3");
        c.addTag(i2, "1");

        HashSet<String> tags = c.getCommonTags(new ArrayList<Image>() {{
            add(i);
            add(i2);
        }});

        assertEquals(1, tags.size());
        assertTrue(tags.contains("1"));
    }

    @Test
    public void testGetCommonTagsDoNotExist() {
        Image i2 = new Image(d, new File("test" + File.separatorChar + "folder" + File
                .separatorChar + "test1.jpg"), null);

        c.addTag(i, "1");
        c.addTag(i2, "2");
        c.addTag("3");

        HashSet<String> tags = c.getCommonTags(new ArrayList<Image>() {{
            add(i);
            add(i2);
        }});

        assertEquals(0, tags.size());
    }

    @Test
    public void testMoveFileToDirectory() {
        c.setCurrentDirectory(d.getDirectories().get(0));

        assertFalse(i.getCurrentDirectory() == d);

        c.moveFileToDirectory(d.getFile(), i.getFilePath());

        assertTrue(i.getCurrentDirectory() == d);
    }

    @Test
    public void testRevertImageName() {
        assertEquals(i.getImageName(), "test.jpg");
        c.revertImageName(i, "Howdy partner.jpg");
        assertEquals(i.getImageName(), "Howdy partner.jpg");
    }

    @Test
    public void testRootSearch() {
        Directory root = c.getRootDirectory();
        assertEquals(root, c.search(root.getFile()));
    }

    @Test
    public void testValidSearch() {
        // Currently fails because directories don't exist
        Directory childDirectory = c.getCurrentDirectory().getDirectories().get(0);
        assertEquals(childDirectory, c.search(childDirectory.getFile()));
    }

    @Test
    public void testGetAllImageHistories(){
        String oldName = i.getImageName();
        String tag = UUID.randomUUID().toString().replace("-", "");
        c.addTag(i, tag);
        String newName = i.getImageName();
        String[] history = new String[]{oldName, newName, 0, i.getFilePath().getAbsolutePath()};
        ArrayList<String[]> testHistories = new ArrayList<>();
        histories.add(history);
        ArrayList<String[]> histories = c.getAllImageHistories();
        for (int i = 0; i < histories.size(); i ++){
            ArrayList<String> testH = testHistories.get(i);
            ArrayList<String> h = histories.get(i);
            testH.remove(2);
            h.remove(2);
            assertEquals(testH, h);
        }
}