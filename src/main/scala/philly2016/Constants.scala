package nescala.philly2016

import org.joda.time.{DateTime, DateMidnight, DateTimeZone, LocalDateTime}
import java.text.SimpleDateFormat

import philly2016.Schedule2016

object Constants {
  val Day1EventIdNum = 227877388
  val Day2EventIdNum = 227877435

  val Day1EventId = Day1EventIdNum.toString
  val Day2EventId = Day2EventIdNum.toString

  val ProposalsURL = "https://github.com/nescalas/proposals-2016"
  val VotingFormURL = "http://goo.gl/forms/kBwCYLOJNH"

  val TZ = DateTimeZone.forID("US/Eastern")

  val Year2016 = new DateMidnight(TZ).withYear(2016)

  val dayOneTime =
    new LocalDateTime(TZ)
      .withYear(2016).withMonthOfYear(3)
      .withDayOfMonth(4).withMinuteOfHour(0)
      .withSecondOfMinute(0).withMillisOfSecond(0)

  val proposalsOpen = Year2016.withMonthOfYear(1).withDayOfMonth(7)
  val proposalsClose = Year2016.withMonthOfYear(1).withDayOfMonth(15)
  val votingOpens = Year2016.withMonthOfYear(1).withDayOfMonth(18)
  val votingCloses = Year2016.withMonthOfYear(1).withDayOfMonth(25)

  lazy val DefaultDateFormat = new SimpleDateFormat("E MMM d")
  lazy val DefaultTimeFormat = new SimpleDateFormat("HH:mm a")

  lazy val DayOneTimeStr = DefaultDateFormat.format(dayOneTime.toDate)
  lazy val ProposalsOpenStr = DefaultDateFormat.format(proposalsOpen.toDate)
  lazy val ProposalsCloseStr = DefaultDateFormat.format(proposalsClose.toDate)
  lazy val VotesOpenStr = DefaultDateFormat.format(votingOpens.toDate)
  lazy val VotesCloseStr = DefaultDateFormat.format(votingCloses.toDate)

  def proposingIsOpen = (proposalsOpen.isBeforeNow && proposalsClose.isAfterNow)
  def votingIsOpen = (votingOpens.isBeforeNow && votingCloses.isAfterNow)
  def votingIsClosed = votingCloses.isBeforeNow

  private def time(hour: Int, minute: Int) =
    dayOneTime.withHourOfDay(hour).withMinuteOfHour(minute)

  lazy val PhillySchedule = Schedule2016.load().get

}

