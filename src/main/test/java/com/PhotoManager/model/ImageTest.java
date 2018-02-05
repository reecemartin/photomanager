package com.PhotoManager.model;

import com.PhotoManager.Controller;

import com.PhotoManager.model.Directory;
import com.PhotoManager.model.Image;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.io.File;
import java.util.UUID;

public class ImageTest {

    private Controller c;
    private Directory d;

    @BeforeEach
    public void setUp() {
        c = new Controller(new File("test.test"));
    }

    @Test
    public void testAddInitialTags() {
        for (int i = 0; i <= 100; i++) {
            setUp();
            String tag = UUID.randomUUID().toString().replace("-", "");
            Image img = new Image(new Directory(c, null,
                    new File("test.test"), null), new File("test.test" +
                    File.separator + "name @" + tag + ".test"), null);
            System.out.println(tag);
            assertEquals(1, img.getTags().size());
            for (String t : img.getTags()) {
                assertEquals(tag, t.replace("@", ""));
            }
            System.out.println("OK");
        }
    }


    @Test
    public void testAddTag() {
        for (int i = 0; i <= 100; i++) {
            setUp();
            Image img = new Image(new Directory(c, null, new File("test.test"), null), new File
                    ("test.test"), null);
            String tag = UUID.randomUUID().toString().replace("-", "");
            System.out.println(tag);
            img.addTag(tag);
            assertEquals(1, img.getTags().size());
            for (String t : img.getTags()) {
                assertEquals(tag, t.replace("@", ""));
            }
            System.out.println("OK");

        }
    }

    @Test
    public void testRemoveTag() {
        for (int i = 0; i <= 100; i++) {
            setUp();
            String tag = UUID.randomUUID().toString().replace("-", "");
            Image img = new Image(new Directory(c, null,
                    new File("test.test"), null), new File("test.test" +
                    File.separator + "name @" + tag + ".test"), null);
            System.out.println(tag);
            img.removeTag(tag);
            assertEquals(0, img.getTags().size());
            System.out.println("OK");
        }
    }

    @Test
    public void testAddOrRemoveTag() {
        for (int i = 0; i <= 100; i++) {
            setUp();
            Image img = new Image(new Directory(c, null, new File("test.test"), null), new File
                    ("test.test"), null);
            String tag = UUID.randomUUID().toString().replace("-", "");
            System.out.println(tag);
            img.addTag(tag);
            assertEquals("test @" + tag + ".test", img.getImageName());
            img.removeTag(tag);
            assertEquals("test.test", img.getImageName());
        }
    }

    @Test
    public void testRemoveAllTags() {
        for (int i = 0; i <= 100; i++) {
            setUp();
            String tag = "";
            Image img = new Image(new Directory(c, null,
                    new File("test.test"), null), new File
                    ("test.test"), null);
            for (int a = 0; a <= 10; a++) {
                tag = UUID.randomUUID().toString().replace("-", "");
                img.addTag(tag);
            }
            img.removeAllTags();
            assertEquals(0, img.getTags().size());
        }
    }

    @Test
    public void testSetFilePath() {
        for (int i = 0; i <= 100; i++) {
            setUp();
            Image img = new Image(new Directory(c, null,
                    new File("test.test"), null), new File
                    ("test.test"), null);
            img.setFilePath(new File("test1.test"));
            assertEquals("test1.test", img.getImageName());
        }
    }

    @Test
    public void testForceFilePath() {
        for (int i = 0; i <= 100; i++) {
            setUp();
            String tag = UUID.randomUUID().toString().replace("-", "");
            Image img = new Image(new Directory(c, null,
                    new File("test.test"), null), new File
                    ("test.test" +
                            File.separator + "name @" + tag + ".test"), null);
            File newF = new File("test.test");
            img.forceFilePath(newF);
            assertEquals(0, img.getTags().size());
            assertEquals("test.test", img.getImageName());
        }
    }


}