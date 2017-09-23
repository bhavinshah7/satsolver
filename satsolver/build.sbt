import Dependencies._
enablePlugins(JavaAppPackaging)
lazy val root = (project in file(".")).
  settings(
    inThisBuild(List(
      organization := "com.satsolver",
      scalaVersion := "2.12.1",
      version      := "0.1.0-SNAPSHOT"
    )),
    name := "SatSolver",
	mainClass in (Compile, run) := Some("com.satsolver.DPLLMain"),
	mainClass in (Compile, packageBin) := Some("com.satsolver.DPLLMain"),
    libraryDependencies += scalaTest % Test,
	libraryDependencies ++= {
		Seq(
			"com.typesafe.akka" %% "akka-actor" % "2.5.4",
			"com.typesafe.akka" %% "akka-testkit" % "2.5.4" % Test,
			"org.sat4j" % "org.sat4j.core" % "2.3.1"
		)
	}
  )
