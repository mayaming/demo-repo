package com.github.mayaming.demo_repo.spark.main

import java.util.Date

import org.apache.spark.sql.SparkSession

object SimpleLineCount {
  def main(args: Array[String]) {
    val fp = "/user/yaming/big.txt"

    val spark = SparkSession
      .builder
      .getOrCreate

    val lines = spark.sparkContext.textFile(fp)
    println(s"${new Date}: There're ${lines.count()} lines for $fp")
    spark.stop()
  }
}

