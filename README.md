# camel-quarkus-projects

## Über dieses Repository

Sammlung von Proof-of-Concept-Projekten mit Apache Camel und Quarkus.

## Projekte

### `camel-quarkus-jms-xa`

#### Zweck

Demonstriert die transaktionale Verarbeitung von JMS-Nachrichten mit Camel
Quarkus, Artemis und Narayana JTA. Der Test provoziert einen Fehler in der
Route und prüft, dass die Nachricht in der Dead-Letter-Queue (DLA) landet.

#### Voraussetzungen

JDK 25 und Maven.

#### Tests

Vom Repository-Root ausführen:

```shell
mvn --projects camel-quarkus-jms-xa test
```
