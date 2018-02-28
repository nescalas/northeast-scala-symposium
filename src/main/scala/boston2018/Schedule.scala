package nescala.boston2018

import java.time.{LocalTime}
import scala.util.Try
import Json._

object Schedule {

  def startTime = LocalTime.parse("08:15")
  def lunchTime = LocalTime.parse("12:30")

  /** Something to fit into the day's schedule.
   */
  sealed abstract trait Slot {
    def minutes: Int
  }

  case object Registration extends Slot {
    val minutes = 45
  }
  case class Remarks(minutes: Int) extends Slot
  case class Talk(proposal: Proposal) extends Slot {
    val minutes = proposal.talk_format.lengthInMinutes
    override def toString = s"Talk(${proposal.id})"
  }
  case class Break(minutes: Int) extends Slot
  case object Lunch extends Slot {
    val minutes = 90
  }

  /** A slot scheduled at a specific time.
   */
  case class Item(slot: Slot, startTime: LocalTime) {
    def endTime: LocalTime = startTime.plusMinutes(slot.minutes)
    def straddlesLunch: Boolean = startTime.isBefore(lunchTime) && endTime.plusMinutes(10).isAfter(lunchTime)
    def isTalk: Boolean = slot match {
      case Talk(_) => true
      case _       => false
    }
    def followedBy(s: Slot): Item = Item(s, endTime) 

    override def toString = s"$slot @ $startTime"
  }

  /** Given a start time and a list of proposals to be given, build a day's schedule.
   */
  def nescalaProgram(talks: Seq[Talk]): Seq[Item] = build(
    startTime,
    Seq(
      Registration,
      Remarks(5)
    ) ++ talks
    :+ Remarks(5)
  )

  /** Schedule items one after the next, inserting breaks or lunch where appropriate.
   */
  def build(startTime: LocalTime, items: Seq[Slot]): Seq[Item] = items match {
    case Nil =>  Nil
    case (firstOrder :: rst) => rst.foldLeft(Seq(Item(firstOrder, startTime))){ 
      //If the last thing took us past our lunch threshold, have lunch next.
      case (items@(previousItem :: _), nextSlot) if previousItem.straddlesLunch => {
          val lunch = previousItem.followedBy(Lunch)
          lunch.followedBy(nextSlot) +: lunch +: items
        }
      //If we have two talks scheduled in a row, put a break between them.
      case (items@(previousItem :: _), nextTalk@Talk(_)) if previousItem.isTalk => {
          val break = previousItem.followedBy(Break(10))
          break.followedBy(nextTalk) +: break +: items
        }
      //Otherwise, the next thing just happens after the previous thing.
      case (items@(previousItem :: _), nextSlot)  => previousItem.followedBy(nextSlot) +: items
      case _ => Nil
    }.reverse
  }

  /** Load a resource file containing a JSON array of proposal id's representing the day's talk order.
   */
  def load(): Try[Seq[Item]] = loadJsonResource[Array[String]]("/2018/program.json").map(
    _.toSeq.flatMap(Site.proposalStore.get).map(Talk)
  ).map(nescalaProgram)

}
