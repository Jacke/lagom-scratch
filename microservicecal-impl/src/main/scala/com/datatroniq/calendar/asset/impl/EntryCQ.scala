package com.datatroniq.calendar.asset.impl
import java.time.LocalDateTime
import akka.Done
import com.lightbend.lagom.scaladsl.persistence.{
  AggregateEvent,
  AggregateEventTag,
  PersistentEntity
}
import com.lightbend.lagom.scaladsl.persistence.PersistentEntity.ReplyType
import com.lightbend.lagom.scaladsl.playjson.{
  JsonSerializer,
  JsonSerializerRegistry
}
import play.api.libs.json.{Format, Json}
import scala.collection.immutable.Seq
import com.datatroniq.calendar.asset.api._
import play.api.libs.json.JodaReads._
import play.api.libs.json.JodaWrites._
import com.datatroniq.calendar.utils.Formats._

/**
 * Events
 */
case class AssetEntryCreated(entry: Entry) extends MicroserviceCalEvent
object AssetEntryCreated {
  implicit val format4: Format[Entry] = Json.format[Entry]
  implicit val format: Format[AssetEntryCreated] = Json.format
}

case class AssetEntryUpdated(entry: Entry) extends MicroserviceCalEvent
object AssetEntryUpdated {
  implicit val format4: Format[Entry] = Json.format[Entry]
  implicit val format: Format[AssetEntryUpdated] = Json.format
}

case class AssetEntryDeleted(entryId: Int) extends MicroserviceCalEvent
object AssetEntryDeleted {
  implicit val format: Format[AssetEntryDeleted] = Json.format
}
case class AssetEntryExceptionCreated(entryException: EntryException) extends MicroserviceCalEvent
object AssetEntryExceptionCreated {
  implicit val format5: Format[EntryException] = Json.format[EntryException]
  implicit val format: Format[AssetEntryExceptionCreated] = Json.format
}
case class AssetEntryExceptionDeleted(entryId: Int) extends MicroserviceCalEvent
object AssetEntryExceptionDeleted {
  implicit val format: Format[AssetEntryExceptionDeleted] = Json.format
}



/**
 * Commands
 */
case class AssetEntryExceptionCreate(entryException: EntryException) extends MicroserviceCalCommand[EntryException]
object AssetEntryExceptionCreate {
  implicit val format5: Format[EntryException] = Json.format[EntryException]
  implicit val format6:Format[AssetEntryExceptionCreate] = Json.format[AssetEntryExceptionCreate]
}

case class AssetEntryExceptionDelete(entry_exception_id: Int) extends MicroserviceCalCommand[Int]
object AssetEntryExceptionDelete {
  implicit val format6:Format[AssetEntryExceptionDelete] = Json.format[AssetEntryExceptionDelete]
}

case class AssetEntryExceptions(entry_id: Int) extends MicroserviceCalCommand[List[EntryException]]
object AssetEntryExceptions {
  implicit val format7:Format[AssetEntryExceptions] = Json.format[AssetEntryExceptions]
}

case class AssetEntries(entryId: Int) extends MicroserviceCalCommand[List[Entry]]
object AssetEntries {
  implicit val format4: Format[Entry] = Json.format[Entry]
  implicit val format: Format[AssetEntries] = Json.format
}


case class AssetEntryCreate(entry: Entry) extends MicroserviceCalCommand[Entry]
object AssetEntryCreate {
  implicit val format4: Format[Entry] = Json.format[Entry]
  implicit val format: Format[AssetEntryCreate] = Json.format
}

case class AssetEntryUpdate(entry: Entry) extends MicroserviceCalCommand[Entry]
object AssetEntryUpdate {
  implicit val format4: Format[Entry] = Json.format[Entry]
  implicit val format: Format[AssetEntryUpdate] = Json.format
}

case class AssetEntryDelete(entryId: Int) extends MicroserviceCalCommand[Int]
object AssetEntryDelete {
  implicit val format4: Format[Entry] = Json.format[Entry]
  implicit val format: Format[AssetEntryDelete] = Json.format
}
