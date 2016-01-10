package nescala

import scala.collection.JavaConversions.propertiesAsScalaMap

abstract class Config {
  private lazy val props: Option[Map[String,String]] = {
    Option(getClass.getResourceAsStream("/meetup.properties")).map { stream =>
      val props = new java.util.Properties
      props.load(stream)
      stream.close()
      props.toMap
    }
  }

  def propertyOption(name: String): Option[String] = {
    Option(System.getenv(name)).orElse(props.flatMap(_.get(name)))
  }

  def property(name: String) =
     propertyOption(name) match {
      case None => sys.error("missing property %s" format name)
      case Some(value) => value
    }

  def intProperty(name: String) =
    try {
      property(name).toInt
    } catch { case nfe: NumberFormatException =>
      sys.error("%s was not an int" format property(name))
    }

  def booleanProperty(name: String, default: Boolean = false) =
    propertyOption(name).map { s =>
      s.toLowerCase match {
        case "false" | "f" | "0" | "n" | "no" => false
        case "true"  | "n" | "1" | "y" | "yes" => true
        case _ => sys.error(s"""Bad boolean value "$s" for $name""")
      }
    }.
    getOrElse(default)

  val siteSecret = property("secret")
}
