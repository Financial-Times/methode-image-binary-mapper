package com.ft.methodeimagebinarymapper.service;

import com.ft.methodeimagebinarymapper.exception.TransformationException;
import com.ft.methodeimagebinarymapper.model.EomFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private static final Logger LOGGER = LoggerFactory.getLogger(ExternalBinaryUrlFilter.class);

    private final List<String> externalBinaryUrlWhitelist;

    public ExternalBinaryUrlFilter(final List<String> externalBinaryUrlWhitelist) {
        this.externalBinaryUrlWhitelist = externalBinaryUrlWhitelist;
    }

    public boolean filter(final EomFile eomFile, final String tid) {
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
            LOGGER.error(String.format("Couldn't process image's attributes for filtering for external binary URL. uuid=%s tid=%s", eomFile.getUuid(), tid), e);
            throw new TransformationException(e);
        }
    }

    private DocumentBuilder getDocumentBuilder() throws ParserConfigurationException {
        final DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        documentBuilderFactory.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
        return documentBuilderFactory.newDocumentBuilder();
    }
}
