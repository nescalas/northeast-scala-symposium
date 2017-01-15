package nescala.nyc2017

import nescala.{ Meetup, SessionCookie }
import unfiltered.Cycle.Intent
import unfiltered.request.{GET, Path, HttpRequest, Seg, &}
import unfiltered.response.{Pass, ResponseFunction}

object Site extends Templates {

  def ssl(
    req: HttpRequest[Any],
    pathVars: Map[String, String]
  ) = req match {
    case GET(req) & Path(Seg(".well-known" :: "acme-challenge" :: "NVehMsEOuhK1JtEu_gme93Utb5otcrmq-K3u5LuFcmY" :: _)) => 
      unfiltered.response.ResponseString("NVehMsEOuhK1JtEu_gme93Utb5otcrmq-K3u5LuFcmY.ql_s6qM7EE4MZA4mzgKM0L_-v8c-KU5yzp0h5Rw5YJw")
    case _ => Pass
  }

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
