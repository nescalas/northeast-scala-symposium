package philly2016

import java.io.InputStreamReader

import org.joda.time._
import org.clapper.markwrap._
import com.github.tototoshi.csv._

import scala.util.Try

/** Schedule item.
  *
  * @param time         start time
  * @param duration     duration
  * @param speaker      speaker, or None
  * @param activity     activity title, or None
  * @param video        link to video, or None
  * @param slides       link to slides, or None
  * @param description  description, as an HTML string, or None
  */
case class ScheduleSlot(time:        LocalTime,
                        duration:    Duration,
                        speaker:     Option[String],
                        meetupID:    Option[String],
                        activity:    Option[String],
                        video:       Option[String],
                        slides:      Option[String],
                        description: Option[String])

object Schedule2016 {
  // Load the schedule. In a decidedly non-functional hack, throws exceptions
  // on error.
  def load(): Try[Array[ScheduleSlot]] = Try {

    def someOrNone(s: String) = {
      if (s.length == 0) None else Some(s)
    }

    def someOrNoneMapped(s: String)(mapper: String => String) = {
      someOrNone(s).map(mapper)
    }

    val csvStream = new InputStreamReader(
      getClass.getResourceAsStream("/2016/Schedule.csv"), "UTF-8"
    )
    val csvLines = CSVReader.open(csvStream).toStream
    val headerMap = csvLines(0).zipWithIndex.toMap

    // Using the constant
    val markdownParser = MarkWrap.parserFor(MarkupType.Markdown)

    val body = csvLines.drop(1)
    body.map { fieldList =>
      val fields: Array[String] = fieldList.toArray
      def field(header: String) = fields(headerMap(header)).trim()

      // Duration appears as 0:40, 0:10 in the spreadsheet.
      val durationField = field("Duration")
      val duration = durationField.split(":") match {
        case Array(hours, minutes) => {
          Duration.standardHours(hours.toLong)
                  .plus(Duration.standardMinutes(minutes.toLong))
        }
        case Array(minutes) => {
          Duration.standardMinutes(minutes.toLong)
        }
        case _ => throw new Exception(s"Bad duration: $durationField")
      }

      val desc = someOrNoneMapped(field("Description")) { s =>
        // Wrap the whole thing in a <span>, since it could be multiple
        // paragraphs, which won't parse (because there's no root container
        // in that case).
        s"<div>${markdownParser.parseToHTML(s)}</div>"
      }

      // Time is "hh:mm AM" or "hh:mm PM". Hard to parse that with JodaTime,
      // so we cheat.
      val timeField = field("Start").split("""\s+""").map(_.toLowerCase)
      val baseTime = LocalTime.parse(timeField.head)
      val time = timeField.last match {
        case "pm" if baseTime.getHourOfDay < 12 => baseTime.plusHours(12)
        case _                                  => baseTime
      }
      ScheduleSlot(time        = time,
                   duration    = duration,
                   speaker     = someOrNone(field("Speaker")),
                   activity    = someOrNone(field("Activity")),
                   meetupID    = someOrNone(field("Meetup ID")),
                   video       = someOrNone(field("Video")),
                   slides      = someOrNone(field("Slides")),
                   description = desc)
    }.
    toArray.
    sortBy { _.time.getMillisOfDay }
  }

  /** Get rid of entity codes that XML doesn't like.
    */
  private def fixHTML(html: String) = {

  }
}
