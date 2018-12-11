package com.github.mayaming.demo_repo.spark.main

import java.util.Date

import joptsimple.{OptionParser, OptionSet}
import org.apache.spark.sql.SparkSession

object SimpleLineCount {
  def cmdParse(args: Array[String]): OptionSet = {
    val parser = new OptionParser(false)
    parser.accepts("filepath", "Path of the file to be counted")
      .withOptionalArg()
      .describedAs("filepath")
      .ofType(classOf[String])
      .defaultsTo("/user/yaming/big.txt")

    if (args.length == 0) {
      parser.printHelpOn(System.out)
      System.exit(1)
    }

    parser.parse(args : _*)
  }

  def main(args: Array[String]) {
    val fp = cmdParse(args).valueOf("filepath").toString

    val spark = SparkSession
      .builder
      .getOrCreate

    val lines = spark.sparkContext.textFile(fp)
    println(s"${new Date}: There're ${lines.count()} lines for $fp")
    spark.stop()
  }
}

