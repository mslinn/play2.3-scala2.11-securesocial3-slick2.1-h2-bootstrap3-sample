import play.core.PlayVersion.{current => playVersion}
import sbt.Keys._

name := "securesocial-slick-bootstrap3-sample"
organization := "com.micronautics"

scalaVersion := "2.11.5"
version := "0.1.0"

libraryDependencies ++= Seq(
		jdbc,
		"com.typesafe.play"         %% "play"                % playVersion withSources(),
    "com.typesafe.play"         %% "play-json"           % playVersion withSources(),
    //"org.postgresql"            %  "postgresql"        % "9.3-1102-jdbc41" withSources(),
    "ws.securesocial"           %% "securesocial"        % "master-SNAPSHOT" withSources(),
    "com.typesafe.slick"        %% "slick"               % "2.1.0" withSources(),
    "org.webjars"               %% "webjars-play"        % "2.3.0-2",
    //"org.scalatestplus"         %% "play"                % "1.2.0" % "test",

		"org.webjars"               %  "bootstrap"           % "3.3.2",
		//"org.scalaj"                %% "scalaj-http"         % "0.3.14",
		//"net.sf.jtidy"              %  "jtidy"               % "r938",
		//com.github.scala-incubator.io"  %% "scala-io-core" % "0.4.2",
		//"com.github.scala-incubator.io" %% "scala-io-file" % "0.4.2",
		//"org.jdom"                  %  "jdom2"               % "2.0.5",
		//"jaxen"                     %  "jaxen"               % "1.1.6",
		//"org.fluentlenium"          %  "fluentlenium-core"   % "0.9.2",
		//"org.squeryl"               %% "squeryl"             % "0.9.6-RC2",
		//"com.netflix.rxjava"      % "rxjava-scala"         % "0.15.0",
		"org.scalatest"             %% "scalatest"           % "2.2.1" % "test" withSources(),
		"junit"                     %  "junit"               % "4.12" % "test",
		//"org.json4s"                %  "json4s-native_2.10"  % "3.2.5",
		//"net.databinder.dispatch"   %  "dispatch-core_2.10"  % "0.11.0",
		//"com.squareup.retrofit"     %  "retrofit"            % "1.0.0",
		//"org.scala-lang"            %  "scala-swing"         % "2.10.3",
		//"org.scala-lang"            %  "scala-reflect"       % "2.10.3",
		//"org.scala-lang.modules"    %% "scala-async"         % "0.9.0-M2",
		//"com.typesafe.play"         %% "play-slick"          % "0.6.0.1",
		"com.github.nscala-time"      %% "nscala-time"         % "1.6.0" withSources(),
		"org.joda"                    %  "joda-convert"        % "1.6" withSources(),
		"com.github.tototoshi"        %% "slick-joda-mapper"   % "1.2.0" withSources()
		//"org.seleniumhq.selenium"   %  "selenium-java"       % "2.35.0"
	)

resolvers ++= Seq(
  Resolver.typesafeRepo("releases"),
	Resolver.url("Objectify Play Repository (release)", url("http://schaloner.github.com/releases/"))(Resolver.ivyStylePatterns),
	Resolver.url("Objectify Play Repository (snapshot)", url("http://schaloner.github.com/snapshots/"))(Resolver.ivyStylePatterns),
	Resolver.url("play-easymail (release)", url("http://joscha.github.com/play-easymail/repo/releases/"))(Resolver.ivyStylePatterns),
	Resolver.url("play-easymail (snapshot)", url("http://joscha.github.com/play-easymail/repo/snapshots/"))(Resolver.ivyStylePatterns),
	Resolver.url("play-authenticate (release)", url("http://joscha.github.com/play-authenticate/repo/releases/"))(Resolver.ivyStylePatterns),
	Resolver.url("play-authenticate (snapshot)", url("http://joscha.github.com/play-authenticate/repo/snapshots/"))(Resolver.ivyStylePatterns),
	Resolver.sonatypeRepo("snapshots")
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
   |//import scala.slick.driver.PostgresDriver.simple._
   |import scala.reflect.runtime.universe._
   |""".stripMargin

logLevel := Level.Warn

logLevel in test := Level.Info // Level.Info is needed to see detailed output when running tests

logLevel in compile := Level.Warn

