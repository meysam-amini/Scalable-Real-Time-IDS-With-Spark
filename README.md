# Scalalable Real-Time IDS Using SparkML

Real-Time Streaming Analytics System for Detecting Network Anomalies in Scalable and Distributed manner,
a SpringBoot project, runs a Spark Structured Streaming Driver that Receives Streaming Json records from
Kafka cluster and displays suspicious traffics online (in a combination of using Server Sent Event , MongoDB
and ReactiveRepository) on an Angular Frontend. Argus tool is used for capturing packets from a network
interface, and sending them to a Kafka Producer (SpringBoot application that produce records to a Topic on
Kafka cluster) from a remote location.
