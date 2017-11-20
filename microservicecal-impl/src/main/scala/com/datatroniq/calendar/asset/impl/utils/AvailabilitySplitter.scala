package com.datatroniq.calendar.utils
import org.joda.time._
import org.joda.time.format._
import com.datatroniq.calendar.asset.api._
import scala.annotation.tailrec

//case class Availability(from: DateTime, end: DateTime)
//case class AssetAvailabilityWrapper(assetId: Int, availability: List[Availability])

//case class Entry(id: Option[Int] = None, asset_id: Int, name: String, startDateUtc: DateTime, endDateUtc: DateTime,
//  var duration: Int, isAllDay: Boolean = false,
//  isRecuring: Boolean = false, recurrencePattern: String = "")
//case class EntryException(id: Option[Int] = None, entry_id: Int, startDateUtc: DateTime, endDateUtc: DateTime)

object AvailabilitySplitter {
  val pattern = DateTimeFormat.forPattern("MM/dd/yyyy HH:mm:ss")
  // Recur event -> normal event
  def recurenceParsing(entries: List[Entry]): List[Entry] =
    entries.map(e => e.recur()).flatten

  def testRecur() = {
    val test1 = recurenceParsing(
      List(
        Entry(None,
              0,
              "name",
              DateTime.parse("11/18/2017 08:10:00", pattern),
              DateTime.parse("11/18/2017 17:10:00", pattern),
              isRecuring = true,
              recurrencePattern = "MON-TUE"))
    )

    val test2 = recurenceParsing(
      List(
        Entry(None,
              0,
              "name",
              DateTime.parse("11/20/2017 08:10:00", pattern),
              DateTime.parse("11/20/2017 17:10:00", pattern),
              isRecuring = true,
              recurrencePattern = "MON-WED"))
    )


    val test3 = recurenceParsing(
      List(
        Entry(None,
              0,
              "name",
              DateTime.parse("11/21/2017 08:10:00", pattern),
              DateTime.parse("11/21/2017 17:10:00", pattern),
              isRecuring = true,
              recurrencePattern = "MON-WED"))
    )
    println("test1")
    println(test1)
    println("test2")
    println(test2)
    println("test3")
    println(test3)
  }

  def split(entries: List[Entry],
            exceptions: List[EntryException]): List[Availability] = {
    entries.map { entry =>
      val interval =
        new org.joda.time.Interval(entry.startDateUtc, entry.endDateUtc)
      val currentExceptions = exceptions.filter(target =>
        interval.contains(new Interval(target.startDateUtc, target.endDateUtc)))

      @tailrec
      def generateAvailability(
          workdayEnd: DateTime,
          currentExceptions: List[EntryException],
          start: DateTime,
          end: DateTime,
          otherExceptions: List[EntryException] = List(),
          avaliabities: List[Availability] = List()): List[Availability] = {
        if (currentExceptions.length > 0) {
          println("otherExceptions.length")
          println(otherExceptions.length)
          if (otherExceptions.length != 0) {
            val targetException = otherExceptions.head
            val newAvaliability =
              Availability(start, targetException.startDateUtc)
            println(
              s"New avaliability ${start}: ${targetException.startDateUtc}")
            generateAvailability(workdayEnd,
                                 currentExceptions,
                                 targetException.endDateUtc,
                                 otherExceptions.head.startDateUtc,
                                 otherExceptions.tail,
                                 (newAvaliability :: avaliabities))
          } else {
            (avaliabities :+ Availability(currentExceptions.last.endDateUtc,
                                          workdayEnd))
          }
        } else {
          List(Availability(start, workdayEnd))
        }
      }
      val z = generateAvailability(entry.endDateUtc,
                                   currentExceptions,
                                   entry.startDateUtc,
                                   entry.endDateUtc,
                                   currentExceptions)
      Availability(entry.startDateUtc, entry.endDateUtc)
    }
  }
  
  def test() = {
    val pattern = DateTimeFormat.forPattern("MM/dd/yyyy HH:mm:ss")
    split(
      List(
        Entry(None,
              0,
              "name",
              DateTime.parse("11/18/2017 08:10:00", pattern),
              DateTime.parse("11/18/2017 17:10:00", pattern))),
      List(
        EntryException(None,
                       0,
                       DateTime.parse("11/18/2017 06:10:00", pattern),
                       DateTime.parse("11/18/2017 08:09:00", pattern)),
        EntryException(None,
                       0,
                       DateTime.parse("11/18/2017 11:10:00", pattern),
                       DateTime.parse("11/18/2017 12:10:00", pattern)),
        EntryException(None,
                       0,
                       DateTime.parse("11/18/2017 14:10:00", pattern),
                       DateTime.parse("11/18/2017 16:09:59", pattern))
      )
    )
  }
}
