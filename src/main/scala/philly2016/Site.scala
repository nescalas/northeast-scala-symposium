package nescala.philly2016

import com.google.common.cache.{ CacheBuilder, CacheLoader }
import dispatch._

import nescala.{ Meetup, SessionCookie }
import nescala.request.UrlDecoded
import unfiltered.request.{ DELETE, GET, HttpRequest, Params, Path, POST, Seg, & }
import unfiltered.request.QParams._
import unfiltered.response.{ JsonContent, Redirect, ResponseString, ResponseFunction, Unauthorized }
import unfiltered.Cycle.Intent
import scala.util.Random
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

  def pages: Intent[Any, Any] = {
    case GET(req) & Path(Seg(Nil)) =>
      respond(req)(indexPage(sponsors.get("nescala")))
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
