/* (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * (c) 2001 - 2013 OpenPlans
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.web.data.store.panel;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

import org.apache.wicket.model.Model;
import org.geoserver.data.test.MockData;
import org.geoserver.web.util.MapModel;
import org.geotools.data.DataUtilities;
import org.junit.Assume;
import org.junit.Before;
import org.junit.Test;

public class FileModelTest {

    File root;

    @Before
    public void init() throws IOException {
        root = File.createTempFile("file", "test", new File("target"));
        root.delete();
        root.mkdirs();
    }

    @Test
    public void testAbsolute() throws IOException {
        // create test file
        File f = new File("target/fileModelTest.xml");
        try {
            f.createNewFile();
            FileModel model = new FileModel(new Model<String>(), root);
            model.setObject(f.getAbsolutePath());
            String path = (String) model.getObject();
            assertEquals(DataUtilities.fileToURL(f).toString(), path);
            assertTrue("Expected '" + path + "' to end with 'target/fileModelTest.xml'",
                    path.endsWith("target/fileModelTest.xml"));
        } finally {
            f.delete();
        }
    }

    @Test
    public void testAbsoluteSpaceNonAscii() throws IOException {
        Assume.assumeTrue(Charset.defaultCharset().newEncoder().canEncode("café"));
        // create test file
        File f = new File("target/file café.xml");
        try {
            f.createNewFile();
            FileModel model = new FileModel(new Model<String>(), root);
            model.setObject(f.getAbsolutePath());
            String path = (String) model.getObject();
            assertEquals(DataUtilities.fileToURL(f).toString(), path);
            assertTrue("Expected '" + path + "' to end with 'target/file%20caf%C3%A9.xml'",
                    path.endsWith("target/file%20caf%C3%A9.xml"));
        } finally {
            f.delete();
        }
    }

    @Test
    public void testAbsoluteToRelative() throws IOException {
        // pick up an existing file
        File data = new File(root, "data");
        File cite = new File(data, MockData.CITE_PREFIX);
        File buildings = new File(cite, "Buildings.properties");
        FileModel model = new FileModel(new Model<String>(), root);
        model.setObject(buildings.getAbsolutePath());
        String path = (String) model.getObject();
        assertEquals("file:data/cite/Buildings.properties", path);
    }

    @Test
    public void testAbsoluteToRelativeSpaceNonAscii() throws IOException {
        Assume.assumeTrue(Charset.defaultCharset().newEncoder().canEncode("café"));
        // create test file
        File f = new File(root, "file café.xml");
        try {
            f.createNewFile();
            FileModel model = new FileModel(new Model<String>(), root);
            model.setObject(f.getAbsolutePath());
            String path = (String) model.getObject();
            assertEquals("file:file%20caf%C3%A9.xml", path);
        } finally {
            f.delete();
        }
    }

    @Test
    public void testRelativeUnmodified() throws IOException {
        FileModel model = new FileModel(new Model<String>(), root);
        model.setObject("file:data/cite/Buildings.properties");
        String path = (String) model.getObject();
        assertEquals("file:data/cite/Buildings.properties", path);
    }

    @Test
    public void testRelativeUnmodifiedSpaceNonAscii() throws IOException {
        FileModel model = new FileModel(new Model<String>(), root);
        model.setObject("file:data/file%20caf%C3%A9.xml");
        String path = (String) model.getObject();
        assertEquals("file:data/file%20caf%C3%A9.xml", path);
    }

    @Test
    public void testUrl() throws IOException {
        Map map = new HashMap();
        map.put("url", new URL("file:data/cite/Buildings.properties"));
        MapModel mapModel = new MapModel(map, "url");
        FileModel model = new FileModel(mapModel, root);
        String path = (String) model.getObject();
        assertEquals("file:data/cite/Buildings.properties", path);
    }

    @Test
    public void testUrlSpaceNonAscii() throws IOException {
        Map map = new HashMap();
        map.put("url", new URL("file:data/file%20caf%C3%A9.xml"));
        MapModel mapModel = new MapModel(map, "url");
        FileModel model = new FileModel(mapModel, root);
        String path = (String) model.getObject();
        assertEquals("file:data/file%20caf%C3%A9.xml", path);
    }

}
