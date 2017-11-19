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
      val currentDay = startDateUtc.dayOfWeek().getAsShortText().toUpperCase()
      val start = pattern(0)
      val end = pattern(1)
      val days = List("MON", "TUE", "WED", "THU", "FRI", "SAT", "SUN")
      val skippedDays = days.drop(days.indexOf(end)+1)
      println(s"currentDay: ${days.indexOf(currentDay)}")
      println(s"start ${days.indexOf(start)}")
      println(s"end ${days.indexOf(end)}")
      println(s"skippedDays: ${skippedDays}")
      println(days.indexOf(currentDay))
      println(days.drop(days.indexOf(currentDay)))
      println(days.drop(days.indexOf(currentDay)).filter(d => !skippedDays.contains(d) ))
      println

      val targetDays = days.drop(days.indexOf(currentDay))filter(d => !skippedDays.contains(d) )
      println("targetDays")
      println(targetDays)

      targetDays.map { day =>
        Entry(id,
              asset_id,
              name,
              startDateUtc.plusDays(days.indexOf(day)),
              endDateUtc.plusDays(days.indexOf(day)))
      }
    } else { List(this) }
  }
}
