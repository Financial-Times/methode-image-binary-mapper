# UPP - Methode Image Binary Mapper

Methode Image Binary Mapper reads from the NativeCMSPublicationEvents queue, identifies the images received from Methode, transforms the binary content into UP format and writes the message to the CMSPublicationEvents queue.

## Primary URL

https://upp-prod-delivery-glb.upp.ft.com/__methode-image-binary-mapper/

## Service Tier

Platinum

## Lifecycle Stage

Production

## Delivered By

content

## Supported By

content

## Known About By

- dimitar.terziev
- hristo.georgiev
- elina.kaneva
- georgi.kazakov
- kalin.arsov
- mihail.mihaylov
- boyko.boykov

## Host Platform

AWS

## Architecture

The Methode Image Binary Mapper reads from the NativeCMSPublicationEvents Kafka topic and after mapping and transforming of the received message it forwards a modified message to the CMSPublicationEvents Kafka topic.

## Contains Personal Data

No

## Contains Sensitive Data

No

## Dependencies

- upp-kafka

## Failover Architecture Type

ActiveActive

## Failover Process Type

FullyAutomated

## Failback Process Type

FullyAutomated

## Failover Details

The service is deployed in both Delivery clusters, the failover guide for the cluster is located here:
https://github.com/Financial-Times/upp-docs/tree/master/failover-guides/delivery-cluster

## Data Recovery Process Type

NotApplicable

## Data Recovery Details

The service does not store data, so it does not require any data recovery steps.

## Release Process Type

PartiallyAutomated

## Rollback Process Type

Manual

## Release Details

Failover is not need when a new version of the service is deployed to production because the publication payloads are persisted in Kafka while the service is unavailable.

## Key Management Process Type

Manual

## Key Management Details

To access the service clients need to provide basic auth credentials.
To rotate credentials you need to login to a particular cluster and update varnish-auth secrets.

## Monitoring

Service in UPP K8S delivery clusters:
- Pub-Prod-EU health: https://upp-prod-delivery-eu.upp.ft.com/__health/__pods-health?service-name=methode-image-binary-mapper
- Pub-Prod-US health: https://upp-prod-delivery-us.upp.ft.com/__health/__pods-health?service-name=methode-image-binary-mapper

## First Line Troubleshooting

https://github.com/Financial-Times/upp-docs/tree/master/guides/ops/first-line-troubleshooting

## Second Line Troubleshooting

Please refer to the GitHub repository README for more troubleshooting information.
