package com.github.mayaming.demo_repo.kafka

import java.text.SimpleDateFormat
import java.util.Date

import joptsimple.{OptionParser, OptionSet}
import kafka.client.ClientUtils
import kafka.cluster.BrokerEndPoint
import kafka.utils.CommandLineUtils
import org.apache.kafka.clients.consumer.{ConsumerRecord, KafkaConsumer}
import org.apache.kafka.common.TopicPartition

import scala.collection.JavaConverters._

case class TopicInfo(topic: String, partitions: Seq[TopicPartitionInfo], totalEntries: Long) {
  override def toString: String = {
    val sb = new StringBuilder
    sb.append(topic).append(s" ($totalEntries):\n")
    partitions.foreach(p => {
      sb.append("    ").append(p.toString).append("\n")
    })
    sb.toString()
  }
}

case class TopicPartitionInfo(topic: String, partition: Int, earliestOffset: Long, latestOffset: Long,
                              earliestTs: Date, latestTs: Date, entries: Long, leader: String, replicas: Seq[String]) {
  def strDate(d: Date): String = {
    new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(d)
  }

  override def toString: String = {
    val d1 = strDate(earliestTs)
    val d2 = strDate(latestTs)
    val reps = replicas.mkString(",")
    s"$partition ($entries) - $earliestOffset ($d1) ~ $latestOffset ($d2), leader=$leader, replicas=$reps"
  }
}

object KafkaUtils {
  private def pollOne(consumer: KafkaConsumer[String, String]): ConsumerRecord[String, String] = {
    val records = consumer.poll(10000)
    if (!records.isEmpty) {
      records.iterator.next
    }
    else {
      null
    }
  }

  def topicInfo(topics: Seq[String], brokers: Seq[String]): Seq[TopicInfo] = {
    val metadataTargetBrokers = brokers.zipWithIndex.map { case (address, brokerId) =>
      BrokerEndPoint.createBrokerEndPoint(brokerId, address)
    }
    val clientId = "KafkaUtils"
    val topicsMetadata = ClientUtils.fetchTopicMetadata(topics.toSet, metadataTargetBrokers, clientId, 10000).topicsMetadata
    val topicInfos = topicsMetadata.map(t => {
      val consumer = new KafkaConsumer[String, String](Map[String, Object]("bootstrap.servers" -> brokers.mkString(","),
        "group.id" -> clientId,
        "key.deserializer" -> "org.apache.kafka.common.serialization.StringDeserializer",
        "value.deserializer" -> "org.apache.kafka.common.serialization.StringDeserializer").asJava)
      val topicPartitions = t.partitionsMetadata.map(p => (new TopicPartition(t.topic, p.partitionId), p))
      val partitionInfos = topicPartitions.map(t => {
        val tp = t._1
        val meta = t._2
        val tps = Seq(tp).asJava
        consumer.assign(tps)
        var earliestOffset = -1L
        var latestOffset = -1L
        var earliestTs: Date = null
        var latestTs: Date = null

        val beginningOffsets = consumer.beginningOffsets(tps)
        consumer.seek(tp, beginningOffsets.asScala.head._2)
        var record = pollOne(consumer)
        if (record != null) {
          earliestOffset = record.offset()
          earliestTs = new Date(record.timestamp())
        }

        val endOffsets = consumer.endOffsets(tps)
        consumer.seek(tp, endOffsets.asScala.head._2-1)
        record = pollOne(consumer)
        if (record != null) {
          latestOffset = record.offset()
          latestTs = new Date(record.timestamp())
        }

        TopicPartitionInfo(tp.topic(), tp.partition(), earliestOffset, latestOffset, earliestTs, latestTs,
          latestOffset - earliestOffset, meta.leader.map(_.connectionString).getOrElse("No leader"), meta.replicas.map(_.connectionString))
      })
      TopicInfo(t.topic, partitionInfos, partitionInfos.map(_.entries).sum)
    })
    topicInfos
  }

  def cmdParse(args: Array[String]): OptionSet = {
    val parser = new OptionParser(false)
    parser.accepts("brokers", "The list of hostname and port of the server to connect to. Default to 127.0.0.1:9092 if not provided")
      .withOptionalArg()
      .describedAs("brokers")
      .ofType(classOf[String])
      .defaultsTo(scala.util.Properties.envOrElse("BROKERS", "127.0.0.1:9092"))
      .withValuesSeparatedBy(",")

    parser.accepts("topics", "REQUIRED: The topics to display information for")
      .withRequiredArg
      .describedAs("topics")
      .ofType(classOf[String])
      .defaultsTo(scala.util.Properties.envOrElse("TOPICS", ""))
      .withValuesSeparatedBy(",")

    if (args.length == 0) {
      CommandLineUtils.printUsageAndDie(parser, "An interactive shell for displaying topics information")
    }

    parser.parse(args : _*)
  }

  def main(args: Array[String]): Unit = {
    val options = cmdParse(args)
    val brokers = options.valuesOf("brokers").asScala.map(_.toString)
    val topics = options.valuesOf("topics").asScala.map(_.toString)
    val topicInfos = topicInfo(topics, brokers)
    topicInfos.foreach(println)
  }
}

