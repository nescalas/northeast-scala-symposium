package nescala

import dispatch._ // for future enrichment
import unfiltered.Cycle.Intent
import unfiltered.request.{ HttpRequest, GET, Params, Path, Seg, & }
import unfiltered.response.{ Redirect, SetCookies }
import unfiltered.Cookie
import com.ning.http.client.oauth.RequestToken
import scala.concurrent.ExecutionContext.Implicits.global
import nescala.request.UrlEncoded

/** authentication endpoints */
object Northeast extends Config {

  object Error extends Params.Extract("error", Params.first)
  object State extends Params.Extract("state", Params.first)
  object Code extends Params.Extract("code", Params.first)

  val callback = s"${property("host")}/authenticated"

  def index = Redirect("/")

  def login(
    req: HttpRequest[Any],
    pathVars: Map[String, String]
  ) = req match {
    case Params(p) => Redirect(Meetup.authorize(callback, State.unapply(p)))
  }

  def logout(
    req: HttpRequest[Any],
    pathVars: Map[String, String]
  ) = SessionCookie.discard ~> Redirect("/")

  def authenticated(
    req: HttpRequest[Any],
    pathVars: Map[String, String]
  ) = req match {
    case Params(params) =>
      params match {
        case Error(error) =>
          index
        case Code(code) =>
          Meetup.access(code, callback).map {
            case (access, refresh) =>
              val session = Session.create(access, refresh)
              val member = nescala.boston2015.Member
                .sync(session.memberId.apply().toString)
              SessionCookie.drop(session) ~>
                Redirect(State.unapply(params) match {
                  case Some("propose") =>
                    "/2016/talks#speak"
                  case Some(value) =>
                    s"/2016/talks#$value"
                  case _ =>
                    "/"
                })
            case _ =>
              index
          }.getOrElse {
            index
          }
        case _ =>
          index
      }
  }
}
