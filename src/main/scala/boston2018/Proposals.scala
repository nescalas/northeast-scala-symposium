package nescala.boston2018

import java.io.InputStreamReader

import org.json4s._
import org.json4s.native.Serialization
import org.json4s.native.Serialization.{read, write}

import scala.util.Try

case class Proposal(
  `abstract`: String,
  audience_level: String,
  avatar: String,
  bio: String,
  description: String,
  location: String,
  name: String,
  organization: String,
  state: String,
  tags: Seq[String],
  talk_format: Proposal.TalkFormat,
  title: String,
  twitter: String,
  url: String
) {
  def nameWords: Seq[String] = name.split("\\s+")
  def id: String = s"${nameWords.head.head}${nameWords.last}-${title.toLowerCase.takeWhile(_.isLetter)}".toLowerCase
  def nameLink = if (!url.isEmpty) s"[$name]($url)" else name
  def twitterLink = if (!twitter.isEmpty) s"([@$twitter](https://twitter.com/$twitter))" else ""
}

object Proposal {
  
  sealed abstract trait TalkFormat {
    def lengthInMinutes: Int
  }
  case object Lightning extends TalkFormat {
    val lengthInMinutes = 15
  }
  case object Medium    extends TalkFormat{
    val lengthInMinutes = 30
  }
  case object Long      extends TalkFormat{
    val lengthInMinutes = 45
  }
  
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
  def load(): Try[Array[Proposal]] = Try {
    val jsonStream = new InputStreamReader(
      getClass.getResourceAsStream("/2018/proposals.json"), "UTF-8"
    )
    read[Array[Proposal]](jsonStream)
  }
}
