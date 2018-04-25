package com.ft.methodeimagebinarymapper.service;

import com.ft.methodeimagebinarymapper.exception.TransformationException;
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
import java.util.List;

public class ExternalBinaryUrlFilter {

    private final List<String> externalBinaryUrlWhitelist;

    public ExternalBinaryUrlFilter(final List<String> externalBinaryUrlWhitelist) {
        this.externalBinaryUrlWhitelist = externalBinaryUrlWhitelist;
    }

    public boolean filter(EomFile eomFile) {
        try {
            final DocumentBuilder documentBuilder = getDocumentBuilder();
            final XPath xpath = XPathFactory.newInstance().newXPath();
            final Document attributesDocument = documentBuilder.parse(new InputSource(new StringReader(eomFile.getAttributes())));
            final String externalBinaryUrl = xpath.evaluate("/meta/picture/ExternalUrl", attributesDocument);
            for (final String sample : externalBinaryUrlWhitelist) {
                if (externalBinaryUrl.matches(sample)) {
                    return true;
                }
            }
            return false;
        } catch (ParserConfigurationException | IOException | XPathExpressionException | SAXException e) {
            throw new TransformationException(e);
        }
    }

    private DocumentBuilder getDocumentBuilder() throws ParserConfigurationException {
        final DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        documentBuilderFactory.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
        return documentBuilderFactory.newDocumentBuilder();
    }
}
