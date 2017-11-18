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
	def split(entries: List[Entry], exceptions: List[EntryException]):List[Availability] = {
		entries.map { entry =>
		    val interval = new org.joda.time.Interval(entry.startDateUtc, entry.endDateUtc)
		    val currentExceptions = exceptions.filter(target => 
		      interval.contains(new Interval(target.startDateUtc ,target.endDateUtc))  
		    )
		    println(interval)
		    println()

		    currentExceptions.map { exc => 
		    	println("Exception:")
		    	println(exc.startDateUtc)
		    	println(exc.endDateUtc)
		    	println()
		    }

		    @tailrec
		    def generateAvailability(currentExceptions: List[EntryException], 
		    						 start: DateTime, 
		    						 end: DateTime, 
		    						 otherExceptions: List[EntryException] = List(),
		    						 avaliabities: List[Availability] = List() ):List[Availability] = {
		    	if (currentExceptions.length > 0) {
			    	if (start != end && otherExceptions.length != 0 ) {
			    		val targetException = currentExceptions.head
			    		val newAvaliability = Availability(start, targetException.startDateUtc)
			    		println(s"New avaliability ${start}: ${targetException.startDateUtc}")
				    	generateAvailability(currentExceptions,
				    						 targetException.endDateUtc, 
				    						 otherExceptions.tail.head.startDateUtc,  
				    						 otherExceptions.tail,
				    						 (newAvaliability :: avaliabities) )
			    	} else {
			    		avaliabities
			    	}
		    	} else {
		    		List( Availability(start, end) )
		    	}
		    }
		    generateAvailability(currentExceptions, entry.startDateUtc, entry.endDateUtc, currentExceptions).map { a =>
		    	println(a.from)
		    	println(a.end)
		    	println()
		    }

			Availability(entry.startDateUtc, entry.endDateUtc)
		}
	}
	def test() = {
		val pattern = DateTimeFormat.forPattern("MM/dd/yyyy HH:mm:ss")
		split(
			List(Entry(None, 0, "name", DateTime.parse("11/18/2017 08:10:00",pattern), 
										DateTime.parse("11/18/2017 17:10:00",pattern) )),


			List(EntryException(None, 0, DateTime.parse("11/18/2017 06:10:00",pattern), 
										 DateTime.parse("11/18/2017 08:09:00",pattern)),

				EntryException(None, 0, DateTime.parse("11/18/2017 11:10:00",pattern), 
										 DateTime.parse("11/18/2017 12:10:00",pattern)),
				EntryException(None, 0, DateTime.parse("11/18/2017 14:10:00",pattern), 
										 DateTime.parse("11/18/2017 17:09:59",pattern)))
		)
	}
}