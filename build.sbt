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
  "org.clapper" %% "markwrap" % "1.0.2",
  "com.github.tototoshi" %% "scala-csv" % "1.2.2",
  // local cache
  "com.google.guava" % "guava" % "14.0",
  "com.google.code.findbugs" % "jsr305" % "3.0.0" // http://stackoverflow.com/questions/19030954/cant-find-nullable-inside-javax-annotation
)

scalacOptions ++= Seq("-deprecation", "-unchecked")

seq(Revolver.settings: _*)

// sbt-less is a pain in the ass to use outside Play. Here, we do it manually.
// Assumes Node.js and lessc are installed and in the path.

val lessc = taskKey[Seq[File]]("run lessc")
lessc := {

  import scala.language.postfixOps
  import grizzled.file.{util => fileutil}
  import java.io.File
  import sys.process._

  val log = streams.value.log
  val sourceDir = sourceDirectory.value

  def relativeSource(path: String) = {
    val parent = fileutil.dirname(sourceDir.getPath)
    path.drop(parent.length + 1)
  }

  // Get the list of sources.
  val lessFiles: sbt.PathFinder = sourceDir ** "*.less"

  for (source <- lessFiles.get) {
    val target = new File(source.getPath.replace(".less", ".css"))
    if (target.lastModified < source.lastModified) {
      val relSource = relativeSource(source.getPath)
      val relOutput = relSource.replace(""".less""", ".css")
      val cmd = s"lessc --no-js $relSource $relOutput"
      println(s"+ $cmd")
      cmd.!!
    }
  }

  lessFiles.get

}

// Compile hooks

compile in Compile <<= (compile in Compile) dependsOn(lessc)


