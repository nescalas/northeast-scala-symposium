package nescala

import unfiltered.Cycle.Intent
import unfiltered.jetty.Http
import unfiltered.filter.Planify
import unfiltered.kit.Routes
import unfiltered.request.{HttpRequest, GET, Seg, Path, Method, &}
import unfiltered.response.{NotFound, ResponseString, Pass}

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
      Routes.specify[Any,Any](
        "/" -> boston2018.Site.index,
        "/login" -> Northeast.login,
        "/logout" -> Northeast.logout,
        "/authenticated" -> Northeast.authenticated,
        "/css/less/:rest" -> CommonHandlers.cssless,
        "/2017" -> nyc2017.Site.index,
        "/2016" -> philly2016.Site.index,
        "/2015" -> boston2015.Site.index,
        "/2015/talks" -> boston2015.Site.talks,
        "/2015/talks/peek" -> boston2015.Site.talksPeek,
        "/2015/talks/:id" -> boston2015.Site.talk,
        "/2015/talks/:id/votes" -> boston2015.Site.votes,
        "/2014" -> nyc2014.Nyc.index,
        "/2014/friends" -> nyc2014.Nyc.friends,
        "/2014/talks" -> nyc2014.Proposals.list,
        "/2014/tally" -> nyc2014.Tally.talks,
        "/2013" -> philly.Philly.index,
        "/2013/friends" -> philly.Philly.friends,
        "/2013/talks" -> philly.Proposals.list,
        "/philly/rsvps/:event_id" -> philly.Philly.rsvps,
        "/2013/talk_tally" -> philly.Tally.talks,
        "/2012" -> boston.Boston.indexPage,
        "/2012/friends" -> boston.Boston.friends,
        "/boston/rsvps" -> boston.Boston.api,
        "/2012/pannels" -> boston.Boston.pannelProposals,
        "/2012/talks" -> boston.Boston.talkProposals,
        "/nyc/rsvps" -> nyc.Nyc.rsvps,
        "/nyc/photos" -> nyc.Nyc.photos,
        "/.well-known/acme-challenge/NVehMsEOuhK1JtEu_gme93Utb5otcrmq-K3u5LuFcmY" -> nyc2017.Site.ssl
      )
    }).run(
      _ => (),
      _ => dispatch.Http.shutdown()
    )
  }
}

/** Common HTTP handlers, useful for any of the sub-pages.
  */
object CommonHandlers {

  def assetPreprocessor
    (preprocessor: String,
     output: String)
    (req: HttpRequest[Any],
    pathVars: Map[String, String]) = req match {
    // This case handles sbt-less generated files. If we're running from a
    // jar file, the compiled CSS files are available under a specific,
    // versioned resource path. If we're running in development mode, they're
    // available under "target". This handler checks for both and is necessary
    // because we're not using sbt-less in a platform, like Play, that has an
    // asset manager layer.
    case GET(req) & Path(Seg(output :: preprocessor :: rest)) => {
      import nescala.BuildInfo
      import scala.io.Source
      import java.io.File
      val thing = rest.mkString("/")
      val rsrc = s"/webjars/root/${BuildInfo.version}/$output/$thing"
      val path = s"target/web/$preprocessor/main/$output/$thing"
      // Try the local compile path first. If not there, try as a resource.
      val sourceOpt = if (new File(path).exists) {
        Some(Source.fromFile(path))
      }
      else {
        sourceFromResource(rsrc)
      }

      sourceOpt.map { source => ResponseString(source.mkString) }
               .getOrElse(NotFound ~> ResponseString(s"Not found: $output/$preprocessor/$thing"))
    }
  }

  def cssless  = assetPreprocessor("less", "css")_
  def sassless = assetPreprocessor("sass", "css")_

  private def sourceFromResource(resource: String): Option[Source] = {
    val stream = Option(getClass.getResourceAsStream(resource))
    stream.map { Source.fromInputStream(_) }
  }
}
