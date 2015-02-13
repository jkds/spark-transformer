package com.datastax.demo

import java.util.Date

import akka.actor.{ActorLogging, Actor, ActorRef}
import com.datastax.spark.connector.streaming._
import kafka.serializer.StringDecoder
import org.apache.spark.storage.StorageLevel
import org.apache.spark.streaming.kafka.KafkaUtils
import org.apache.spark.streaming.{Seconds, StreamingContext}

/** The KafkaStreamActor creates a streaming pipeline from Kafka to Cassandra via Spark.
  * It creates the Kafka stream which streams the raw data, transforms it, to
  * a column entry for a specific credit card and saves the new data to a
  * cassandra table as it arrives.
  */
class KafkaStreamingActor(kafkaParams: Map[String, String],
                          ssc: StreamingContext,
                          settings: DemoSettings,
                          listener: ActorRef) extends Actor with ActorLogging {

  import org.apache.spark.streaming.StreamingContext._
  import settings._


  log.info("Creating Kafka Stream from {} topic for mapping to a transaction", KafkaTopic)

  val kafkaStream = KafkaUtils.createStream[String, String, StringDecoder, StringDecoder](
    ssc, kafkaParams, Map(KafkaTopic -> 10), StorageLevel.DISK_ONLY_2)
    .map { case (_, line) => line.split(",")}
    .map(Txn(_))

  /** Saves the raw data to Cassandra - raw table. */
  kafkaStream.saveToCassandra(CassandraKeySpace, CassandraTxnTable)

  log.info("Creating aggregate stream reducing by key and window")
  /** For a given merchant sum the amounts, add a date/timestamp and
    * then save it to a C* table
    */
  kafkaStream.map(txn => (txn.merchant, txn.amount))
    .reduceByKeyAndWindow((a: Double, b: Double) => (a + b), Seconds(30), Seconds(30))
    .map { txn =>
      val (merchant, amount) = txn
      (merchant, amount, new Date())
    }.saveToCassandra(CassandraKeySpace, CassandraAmountAggregatesTable)

  /** Notifies the supervisor that the Spark Streams have been created and defined.
    * Now the [[StreamingContext]] can be started. */
  listener ! OutputInitialised

  def receive: Actor.Receive = {
    case e => // ignore
  }
}

