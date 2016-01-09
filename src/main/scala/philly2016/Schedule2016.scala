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
  * @param description  description, as an HTML string, or None
  */
case class ScheduleSlot(val time:        LocalTime,
                        val duration:    Duration,
                        val speaker:     Option[String],
                        val activity:    Option[String],
                        val description: Option[String])

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
      getClass.getResourceAsStream("/2016/Schedule.csv")
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
      val activity = someOrNone(field("Activity"))
      val desc = someOrNoneMapped(field("Description")) {
        markdownParser.parseToHTML(_)
      }

      val speaker = someOrNone(field("Speaker"))

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
                   speaker     = speaker,
                   activity    = activity,
                   description = desc)
    }.
    toArray.
    sortBy { _.time.getMillisOfDay }
  }
}
