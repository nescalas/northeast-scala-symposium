package nescala.boston2018

case class Sponsor(name: String, homepage: String, logo: String, sponsorship: String)

object Sponsor {
  val sponsors = Seq(
    Sponsor("CiBO Technologies", "http://www.cibotechnologies.com/", "/sponsors/cibo-technologies-logo.svg", "catering breakfast on Day 2"),
    Sponsor("Threat Stack", "http://threatstack.com/", "/sponsors/threat-stack-logo.png", "sponsoring lunch at [Clover Food Lab](https://www.cloverfoodlab.com/locations/location/?l=cloverknd) on the day of the Unconference."),
    Sponsor("Twilio", "http://twilio.com/", "/sponsors/twilio-logo.svg", "catering happy hour after the Unconference"),
    Sponsor("Bridgewater Associates", "http://bwater.com/", "/sponsors/bridgewater-logo.svg", "sponsoring videography for us and for the Typelevel Summit.")
  )
}
