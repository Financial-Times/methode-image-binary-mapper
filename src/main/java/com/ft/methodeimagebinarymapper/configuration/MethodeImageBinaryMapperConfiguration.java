package com.ft.methodeimagebinarymapper.configuration;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.ft.platform.dropwizard.AppInfo;
import com.ft.platform.dropwizard.ConfigWithAppInfo;
import io.dropwizard.Configuration;

import java.util.List;

public class MethodeImageBinaryMapperConfiguration extends Configuration implements ConfigWithAppInfo {
    private final ConsumerConfiguration consumer;
    private final ProducerConfiguration producer;
    private final String contentUriPrefix;
    private final List<String> externalBinaryUrlWhitelist;

    @JsonProperty
    private AppInfo appInfo = new AppInfo();

    public MethodeImageBinaryMapperConfiguration(@JsonProperty("consumer") ConsumerConfiguration consumer,
                                                 @JsonProperty("producer") ProducerConfiguration producer,
                                                 @JsonProperty("contentUriPrefix") String contentUriPrefix,
                                                 @JsonProperty("externalBinaryUrlWhitelist") List<String> externalBinaryUrlWhitelist) {
        this.consumer = consumer;
        this.producer = producer;
        this.contentUriPrefix = contentUriPrefix;
        this.externalBinaryUrlWhitelist = externalBinaryUrlWhitelist;
    }


    public ConsumerConfiguration getConsumerConfiguration() {
        return consumer;
    }

    public ProducerConfiguration getProducerConfiguration() {
        return producer;
    }

    public List<String> getExternalBinaryUrlWhitelist() {
        return externalBinaryUrlWhitelist;
    }

    public String getContentUriPrefix() {
        return contentUriPrefix;
    }

    @Override
    public AppInfo getAppInfo() {
        return appInfo;
    }
}
