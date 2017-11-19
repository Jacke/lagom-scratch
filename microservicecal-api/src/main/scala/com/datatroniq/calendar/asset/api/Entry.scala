package com.datatroniq.calendar.asset.api

import play.api.libs.json.{Format, Json}
import play.api.libs.json.JodaReads._
import play.api.libs.json.JodaWrites._
import com.lightbend.lagom.scaladsl.api.transport.Method
import org.joda.time.DateTime
import org.joda.time.Minutes

object EntryFactory {
  def apply(id: Option[Int] = None,
                 asset_id: Int,
                 name: String,
                 startDateUtc: DateTime,
                 endDateUtc: DateTime,
                 duration: Int = 0,
                 isAllDay: Boolean = false,
                 isRecuring: Boolean = false,
                 recurrencePattern: String = ""):Entry = {
/* 
      TODO: Check if reccurencePattern validated by the same entry day 
        Entry on monday, reccurence pattern from monday
        Entry on wed then pattern should be from wed
        
        if (isRecuring) {
          // Validate recurrencePattern pattern
          val pattern = recurrencePattern.split("-")
          val currentDay = startDateUtc.dayOfWeek().getAsShortText().toUpperCase()
          val start = pattern(0)
          val end = pattern(1)
          if (!(start == )) {
          }
        }
*/
    val newDuration = Minutes.minutesBetween(startDateUtc, endDateUtc).getMinutes()
    new Entry(id,
              asset_id,
              name,
              startDateUtc,
              endDateUtc,
              newDuration,
              isAllDay,
              isRecuring,
              recurrencePattern)
  }

  def apply(entry: Entry):Entry = {
    val newDuration = Minutes.minutesBetween(entry.startDateUtc, entry.endDateUtc).getMinutes()
    new Entry(entry.id,
              entry.asset_id,
              entry.name,
              entry.startDateUtc,
              entry.endDateUtc,
              newDuration,
              entry.isAllDay,
              entry.isRecuring,
              entry.recurrencePattern)
  }
}

case class Entry(id: Option[Int] = None,
                 asset_id: Int,
                 name: String,
                 startDateUtc: DateTime,
                 endDateUtc: DateTime,
                 var duration: Int = 0,
                 isAllDay: Boolean = false,
                 isRecuring: Boolean = false,
                 recurrencePattern: String = "") {
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

      val targetDays = days.drop(days.indexOf(currentDay))filter(d => !skippedDays.contains(d) )
      targetDays.map { day =>
        EntryFactory(Entry(id,
              asset_id,
              name,
              startDateUtc.plusDays(days.indexOf(day)),
              endDateUtc.plusDays(days.indexOf(day))) )
      }
    } else { List(EntryFactory(this)) }
  }
}
