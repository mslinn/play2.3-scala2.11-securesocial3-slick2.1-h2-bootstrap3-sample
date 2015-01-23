import play.core.PlayVersion.{current => playVersion}
import sbt.Keys._

name := "play2.3-scala2.11-slick2.1-securesocial3-bootstrap3-sample"
organization := "com.micronautics"

scalaVersion := "2.11.5"
version := "0.1.0"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

libraryDependencies ++= Seq(
  jdbc,
  "com.typesafe.play"         %% "play"                % playVersion withSources(),
  "com.typesafe.play"         %% "play-json"           % playVersion withSources(),
  "org.webjars"               %% "webjars-play"        % "2.3.0-2",
  "com.typesafe.slick"        %% "slick"               % "2.1.0" withSources(),
  "ws.securesocial"           %% "securesocial"        % "master-SNAPSHOT" withSources(),
  "org.webjars"               %  "bootstrap"           % "3.3.2",
  //"com.typesafe.play"         %% "play-slick"          % "0.6.0.1",
  "com.github.nscala-time"      %% "nscala-time"         % "1.6.0" withSources(),
  "org.joda"                    %  "joda-convert"        % "1.6" withSources(),
  "com.github.tototoshi"        %% "slick-joda-mapper"   % "1.2.0" withSources(),
  //
  //"org.scalatestplus"         %% "play"                % "1.2.0" % "test",
  "org.scalatest"             %% "scalatest"           % "2.2.1" % "test" withSources(),
  "junit"                     %  "junit"               % "4.12" % "test"
)

resolvers ++= Seq(
  Resolver.typesafeRepo("releases"),
  Resolver.sonatypeRepo("snapshots") // for SecureSocial master
)

scalacOptions ++= Seq("-deprecation", "-encoding", "UTF-8", "-feature", "-target:jvm-1.7", "-unchecked",
  "-Ywarn-adapted-args", "-Ywarn-value-discard", "-Xlint")

javacOptions ++= Seq("-Xlint:deprecation", "-Xlint:unchecked", "-source", "1.7", "-target", "1.7", "-g:vars")

updateOptions := updateOptions.value.withCachedResolution(true)

doc in Compile <<= target.map(_ / "none")

publishArtifact in (Compile, packageSrc) := false

logBuffered in Test := false

Keys.fork in Test := false

parallelExecution in Test := false

// define the statements initially evaluated when entering 'console', 'console-quick' but not 'console-project'
initialCommands in console := """ // make app resources accessible
   |Thread.currentThread.setContextClassLoader(getClass.getClassLoader)
   |new play.core.StaticApplication(new java.io.File("."))
   |import java.net.URL
   |import java.text.DateFormat
   |import java.util.Locale
   |import org.joda.time._
   |import org.joda.time.format.{DateTimeFormat, DateTimeFormatter}
   |import play.api.db.DB
   |import play.api.libs.json._
   |import play.api.Play.current
   |import play.Logger
   |import scala.slick.driver.H2Driver.simple._
   |import scala.reflect.runtime.universe._
   |""".stripMargin

logLevel := Level.Warn

logLevel in test := Level.Info // Level.Info is needed to see detailed output when running tests

logLevel in compile := Level.Warn
