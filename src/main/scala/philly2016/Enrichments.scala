package nescala.philly2016

import java.util.{Calendar, Date}

object Enrichments {
  implicit class EnrichedDate(date: Date) {
    // java.util.Calendar must be the worst date handling API ever.
    private val calendar = Calendar.getInstance
    calendar.setTime(date)

    def field(calendarField: Int) = calendar.get(calendarField)
  }
}
