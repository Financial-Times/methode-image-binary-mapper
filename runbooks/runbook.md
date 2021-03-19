<!--
    Written in the format prescribed by https://github.com/Financial-Times/runbook.md.
    Any future edits should abide by this format.
-->
# UPP - Methode Image Binary Mapper

Methode Image Binary Mapper reads from the NativeCMSPublicationEvents queue, identifies the images received from Methode, transforms the binary content into UP format and writes the message to the CMSPublicationEvents queue.

## Code

up-mibm

## Primary URL

https://upp-prod-delivery-glb.upp.ft.com/__methode-image-binary-mapper/

## Service Tier

Platinum

## Lifecycle Stage

Production

## Host Platform

AWS

## Architecture

The Methode Image Binary Mapper reads from the NativeCMSPublicationEvents Kafka topic and after mapping and transforming of the received message it forwards a modified message to the CMSPublicationEvents Kafka topic.

## Contains Personal Data

No

## Contains Sensitive Data

No

<!-- Placeholder - remove HTML comment markers to activate
## Can Download Personal Data
Choose Yes or No

...or delete this placeholder if not applicable to this system
-->

<!-- Placeholder - remove HTML comment markers to activate
## Can Contact Individuals
Choose Yes or No

...or delete this placeholder if not applicable to this system
-->

## Failover Architecture Type

ActiveActive

## Failover Process Type

FullyAutomated

## Failback Process Type

FullyAutomated

## Failover Details

The service is deployed in both Delivery clusters, the failover guide for the cluster is located here:
<https://github.com/Financial-Times/upp-docs/tree/master/failover-guides/delivery-cluster>

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

<!-- Placeholder - remove HTML comment markers to activate
## Heroku Pipeline Name
Enter descriptive text satisfying the following:
This is the name of the Heroku pipeline for this system. If you don't have a pipeline, this is the name of the app in Heroku. A pipeline is a group of Heroku apps that share the same codebase where each app in a pipeline represents the different stages in a continuous delivery workflow, i.e. staging, production.

...or delete this placeholder if not applicable to this system
-->

## Key Management Process Type

Manual

## Key Management Details

To access the service clients need to provide basic auth credentials.
To rotate credentials you need to login to a particular cluster and update varnish-auth secrets.

## Monitoring

Service in UPP K8S delivery clusters:

*   Pub-Prod-EU health: <https://upp-prod-delivery-eu.upp.ft.com/__health/__pods-health?service-name=methode-image-binary-mapper>
*   Pub-Prod-US health: <https://upp-prod-delivery-us.upp.ft.com/__health/__pods-health?service-name=methode-image-binary-mapper>

## First Line Troubleshooting

<https://github.com/Financial-Times/upp-docs/tree/master/guides/ops/first-line-troubleshooting>

## Second Line Troubleshooting

Please refer to the GitHub repository README for more troubleshooting information.