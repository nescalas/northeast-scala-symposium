package nescala.boston2018

import nescala.{ Meetup, SessionCookie }
import org.clapper.markwrap.{MarkupType, MarkWrap}
import unfiltered.response.Html5
import java.net.URLEncoder.encode
import java.text.SimpleDateFormat
import java.util.{ Date, TimeZone }
import java.time.{ZoneId, ZonedDateTime}
import java.time.format.DateTimeFormatter
import scala.util.Random

trait Templates {

  def markdownParser = MarkWrap.parserFor(MarkupType.Markdown)

  case class Section(
    id: String,
    shortTitle: String,
    title: String,
    body: String,
    hasContent: Boolean = true
  )

  case class Header(
    image: String,
    twitter: String,
    twitterTitle: String,
    heading: String,
    subheading: () => String,
    description: String
  )

  //We'll use this to pick slogans randomly.
  val random = new Random
  def slogans  = Seq(
    "of the people, by the people, for the people",
    "give me type safety or give me death",
    "one if by object-oriented, two if by functional",
    "mens et manus et compiler"
  )

  val defaultHeader = Header(
    "http://nescala.org/images/nescalas-logo.png",
    "@nescalas",
    "nescala 2018",
    "nescala 2018",
    { ()  => slogans(random.nextInt(slogans.length)) },
    "Your friendly neighborhood Scala conference, chugging through the Northeast since 2011."
  )

  val backToTop = Section(
    "/",
    "back to top",
    "",
    "",
    false
  )

  def baseSections(schedule: Seq[Schedule.Item]) = Seq(
    Section(
      "about",
      "About",
      "About",
      """|Come one, come all!
         |
         |Since 2011, the New York, Boston, and Philadelphia Scala meetups have hosted together a community-driven Scala conference. Part of what "community-driven" means is the program comes from the attendees. All talks on the conference's main day are [proposed by the attendees](https://www.papercall.io/nescala-boston-2018) (that could be you!), who then vote to select which will be given.
         |
         |Our friendship with [Typelevel](http://typelevel.org) continues. The conference will once again coincide with the [Typelevel Summit](https://typelevel.org/event/2018-03-summit-boston/).
         |
         |For the third time, we go to Boston, at the [Broad Institute](http://broadinstitute.org/) of MIT and Harvard. For the first time, the conference is starting on a Sunday, taking place __March 18-20__:
         |
         |-  __Sunday__ will be the Unconference, *traditionally day 2*.
         |-  __Monday__ will be nescala proper, *traditionally day 1*.
         |-  __Tuesday__ will be the Typelevel Summit.
         |
         |Newcomers and veterans, northeasterners and the global jet-set, rebels and redcoats: all are welcome!
         |""".stripMargin('|')
    ),
    Section(
      "location",
      "Location",
      "Location",
      """|The kind folks at the [Broad Institute](http://broadinstitute.org/) know a thing or two about editing code; just ask them about [CRISPR/cas9](https://en.wikipedia.org/wiki/CRISPR#Cas9). They are generously hosting the conference this year. Located at 415 Main Street, Cambridge, the Institute is just a block away from [the Kendall/MIT stop](https://www.mbta.com/schedules_and_maps/subway/lines/stations/?stopId=12412) on the T (red line).
         |
         |You may want to dress warmly; Kendall Square has [a lot going on](https://www.kendallsq.org/wp-content/uploads/2016/04/2016-KSA-Walking-Map.pdf), but even the vernal equinox in these parts can be nasty:
         |
         |“Anyone who lives in Boston knows that it’s March that’s the cruelest, holding out a few days of false hope and then gleefully hitting you with the shit.” ― Stephen King, *Dreamcatcher*
         |""".stripMargin('|')
    ),
    Section(
      "rsvp",
      "RSVP",
      "RSVP",
      """|To secure your spot, just RSVP to the [Day 2 meetup](https://www.meetup.com/nescala/events/247144352/). The cost is $60. This ticket will *also* get you in to the Typelevel Summit.
         |
         |No ticket is required in order to attend [Day 1](https://www.meetup.com/nescala/events/247168183/), but please do RSVP.
         |
         |""".stripMargin('|')
    ),
    scheduleSection(schedule),
    Section(
      "friends",
      "Friends",
      "Thank you!",
      Sponsor.sponsors.map{s => s"###[![${s.name}](${s.logo})](${s.homepage})\nis ${s.sponsorship}."}.mkString("\n\n")
    ),
    Section(
      "higher-kindness",
      "Code",
      "Code of Conduct",
      """|Nobody likes a jerk. Show respect for those around you.
         |
         |Nescala is dedicated to providing a harassment-free experience for everyone, regardless of gender, gender identity and expression, sexual orientation, disability, physical appearance, body size, race, or religion (or lack
thereof). We do not tolerate harassment of participants in any form. All communication should be appropriate for a technical audience, including people of many different backgrounds. Sexual language, innuendo, and imagery is not appropriate for any symposium venue, including talks.
         |
         |Participants violating these rules may be asked to leave without a refund, at the sole discretion of the organizers.
         |
         |Since this is a gathering of static typists, offenders will be caught at compile time.""".stripMargin('|')
    )
  )

  def proposalHeader(p: Proposal) = Header(
    p.avatar,
    if (!p.twitter.isEmpty) s"@${p.twitter}" else "@nescalas",
    p.title,
    p.name,
    { () => p.title },
    p.`abstract`
  )

  
  def proposalDirectorySection(format: Proposal.TalkFormat, proposals: Iterable[Proposal]) = { 
    Section(
      s"$format",
      s"$format",
      s"$format",
      proposals.toSeq.sortBy(_.nameWords.last.toLowerCase).map("- " + _.summaryLink).mkString("\n")
    )
  }
  
  def proposalSections(p: Proposal): Seq[Section] = Seq(
    Section(
      s"/proposals#${p.talk_format}",
      s"↖${p.talk_format}",
      "",
      "",
      false
    ),
    Section(
      "speaker",
      "speaker",
      p.name,
      s"""|${p.nameLink} ${p.twitterLink}
          |<br />${p.organization}
          |
          |${p.bio}
          """.stripMargin('|')
    ),
    Section(
      "abstract",
      "abstract",
      "Abstract",
      p.`abstract`
    ),
    Section(
      "details",
      "details",
      "Details",
      p.description
    )
  )

  def scheduleRow(item: Schedule.Item) = s"${item.startTime}|${item.slot.minutes} min|${item.slot.description}"
  def scheduleSection(items: Iterable[Schedule.Item]) = Section(
    "schedule",
    "schedule",
    "Day 2 Schedule",
    s"""|Time|Duration|Description
        |:--:|:-----:|:----------
        |${items.map(scheduleRow).mkString("\n")}
        |""".stripMargin('|')
  )
  
  private def navItem(section: Section): xml.NodeSeq =
    <li><a href={if (section.hasContent) "#" + section.id else section.id }>{section.shortTitle}</a></li>

  private def article(section: Section): xml.NodeSeq =
    <article id={ section.id }>
      <h2 class="major">{ section.title}</h2>
      { val bodyHtml = markdownParser.parseToHTML(section.body)
        scala.xml.XML.loadString(s"<div>$bodyHtml</div>") }
    </article>

  def layout
   (sections: Iterable[Section] = baseSections(Site.schedule), header: Header = defaultHeader)
   (session: Option[SessionCookie] = None)
    = Html5(
      <html>
      <!--
        Dimension by HTML5 UP
        html5up.net | @ajlkn
        Free for personal and commercial use under the CCA 3.0 license (html5up.net/license)
      -->
        <head>
          <title>northeast scala symposium</title>
          <meta charset="utf-8" />
          <meta name="viewport" content="width=device-width, initial-scale=1, user-scalable=no" />
          <meta name="twitter:card" content="summary" />
          <meta name="twitter:site" content={ header.twitter } />
          <meta name="twitter:title" content={ header.twitterTitle } />
          <meta name="twitter:description" content={ header.description } />
          <meta name="twitter:image" content={ header.image } />
          <link rel="stylesheet" href="/css/2018/boston2018.css" />
          <link rel="stylesheet" href="/css/2018/addon.css" />
          <link rel="stylesheet" href="/css/2018/font-awesome.min.css" />
          <!--[if lte IE 9]><link rel="stylesheet" href="/css/2018/ie9-boston2018.css" /><![endif]-->
          <noscript><link rel="stylesheet" href="/css/2018/noscript-boston2018.css" /></noscript>
        </head>
        <body>

          <!-- Wrapper -->
            <div id="wrapper">

              <!-- Header -->
                <header id="header">
                  <div class="logo" style="border-width:0px">
                    <img src={ header.image } width="100%" />
                  </div>
                  <div class="content">
                    <div class="inner">
                      <h1>{ header.heading }</h1>
                      <p>{ header.subheading() }</p>
                    </div>
                  </div>
                  <nav>
                    <ul>
                      { sections.map(navItem) }
                    </ul>
                  </nav>
                </header>

              <!-- Main -->
                <div id="main">
                      { sections.filter(_.hasContent).map(article) }
                </div>

              <!-- Footer -->
                <footer id="footer">
                  <p class="copyright">made possible with <span class="love">&#10084;</span> from the <a href="http://www.meetup.com/boston-scala/">Boston</a>, <a href="http://www.meetup.com/scala-phase/">Philadelphia</a>, and <a href="http://www.meetup.com/ny-scala/">New York</a> scala enthusiasts</p>
                  <p class="copyright">This year's site design: <a href="https://html5up.net">HTML5 UP</a></p>
                  <br />
                  <p class="copyright"><a href="/2017">2017</a> | <a href="/2016">2016</a> | <a href="/2015">2015</a> | <a href="/2014">2014</a> | <a href="/2013">2013</a> | <a href="/2012">2012</a> | <a href="/2011">2011</a></p>
                </footer>

            </div>

          <!-- BG -->
            <div id="bg"></div>

          <!-- Scripts -->
            <script src="/js/2018/jquery.min.js"></script>
            <script src="/js/2018/skel.min.js"></script>
            <script src="/js/2018/util.js"></script>
            <script src="/js/2018/main.js"></script>

        </body>
      </html>
      )
}
