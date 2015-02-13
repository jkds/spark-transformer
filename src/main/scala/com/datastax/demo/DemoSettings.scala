package com.datastax.demo

import com.typesafe.config.{ConfigFactory, Config}

/**
 * Created by jameskavanagh on 10/02/2015.
 */
class DemoSettings(conf : Option[Config] = None) {

  val rootConfig = ConfigFactory.load

  protected val kafka = rootConfig.getConfig("kafka")
  protected val cassandra = rootConfig.getConfig("cassandra")
  protected val spark = rootConfig.getConfig("spark")
  /* Cassandra Settings */
  val CassandraKeySpace                     = cassandra.getString("keyspace.name")
  val CassandraTxnTable                     = cassandra.getString("rawtable.name")
  val CassandraAmountAggregatesTable        = cassandra.getString("aggregatetable.name")
  val CassandraHosts                        = cassandra.getString("connection.host")
  /* Kafka Settings */
  val KafkaTopic                            = kafka.getString("topic.raw")
  /* Spark Settings */
  val SparkMaster                           = spark.getString("master")
  val SparkCleanerTTL                       = spark.getString("cleaner.ttl.seconds")
  val SparkStreamingBatchWindow             = spark.getInt("streaming.batch.interval")

}
