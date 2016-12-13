# Methode Image Binary Mapper
This is a web application which listens to the NativeCmsPublicationEvents Kafka topic for publishing events coming from Methode and process only the messages
containing an image. It extracts the image binary, creates a message with this information and writes it to the CmsPublicationEvents topic.

## Introduction

The service listens to the NativeCmsPublicationEvents Kafka topic and ingests the image messages coming from Methode.
The image messages coming from Methode have the header: `Origin-System-Id: http://cmdb.ft.com/systems/methode-web-pub` and the JSON payload has the 
field `"type":"Image"`. Other messages are discarded.

## Running locally
To compile, run tests and build jar
    
    mvn clean verify 

To run locally, run:
    
    java -jar target/methode-image-binary-mapper-1.0-SNAPSHOT.jar server methode-image-binary-mapper.yaml

## Healthchecks 
http://localhost:26080/__health

## Admin Endoint
http://localhost:26081