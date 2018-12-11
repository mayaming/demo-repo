package com.github.mayaming.demo_repo.spark.launch

import joptsimple.{OptionParser, OptionSet}
import org.apache.spark.launcher.{InProcessLauncher, SparkAppHandle}
import org.apache.spark.scheduler.SparkListener

object YarnClusterLauncher {
  def cmdParse(args: Array[String]): OptionSet = {
    val parser = new OptionParser(false)

    parser.accepts("app-jar", "App jar that contains business logic")
      .withRequiredArg()
      .describedAs("app-jar")
      .ofType(classOf[String])
      .defaultsTo("/home/mayaming/working/dataifs-lambda-sample/target/dataifs-lambda-sample-1.0-SNAPSHOT.jar")

    parser.accepts("main-class", "Main class in the app jar to be launched")
      .withRequiredArg()
      .describedAs("main-class")
      .ofType(classOf[String])
      .defaultsTo("com.github.mayaming.demo_collection.scala_example.spark.main.SimpleLineCount")

    parser.accepts("app-name", "Spark app name")
      .withOptionalArg()
      .describedAs("app-name")
      .ofType(classOf[String])
      .defaultsTo("SimpleLineCount")

    parser.accepts("spark-archive", "Spark archive path, which could accelerate application startup")
      .withOptionalArg()
      .describedAs("spark-archive")
      .ofType(classOf[String])
      .defaultsTo("hdfs:///user/yaming/spark-2.3.0-hadoop2.6.zip")

    if (args.length == 0) {
      parser.printHelpOn(System.out)
      System.exit(1)
    }

    parser.parse(args : _*)
  }

  def main(args: Array[String]): Unit = {
    val options = cmdParse(args)
    val sparkAppHandler = new InProcessLauncher()
      .setAppResource(options.valueOf("app-jar").toString)
      .setMainClass(options.valueOf("main-class").toString)
      .setAppName(options.valueOf("app-name").toString)
      .setDeployMode("cluster")
      .setMaster("yarn")
      .setConf("spark.yarn.archive", options.valueOf("spark-archive").toString)
      .startApplication()
  }
}
