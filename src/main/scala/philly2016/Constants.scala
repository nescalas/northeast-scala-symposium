package nescala.philly2016

import java.util.Calendar

import org.joda.time.{DateTime, DateMidnight, DateTimeZone, LocalDateTime}
import java.text.SimpleDateFormat

import philly2016.Schedule2016

object Constants {
  val Day1EventIdNum = 227877388
  val Day2EventIdNum = 227877435

  val Day1EventId = Day1EventIdNum.toString
  val Day2EventId = Day2EventIdNum.toString
  val Day1URL     = s"http://www.meetup.com/nescala/events/$Day1EventId/"
  val Day2URL     = s"http://www.meetup.com/nescala/events/$Day2EventId/"

  val ProposalsURL = "https://github.com/nescalas/proposals-2016"
  val VotingFormURL = "http://goo.gl/forms/kBwCYLOJNH"

  val TZ = DateTimeZone.forID("US/Eastern")

  // Used for forcing time zone in date format
  private val cal = Calendar.getInstance(TZ.toTimeZone)

  val Year2016 = new DateTime(TZ)
    .withYear(2016)
    .withMonthOfYear(1)
    .withDayOfMonth(1)
    .withHourOfDay(0)
    .withMinuteOfHour(0)
    .withSecondOfMinute(0)

  val dayOneTime =
    new LocalDateTime(TZ)
      .withYear(2016)
      .withMonthOfYear(3)
      .withDayOfMonth(4)
      .withMinuteOfHour(0)
      .withSecondOfMinute(0)
      .withMillisOfSecond(0)

  val ProposalsOpen = Year2016
    .withMonthOfYear(1)
    .withDayOfMonth(7)
    .withHourOfDay(12)
    .withMinuteOfHour(0)
  val ProposalsClose = Year2016
    .withMonthOfYear(1)
    .withDayOfMonth(15)
    .withHourOfDay(23)
    .withMinuteOfHour(59)

  val RSVPsOpen = Year2016
    .withMonthOfYear(1)
    .withDayOfMonth(16)
    .withHourOfDay(12)
  val VotingOpens = Year2016.withMonthOfYear(1).withDayOfMonth(18)
  val VotingCloses = Year2016.withMonthOfYear(1).withDayOfMonth(25)

  lazy val DefaultDateFormat = new SimpleDateFormat("E MMM d")
  DefaultDateFormat.setCalendar(cal)
  lazy val DefaultTimeFormat = new SimpleDateFormat("HH:mm a")
  DefaultTimeFormat.setCalendar(cal)

  lazy val DayOneTimeStr = DefaultDateFormat.format(dayOneTime.toDate)
  lazy val ProposalsOpenStr = DefaultDateFormat.format(ProposalsOpen.toDate)
  lazy val ProposalsCloseStr = DefaultDateFormat.format(ProposalsClose.toDate)
  lazy val VotesOpenStr = DefaultDateFormat.format(VotingOpens.toDate)
  lazy val VotesCloseStr = DefaultDateFormat.format(VotingCloses.toDate)

  def proposingIsOpen = (ProposalsOpen.isBeforeNow && ProposalsClose.isAfterNow)
  def votingIsOpen = (VotingOpens.isBeforeNow && VotingCloses.isAfterNow)
  def votingIsClosed = VotingCloses.isBeforeNow
  def rsvpsAreOpen = RSVPsOpen.isAfterNow

  private def time(hour: Int, minute: Int) =
    dayOneTime.withHourOfDay(hour).withMinuteOfHour(minute)

  lazy val PhillySchedule = Schedule2016.load().get

  // Target for off-site anchors.
  val OffsiteAnchorTarget = "nescala-offsite"
}

