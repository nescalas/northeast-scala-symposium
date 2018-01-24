package nescala.boston2018

import nescala.{ Meetup, SessionCookie }
import org.clapper.markwrap.{MarkupType, MarkWrap}
import unfiltered.response.Html5
import java.net.URLEncoder.encode
import java.text.SimpleDateFormat
import java.util.{ Date, TimeZone }
import java.time.{ZoneId, ZonedDateTime}
import java.time.format.DateTimeFormatter

trait Templates {

  def markdownParser = MarkWrap.parserFor(MarkupType.Markdown)

  case class Section(
    id: String,
    title: String,
    body: String
  )

  def baseSections = Seq(
    Section(
      "about",
      "About",
      """|Come one, come all!
         |
         |Join us. This is going to be great.
         |
         |March 19 at the [Broad Institute](http://broadinstitute.org/).
         |""".stripMargin('|')
    )
  )

  private def navItem(section: Section): xml.NodeSeq =
    <li><a href={s"#${section.id}"}>{section.title}</a></li>

  private def article(section: Section): xml.NodeSeq = 
    <article id={ section.id }>
      <h2 class="major">{ section.title}</h2>
      { val bodyHtml = markdownParser.parseToHTML(section.body)
        scala.xml.XML.loadString(s"<div>$bodyHtml</div>") }
    </article>

  def layout
   (sections: Seq[Section])
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
          <link rel="stylesheet" href="/css/sass/boston2018.css" />
          <link rel="stylesheet" href="/css/2018/font-awesome.min.css" />
          <!--[if lte IE 9]><link rel="stylesheet" href="/css/sass/ie9-boston2018.css" /><![endif]-->
          <noscript><link rel="stylesheet" href="/css/sass/noscript-boston2018.css" /></noscript>
        </head>
        <body>

          <!-- Wrapper -->
            <div id="wrapper">

              <!-- Header -->
                <header id="header">
                  <div class="logo" style="border-width:0px">
                    <img src="/images/nescalas-logo.png" width="100%" />
                  </div>
                  <div class="content">
                    <div class="inner">
                      <h1>nescala 2018</h1>
                      <p></p>
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
                      { sections.map(article) }
                </div>

              <!-- Footer -->
                <footer id="footer">
                  <p class="copyright">made possible with <span class="love">&#10084;</span> from the <a href="http://www.meetup.com/boston-scala/">Boston</a>, <a href="http://www.meetup.com/scala-phase/">Philadelphia</a>, and <a href="http://www.meetup.com/ny-scala/">New York</a> scala enthusiasts</p>
                  <p class="copyright">This year's design: <a href="https://html5up.net">HTML5 UP</a></p>
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
