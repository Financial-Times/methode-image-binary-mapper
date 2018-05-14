package com.ft.methodeimagebinarymapper.messaging;

import com.ft.messagequeueproducer.MessageProducer;
import com.ft.messagequeueproducer.model.KeyedMessage;
import com.ft.messaging.standards.message.v1.Message;
import com.ft.messaging.standards.message.v1.MessageType;
import com.ft.messaging.standards.message.v1.SystemId;
import com.ft.methodeimagebinarymapper.exception.ContentMapperException;
import com.ft.methodeimagebinarymapper.exception.TransformationException;
import com.ft.methodeimagebinarymapper.model.BinaryContent;
import com.ft.methodeimagebinarymapper.model.EomFile;
import com.ft.methodeimagebinarymapper.service.ExternalBinaryUrlFilter;
import com.ft.methodeimagebinarymapper.service.MethodeImageBinaryMapper;
import com.ft.methodeimagebinarymapper.service.MethodePDFBinaryMapper;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.time.OffsetDateTime;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.ws.rs.core.UriBuilder;

import static com.ft.api.util.transactionid.TransactionIdUtils.TRANSACTION_ID_HEADER;
import static com.ft.messaging.standards.message.v1.MediaType.JSON;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class MessageProducingContentMapperTest {

  private static final ObjectMapper JACKSON_MAPPER = new ObjectMapper();
  private static final SystemId SYSTEM_ID = SystemId.systemId("junit_system");
  private static final UriBuilder URI_BUILDER = UriBuilder.fromUri("http://www.example.org/content").path("{uuid}");
  private static final String BINARY_VALUE = "Test Content";
  private static final String PUBLISH_REF = "junit12345";
  private static final MessageType CMS_CONTENT_PUBLISHED = MessageType.messageType("cms-content-published");
  private static final String IMAGE_MEDIA_TYPE = "image/jpeg";
  private static final String PDF_MEDIA_TYPE = "application/pdf";

  private MessageProducingContentMapper mapper;

  @Mock
  private MethodeImageBinaryMapper imageBinaryMapper;
  @Mock
  private MethodePDFBinaryMapper pdfBinaryMapper;
  @Mock
  private ExternalBinaryUrlFilter externalBinaryUrlFilter;
  @Mock
  private MessageProducer producer;
  @Mock
  private EomFile incoming;

  @SuppressWarnings("unchecked")
  @Test
  public void thatImageMessageIsProducedAndSent() throws Exception {
    mapper = new MessageProducingContentMapper(imageBinaryMapper, pdfBinaryMapper, externalBinaryUrlFilter, JACKSON_MAPPER, SYSTEM_ID.toString(), producer, URI_BUILDER);

    UUID uuid = UUID.randomUUID();
    Date lastModified = new Date();
    BinaryContent content = new BinaryContent(uuid.toString(), BINARY_VALUE.getBytes(), lastModified, PUBLISH_REF, IMAGE_MEDIA_TYPE);

    when(imageBinaryMapper.mapImageBinary(any(EomFile.class), eq(PUBLISH_REF), eq(lastModified))).thenReturn(content);

    BinaryContent actual = mapper.mapImageBinary(incoming, PUBLISH_REF, lastModified);

    assertThat(actual, equalTo(content));

    @SuppressWarnings("rawtypes")
    ArgumentCaptor<List> listCaptor = ArgumentCaptor.forClass(List.class);
    verify(producer).send(listCaptor.capture());

    List<Message> messages = listCaptor.getValue();
    assertThat(messages.size(), equalTo(1));

    Message actualMessage = messages.get(0);
    verifyMessage(actualMessage, uuid, lastModified, IMAGE_MEDIA_TYPE, content);
  }

  @SuppressWarnings("unchecked")
  @Test
  public void thatPDFMessageIsProducedAndSent() throws Exception {
    mapper = new MessageProducingContentMapper(imageBinaryMapper, pdfBinaryMapper, externalBinaryUrlFilter, JACKSON_MAPPER, SYSTEM_ID.toString(), producer, URI_BUILDER);

    UUID uuid = UUID.randomUUID();
    Date lastModified = new Date();
    BinaryContent content = new BinaryContent(uuid.toString(), BINARY_VALUE.getBytes(), lastModified, PUBLISH_REF, PDF_MEDIA_TYPE);

    when(pdfBinaryMapper.mapBinary(any(EomFile.class), eq(PUBLISH_REF), eq(lastModified))).thenReturn(content);

    BinaryContent actual = mapper.mapPDFBinary(incoming, PUBLISH_REF, lastModified);

    assertThat(actual, equalTo(content));

    @SuppressWarnings("rawtypes")
    ArgumentCaptor<List> listCaptor = ArgumentCaptor.forClass(List.class);
    verify(producer).send(listCaptor.capture());

    List<Message> messages = listCaptor.getValue();
    assertThat(messages.size(), equalTo(1));

    Message actualMessage = messages.get(0);
    verifyMessage(actualMessage, uuid, lastModified, PDF_MEDIA_TYPE, content);
  }

  @SuppressWarnings({"unchecked", "rawtypes"})
  private void verifyMessage(Message actualMessage, UUID expectedUuid, Date expectedLastModified, String expectedMediaType, BinaryContent expectedContent)
          throws Exception {

    assertThat(actualMessage.getMessageType(), equalTo(CMS_CONTENT_PUBLISHED));
    assertThat(actualMessage.getOriginSystemId(), equalTo(SYSTEM_ID));
    assertThat(actualMessage.getContentType(), equalTo(JSON));
    assertThat(actualMessage.getCustomMessageHeader(TRANSACTION_ID_HEADER), equalTo(PUBLISH_REF));

    Map messageBody = JACKSON_MAPPER.readValue(actualMessage.getMessageBody(), Map.class);
    assertThat(messageBody.get("contentUri"), equalTo("http://www.example.org/content/" + expectedUuid.toString()));
    assertThat(OffsetDateTime.parse((String) messageBody.get("lastModified")).toInstant(), equalTo(expectedLastModified.toInstant()));
    assertThat(messageBody.get("mediaType"), equalTo(expectedMediaType));

    Object payload = messageBody.get("payload");
    assertThat(payload, instanceOf(String.class));
    String actualContent = (String) payload;

    assertThat(actualContent, equalTo(Base64.getEncoder().encodeToString(expectedContent.getValue())));

    assertThat(actualMessage instanceof KeyedMessage, equalTo(true));
    assertThat(((KeyedMessage) actualMessage).getKey(), equalTo(expectedUuid.toString()));
  }

  @Test(expected = ContentMapperException.class)
  public void thatNoMessageIsSentWhenObjectMapperFails()
          throws Exception {

    ObjectMapper failing = mock(ObjectMapper.class);
    mapper = new MessageProducingContentMapper(imageBinaryMapper, pdfBinaryMapper, externalBinaryUrlFilter, failing, SYSTEM_ID.toString(), producer, URI_BUILDER);

    UUID uuid = UUID.randomUUID();
    Date lastModified = new Date();
    BinaryContent content = new BinaryContent(uuid.toString(), BINARY_VALUE.getBytes(), lastModified, PUBLISH_REF, IMAGE_MEDIA_TYPE);

    when(imageBinaryMapper.mapImageBinary(any(EomFile.class), eq(PUBLISH_REF), eq(lastModified))).thenReturn(content);

    when(failing.writeValueAsString(any())).thenThrow(new JsonGenerationException("test exception"));

    try {
      mapper.mapImageBinary(incoming, PUBLISH_REF, lastModified);
    } finally {
      verifyZeroInteractions(producer);
    }
  }

  @SuppressWarnings("unchecked")
  @Test
  public void thatNoMessageIsSentWhenExternalBinaryUrlDetected() throws Exception {
    mapper = new MessageProducingContentMapper(imageBinaryMapper, pdfBinaryMapper, externalBinaryUrlFilter, JACKSON_MAPPER, SYSTEM_ID.toString(), producer, URI_BUILDER);
    UUID uuid = UUID.randomUUID();
    Date lastModified = new Date();
    BinaryContent content = new BinaryContent(uuid.toString(), BINARY_VALUE.getBytes(), lastModified, PUBLISH_REF, IMAGE_MEDIA_TYPE);
    when(externalBinaryUrlFilter.filter(any(EomFile.class), anyString())).thenReturn(true);
    when(imageBinaryMapper.mapImageBinary(any(EomFile.class), eq(PUBLISH_REF), eq(lastModified))).thenReturn(content);

    BinaryContent actual = mapper.mapImageBinary(incoming, PUBLISH_REF, lastModified);

    assertNull(actual);
  }

  @SuppressWarnings("unchecked")
  @Test(expected = TransformationException.class)
  public void thatNoMessageIsSentWhenExternalBinaryUrlDetectionFails() throws Exception {
    mapper = new MessageProducingContentMapper(imageBinaryMapper, pdfBinaryMapper, externalBinaryUrlFilter, JACKSON_MAPPER, SYSTEM_ID.toString(), producer, URI_BUILDER);
    UUID uuid = UUID.randomUUID();
    Date lastModified = new Date();
    BinaryContent content = new BinaryContent(uuid.toString(), BINARY_VALUE.getBytes(), lastModified, PUBLISH_REF, IMAGE_MEDIA_TYPE);
    when(externalBinaryUrlFilter.filter(any(EomFile.class), anyString())).thenThrow(new TransformationException(new RuntimeException("just because")));
    when(imageBinaryMapper.mapImageBinary(any(EomFile.class), eq(PUBLISH_REF), eq(lastModified))).thenReturn(content);

    mapper.mapImageBinary(incoming, PUBLISH_REF, lastModified);
  }
}
