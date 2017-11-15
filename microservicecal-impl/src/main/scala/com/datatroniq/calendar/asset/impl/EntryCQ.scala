package com.datatroniq.calendar.asset.impl
import java.time.LocalDateTime
import akka.Done
import com.lightbend.lagom.scaladsl.persistence.{AggregateEvent, AggregateEventTag, PersistentEntity}
import com.lightbend.lagom.scaladsl.persistence.PersistentEntity.ReplyType
import com.lightbend.lagom.scaladsl.playjson.{JsonSerializer, JsonSerializerRegistry}
import play.api.libs.json.{Format, Json}
import scala.collection.immutable.Seq
import com.datatroniq.calendar.asset.api._

// Events
case class AssetEntryCreated(entry: Entry) extends MicroserviceCalEvent
object AssetEntryCreated {  implicit val format4: Format[Entry] = Json.format[Entry]
   implicit val format: Format[AssetEntryCreated] = Json.format}

case class AssetEntryUpdated(entry: Entry) extends MicroserviceCalEvent
object AssetEntryUpdated {  implicit val format4: Format[Entry] = Json.format[Entry]
   implicit val format: Format[AssetEntryUpdated] = Json.format}

case class AssetEntryDeleted(entryId: Int) extends MicroserviceCalEvent
object AssetEntryDeleted {implicit val format: Format[AssetEntryDeleted] = Json.format}


// Commands
case class AssetEntryCreate(entry: Entry) extends MicroserviceCalCommand[String]
object AssetEntryCreate { implicit val format4: Format[Entry] = Json.format[Entry]
  implicit val format: Format[AssetEntryCreate] = Json.format}

case class AssetEntryUpdate(entry: Entry) extends MicroserviceCalCommand[String]
object AssetEntryUpdate { implicit val format4: Format[Entry] = Json.format[Entry]
  implicit val format: Format[AssetEntryUpdate] = Json.format}

case class AssetEntryDelete(entry: Entry) extends MicroserviceCalCommand[String]
object AssetEntryDelete { implicit val format4: Format[Entry] = Json.format[Entry]
  implicit val format: Format[AssetEntryDelete] = Json.format}
