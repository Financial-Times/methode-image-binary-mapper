package com.ft.methodeimagebinarymapper.service;

import com.ft.methodeimagebinarymapper.exception.TransformationException;
import com.ft.methodeimagebinarymapper.model.BinaryContent;
import com.ft.methodeimagebinarymapper.model.EomFile;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.IOException;
import java.io.StringReader;
import java.util.Date;

public class MethodePDFBinaryMapper {

  private static final String DEFAULT_MEDIA_TYPE = "application/pdf";

  public BinaryContent mapBinary(EomFile eomFile, String transactionId, Date lastModifiedDate) {
    return new BinaryContent(eomFile.getUuid(), eomFile.getValue(), lastModifiedDate, transactionId, DEFAULT_MEDIA_TYPE);
  }
}
