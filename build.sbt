import com.typesafe.sbt.packager.archetypes.JavaAppPackaging

seq(lsSettings :_*)

libraryDependencies ++= Seq(
  "net.databinder" %% "unfiltered-filter" % "0.7.1",
  "net.databinder" %% "unfiltered-jetty" % "0.7.1",
  "net.databinder" %% "unfiltered-json4s" % "0.7.1",
  // http client
  "net.databinder.dispatch" %% "dispatch-json4s-native" % "0.10.0",
  // persistance
  "net.debasishg" %% "redisclient" % "2.13",
  // loging
  "org.slf4j" % "slf4j-jdk14" % "1.6.2",
  // date math
  "joda-time" % "joda-time" % "2.1",
  "org.joda" % "joda-convert" % "1.2",
  "org.clapper" %% "markwrap" % "1.1.0",
  "org.clapper" % "javautil" % "3.1.2",
  "com.github.tototoshi" %% "scala-csv" % "1.2.2",
  "io.backchat.inflector" %% "scala-inflector" % "1.3.5",
  // local cache
  "com.google.guava" % "guava" % "14.0",
  "com.google.code.findbugs" % "jsr305" % "3.0.0" // http://stackoverflow.com/questions/19030954/cant-find-nullable-inside-javax-annotation
)

scalacOptions ++= Seq("-deprecation", "-unchecked")

seq(Revolver.settings: _*)

lazy val root = (project in file(".")).
  enablePlugins(BuildInfoPlugin).
  enablePlugins(SbtWeb).
  enablePlugins(JavaAppPackaging).
  settings(
    buildInfoKeys := Seq[BuildInfoKey](name, version, scalaVersion, sbtVersion),
    buildInfoPackage := "nescala"
  )

includeFilter in (Assets, LessKeys.less) := "*.less"

def exec(command: String) = {
  import sys.process._
  println(s"Running: $command")
  print(command.!!)
}

// Use this task instead of "assembly" to build a fat jar (e.g., for testing
// on a server that isn't Heroku). It ensures that sbt-less files end up in
// the jar.
addCommandAlias("fatjar", ";copyAssets;package;assembly")
val copyAssets = taskKey[Unit]("run copyAssets")
copyAssets := {
  val scalaMajor = scalaVersion.value.split("""\.""").take(2).mkString(".")
  val targetDir = s"target/scala-${scalaMajor}/classes/resources/webjars/root/${version.value}"
  exec(s"mkdir -p ${targetDir}")
  exec(s"cp -r target/web/less/main/css ${targetDir}")
}
