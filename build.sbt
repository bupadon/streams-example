version       := "0.1"

scalaVersion  := "2.11.11"

scalacOptions := Seq("-unchecked", "-deprecation", "-encoding", "utf8")
scalacOptions in Test ++= Seq("-Yrangepos")

libraryDependencies ++= {
  val akkaV = "2.5.4"

  Seq(
    "com.typesafe.akka"   %%  "akka-actor"    % akkaV,
    "com.typesafe.akka"   %%  "akka-stream"    % akkaV,
    "com.typesafe.akka"   %%  "akka-slf4j"    % akkaV
  )
}

assemblyJarName in assembly := "streams-example.jar"
test in assembly := {}

Revolver.settings
