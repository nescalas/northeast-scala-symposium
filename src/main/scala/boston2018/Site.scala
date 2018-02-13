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
import java.util.concurrent.TimeUnit

object Site extends Templates {

  lazy val proposalStore: Map[String, Proposal] = Proposal.load() match {
    case Success(proposals) => proposals.map(_.id).zip(proposals).toMap
    case Failure(err) => { err.printStackTrace(); Map.empty}
  }

  def index(
    req: HttpRequest[Any],
    pathVars: Map[String, String]
  ) = respond(req)(layout(baseSections))

  def proposal(
    req: HttpRequest[Any],
    pathVars: Map[String, String]
  ) = req match {
    case GET(req) =>
      pathVars
        .get("id")
        .flatMap{id => proposalStore.get(id)}
        .map{proposal: Proposal => respond(req)(layout(baseSections))}
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
