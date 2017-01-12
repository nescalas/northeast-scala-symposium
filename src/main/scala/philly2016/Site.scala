package nescala.philly2016

import com.google.common.cache.{ CacheBuilder, CacheLoader }
import dispatch._

import nescala.{ Meetup, SessionCookie }
import unfiltered.request.{ GET, HttpRequest, Path, Seg, & }
import unfiltered.response._
import unfiltered.Cycle.Intent
import java.util.concurrent.TimeUnit

object Site extends Templates {

  import Constants._

  def talks(anchor: String = "") =
    if (anchor.nonEmpty) Redirect(s"/2016/talks#$anchor")
    else Redirect("/2016/talks")

  // cache sponsor list for one hour
  def sponsors = CacheBuilder.newBuilder
    .expireAfterWrite(1, TimeUnit.HOURS)
    .build(new CacheLoader[String, List[Meetup.Sponsor]] {
      def load(urlname: String) = Meetup.sponsors(urlname).apply()
    })

  def index(
    req: HttpRequest[Any],
    pathVars: Map[String, String]
  ) = req match {
    case GET(req) & Path(Seg("2016" :: Nil)) =>
      respond(req)(indexPage(sponsors.get(Constants.MeetupOrgName)))
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
