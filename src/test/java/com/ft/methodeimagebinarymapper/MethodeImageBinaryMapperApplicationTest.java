package com.ft.methodeimagebinarymapper;

import com.ft.message.consumer.MessageListener;
import com.ft.messagequeueproducer.MessageProducer;
import com.ft.messaging.standards.message.v1.Message;
import com.ft.messaging.standards.message.v1.SystemId;

import com.ft.methodeimagebinarymapper.configuration.ConsumerConfiguration;
import com.ft.methodeimagebinarymapper.configuration.MethodeImageBinaryMapperConfiguration;
import com.ft.methodeimagebinarymapper.configuration.ProducerConfiguration;
import com.google.common.io.Files;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import com.sun.jersey.api.client.Client;
import io.dropwizard.setup.Environment;
import io.dropwizard.testing.junit.DropwizardAppRule;
import org.junit.ClassRule;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static com.ft.api.util.transactionid.TransactionIdUtils.TRANSACTION_ID_HEADER;
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.hamcrest.Matchers.endsWith;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;

public class MethodeImageBinaryMapperApplicationTest {

    private static final String CONFIG_FILE = "methode-image-binary-mapper-test.yaml";
    private static final String TRANSACTION_ID = "tid_ptvw9xpnhv";
    private static final String IMAGE_UUID = "dff0c574-4392-11e6-9b66-0712b3873ae1";

    private static MessageListener consumer;

    private static MessageProducer producer = mock(MessageProducer.class);

    public static class StubMethodeImageModelMapperApplication extends MethodeImageBinaryMapperApplication {

        @Override
        protected void startListener(Environment environment, MessageListener listener,
                                     ConsumerConfiguration config, Client consumerClient) {
            consumer = listener;
        }

        @Override
        protected MessageProducer configureMessageProducer(Environment environment, ProducerConfiguration config) {
            return producer;
        }
    }

    @ClassRule
    public static DropwizardAppRule<MethodeImageBinaryMapperConfiguration> appRule =
            new DropwizardAppRule<>(StubMethodeImageModelMapperApplication.class, CONFIG_FILE);

    @SuppressWarnings("unchecked")
    @Test
    public void testMethodeImageBinaryMessageIsProcessed() throws IOException {
        String messageBody = Files.toString(new File("src/test/resources/native-methode-image-model.json"), UTF_8);
        SystemId methodeSystemId = SystemId.systemIdFromCode("methode-web-pub");

        Message message = new Message.Builder()
                .withMessageId(UUID.randomUUID())
                .withMessageType("cms-content-published")
                .withOriginSystemId(methodeSystemId)
                .withMessageTimestamp(new Date())
                .withContentType("application/json")
                .withMessageBody(messageBody)
                .build();
        message.addCustomMessageHeader(TRANSACTION_ID_HEADER, TRANSACTION_ID);

        assertThat(consumer.onMessage(message, TRANSACTION_ID), equalTo(true));

        ArgumentCaptor<List> sent = ArgumentCaptor.forClass(List.class);

        verify(producer).send(sent.capture());
        List<Message> sentMessages = sent.getValue();
        assertThat(sentMessages.size(), equalTo(1));
        checkImageBinaryMessage(sentMessages.get(0), TRANSACTION_ID);
    }

    private void checkImageBinaryMessage(Message actual, String txId) {
        assertThat(actual.getCustomMessageHeader(TRANSACTION_ID_HEADER), equalTo(txId));

        DocumentContext json = JsonPath.parse(actual.getMessageBody());
        String jsonPayload = json.read("$.payload");

        assertThat(json.read("$.contentUri"), endsWith("/image/model/" + IMAGE_UUID));
        assertThat(jsonPayload, equalTo("/9j/4AAQSkZJRgABAQAAAQABAAD/7QPEUGhvdG9zaG9wIDMuMAA4QklNBAQAAAAAA4wcAQAAAg" +
                "ACHAEFADdNRURXQVMsTUVETE9OLE1FRFRPUixBUEMsQVNJQSxBT05MLFJPTkwsVVNBLENBTixTQU0sQklaHAEUAAIACxwBFgAC" +
                "AAEcAR4ABlJUUlBJWBwBKAAIMTM5NTE0ODYcATIAA1BJWBwBPAABNBwBRgAIMjAxNDAzMTgcAVAACzEzMjgzNCswMDAwHAFaAA" +
                "MbJTUcAgAAAgACHAIFABJTQUlOU0JVUlknUy1TQUxFUy8cAgcACE9SSUdJTkFMHAIKAAE0HAIPAAFJHAIUAA9SRVMgUkVUIEZJ" +
                "TiBCSVocAhYADUdNMUVBM0kxTkpaMDEcAhkAFzpyZWw6ZDpibTpHRjJFQTNJMTBYTjAxHAI3AAgyMDE0MDMxOBwCPAALMTMyOD" +
                "M0KzAwMDAcAkEADUpQRUdUT0lJMi9NRUQcAkYACDEuMC4wLjE2HAJQAA5MVUtFIE1BQ0dSRUdPUhwCWgAGTG9uZG9uHAJkAANH" +
                "QlIcAmUADlVuaXRlZCBLaW5nZG9tHAJnAAZMT04xMTUcAmkAPEEgc2hvcHBlciBwYXNzZXMgYSBzaWduYWdlIGZvciBhIFNhaW" +
                "5zYnVyeSdzIHN0b3JlIGluIExvbmRvbhwCbgAHUkVVVEVSUxwCcwAGWDAxOTgxHAJ4AWpBIHNob3BwZXIgcGFzc2VzIGEgc2ln" +
                "bmFnZSBmb3IgYSBTYWluc2J1cnkncyBzdG9yZSBpbiBMb25kb24gTWFyY2ggMTgsIDIwMTQuIEJyaXRhaW4ncyBKIFNhaW5zYn" +
                "VyeSBlbmRlZCBhIG5pbmUteWVhciBydW4gb2YgcXVhcnRlcmx5IHNhbGVzIGdyb3d0aCBvbiBUdWVzZGF5LCB1bmRlcmxpbmlu" +
                "ZyBzbHVnZ2lzaCB0cmFkaW5nIG1vbWVudHVtIGZvciB0aGUgY291bnRyeSdzIG1ham9yIGdyb2NlcnMgaW4gdGhlIGVhcmx5IG" +
                "1vbnRocyBvZiAyMDE0IGFuZCByZWZsZWN0aW5nIGEgdG91Z2ggY29tcGFyYXRpdmUgZmlndXJlIGxhc3QgeWVhci4gICBSRVVU" +
                "RVJTL0x1a2UgTWFjR3JlZ29yICAoQlJJVEFJTiAtIFRhZ3M6IEJVU0lORVNTKRwCegAFTE0vSkccAoIAAjNTHAKHAAJlbhwHCg" +
                "ABABwHFAAEf////xwHWgAEAAr0ojhCSU0D7QAAAAAAEABIAAAAAQACAEgAAAABAAL/2wBDAAUDBAQEAwUEBAQFBQUGBwwIBwcH" +
                "Bw8LCwkMEQ8SEhEPERETFhwXExQaFRERGCEYGh0dHx8fExciJCIeJBweHx7/2wBDAQUFBQcGBw4ICA4eFBEUHh4eHh4eHh4eHh" +
                "4eHh4eHh4eHh4eHh4eHh4eHh4eHh4eHh4eHh4eHh4eHh4eHh4eHh7/wAARCASACAADASIAAhEBAxEB/8QAHwAAAQUBAQEBAQEA" +
                "AAAAAAAAAAECAwQFBgcICQoL/8QAtRAAAgEDAwIEAwUFBAQAAAF9AQIDAAQRBRIhMUEGE1FhByJxFDKBkaEII0KxwRVS0fAkM2" +
                "JyggkKFhcYGRolJicoKSo0NTY3ODk6Q0RFRkdISUpTVFVWV1hZWmNkZWZnaGlqc3R1dnd4eXqDhIWGh4iJipKTlJWWl5iZmqKj" +
                "pKWmp6ipqrKztLW2t7i5usLDxMXGx8jJytLT1NXW19jZ2uHi4+Tl5ufo6erx8vP09fb3+Pn6/8QAHwEAAwEBAQEBAQEBAQAAAA" +
                "AAAAECAwQFBgcICQoL/8QAtREAAgECBAQDBAcFBAQAAQJ3AAECAxEEBSExBhJBUQdhcRMiMoEIFEKRobHBCSMzUvAVYnLRChYk" +
                "NOEl8RcYGRomJygpKjU2Nzg5OkNERUZHSElKU1RVVldYWVpjZGVmZ2hpanN0dXZ3eHl6goOEhYaHiImKkpOUlZaXmJmaoqOkpa" +
                "anqKmqsrO0tba3uLm6wsPExcbHyMnK0tPU1dbX2Nna4uPk5ebn6Onq8vP09fb3+Pn6"));
    }

    @Test
    public void testMessageFromInvalidSourceIsDiscarded() throws IOException {
        String messageBody = Files.toString(new File("src/test/resources/native-wp-content.json"), UTF_8);
        SystemId methodeSystemId = SystemId.systemIdFromCode("wordpress");

        Message message = new Message.Builder()
                .withMessageId(UUID.randomUUID())
                .withMessageType("cms-content-published")
                .withOriginSystemId(methodeSystemId)
                .withMessageTimestamp(new Date())
                .withContentType("application/json")
                .withMessageBody(messageBody)
                .build();

        assertThat(consumer.onMessage(message, TRANSACTION_ID), equalTo(true));

        verifyZeroInteractions(producer);
    }

    @Test
    public void testEmptyPayloadMessageIsDiscarded() throws IOException {
        String messageBody = Files.toString(new File("src/test/resources/native-empty-payload-image-model.json"), UTF_8);
        SystemId methodeSystemId = SystemId.systemIdFromCode("methode-web-pub");

        Message message = new Message.Builder()
                .withMessageId(UUID.randomUUID())
                .withMessageType("cms-content-published")
                .withOriginSystemId(methodeSystemId)
                .withMessageTimestamp(new Date())
                .withContentType("application/json")
                .withMessageBody(messageBody)
                .build();
        message.addCustomMessageHeader(TRANSACTION_ID_HEADER, TRANSACTION_ID);

        assertThat(consumer.onMessage(message, TRANSACTION_ID), equalTo(true));
        verifyZeroInteractions(producer);
    }


}
