# BuildStuff2019

Writing messages published to Kafka into Maria DB


## Prerequisites

* Java 1.8
* Maria DB 10.4
* Kafka 2.12-2.3.0

### Instructions

* start kafka

```
bin\zookeeper-server-start.sh config\zookeeper.properties
bin\kafka-server-start.sh config\server.properties
```



* run the producer:

```
bin\kafka-console-producer.sh --broker-list localhost:9092 --topic test-consumer-group
```

*run Spring Boot app:

```
mvn package -DskipTests
java -jar target\carbs-0.0.1-SNAPSHOT.jar
```
