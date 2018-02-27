package nescala.boston2018

import org.json4s._
import org.json4s.native.Serialization
import org.json4s.native.Serialization.{read, write}

import java.io.InputStreamReader
import scala.util.Try

object Json {
  import Proposal._

  class TalkFormatSerializer extends CustomSerializer[TalkFormat](format => (
    // unmarshal Function
    {
      case JString(talk_format) => {
        // helper functions, could be improved by having a mapping
        def stringToTalkFormat(in: String): TalkFormat = in match {
          case "Lightning (15 minutes)" => Lightning
          case "Medium (30 minutes)"    => Medium
          case "Long (45 minues)"       => Long
        }

        stringToTalkFormat(talk_format)
      }
    },
    // marshal Function
    {

      case talk_format: TalkFormat => {
        def talkFormatToString(talk_format: TalkFormat): String = talk_format match {
          case Lightning => "Lightning (15 minutes)"
          case Medium    => "Medium (30 minutes)"
          case Long      => "Long (45 minues)"

        }

        JString(talkFormatToString(talk_format))
      }
    }
    )
  )
  implicit val formats = Serialization.formats(NoTypeHints) + new TalkFormatSerializer

  def loadJsonResource[T](path: String, encoding: String = "UTF-8")(implicit m: Manifest[T]): Try[T]  = Try {
    val jsonStream = new InputStreamReader(
      getClass.getResourceAsStream(path), encoding
    )
    read[T](jsonStream)

  }

}
