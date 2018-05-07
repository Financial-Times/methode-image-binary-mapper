package com.ft.methodeimagebinarymapper.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ft.methodeimagebinarymapper.model.EomFile;
import com.google.common.io.Files;
import org.junit.Test;

import java.io.File;
import java.time.Instant;
import java.util.Arrays;
import java.util.Date;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ExternalBinaryUrlFilterTest {

    @Test
    public void testPresentExternalUrlIsFiltered() throws Exception {
        EomFile eomFile = new EomFile("uuid", "type", new byte[]{}, "<meta><picture><ExternalUrl>https://ig.ft.com/Pigl1em.png</ExternalUrl></picture></meta>", "workflowStatus", "systemAttributes", "usageTickets", Date.from(Instant.now()));
        ExternalBinaryUrlFilter externalBinaryUrlFilter = new ExternalBinaryUrlFilter(Arrays.asList("https://ig\\.ft\\.com/.*"));

        assertTrue(externalBinaryUrlFilter.filter(eomFile, "tid_123"));
    }

    @Test
    public void testPresentExternalUrlNotConformingWhitelistIsNotFiltered() throws Exception {
        EomFile eomFile = new EomFile("uuid", "type", new byte[]{}, "<meta><picture><ExternalUrl>https://i.imgur.com/Pigl1em.png</ExternalUrl></picture></meta>", "workflowStatus", "systemAttributes", "usageTickets", Date.from(Instant.now()));
        ExternalBinaryUrlFilter externalBinaryUrlFilter = new ExternalBinaryUrlFilter(Arrays.asList("https://ig\\.ft\\.com/.*"));

        assertFalse(externalBinaryUrlFilter.filter(eomFile, "tid_123"));
    }

    @Test
    public void testEmptyExternalUrlIsFiltered() throws Exception {
        EomFile eomFile = new EomFile("uuid", "type", new byte[]{}, "<meta><picture><ExternalUrl></ExternalUrl></picture></meta>", "workflowStatus", "systemAttributes", "usageTickets", Date.from(Instant.now()));
        ExternalBinaryUrlFilter externalBinaryUrlFilter = new ExternalBinaryUrlFilter(Arrays.asList("https://ig\\.ft\\.com/.*"));

        assertFalse(externalBinaryUrlFilter.filter(eomFile, "tid_123"));
    }

    @Test
    public void testMissingExternalUrlIsFiltered() throws Exception {
        EomFile eomFile = new EomFile("uuid", "type", new byte[]{}, "<meta><picture></picture></meta>", "workflowStatus", "systemAttributes", "usageTickets", Date.from(Instant.now()));
        ExternalBinaryUrlFilter externalBinaryUrlFilter = new ExternalBinaryUrlFilter(Arrays.asList("https://ig\\.ft\\.com/.*"));

        assertFalse(externalBinaryUrlFilter.filter(eomFile, "tid_123"));
    }

    @Test
    public void testEmptyClosedExternalUrlIsFiltered() throws Exception {
        EomFile eomFile = new EomFile("uuid", "type", new byte[]{}, "<meta><picture><ExternalUrl /></picture></meta>", "workflowStatus", "systemAttributes", "usageTickets", Date.from(Instant.now()));
        ExternalBinaryUrlFilter externalBinaryUrlFilter = new ExternalBinaryUrlFilter(Arrays.asList("https://ig\\.ft\\.com/.*"));

        assertFalse(externalBinaryUrlFilter.filter(eomFile, "tid_123"));
    }

    @Test
    public void testRealImageIsNotFiltered() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        EomFile eomFile = mapper.readValue(Files.toString(new File("src/test/resources/fddf58e4-4899-11e8-8ae9-4b5ddcca99b3-native.json"), UTF_8), EomFile.class);
        ExternalBinaryUrlFilter externalBinaryUrlFilter = new ExternalBinaryUrlFilter(Arrays.asList("https://ig\\.ft\\.com/.*"));

        assertFalse(externalBinaryUrlFilter.filter(eomFile, "tid_123"));

        eomFile = mapper.readValue(Files.toString(new File("src/test/resources/2dbf9260-4e65-11e8-a7a9-37318e776bab-native.json"), UTF_8), EomFile.class);

        assertFalse(externalBinaryUrlFilter.filter(eomFile, "tid_123"));
    }
}