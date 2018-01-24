package nescala.boston2018

import com.google.common.cache.{ CacheBuilder, CacheLoader }
import dispatch._ // for future pimping
import dispatch.Defaults._
import nescala.{ Meetup, SessionCookie }
import nescala.request.UrlDecoded
import org.joda.time.{ DateMidnight, DateTimeZone, LocalDateTime }
import unfiltered.request.{ DELETE, GET, HttpRequest, Params, Path, POST, Seg, & }
import unfiltered.request.QParams._
import unfiltered.response.{ JsonContent, Redirect, ResponseString, ResponseFunction, Unauthorized, Pass }
import unfiltered.Cycle.Intent
import scala.util.control.NonFatal
import scala.util.Random
import java.util.concurrent.TimeUnit

object Site extends Templates {

  def index(
    req: HttpRequest[Any],
    pathVars: Map[String, String]
  ) = respond(req)(layout(baseSections))


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
