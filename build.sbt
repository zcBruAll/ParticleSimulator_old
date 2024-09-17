val scala3Version = "3.5.0"

lazy val root = project
  .in(file("."))
  .settings(
    name := "particleS_sim",
    version := "0.1.0-SNAPSHOT",

    scalaVersion := scala3Version,

    libraryDependencies ++= Seq(
      "org.scalameta" %% "munit" % "1.0.0" % Test,
      "org.scalafx" %% "scalafx" % "22.0.0-R33")
  )
