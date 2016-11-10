package com.ft.methodeimagebinarymapper.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Date;

@JsonIgnoreProperties(ignoreUnknown = true)
public class EomFile {

    private final String uuid;
    private final String type;
    private final byte[] value;
    private final String attributes;
	private final String workflowStatus;
	private final String systemAttributes;
	private final String usageTickets;
    private final Date lastModified;

    public EomFile(@JsonProperty("uuid") String uuid,
                   @JsonProperty("type") String type,
                   @JsonProperty("value") byte[] bytes,
                   @JsonProperty("attributes") String attributes,
                   @JsonProperty("workflowStatus") String workflowStatus,
                   @JsonProperty("systemAttributes") String systemAttributes,
                   @JsonProperty("usageTickets") String usageTickets,
                   @JsonProperty("lastModified") Date lastModified) {
        this.uuid = uuid;
        this.type = type;
        this.value = bytes;
        this.attributes = attributes;
        this.workflowStatus = workflowStatus;
        this.systemAttributes = systemAttributes;
        this.usageTickets = usageTickets;
        this.lastModified = lastModified;
    }

    public String getUuid() {
        return uuid;
    }

    public String getType() {
        return type;
    }

    public byte[] getValue() {
        return value;
    }

    public String getAttributes() {
        return attributes;
    }

	public String getWorkflowStatus() {
		return workflowStatus;
	}

	public String getSystemAttributes() {
		return systemAttributes;
	}

    public String getUsageTickets() {
        return usageTickets;
    }

    public Date getLastModified() {
        return lastModified;
    }

}
