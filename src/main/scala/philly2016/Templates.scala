package nescala.philly2016

import nescala.{ Meetup, SessionCookie, Util }
import unfiltered.response.Html5
import java.text.SimpleDateFormat
import java.util.{ Calendar, Date, TimeZone }

import scala.xml.Elem

trait Templates {

  import Enrichments.EnrichedDate
  import Constants._
  import nescala.XMLEnrichments._

  private def offsiteLink(url: String, text: String) = {
    <a href={url} target={OffsiteAnchorTarget}>{text}</a>
  }

  private def timestamp(d: Date, offset: Boolean = true) = {
    def fmt(f: String) = new SimpleDateFormat(f) {
      if (offset) {
        setTimeZone(TimeZone.getTimeZone("US/Eastern"))
      }
    }
    val time = (if (d.field(Calendar.MINUTE) > 0) fmt("h:mm") else fmt("h")).format(d)
    <span class="time">{ time }</span><span class="ampm">{ fmt("aa").format(d).toLowerCase }</span>
  }

  private def pollsClosed = {
    <span>
      Votes on {offsiteLink(Constants.ProposalsURL, "talk proposals")}
      are in. Winners are below.
    </span>
  }

  private def levelToHeader(level: Int) = {
    level match {
      case 1 => <h1></h1>
      case 2 => <h2></h2>
      case 3 => <h3></h3>
      case 4 => <h4></h4>
      case _ => <h2></h2>
    }
  }

  private def augmentHeading(heading: scala.xml.Elem, level: Int) = {
    val icon = <span class="section-icon">&#x00a7;&nbsp;</span>
    val header = levelToHeader(level)
    header.copy(child = icon.toSeq ++ heading.toSeq)
  }

  private def sectionHeader(id: String, heading: scala.xml.Elem, level: Int = 2) = {
    <a href={s"#$id"} class="section">
      {augmentHeading(heading, level)}
    </a>
  }

  private def sectionHeaderWithContent(id: String,
                                       heading: scala.xml.Elem,
                                       headingClass: String = "three-fifths",
                                       contentClass: String = "two-fifths",
                                       level: Int = 2)
                                      (content: => scala.xml.Elem) = {
    <div class="grid">
      <div class={"unit " + headingClass}>
        {augmentHeading(heading, level)}
      </div>
      <div class={"unit " + contentClass}>
        {content}
      </div>
    </div>
  }

  private def renderSchedule = {
    val speakerMeetupIDs = PhillySchedule.flatMap { _.meetupID }
    val speakers = Meetup.members(speakerMeetupIDs).map { member =>
      member.id -> member
    }.toMap

    val schedule = PhillySchedule.zipWithIndex.map { case (slot, index) =>
      import org.joda.time._
      val oddEven = if ((index % 2) == 0) "even" else "odd"

      <div class={s"grid $oddEven"}>
        <span class="right unit one-fifth time-slot">
          {
          val hour = slot.time.get(DateTimeFieldType.hourOfDay)
          val minute = slot.time.get(DateTimeFieldType.minuteOfHour)
          f"$hour:$minute%02d"
          }
        </span>
        <span class="unit one-fifth activity">{
          slot.activity.getOrElse("")
        }</span>
        <span class="unit one-fifth align-center">{
          val photo = slot.meetupID.flatMap { id =>
            speakers.get(id).map { m =>
              <img class="speaker-photo" src={m.photo}/>
            }
          }
          .getOrElse(<span>&nbsp;</span>)

          val speakerName = slot.speaker.map { s =>
            <div class="speaker-name">{s}</div>
          }.
          getOrElse(<span>&nbsp;</span>)

          val twitter = (for { id <- slot.meetupID
                               m  <- speakers.get(id)
                               t  <- m.twttr } yield {
            <div class="twitter-handle">{t}</div>
          })
          .getOrElse(<span>&nbsp;</span>)

          photo ++ speakerName ++ twitter
        }
        </span>

        <span class="unit two-fifths description">{
          slot.description.map { s =>
            val e = try {
              scala.xml.XML.loadString(s)
            }
            catch {
              case ex: Exception => {
                println(s"Failed to parse HTML:\n$s\n")
                throw ex
              }
            }

            // Find all links (<a> tags). Any with off-page href
            // attributes (i.e., those not beginning with "#") should
            // open in a new tab.
            e.child.map { c =>
              c match {
                case e: Elem if e.label == "a"  => {
                  val href = e.attribute("href")
                              .flatMap(_.headOption)
                              .map(_.text)
                              .getOrElse("")
                  if (! href.startsWith("#")) {
                    e.attr("target", OffsiteAnchorTarget)
                  }
                  else {
                    e
                  }
                }
                case e => e
              }
            }
          }
          .getOrElse(<span></span>)
        }
        </span>
      </div>
    }

    schedule
  }

  def indexPage
   (sponsors: List[Meetup.Sponsor])
   (session: Option[SessionCookie] = None) =
    layout(session)(scripts = Seq.empty[String])(
      <div class="grid well">
        <div class="unit full center center-on-mobiles">
          <p>
            The Northeast Scala Symposium is the original Scala community-based
            conference. We're gearing up for our
            <a href="#what">6th year.</a>
          </p>
        {
          if (rsvpsAreOpen) {
            <div>
              <p>
                RSVP on
                {offsiteLink("http://www.meetup.com/nescala/", "Meetup")}:
              </p>

              <p>
                {offsiteLink(s"http://www.meetup.com/nescala/events/$Day1EventId/",
                             "Day one")} |
                {offsiteLink(s"http://www.meetup.com/nescala/events/$Day2EventId/",
                             "Day two")}
              </p>
              {
                val rsvps = Meetup.rsvpCount(MeetupOrgName, Day1EventId)
                <p>
                  {Util.singularPlural(rsvps.yesCount, "ticket")} sold for Day One.
                  {
                    val left = (rsvps.limit - rsvps.yesCount)
                    val reallyLeft = if (left < 0) 0 else left
                    Util.singularPlural(reallyLeft, "ticket")
                  }
                  left.<sup>*</sup>
                </p>
              }
            </div> ++ {
            if (votingIsOpen) {
              <div>
                Then,
                {offsiteLink(VotingFormURL, "vote for talks")}
                (by {VotesCloseDateStr} at {VotesCloseTimeStr} EST).
              </div>
            }
            else if (votingIsClosed) {
              <div></div>
            }

            else {
              <div>Voting opens {VotesOpenDateStr} at {VotesOpenTimeStr} EST.</div>
            }
          } ++
          <div class="note">
            <hr/>
            <sup>*</sup>Note: The Hub has a limit of 200 attendees. We are holding 10
            tickets in reserve, to ensure that each speaker whose talk is
            chosen has a ticket. Once we are certain all speakers are
            covered, we will release whatever tickets remain.
          </div>
          }
          else if (proposingIsOpen) {
            <div>
              {offsiteLink(ProposalsURL, "Propose a talk")}
              (by {ProposalsCloseStr})
            </div>
          }
          else {
            <p>Stay tuned!</p>
          }
        }
        </div>
      </div>
      <div class="communicate">
        <a class="icon" href="http://twitter.com/nescalas"
           target={OffsiteAnchorTarget}>
          <i class="fa fa-twitter"></i><span>Follow us.</span>
        </a>
        <a class="icon" href="http://www.meetup.com/nescala/"
           target={OffsiteAnchorTarget}>
          <i class="icon-scala"></i><span>Join us</span>
        </a>
        <a href="#what" class="icon"><i class="fa fa-check-circle-o"></i><span>Learn with us</span></a>
        <a href="#when" class="icon"><i class="fa fa-calendar-o"></i><span>Mark your calendar</span></a>
        <a href="#where" class="icon"><i class="fa fa-map-marker"></i><span>Fire up your GPS</span></a>
      </div>
    )(
      <div class="inverse">
        <div class="grid">
          <div class="unit">
            {sectionHeader("what", <span>This is <strong>your</strong> conference.</span>)}
            <p>
              The Northeast Scala Symposium is a
              {offsiteLink("http://scala-lang.org/", "Scala")}-focused
              <strong>community</strong> gathering.
            </p>
            <p>
              NE Scala offers a mix of speaker-oriented conference presentations
              with unconference-style sessions and discussions. (And coffee.
              Lots of coffee.) All presenters are attendees, and all
              attendees select presenters.
            </p>
          </div>
        </div>

        {sectionHeaderWithContent("day1", <span>Day 1 schedule</span>) {
          <div id="live-stream-blurb">
            <h3>Live Streaming</h3>
            <i class="icon fa fa-video-camera"></i>
            This year, thanks to <a href="#sponsors">Typesafe</a>, the Day 1
            talks will be streamed live, over the Internet, for up to 100
            remote watchers. If you can't join us in person, you can join
            us virtually. Details will be posted here when they're available.
          </div>
        }}

        <div id="schedule">{
          if (! votingIsClosed) {
            <div class="grid odd">
              <div class="unit">
                When voting is complete and the votes are tallied, the schedule
                will magically appear here.
              </div>
            </div>
          }
          else {
            renderSchedule
          }
        }</div>
        <div class="grid">
          <div class="unit">
            {sectionHeader("day2", <span>Day 2 schedule</span>)}
            <p>See the
              {offsiteLink(Day2URL, "Meetup page")}.
            </p>
          </div>
        </div>
      </div>
      <div class="regular">
        <div class="grid">
          <div class="unit">
            {sectionHeader("when", <span>When</span>)}
            <p>
              NEScala is held annually. In 2016, we will
              descend upon the City of Brotherly Love, on
              {offsiteLink(Day1URL, "Friday, March 4")} and
              {offsiteLink(Day2URL, "Saturday, March 5")}.
            </p>
            <p>
              To attend, RSVP separately for
              {offsiteLink(Day1URL, "day one")} ($60) and/or
              {offsiteLink(Day2URL, "day two")} (free).
            </p>
          </div>
        </div>
        {
        if (! rsvpsAreOpen) {
          <div class="grid">
            <div class="unit full center">
              <div class="warning">
                <p>NOTE: RSVPs aren't open yet! They'll open on
                Saturday, January 16th at noon (Philadelphia time, a.k.a., Eastern
                Standard).</p>

                <p>There are only 200 slots available (minus reserved slots for
                  speakers). In previous years, the conference has sold out in hours
                  or even minutes.</p>
              </div>
            </div>
            <div class="one-fourth"></div>
          </div>
        }}
      </div>
      <div class="inverse">
        <div class="grid">
          <div class="unit whole">
            {sectionHeader("where", <span>Where</span>)}
            <p>
              This year's symposium will be held at
              {offsiteLink("http://thehub.com/locations/cira-centre/",
                           "The Hub Cira Centre")},
              located at
              {offsiteLink("https://www.google.com/maps/place/2929+Arch+St,+Philadelphia,+PA+19104/@40.002498,-75.1180329,11z/data=!4m2!3m1!1s0x89c6c64bdd84625b:0xbe940a019709b223",
                           "2929 Arch Street, Philadelphia, Pa 19104")},
              right next to historic
              {offsiteLink("https://en.wikipedia.org/wiki/30th_Street_Station",
                           "30th Street Station")},
              which makes it highly accessible via mass transit.
            </p>
            <p>
              Here's a Google Map link showing
              {offsiteLink("https://goo.gl/3eOMn0",
                           "hotels near 30th Street Station")}.
            </p>
          </div>
        </div>
      </div>
      <div class="regular">
        <div class="grid">
          <div class="unit whole">
            {sectionHeader("kindness", <span>Be kind</span>)}
            <p>
              Nobody likes a jerk, so <strong>show respect</strong> for those
              around you.
            </p>
            <p>
              NE Scala is dedicated to providing a harassment-free experience
              for everyone, regardless of gender, gender identity and
              expression, sexual orientation, disability, physical appearance,
              body size, race, or religion (or lack thereof). We
              <strong>do not</strong> tolerate harassment of participants in
              any form.
            </p>
            <p>
              All communication should be appropriate for a technical audience,
              including people of many different backgrounds. Sexual language,
              innuendo, and imagery is not appropriate for any symposium venue,
              including talks.
            </p>
            <p>
              Participants violating these rules may be asked to leave without
              a refund, at the sole discretion of the organizers.
            </p>
            <p>
              Since this is a gathering of static typists, offenders will be
              caught at compile time.
            </p>
          </div>
        </div>
      </div>
      <div class="inverse">
        <div id="sponsors" class="sponsors grid">
          <div class="unit whole">
            {sectionHeader("friends", <span>Friends</span>)}
            <p>Below are some of the sponsors who made this possible.</p>
          </div>
          {
            sponsors.grouped(3).map { group =>
              <div class="grid center-on-mobiles">{
                group.map { sponsor =>
                  <div class="sponsor unit one-third">
                    <a class="friend" title={sponsor.name} href={sponsor.link}
                       target={OffsiteAnchorTarget}>
                      <img src={sponsor.image}/>
                    </a>
                    <div class="blurb">{sponsor.info}</div>
                  </div>
                }
              }</div>
            }
          }
        </div>
      </div>
      <div class="regular">
        <div class="grid">
          <div class="unit whole">
            {sectionHeader("related", <span>Related coolness</span>)}
            <p>
              This year, we're excited to tell you about some <i>other</i>
              events that are associated with NE Scala 2016.
            </p>

            {sectionHeader("typelevel", <span>Typelevel Summit US</span>, level=3)}

            <p>
              On the 2nd and 3rd of March, the two days before NE Scala,
              there will be a
              {offsiteLink("http://typelevel.org/blog/2015/12/11/announcement_summit.html",
                           "Typelevel Summit")}
              including talks, discussion and hacking, focused on the
              Typelevel family of {offsiteLink("http://typelevel.org", "projects")}.
            </p>
            <p>
              The summit is open to all, not just current contributors to
              and users of the Typelevel projects, and the Typelevel organizers
              are especially keen to encourage participation from people who
              are new to them.
            </p>
            <p>
              While many of the Typelevel projects use somewhat "advanced"
              Scala, they are a lot more approachable than many people think,
              and a major part of Typelevel's mission is to make the ideas
              they embody much more widely accessible.
            </p>
            <p>
              So, if you're interested in types and pure functional
              programming, want to make those ideas commonplace and are
              willing to abide by the
              {offsiteLink("http://typelevel.org/conduct.html",
                           "Typelevel code of conduct")},
              then this is the place for you and we'd love to see you there.
            </p>

            {sectionHeader("spark", <span>NewCircle public Spark class</span>, level=3)}

            <p>
              {offsiteLink("http://newcircle.com", "NewCircle")} is
              offering a public
              {offsiteLink("https://newcircle.com/instructor-led-training/apache-spark-development-bootcamp?utm_campaign=nescala_2016&utm_source=nescala_website&utm_medium=referral", "Intro to Spark")}
              class, to be held in Philadelphia March 15—17. NE Scala attendees
              get a $200 discount on the class fee. (The promotion code
              will be emailed to all those whose RSVP, a week or two after RSVPs
              open.) The class will be held in Suite 1700 of the
              {offsiteLink("http://www.americanexecutivecenters.com/locations/philadelphia/",
                           "American Executive Center")}
              at
              {offsiteLink("https://www.google.com/maps/place/1515+Market+St,+Philadelphia,+PA+19102/@39.9529779,-75.1683976,17z/data=!3m1!4b1!4m2!3m1!1s0x89c6c62e274ce023:0xa4419ca196a9a5a8",
                           "1515 Market Street, Philadelphia, PA 19102")}.
              Full details on the class, including registration information,
              are on the
              {offsiteLink("https://newcircle.com/class/big-data/apache-spark-development-bootcamp/philadelphia-venue-tbd/2016-03-15?utm_campaign=nescala_2016&utm_source=nescala_website&utm_medium=referral",
                           "NewCircle event page")}.
            </p>
          </div>

        </div>
      </div>
    )

  def layout
   (session: Option[SessionCookie] = None)
   (scripts: Iterable[String] = Nil,
    styles: Iterable[String] = Nil)
   (headContent: xml.NodeSeq)
   (bodyContent: xml.NodeSeq) = Html5(
    <html>
      <head>
        <meta http-equiv="Content-Type" content="text/html;charset=utf-8" />
        <meta name="viewport" content="width=device-width,initial-scale=1"/>
        <title>&#8663;northeast scala symposium</title>
        <link href="https://fonts.googleapis.com/css?family=Source+Code+Pro|Montserrat:400,700|Open+Sans:300italic,400italic,700italic,400,300,700" rel="stylesheet" type="text/css"/>
        <link href="https://fonts.googleapis.com/css?family=Vollkorn:400,400italic,700,700italic" rel="stylesheet" type="text/css"/>
        <link href="https://fonts.googleapis.com/css?family=Signika:400,700" rel="stylesheet" type="text/css"/>
        <link rel="stylesheet" type="text/css" href="/css/gridism.css" />
        <link rel="stylesheet" type="text/css" href="/css/normalize.css" />
        <link href="//netdna.bootstrapcdn.com/font-awesome/4.0.3/css/font-awesome.css" rel="stylesheet"/>
        <link href="/css/font-mfizz/font-mfizz.css" rel="stylesheet"/>
        <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/font-awesome/4.5.0/css/font-awesome.min.css"/>
        <link rel="stylesheet" type="text/css" href="/cssless/philly2016.css" />
        <script type="text/javascript" src="http://ajax.googleapis.com/ajax/libs/jquery/2.1.1/jquery.min.js"></script>{
          styles.map { s => <link rel="stylesheet" type="text/css" href={s}/> } ++
          scripts.map { s => <script type="text/javascript" src={s}></script> }
        }
        <script type="text/javascript" src="/js/2016/util.js"></script>
      </head>
      <body class="wrap wider">
        <div class="unit whole center-on-mobiles top section" id="banner">
          <div class="title-block">
            <h1>NE Scala 2016</h1>
            <h2>Philadelphia railyard edition</h2>
          </div>
          { headContent }
          <hr/>
        </div>
        { bodyContent }
        <div class="footer unit whole center-on-mobiles">
          <hr/>
          <div class="container inverse">
          <a href="#top">NE Scala</a> is organized with <span class="love">❤</span> from the
          <div>
            {offsiteLink("http://www.meetup.com/scala-phase/", "Philadelphia")},
            {offsiteLink("http://www.meetup.com/ny-scala/", "New York")}, and
            {offsiteLink("http://www.meetup.com/boston-scala/", "Boston")}
            Scala enthusiasts,
            our <a href="#friends">friends</a> and, of course, all of
            {offsiteLink("http://www.meetup.com/nescala/photos/", "you")}.
          </div>
          <div>
            Hosting by the fine folks @
            {offsiteLink("https://www.heroku.com/", "Heroku")}.
          </div>
          <div>
            Problems with the website?
            {offsiteLink("https://github.com/nescalas/northeast-scala-symposium/issues",
                         "Open an issue")}.
          </div>
          <div>
            &nbsp;
          </div>
          <div>
            previous years:
              <a href="/2015">2015</a> |
              <a href="/2014">2014</a> |
              <a href="/2013">2013</a> |
              <a href="/2012">2012</a> |
              <a href="/2011">2011</a>
          </div>
          </div>
        </div>
      </body>
    </html>
  )
}
