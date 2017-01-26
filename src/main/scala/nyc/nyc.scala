package nescala.nyc

import unfiltered.request._
import unfiltered.response._
import nescala.{ Cached, Config, Meetup }
import org.json4s.native.JsonMethods.{ compact, render }

object Nyc extends Config {

  // "/nyc/rsvps")
  def rsvps(
    req: HttpRequest[Any],
    pathVars: Map[String, String]
  ) = req match {
    case GET(_) & Jsonp.Optional(jsonp) =>
      JsonContent ~> ResponseString(
        jsonp.wrap(
          compact(render(Cached.Nyc.rsvps))))
  }

  // "/nyc/photos"
  def photos(
    req: HttpRequest[Any],
    pathVars: Map[String, String]
  ) = req match {
    case GET(Jsonp.Optional(jsonp)) =>
     JsonContent ~> ResponseString(
       jsonp.wrap(
         compact(render(Cached.Nyc.photos))))
  }

  /*def site: unfiltered.Cycle.Intent[Any, Any] = {
    case GET(Path("/nyc/rsvps") & Jsonp.Optional(jsonp)) =>
      JsonContent ~> ResponseString(
        jsonp.wrap(
          compact(render(Cached.Nyc.rsvps))))
    case GET(Path("/nyc/photos") & Jsonp.Optional(jsonp)) =>
      JsonContent ~> ResponseString(
        jsonp.wrap(
          compact(render(Cached.Nyc.photos))))
  }*/
}
