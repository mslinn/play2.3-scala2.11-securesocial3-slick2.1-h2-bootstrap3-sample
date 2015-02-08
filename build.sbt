name := "play2.3-scala2.11-scalikejdbc-securesocial3-bootstrap3-sample"

organization := "com.micronautics"

version := "1.0.0-SNAPSHOT"

scalaVersion := "2.11.5"

libraryDependencies ++= Seq(
  jdbc,
  "org.scalikejdbc"           %% "scalikejdbc-async"             % "0.5.5",
  "org.scalikejdbc"           %% "scalikejdbc-async-play-plugin" % "0.5.5",
  "com.github.mauricio"       %% "postgresql-async"              % "0.2.16",
  "org.postgresql"            %  "postgresql"                    % "9.3-1102-jdbc41",

  "org.webjars"               %% "webjars-play"                  % "2.3.0-2",
  "org.webjars"               %  "bootstrap"                     % "3.3.2",

  "ws.securesocial"           %% "securesocial"                  % "3.0-M3",
  "ws.securesocial"           %% "securesocial"                  % "3.0-M3" classifier "assets",

  "org.scalatestplus"         %% "play"                          % "1.2.0" % "test",
  "org.scalatest"             %% "scalatest"                     % "2.2.1" % "test",
  "junit"                     %  "junit"                         % "4.12"  % "test"
)

scalacOptions ++= Seq("-deprecation", "-encoding", "UTF-8", "-feature", "-target:jvm-1.7", "-unchecked", "-Ywarn-adapted-args", "-Ywarn-value-discard", "-Xlint")

PlayKeys.routesImport ++= Seq("scala.language.reflectiveCalls")

javacOptions ++= Seq("-Xlint:deprecation", "-Xlint:unchecked", "-source", "1.7", "-target", "1.7", "-g:vars")

// this causes IntelliJ to not find the source and javadoc jars
//updateOptions := updateOptions.value.withCachedResolution(true)

doc in Compile <<= target.map(_ / "none")

publishArtifact in (Compile, packageSrc) := false

lazy val root = (project in file(".")).enablePlugins(PlayScala)
