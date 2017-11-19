package com.datatroniq.calendar.asset.api

import play.api.libs.json.{Format, Json}
import play.api.libs.json.JodaReads._
import play.api.libs.json.JodaWrites._
import com.lightbend.lagom.scaladsl.api.transport.Method
import org.joda.time.DateTime
import org.joda.time.Minutes

case class Entry(id: Option[Int] = None,
                 asset_id: Int,
                 name: String,
                 startDateUtc: DateTime,
                 endDateUtc: DateTime,
                 var duration: Int = 0,
                 isAllDay: Boolean = false,
                 isRecuring: Boolean = false,
                 recurrencePattern: String = "") {
  def durate() = {
    duration = Minutes.minutesBetween(startDateUtc, endDateUtc).getMinutes()
    this
  }
  def recur(): List[Entry] = {
    if (isRecuring) {
      // contains word
      // MON-FRI
      val pattern = recurrencePattern.split("-")
      val start = pattern(0)
      val end = pattern(1)
      val days = List("MON", "TUE", "WED", "THU", "FRI", "SAT", "SUN")
      val targetDays = days.drop(days.indexOf(start)).take(days.indexOf(end))
      targetDays.map { day =>
        Entry(id,
              asset_id,
              name,
              startDateUtc.plusDays(days.indexOf(day) + 1),
              endDateUtc.plusDays(days.indexOf(day) + 1))
      }
    } else { List(this) }
  }
}
