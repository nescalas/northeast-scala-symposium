package nescala.philly2016

import nescala.{ Meetup, SessionCookie }
import unfiltered.response.Html5
import java.text.SimpleDateFormat
import java.util.{ Calendar, Date, TimeZone }

trait Templates {

  import Enrichments.EnrichedDate
  import Constants._

  private def timestamp(d: Date, offset: Boolean = true) = {
    def fmt(f: String) = new SimpleDateFormat(f) {
      if (offset) {
        setTimeZone(TimeZone.getTimeZone("US/Eastern"))
      }
    }
    val time = (if (d.field(Calendar.MINUTE) > 0) fmt("h:mm") else fmt("h")).format(d)
    <span class="time">{ time }</span><span class="ampm">{ fmt("aa").format(d).toLowerCase }</span>
  }

  def pollsClosed = {
    <span>
      Votes on <a href={Constants.ProposalsURL}>talk proposals</a> are in.
      Winners are below.
    </span>
  }

  def indexPage
   (sponsors: List[Meetup.Sponsor])
   (session: Option[SessionCookie] = None) =
    layout(session)(scripts = Seq.empty[String])(
      <div class="grid well">
        <div class="unit full center center-on-mobiles">
          <p>
            The Northeast Scala Symposium is the original Scala community-based
            conference, and we're gearing up for our
            <a href="#what">6th year.</a>
          </p>
        {
          if (votingIsOpen) {
            <div>
              <p>RSVP on <a href="http://www.meetup.com/nescala/">Meetup</a>:</p>
              <a href={s"http://www.meetup.com/nescala/events/$Day1EventId/"}>Day one</a> |
              <a href={s"http://www.meetup.com/nescala/events/$Day2EventId/"}>Day two</a>
            </div>
            <div>
              Then, <a href={VotingFormURL}>vote for talks</a> (by {VotesCloseStr}).
            </div>
          }
          else if (proposingIsOpen) {
            <div>
              <a href={ProposalsURL}>Propose a talk</a> (by {ProposalsCloseStr})
            </div>
          }
          else {
            <p>Stay tuned!</p>
          }
        }
        </div>
      </div>
      <div class="communicate">
        <a class="icon" href="http://twitter.com/nescalas" target="_blank"><i class="fa fa-twitter"></i>
          <span>Follow us.</span>
        </a>
        <a href="http://www.meetup.com/nescala/" target="_blank" class="icon"><i class="icon-scala"></i><span>Join us</span></a>
        <a href="#what" class="icon"><i class="fa fa-check-circle-o"></i><span>Learn with us</span></a>
        <a href="#when" class="icon"><i class="fa fa-calendar-o"></i><span>Mark your calendar</span></a>
        <a href="#where" class="icon"><i class="fa fa-map-marker"></i><span>Fire up your GPS</span></a>
      </div>
    )(
      <div class="inverse" id="what">
        <div class="grid">
          <div class="unit">
            <h2>
              This is <strong>your</strong> conference.
            </h2>
            <p>
              Northeast Scala Symposium is a <a href="http://scala-lang.org/">Scala</a>-focused <strong>community</strong> gathering.
            </p>
            <p>
              NE Scala offers a mix of speaker-oriented conference presentations
              with unconference-style sessions and discussions. (And coffee.
              Lots of coffee.) All presenters are
              attendees, and all attendees select presenters.
            </p>
            <h2><strong>Day 1 schedule</strong></h2>
          </div>
        </div>
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

            PhillySchedule.zipWithIndex.map { case (slot, index) =>
              import org.joda.time._
              val oddEven = if ((index % 2) == 0) "odd" else "even"

              <div class={s"grid $oddEven"}>
                <span class="right unit one-fifth">
                  {
                    val hour = slot.time.get(DateTimeFieldType.hourOfDay)
                    val minute = slot.time.get(DateTimeFieldType.minuteOfHour)
                    f"$hour:$minute%02d"
                  }
                </span>
                <span class="unit one-fifth">{slot.speaker.getOrElse("")}</span>
                <span class="unit one-fifth">{slot.activity.getOrElse("")}</span>
                <span class="unit two-fifths">{
                  slot.description.map { scala.xml.XML.loadString(_) }.
                                   getOrElse(<span></span>)
                }
                </span>
              </div>
            }
          }
        }</div>
        <div class="grid">
          <div class="unit">
            <h2><strong>Day 2 schedule</strong></h2>
            <p>See the <a href={s"http://www.meetup.com/nescala/events/$Day2EventId/"}>Meetup page</a>.</p>
          </div>
        </div>
      </div>
      <div id="when" class="regular">
        <div class="grid">
          <div class="unit">
            <h2>When</h2>
            <p>
              Northeast Scala Symposium is held annually. In 2016, we will
              descend upon the City of Brotherly Love, on
              <a href={s"http://www.meetup.com/nescala/events/$Day1EventId/"}>Friday, March 4</a>
              and <a href={s"http://www.meetup.com/nescala/events/$Day2EventId/"}>Saturday, March 5</a>.
            </p>
            <p>
              To attend, RSVP separately for <a href={s"http://www.meetup.com/nescala/events/$Day1EventId/"}>day one</a> ($60) and/or
              <a href={s"http://www.meetup.com/nescala/events/$Day2EventId/"}>day two</a> (free).
            </p>
          </div>
        </div>
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
      </div>
      <div class="inverse" id="where">
        <div class="grid">
          <div class="unit whole">
            <h2>Where</h2>
            <p>
              This year's symposium will be held at
              <a href="http://thehub.com/locations/cira-centre/">The Hub Cira Center</a>
              located at
              <a href="https://www.google.com/maps/place/2929+Arch+St,+Philadelphia,+PA+19104/@40.002498,-75.1180329,11z/data=!4m2!3m1!1s0x89c6c64bdd84625b:0xbe940a019709b223">2929 Arch Street, Philadelphia, Pa 19104</a>,
              right next to historic <a href="https://en.wikipedia.org/wiki/30th_Street_Station">30th Street Station</a>,
              which makes it highly accessible via mass transit.
            </p>
          </div>
        </div>
      </div>
      <div id="kindness" class="regular">
        <div class="grid">
          <div class="unit whole">
            <h2>Be kind.</h2>
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
      <div class="inverse" id="friends">
        <div class="grid center-on-mobiles">
          <div class="unit whole">
            <h2>Friends</h2>
            <p>Below are some of the sponsors who made this possible.</p>
            <div class="sponsors">{
              sponsors.map { sponsor =>
                <div class="sponsor unit one-third">
                  <a class="friend" title={sponsor.name} href={sponsor.link}>
                    <img src={sponsor.image}/>
                  </a>
                  <div class="blurb">{sponsor.info}</div>
                </div>
              }
            }</div>
          </div>
        </div>
      </div>)
      <div id="kindness" class="inverse">
        <div class="grid">
          <div class="unit whole">
            <h2>Code of Conduct.</h2>
            <p>
              Simply: Be kind.
            </p>
            <p>
              Nobody likes a jerk, so <strong>show respect</strong> for those around you.
            </p>
            <p>
              NE Scala is dedicated to providing a harassment-free experience for everyone, regardless of gender, gender identity and expression, sexual orientation, disability, physical appearance, body size, race, or religion (or lack thereof). We do not tolerate harassment of participants in any form.
            </p>
            <p>
              All communication should be appropriate for a technical audience including people of many different backgrounds. Sexual language, innuendo, and imagery is not appropriate for any symposium venue, including talks.
            </p>
            <p>
              Participants violating these rules may be asked to leave without a refund at the sole discretion of the organizers.
            </p>
            <p>
              Since this is a gathering of static typists, offenders will be caught at compile time.
            </p>
          </div>
        </div>
      </div>

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
        <link rel="stylesheet" type="text/css" href="/cssless/philly2016.css" />
        <script type="text/javascript" src="http://ajax.googleapis.com/ajax/libs/jquery/2.1.1/jquery.min.js"></script>{
          styles.map { s => <link rel="stylesheet" type="text/css" href={s}/> } ++
          scripts.map { s => <script type="text/javascript" src={s}></script> }
        }
        <script type="text/javascript" src=""></script>
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
            <a href="http://www.meetup.com/scala-phase/">Philadelphia</a>,
            <a href="http://www.meetup.com/ny-scala/">New York</a>,
            and <a href="http://www.meetup.com/boston-scala/">Boston</a>
            Scala enthusiasts,
            our <a href="#friends">friends</a> and, of course, of all of
            <a href="http://www.meetup.com/nescala/photos/">you</a>.
          </div>
          <div>
            Hosting by the fine folks @ <a href="https://www.heroku.com/">Heroku</a>
          </div>
          <div>
            Problems with the website? <a href="https://github.com/nescalas/northeast-scala-symposium/issues">Open an issue.</a>
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
