package nescala

trait Config {
  private lazy val props = {
    Option(getClass.getResourceAsStream("/meetup.properties")).map { stream =>
      val props = new java.util.Properties
      props.load(stream)
      stream.close()
      props
    }
  }

  def property(name: String) =
    Option(System.getenv(name)).orElse(props.map(_.getProperty(name))) match {
      case None => sys.error("missing property %s" format name)
      case Some(value) => value
    }

  def intProperty(name: String) =
    try {
      property(name).toInt
    } catch { case nfe: NumberFormatException =>
      sys.error("%s was not an int" format property(name))
    }

  val siteSecret = property("secret")
}
