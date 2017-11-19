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

case class AssetEntryExceptionCreate(entryException: EntryException)
    extends MicroserviceCalCommand[EntryException]
object AssetEntryExceptionCreate {
  implicit val format5: Format[EntryException] = Json.format[EntryException]
  implicit val format6: Format[AssetEntryExceptionCreate] =
    Json.format[AssetEntryExceptionCreate]
}

case class AssetEntryExceptionUpdate(entryException: EntryException)
    extends MicroserviceCalCommand[EntryException]
object AssetEntryExceptionUpdate {
  implicit val format5: Format[EntryException] = Json.format[EntryException]
  implicit val format6: Format[AssetEntryExceptionUpdate] =
    Json.format[AssetEntryExceptionUpdate]
}

case class AssetEntryExceptionDelete(entry_exception_id: Int)
    extends MicroserviceCalCommand[Int]
object AssetEntryExceptionDelete {
  implicit val format6: Format[AssetEntryExceptionDelete] =
    Json.format[AssetEntryExceptionDelete]
}

case class GetAssetEntryExceptions(entry_id: Int)
    extends MicroserviceCalCommand[List[EntryException]]
object GetAssetEntryExceptions {
  implicit val format7: Format[GetAssetEntryExceptions] =
    Json.format[GetAssetEntryExceptions]
}

case class AssetEntryExceptionCreated(entryException: EntryException)
    extends MicroserviceCalEvent
object AssetEntryExceptionCreated {
  implicit val format5: Format[EntryException] = Json.format[EntryException]
  implicit val format: Format[AssetEntryExceptionCreated] = Json.format
}

case class AssetEntryExceptionUpdated(entry: EntryException)
    extends MicroserviceCalEvent
object AssetEntryExceptionUpdated {
  implicit val format4: Format[EntryException] = Json.format[EntryException]
  implicit val format: Format[AssetEntryExceptionUpdated] = Json.format
}

case class AssetEntryExceptionDeleted(entryId: Int) extends MicroserviceCalEvent
object AssetEntryExceptionDeleted {
  implicit val format: Format[AssetEntryExceptionDeleted] = Json.format
}
