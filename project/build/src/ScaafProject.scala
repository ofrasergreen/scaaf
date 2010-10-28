import sbt._
import de.element34.sbteclipsify._

class ScaliProject(info: ProjectInfo) extends DefaultProject(info) with Eclipsify with IdeaProject {
  val rabbitmq = "com.rabbitmq" % "amqp-client" % "1.7.2"
  val slf4j = "org.slf4j" % "slf4j-log4j12" % "1.6.0"
  val sbinary = "org.scala-tools.sbinary" % "sbinary_2.8.0" % "0.3.1-SNAPSHOT"
  
  val scalaToolsSnapshots = "Scala-Tools Maven2 Snapshots Repository" at "http://scala-tools.org/repo-snapshots"
  val scalatest = "org.scalatest" % "scalatest" % "1.2-for-scala-2.8.0.RC6-SNAPSHOT" % "test"
  
  //override def compileOptions = super.compileOptions ++ compileOptions("-Xlog-implicits")
}
