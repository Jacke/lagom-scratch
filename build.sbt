organization in ThisBuild := "com.example"
version in ThisBuild := "1.0-SNAPSHOT"

// the Scala version that will be used for cross-compiled libraries
scalaVersion in ThisBuild := "2.11.8"

val macwire = "com.softwaremill.macwire" %% "macros" % "2.2.5" % "provided"
val scalaTest = "org.scalatest" %% "scalatest" % "3.0.1" % Test
val joda = "joda-time" % "joda-time" % "2.9.9"
val persist = "com.lightbend.lagom" %% "lagom-scaladsl-persistence-jdbc" % "1.3.10"
val postgres = "org.postgresql" % "postgresql" % "9.4.1212"
val slick = "com.typesafe.slick" %% "slick" % "3.2.1"
val nop =  "org.slf4j" % "slf4j-nop" % "1.6.4"
val hikari = "com.typesafe.slick" %% "slick-hikaricp" % "3.2.1"

val slickjoda =  "com.github.tototoshi" %% "slick-joda-mapper" % "2.3.0"
val jodaconvert =  "org.joda" % "joda-convert" % "1.7"

lazy val `microservicecal` = (project in file("."))
  .aggregate(`microservicecal-api`, `microservicecal-impl`)

lazy val `microservicecal-api` = (project in file("microservicecal-api"))
  .settings(
    libraryDependencies ++= Seq(
              lagomScaladslPersistenceJdbc,
lagomScaladslApi    )
  )

lazy val `microservicecal-impl` = (project in file("microservicecal-impl"))
  .enablePlugins(LagomScala)
  .settings(
    libraryDependencies ++= Seq(
      lagomScaladslPersistence,
      lagomScaladslPersistenceJdbc,
//     persist,
      lagomScaladslKafkaBroker,
      lagomScaladslTestKit,
      joda,
      postgres,
      macwire,
      scalaTest,
   //   slick,
      nop,
   //   hikari,
      slickjoda,

      jodaconvert

    )
  )
  .settings(lagomForkedTestSettings: _*)
  .dependsOn(`microservicecal-api`)

