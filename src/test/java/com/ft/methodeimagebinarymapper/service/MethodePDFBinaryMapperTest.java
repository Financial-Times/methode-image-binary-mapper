package com.ft.methodeimagebinarymapper.service;

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


public class MethodePDFBinaryMapperTest {

  private static final String UUID = "d7625378-d4cd-11e2-bce1-002128161462";
  private static final String TX_ID = "junittx";
  private static final byte[] PDF_BINARY = "A PDF File".getBytes();

  private MethodePDFBinaryMapper mapper;

  @Before
  public void setUp(){
    mapper = new MethodePDFBinaryMapper();
  }

  @Test
  public void thatMappingIsSuccessful() throws Exception {
    EomFile eomFile = createSamplePDF();
    Date lastModified = new Date();
    BinaryContent actual = mapper.mapBinary(eomFile, TX_ID, lastModified);

    assertThat(actual.getUuid(), equalTo(UUID));
    assertThat(actual.getPublishReference(), equalTo(TX_ID));
    assertThat(actual.getLastModified(), equalTo(lastModified));
    assertThat(actual.getMediaType(), equalTo("application/pdf"));
    assertThat(actual.getValue(), equalTo(PDF_BINARY));
  }

  private EomFile createSamplePDF() throws Exception {
    final String attributes = loadFile("sample-pdf-attributes.xml");
    final String systemAttributes = loadFile("sample-pdf-system-attributes.xml");
    final String usageTickets = loadFile("sample-pdf-usage-tickets.xml");
    return new EomFile(UUID, "Pdf", PDF_BINARY, attributes, "", systemAttributes, usageTickets, new Date());
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
