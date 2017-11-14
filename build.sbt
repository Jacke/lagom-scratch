organization in ThisBuild := "com.example"
version in ThisBuild := "1.0-SNAPSHOT"

// the Scala version that will be used for cross-compiled libraries
scalaVersion in ThisBuild := "2.11.8"

val macwire = "com.softwaremill.macwire" %% "macros" % "2.2.5" % "provided"
val scalaTest = "org.scalatest" %% "scalatest" % "3.0.1" % Test
val joda = "joda-time" % "joda-time" % "2.9.9"
val persist = "com.lightbend.lagom" % "lagom-scaladsl-persistence-jdbc_2.11" % "1.3.10"
val postgres = "org.postgresql" % "postgresql" % "9.4.1212"

lazy val `microservicecal` = (project in file("."))
  .aggregate(`microservicecal-api`, `microservicecal-impl`)

lazy val `microservicecal-api` = (project in file("microservicecal-api"))
  .settings(
    libraryDependencies ++= Seq(
      lagomScaladslApi
    )
  )

lazy val `microservicecal-impl` = (project in file("microservicecal-impl"))
  .enablePlugins(LagomScala)
  .settings(
    libraryDependencies ++= Seq(
      persist,
      lagomScaladslKafkaBroker,
      lagomScaladslTestKit,
      joda,
      postgres,
      macwire,
      scalaTest
    )
  )
  .settings(lagomForkedTestSettings: _*)
  .dependsOn(`microservicecal-api`)

