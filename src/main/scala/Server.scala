package nescala

import nescala.philly2016.Site._
import unfiltered.Cycle.Intent
import unfiltered.jetty.Http
import unfiltered.filter.Planify
import unfiltered.request.{GET, Seg, Path, Method, &}
import unfiltered.response.{NotFound, ResponseString}

import scala.io.Source

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
        CommonHandlers.handle,
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

/** Common HTTP handlers, useful for any of the sub-pages.
  */
object CommonHandlers {
  def handle: Intent[Any, Any] = {

    // This case handles sbt-less generated files. If we're running from a
    // jar file, the compiled CSS files are available under a specific,
    // versioned resource path. If we're running in development mode, they're
    // available under "target". This handler checks for both and is necessary
    // because we're not using sbt-less in a platform, like Play, that has an
    // asset manager layer.
    case GET(req) & Path(Seg("cssless" :: rest)) => {
      import nescala.BuildInfo
      import scala.io.Source
      import java.io.File
      val thing = rest.mkString("/")
      val rsrc = s"/webjars/root/${BuildInfo.version}/css/$thing"
      val path = s"target/web/less/main/css/$thing"
      // Try the local compile path first. If not there, try as a resource.
      val sourceOpt = if (new File(path).exists) {
        Some(Source.fromFile(path))
      }
      else {
        sourceFromResource(rsrc)
      }

      sourceOpt.map { source => ResponseString(source.mkString) }
               .getOrElse(NotFound ~> ResponseString(s"cssless/$thing"))
    }
  }

  private def sourceFromResource(resource: String): Option[Source] = {
    val stream = Option(getClass.getResourceAsStream(resource))
    stream.map { Source.fromInputStream(_) }
  }
}
