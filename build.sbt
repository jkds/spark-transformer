net.virtualvoid.sbt.graph.Plugin.graphSettings

name := "spark-transformer"

version := "0.1"

scalaVersion := "2.10.4"

val SparkVersion = "1.1.0"
val SparkCassandraVersion = "1.1.0"

libraryDependencies ++= Seq(
  ("org.apache.spark" %%  "spark-core"  % SparkVersion % "provided").
    exclude("org.eclipse.jetty.orbit", "javax.servlet").
    exclude("org.eclipse.jetty.orbit", "javax.transaction").
    exclude("org.eclipse.jetty.orbit", "javax.mail").
    exclude("org.eclipse.jetty.orbit", "javax.activation").
    exclude("commons-beanutils", "commons-beanutils-core").
    exclude("commons-collections", "commons-collections").
    exclude("commons-collections", "commons-collections").
    exclude("com.esotericsoftware.minlog", "minlog")
)

libraryDependencies ++= Seq(
  "org.apache.spark"    %%  "spark-streaming-kafka"                   % SparkVersion % "provided",
  "com.datastax.spark"  %%  "spark-cassandra-connector"               % SparkCassandraVersion % "provided",
  "com.datastax.spark"  %%  "spark-cassandra-connector-embedded"      % SparkCassandraVersion % "provided" exclude("org.slf4j", "slf4j-log4j12"),
  "ch.qos.logback"      %   "logback-classic"                         % "1.0.7",
  "com.typesafe.akka"   %%  "akka-slf4j"                              % "2.2.3" % "provided",
  "org.scalatest"       %%  "scalatest"                               % "2.2.4" % "test"
)

