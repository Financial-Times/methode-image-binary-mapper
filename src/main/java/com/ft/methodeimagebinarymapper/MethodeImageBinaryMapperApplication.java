package com.ft.methodeimagebinarymapper;

import com.codahale.metrics.health.HealthCheckRegistry;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ft.api.util.buildinfo.BuildInfoResource;
import com.ft.api.util.buildinfo.VersionResource;
import com.ft.jerseyhttpwrapper.ResilientClientBuilder;
import com.ft.message.consumer.MessageListener;
import com.ft.message.consumer.MessageQueueConsumerInitializer;
import com.ft.messagequeueproducer.QueueProxyProducer;
import com.ft.methodeimagebinarymapper.configuration.ConsumerConfiguration;
import com.ft.methodeimagebinarymapper.configuration.MethodeImageBinaryMapperConfiguration;
import com.ft.methodeimagebinarymapper.configuration.ProducerConfiguration;
import com.ft.methodeimagebinarymapper.health.CanConnectToMessageQueueProducerProxyHealthcheck;
import com.ft.methodeimagebinarymapper.messaging.MessageProducingContentMapper;
import com.ft.methodeimagebinarymapper.messaging.NativeCmsPublicationEventsListener;
import com.ft.methodeimagebinarymapper.service.MethodeImageBinaryMapper;
import com.ft.methodeimagebinarymapper.service.MethodePDFBinaryMapper;
import com.ft.methodeimagebinarymapper.validation.PublishingValidator;
import com.ft.methodeimagebinarymapper.validation.UuidValidator;
import com.ft.platform.dropwizard.AdvancedHealthCheckBundle;
import com.ft.platform.dropwizard.DefaultGoodToGoChecker;
import com.ft.platform.dropwizard.GoodToGoBundle;
import com.sun.jersey.api.client.Client;
import io.dropwizard.Application;
import io.dropwizard.client.JerseyClientConfiguration;
import io.dropwizard.jersey.setup.JerseyEnvironment;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;

import javax.ws.rs.core.UriBuilder;

public class MethodeImageBinaryMapperApplication extends Application<MethodeImageBinaryMapperConfiguration> {

    public static void main(final String[] args) throws Exception {
        new MethodeImageBinaryMapperApplication().run(args);
    }

    @Override
    public void initialize(Bootstrap<MethodeImageBinaryMapperConfiguration> bootstrap) {
        bootstrap.addBundle(new AdvancedHealthCheckBundle());
        bootstrap.addBundle(new GoodToGoBundle(new DefaultGoodToGoChecker()));
    }

    @Override
    public void run(MethodeImageBinaryMapperConfiguration configuration, Environment environment) throws Exception {
        JerseyEnvironment jersey = environment.jersey();
        jersey.register(new VersionResource());
        jersey.register(new BuildInfoResource());

        final ConsumerConfiguration consumerConfig = configuration.getConsumerConfiguration();
        final ObjectMapper objectMapper = environment.getObjectMapper();

        final com.ft.messagequeueproducer.MessageProducer producer = configureMessageProducer(environment, configuration.getProducerConfiguration());

        final UriBuilder contentUriBuilder = UriBuilder.fromUri(configuration.getContentUriPrefix()).path("{uuid}");

        MessageProducingContentMapper contentMapper = new MessageProducingContentMapper(
                new MethodeImageBinaryMapper(),
                new MethodePDFBinaryMapper(),
                objectMapper, consumerConfig.getSystemCode(),
                producer, contentUriBuilder);

        Client consumerClient = getConsumerClient(environment, consumerConfig);

        MessageListener listener = new NativeCmsPublicationEventsListener(
                consumerConfig.getSystemCode(),
                contentMapper,
                objectMapper,
                new UuidValidator(),
                new PublishingValidator());

        startListener(environment, listener, consumerConfig, consumerClient);

    }

    protected com.ft.messagequeueproducer.MessageProducer configureMessageProducer(Environment environment, ProducerConfiguration config) {
        JerseyClientConfiguration jerseyConfig = config.getJerseyClientConfiguration();
        jerseyConfig.setGzipEnabled(false);
        jerseyConfig.setGzipEnabledForRequests(false);

        Client producerClient = ResilientClientBuilder.in(environment)
                .using(jerseyConfig)
                .usingDNS()
                .named("image-binary-mapper-producer")
                .build();

        final QueueProxyProducer.BuildNeeded queueProxyBuilder = QueueProxyProducer.builder()
                .withJerseyClient(producerClient)
                .withQueueProxyConfiguration(config.getMessageQueueProducerConfiguration());

        final QueueProxyProducer producer = queueProxyBuilder.build();

        environment.healthChecks().register("KafkaProxyProducer",
                new CanConnectToMessageQueueProducerProxyHealthcheck(queueProxyBuilder.buildHealthcheck(),
                        config.getHealthcheckConfiguration(), environment.metrics()));

        return producer;
    }

    protected void startListener(Environment environment, MessageListener listener, ConsumerConfiguration config, Client consumerClient) {
        final MessageQueueConsumerInitializer messageQueueConsumerInitializer =
                new MessageQueueConsumerInitializer(config.getMessageQueueConsumerConfiguration(),
                        listener, consumerClient);

        HealthCheckRegistry healthchecks = environment.healthChecks();
        healthchecks.register("KafkaProxyConsumer",
                messageQueueConsumerInitializer.buildPassiveConsumerHealthcheck(
                        config.getHealthcheckConfiguration(), environment.metrics()
                ));

        environment.lifecycle().manage(messageQueueConsumerInitializer);
    }

    private Client getConsumerClient(Environment environment, ConsumerConfiguration config) {
        JerseyClientConfiguration jerseyConfig = config.getJerseyClientConfiguration();
        jerseyConfig.setGzipEnabled(false);
        jerseyConfig.setGzipEnabledForRequests(false);

        return ResilientClientBuilder.in(environment)
                .using(jerseyConfig)
                .usingDNS()
                .named("image-binary-mapper-consumer")
                .build();
    }
}
