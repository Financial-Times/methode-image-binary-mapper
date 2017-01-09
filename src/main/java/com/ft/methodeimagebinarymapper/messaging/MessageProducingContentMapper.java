package com.ft.methodeimagebinarymapper.messaging;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ft.messagequeueproducer.model.KeyedMessage;
import com.ft.messaging.standards.message.v1.Message;
import com.ft.methodeimagebinarymapper.exception.ContentMapperException;
import com.ft.methodeimagebinarymapper.model.BinaryContent;
import com.ft.methodeimagebinarymapper.model.EomFile;
import com.ft.methodeimagebinarymapper.service.MethodeImageBinaryMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.core.UriBuilder;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.ResolverStyle;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.ft.api.util.transactionid.TransactionIdUtils.TRANSACTION_ID_HEADER;
import static java.time.ZoneOffset.UTC;

public class MessageProducingContentMapper {

    private static final Logger LOG = LoggerFactory.getLogger(MessageProducingContentMapper.class);
    private static final String CMS_CONTENT_PUBLISHED = "cms-content-published";
    private static final DateTimeFormatter RFC3339_FMT =
            DateTimeFormatter.ISO_OFFSET_DATE_TIME.withResolverStyle(ResolverStyle.STRICT);

    private final MethodeImageBinaryMapper binaryContentMapper;
    private final com.ft.messagequeueproducer.MessageProducer producer;
    private final ObjectMapper objectMapper;
    private final String systemId;
    private final UriBuilder contentUriBuilder;

    public MessageProducingContentMapper(MethodeImageBinaryMapper binaryContentMapper, ObjectMapper objectMapper, String systemId,
                                         com.ft.messagequeueproducer.MessageProducer producer, UriBuilder uriBuilder) {

        this.binaryContentMapper = binaryContentMapper;
        this.objectMapper = objectMapper;
        this.systemId = systemId;
        this.producer = producer;
        this.contentUriBuilder = uriBuilder;
    }

    public BinaryContent mapImageBinary(final EomFile eomFile, String transactionId, Date lastModifiedDate) {
        List<BinaryContent> contents = Collections.singletonList(binaryContentMapper.mapImageBinary(eomFile, transactionId, lastModifiedDate));
        producer.send(contents.stream().map(this::createMessage).collect(Collectors.toList()));
        LOG.info("Sent {} messages", contents.size());
        return contents.get(0);
    }

    private Message createMessage(BinaryContent content) {
        Map<String, Object> messageBody = new LinkedHashMap<>();
        messageBody.put("contentUri", contentUriBuilder.build(content.getUuid()).toString());
        messageBody.put("payload", content.getValue());
        String lastModified = RFC3339_FMT.format(OffsetDateTime.ofInstant(content.getLastModified().toInstant(), UTC));
        messageBody.put("lastModified", lastModified);
        messageBody.put("mediaType", content.getMediaType());
        try {
            Message msg = new Message.Builder().withMessageId(UUID.randomUUID())
                    .withMessageType(CMS_CONTENT_PUBLISHED)
                    .withMessageTimestamp(new Date())
                    .withOriginSystemId(systemId)
                    .withContentType("application/json")
                    .withMessageBody(objectMapper.writeValueAsString(messageBody))
                    .build();

            msg.addCustomMessageHeader(TRANSACTION_ID_HEADER, content.getPublishReference());
            return KeyedMessage.forMessageAndKey(msg, content.getUuid());
        } catch (JsonProcessingException e) {
            LOG.error("Unable to write JSON for message", e);
            throw new ContentMapperException("Unable to write JSON for message", e);
        }
    }
}
