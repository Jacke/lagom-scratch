// Comment to get more information during initialization
//logLevel := Level.Warn

resolvers += "Typesafe repository" at "http://repo.typesafe.com/typesafe/releases/"

//addSbtPlugin("com.typesafe.play" % "sbt-plugin" % "2.6.6")

//addSbtPlugin("com.jamesward" % "play-auto-refresh" % "0.0.16")

addSbtPlugin("com.lightbend.lagom" % "lagom-sbt-plugin" % "1.4.0-M2")

libraryDependencies += "com.geirsson" %% "scalafmt-bootstrap" % "0.6.6"

// Needed for importing the project into Eclipse
//addSbtPlugin("com.typesafe.sbteclipse" % "sbteclipse-plugin" % "5.1.0")
