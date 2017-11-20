package com.datatroniq.calendar.asset.impl

import com.lightbend.lagom.scaladsl.server.LocalServiceLocator
import com.lightbend.lagom.scaladsl.testkit.ServiceTest
import org.scalatest.{AsyncWordSpec, BeforeAndAfterAll, Matchers}
import com.datatroniq.calendar.asset.api._
import org.joda.time._
import org.joda.time.format._
import com.datatroniq.calendar.utils.AvailabilitySplitter._

class AvailabilitySplitterSpec
    extends AsyncWordSpec
    with Matchers
    with BeforeAndAfterAll {
  implicit def dateTimeOrdering: Ordering[DateTime] = Ordering.fromLessThan(_ isBefore _)

  val pattern = DateTimeFormat.forPattern("MM/dd/yyyy HH:mm:ss")

  def testRecur():Tuple3[List[Entry],List[Entry],List[Entry]] = {
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
    (test1, test2, test3)
  }

  def test(): List[Availability] = {
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


  "AvailabilitySplitter in example" should {
	  val (test1, test2, test3) = testRecur()
	  val availabilities = test().sortBy(_.from)
	  println(availabilities)
    "When it starts with tue because 21 is tue" in {
	  test3.length should ===(2)
    }
    "When it starts with tue because 20 is mon" in {
    	test2.length should ===(3)
    }
    "When it starts with tue because 18 is sat but we have mon-tue pattern" in {
    	test1.length should ===(0)
    }

    "Avaliabilities from 8 to 17 10" in {
    	availabilities(0).from should ===(DateTime.parse("11/18/2017 08:10:00", pattern))
    	availabilities(2).end should === (DateTime.parse("11/18/2017 17:10:00", pattern))
    }
    "Exception should work" in {
       availabilities.head.end should ===(DateTime.parse("11/18/2017 11:10:00", pattern)) 
       // Date of first active(on workday segment) exception
       // Unused exception unused
       availabilities.head.end should not equal(DateTime.parse("11/18/2017 08:09:00", pattern)) 

    }

  }

}