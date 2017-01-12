package nescala.nyc2017

import nescala.{ Meetup, SessionCookie }
import unfiltered.Cycle.Intent
import unfiltered.request.{GET, Path, HttpRequest, Seg, &}
import unfiltered.response.{Pass, ResponseFunction}

object Site extends Templates {

  def index(
    req: HttpRequest[Any],
    pathVars: Map[String, String]
  ) = req match {
    case GET(req) & Path(Seg(Nil)) =>
      respond(req)(indexPage())
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
