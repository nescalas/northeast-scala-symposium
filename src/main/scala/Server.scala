package nescala

import unfiltered.jetty.Http
import unfiltered.filter.Planify

object Server {
  def main(args: Array[String]) {
    val port = args.length match {
      case 0 => Option(System.getenv("PORT")).getOrElse("8080").toInt
      case 1 => args(0).toInt
      case _ => sys.error(s"Usage: Server [port]")
    }
    Http(port)
    .resources(getClass().getResource("/www"))
    .filter(Planify {
      (Northeast.site /: Seq(
        philly2016.Site.pages,
        boston2015.Site.pages,
        nyc2014.Nyc.site,
        philly.Philly.site,
        boston.Boston.site,
        nyc.Nyc.site))(_ orElse _)
    }).run(
      _ => (),
      _ => dispatch.Http.shutdown()
    )
  }
}
