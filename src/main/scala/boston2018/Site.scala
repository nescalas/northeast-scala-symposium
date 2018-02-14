package nescala.boston2018

import com.google.common.cache.{ CacheBuilder, CacheLoader }
import dispatch._ // for future pimping
import dispatch.Defaults._
import nescala.{ Meetup, SessionCookie }
import nescala.request.UrlDecoded
import org.joda.time.{ DateMidnight, DateTimeZone, LocalDateTime }
import unfiltered.request.{ DELETE, GET, HttpRequest, Params, Path, POST, Seg, & }
import unfiltered.request.QParams._
import unfiltered.response.{ JsonContent, NotFound, Redirect, ResponseString, ResponseFunction, Unauthorized, Pass }
import unfiltered.Cycle.Intent
import scala.util.control.NonFatal
import scala.util.{Random, Try, Success, Failure}
import java.net.URLDecoder
import java.util.concurrent.TimeUnit

object Site extends Templates {

  lazy val proposalStore: Map[String, Proposal] = Proposal.load() match {
    case Success(proposals) => proposals.map(_.id).zip(proposals).toMap
    case Failure(err) => { err.printStackTrace(); Map.empty}
  }

  def index(
    req: HttpRequest[Any],
    pathVars: Map[String, String]
  ) = respond(req)(layout())

  def proposals(
    req: HttpRequest[Any],
    pathVars: Map[String, String]
  ) = respond(req)(
        layout(
          backToTop +:
          proposalStore
            .values
            .groupBy(_.talk_format)
            .toSeq
            .sortBy(_._1.lengthInMinutes)
            .reverse
            .map{case (fmt, ps) => proposalDirectorySection(fmt, ps)}
          ,
          defaultHeader.copy(
            subheading = {() => "talk proposals"}
          )
        )
      )

  def proposal(
    req: HttpRequest[Any],
    pathVars: Map[String, String]
  ) = req match {
    case GET(req) =>
      pathVars
        .get("id")
        .flatMap{id => proposalStore.get(URLDecoder.decode(id, "UTF-8"))}
        .map{p: Proposal => respond(req)(
          layout(
            proposalSections(p),
            proposalHeader(p)
          )
        )}
        .getOrElse(NotFound ~> ResponseString(s"Could not find that proposal."))
    case _ => Pass
  }

  def respond
   (req: HttpRequest[_])
   (handle: Option[SessionCookie] => ResponseFunction[Any]) =
    req match {
      case SessionCookie.Value(sc) =>
        sc.fold(handle(None), { member => handle(Some(member)) })
      case _ =>
        handle(None)
    }
}
