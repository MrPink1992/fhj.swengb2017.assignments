import sbt._
import Dependencies._
import BuildConstants._

lazy val commonSettings: Seq[Def.SettingsDefinition] = Seq(
  organization := org,
  scalaVersion := scalaVer,
  version := buildVer,
  libraryDependencies += scalaTest,
  fork := true
)


// ------------------------------------------------------

lazy val battleShipModel = (project in file("battleship/model/")).
  enablePlugins(ProtobufPlugin).
  settings(commonSettings: _*).
  settings(name := "battleship-model",
    javaSource in ProtobufConfig := ((sourceDirectory in Compile).value / "generated"),
    libraryDependencies += scalacheck
  )

lazy val battleShipFx = (project in file("battleship/jfx"))
  .settings(commonSettings: _*).
  settings(name := "battleship-jfx",
  ).dependsOn(battleShipModel)

// ------------------------------------------------------
// main project
lazy val assignments = (project in file(".")).
  settings(commonSettings: _*).
  settings(name := "assignments").aggregate(battleShipFx)