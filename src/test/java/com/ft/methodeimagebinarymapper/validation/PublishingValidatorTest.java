package com.ft.methodeimagebinarymapper.validation;

import com.ft.methodeimagebinarymapper.model.EomFile;

import org.junit.Before;
import org.junit.Test;

import java.net.URI;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Date;

import static junit.framework.TestCase.assertFalse;
import static junit.framework.TestCase.assertTrue;

public class PublishingValidatorTest {

    private static final String UUID = "d7625378-d4cd-11e2-bce1-002128161462";

    private PublishingValidator publishingValidator;

    @Before
    public void setUp() {
        publishingValidator = new PublishingValidator();
    }

    @Test
    public void testIsValidImageForPublishing() throws Exception {
        EomFile methodeContent = createSampleMethodeImage("Image", "Sample Image Body".getBytes());
        assertTrue(publishingValidator.isValidImageForPublishing(methodeContent));
    }

    @Test
    public void testIsValidImageForPublishingEmptyBody() throws Exception {
        EomFile methodeContent = createSampleMethodeImage("Image", null);
        assertFalse(publishingValidator.isValidImageForPublishing(methodeContent));
    }

    @Test
    public void testIsValidImageForPublishingNotImage() throws Exception {
        EomFile methodeContent = createSampleMethodeImage("Content", "Sample Image Body".getBytes());
        assertFalse(publishingValidator.isValidImageForPublishing(methodeContent));
    }

    @Test
    public void testIsValidPDFForPublishing() throws Exception {
        EomFile methodeContent = createSampleMethodeImage("Pdf", "Sample PDF Body".getBytes());
        assertTrue(publishingValidator.isValidPDFForPublishing(methodeContent));
    }

    @Test
    public void testIsValidPDFForPublishingNotPDF() throws Exception {
        EomFile methodeContent = createSampleMethodeImage("Content", "Sample PDF Body".getBytes());
        assertFalse(publishingValidator.isValidPDFForPublishing(methodeContent));
    }

    private EomFile createSampleMethodeImage(String type, byte[] imageBody) throws Exception {
        final String attributes = loadFile("sample-attributes.xml");
        final String systemAttributes = loadFile("sample-system-attributes.xml");
        final String usageTickets = loadFile("sample-usage-tickets.xml");
        return new EomFile(UUID, type, imageBody, attributes, "", systemAttributes, usageTickets, new Date());
    }

    private String loadFile(final String filename) throws Exception {
        URL resource = getClass().getClassLoader().getResource(filename);
        if (resource != null) {
            final URI uri = resource.toURI();
            return new String(Files.readAllBytes(Paths.get(uri)), "UTF-8");
        }
        return null;
    }
}
