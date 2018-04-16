# Methode Image Binary Mapper

This is a microservice which listens to the `NativeCmsPublicationEvents` Kafka topic for Methode publishing events and processes messages containing an image or a pdf. It extracts the binary, creates a message with this information and writes it to the `CmsPublicationEvents` topic. Downstream, the binary messages are ingested by the `binary-ingester` and written to S3 by the `binary-writer`.

Messages from Methode have the header: `Origin-System-Id: http://cmdb.ft.com/systems/methode-web-pub`, all other system IDs are discarded.

The JSON payload for images have a `type` field of `Image`. PDFs have a `type` field of `Pdf`.

Binaries of those files that have the `ExternalUrl` property set, are not mapped. They are images that should be referred to on those specified links.

## PDF Support

The ability to map PDF types was added in order to support the Editorial workflow for crosswords. This will be changed when PDFs are modelled in UPP as Content.

## Running locally

To compile, run tests and build jar

```
mvn clean verify
```

To run locally, run:

```
java -jar target/methode-image-binary-mapper-1.0-SNAPSHOT.jar server methode-image-binary-mapper.yaml
```

## Healthchecks

To view FT healthcheck results, use the following:

```
curl http://localhost:26080/__health
```

## Admin Endpoint

As with all Dropwizard applications, the server also runs a separate admin port:

```
curl http://localhost:26081
```
