package com.ft.methodeimagebinarymapper.service;

import com.ft.methodeimagebinarymapper.exception.TransformationException;
import com.ft.methodeimagebinarymapper.model.BinaryContent;
import com.ft.methodeimagebinarymapper.model.EomFile;
import org.junit.Before;
import org.junit.Test;

import java.net.URI;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Date;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;


public class MethodeImageBinaryMapperTest {

    private static final String UUID = "d7625378-d4cd-11e2-bce1-002128161462";
    private static final String TX_ID = "junittx";

    private MethodeImageBinaryMapper mapper;

    @Before
    public void setUp(){
        mapper = new MethodeImageBinaryMapper();
    }

    @Test
    public void thatMappingIsSuccessfulWhenContentIsValid() throws Exception {
        EomFile eomFile = createSampleMethodeImage("sample-system-attributes.xml");
        BinaryContent actual = mapper.mapImageBinary(eomFile, TX_ID, new Date());
        assertThat(actual.getUuid(), equalTo(UUID));
        assertThat(actual.getPublishReference(), equalTo(TX_ID));
        assertThat(actual.getMediaType(), equalTo("image/jpeg"));
    }

    @Test
    public void thatMappingIsSuccessfulWhenMediaTypeIsMissing() throws Exception {
        EomFile eomFile = createSampleMethodeImage("sample-system-attributes-missing-media-type.xml");
        BinaryContent actual = mapper.mapImageBinary(eomFile, TX_ID, new Date());
        assertThat(actual.getUuid(), equalTo(UUID));
        assertThat(actual.getPublishReference(), equalTo(TX_ID));
        assertThat(actual.getMediaType(), equalTo("image/jpeg"));
    }

    @Test(expected = TransformationException.class)
    public void thatExceptionIsThrownWhenContentIsInvalid() throws Exception {
        EomFile eomFile = createSampleMethodeImage("sample-system-attributes-invalid.xml");
        mapper.mapImageBinary(eomFile, TX_ID, new Date());
    }

    private EomFile createSampleMethodeImage(String systemAttributesFile) throws Exception {
        final String attributes = loadFile("sample-attributes.xml");
        final String systemAttributes = loadFile(systemAttributesFile);
        final String usageTickets = loadFile("sample-usage-tickets.xml");
        return new EomFile(UUID, "Image", null, attributes, "", systemAttributes, usageTickets, new Date());
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
