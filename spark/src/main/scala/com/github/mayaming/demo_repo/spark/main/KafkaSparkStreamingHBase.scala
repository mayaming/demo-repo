package com.github.mayaming.demo_repo.spark.main

import java.util.ResourceBundle

import com.google.common.collect.Lists
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.common.serialization.BytesDeserializer
import org.apache.kafka.common.utils.Bytes
import org.apache.spark.sql.SparkSession
import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.streaming._
import org.apache.spark.streaming.kafka010.ConsumerStrategies.Subscribe
import org.apache.spark.streaming.kafka010.KafkaUtils
import org.apache.spark.streaming.kafka010.LocationStrategies.PreferConsistent

import scala.collection.mutable
import scala.collection.JavaConverters._
import scala.collection.JavaConversions._

//TODO: 未完待续，https://www.jianshu.com/p/b2fea6687735里讲到了“其实并不太鼓励大家使用Spark 对HBase进行批处理”有待验证
object KafkaSparkStreamingHBase {

  private def createStreamingContext(ctx: SparkContext)() = {
    val ssc = new StreamingContext(SparkContext.getOrCreate, Seconds(5))
    val resourceBundle = ResourceBundle.getBundle("kafka")
    val m = mutable.Map(
      ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG -> resourceBundle.getString("kafka.bootstrap.servers"),
      ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG -> classOf[BytesDeserializer].getCanonicalName,
      ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG -> classOf[BytesDeserializer].getCanonicalName,
      ConsumerConfig.AUTO_OFFSET_RESET_CONFIG -> "latest",
      ConsumerConfig.GROUP_ID_CONFIG -> resourceBundle.getString("group.id"),
      ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG -> "false",
      ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG -> "15000"
    )
    val rawStream = KafkaUtils.createDirectStream[Bytes, Bytes](ssc, PreferConsistent, Subscribe[Bytes, Bytes](Lists.newArrayList(""), m.asJava))
    rawStream.foreachRDD(rdd => {
      //
    })
    ssc
  }

  def main(args: Array[String]) {
    val ctx = SparkContext.getOrCreate
    val ssc = StreamingContext.getOrCreate(null, createStreamingContext(ctx))
    ssc.start()
    ssc.awaitTermination()
  }
}

