resolvers ++= Seq(
  "less is" at "http://repo.lessis.me",
  "coda" at "http://repo.codahale.com")

addSbtPlugin("me.lessis" % "ls-sbt" % "0.1.3")
addSbtPlugin("com.eed3si9n" % "sbt-assembly" % "0.14.1")
addSbtPlugin("io.spray" % "sbt-revolver" % "0.7.1")

libraryDependencies += "org.clapper" %% "grizzled-scala" % "1.4.0"
