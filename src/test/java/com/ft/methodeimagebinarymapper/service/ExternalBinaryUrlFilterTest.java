package com.ft.methodeimagebinarymapper.service;

import com.ft.methodeimagebinarymapper.model.EomFile;
import org.junit.Test;

import java.time.Instant;
import java.util.Date;

import static org.junit.Assert.*;

public class ExternalBinaryUrlFilterTest {

    @Test
    public void testPresentExternalUrlIsFiltered() throws Exception {
        EomFile eomFile = new EomFile("uuid", "type", new byte[]{}, "<meta><picture><ExternalUrl>https://i.imgur.com/Pigl1em.png</ExternalUrl></picture></meta>", "workflowStatus", "systemAttributes", "usageTickets", Date.from(Instant.now()));
        ExternalBinaryUrlFilter externalBinaryUrlFilter = new ExternalBinaryUrlFilter();

        assertTrue(externalBinaryUrlFilter.filter(eomFile));
    }

    @Test
    public void testEmptyExternalUrlIsFiltered() throws Exception {
        EomFile eomFile = new EomFile("uuid", "type", new byte[]{}, "<meta><picture><ExternalUrl></ExternalUrl></picture></meta>", "workflowStatus", "systemAttributes", "usageTickets", Date.from(Instant.now()));
        ExternalBinaryUrlFilter externalBinaryUrlFilter = new ExternalBinaryUrlFilter();

        assertFalse(externalBinaryUrlFilter.filter(eomFile));
    }

    @Test
    public void testMissingExternalUrlIsFiltered() throws Exception {
        EomFile eomFile = new EomFile("uuid", "type", new byte[]{}, "<meta><picture></picture></meta>", "workflowStatus", "systemAttributes", "usageTickets", Date.from(Instant.now()));
        ExternalBinaryUrlFilter externalBinaryUrlFilter = new ExternalBinaryUrlFilter();

        assertFalse(externalBinaryUrlFilter.filter(eomFile));
    }
}