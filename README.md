# Scalalable Real-Time IDS Using SparkML

Real-Time Streaming Analytics System for Detecting Network Anomalies in Scalable and Distributed manner,
a SpringBoot project, runs a Spark Structured Streaming Driver that Receives Streaming Json records from
Kafka cluster and displays suspicious traffics online (in a combination of using Server Sent Event , MongoDB
and ReactiveRepository) on an Angular Frontend. Argus tool is used for capturing packets from a network
interface, and sending them to a Kafka Producer (SpringBoot application that produce records to a Topic on
Kafka cluster) from a remote location.

NOTE: In real world, this system needs Argus as data source, but you can run "kafka-producer" module in test mode by setting "listen.on.tcp=false", and it will send some prepared sample data(which is generated by Argus) to kafka cluster, and the to Spark cluster for processing.

## Tech Stack
* Argus 3.0.8.2
* JDK 11
* Spring Boot 2.5.2
* Apache Kafka 2.1
* Apache Spark 3.0.1 & Hadoop 2.7
* MongoDB 5
* Node 16
* Angular 12

## System Architecture
![Architecture](https://github.com/meysam-amini/Scalable-Real-Time-IDS-With-Spark/assets/59673699/cf5bf4f4-ff05-45df-8dca-71a549580a9a)
