package nescala.boston2018

import scala.util.Try
import Json._

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
  def summaryLink = s"[$name - $title](/proposal/$id/#abstract)"
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
  
  def load(): Try[Array[Proposal]] = loadJsonResource[Array[Proposal]]("/2018/proposals.json")
}
