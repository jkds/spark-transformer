package com.datastax.demo

import akka.actor._
import akka.routing.BroadcastRouter
import com.datastax.spark.connector.embedded.EmbeddedKafka
import org.apache.spark.streaming.{Milliseconds, StreamingContext}
import org.apache.spark.{SparkConf, SparkContext}

import scala.concurrent.duration._

/**
 * Created by jameskavanagh on 09/02/2015.
 */
object DemoApp extends App {

  implicit val system = ActorSystem("data-feeder")
  implicit val timeout = 10 seconds

  val settings = new DemoSettings()
  import settings._

  val log = system.log
  val numOfTxns = if (args.length > 0) Option(args(0).toInt).getOrElse(200) else 200
  val frequencyRange = 600
  val numOfDispatchers = 20

  val kafkaParams = Map[String,String]("group.id"->"ccdemo",
                                        "zookeeper.connect"->"localhost:2181",
                                        "auto.offset.reset"->"smallest")

  lazy val conf = new SparkConf().setAppName("CC-Card-Demo")
    .setMaster(SparkMaster)
    .set("spark.cassandra.connection.host", CassandraHosts)

  log.info("Lazily creating spark context")
  lazy val sc = new SparkContext(conf)
  lazy val ssc = new StreamingContext(sc, Milliseconds(SparkStreamingBatchWindow))

  val bootstrap = system.actorOf(Props(classOf[DemoApp.Bootstrap]))

  system.registerOnTermination {
    ssc.stop(stopSparkContext = true, stopGracefully = true)
    bootstrap ! PoisonPill
  }

  log.info("Awaiting termination...")
  system.awaitTermination()

  class Bootstrap extends Actor {

    log.info("Creating streaming actor")
    system.actorOf(Props(classOf[KafkaStreamingActor], kafkaParams, ssc, settings, self))

    def receive = {
      case OutputInitialised() =>
        log.info("Streaming Started...")
    }
  }


}

