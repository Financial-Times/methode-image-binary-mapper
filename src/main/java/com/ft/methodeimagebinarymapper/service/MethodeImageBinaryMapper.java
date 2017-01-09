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

public class MethodeImageBinaryMapper {

    private static final String DEFAULT_MEDIA_TYPE = "image/jpeg";
    private static final String MEDIA_TYPE_PREFIX = "image/";

    public BinaryContent mapImageBinary(EomFile eomFile, String transactionId, Date lastModifiedDate) {
        String mediaType = getMediaType(eomFile);
        return new BinaryContent(eomFile.getUuid(), eomFile.getValue(), lastModifiedDate, transactionId, mediaType);
    }

    private String getMediaType(EomFile eomFile) {
        String mediaType = DEFAULT_MEDIA_TYPE;
        try {
            final DocumentBuilder documentBuilder = getDocumentBuilder();
            final XPath xpath = XPathFactory.newInstance().newXPath();
            final Document systemAttributesDocument = documentBuilder.parse(new InputSource(new StringReader(eomFile.getSystemAttributes())));
            final String mediaTypeSuffix = xpath.evaluate("/props/imageInfo/fileType", systemAttributesDocument);
            if (!mediaTypeSuffix.isEmpty()) {
                mediaType = MEDIA_TYPE_PREFIX + mediaTypeSuffix.toLowerCase();
            }
        } catch (ParserConfigurationException | IOException | XPathExpressionException | SAXException e) {
            throw new TransformationException(e);
        }
        return mediaType;
    }

    private DocumentBuilder getDocumentBuilder() throws ParserConfigurationException {
        final DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        documentBuilderFactory.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
        return documentBuilderFactory.newDocumentBuilder();
    }

}
