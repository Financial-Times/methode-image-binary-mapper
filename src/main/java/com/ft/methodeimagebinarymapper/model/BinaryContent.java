package com.ft.methodeimagebinarymapper.model;

import java.util.Date;

public class BinaryContent {
    private final String uuid;
    private final byte[] value;
    private final Date lastModified;
    private final String publishReference;

    public BinaryContent(String uuid, byte[] value, Date lastModified, String publishReference) {
        this.uuid = uuid;
        this.value = value;
        this.lastModified = lastModified;
        this.publishReference = publishReference;
    }

    public String getUuid() {
        return uuid;
    }

    public byte[] getValue() {
        return value;
    }

    public Date getLastModified() {
        return lastModified;
    }

    public String getPublishReference() {
        return publishReference;
    }
}
