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
case class AssetCreated(asset: Asset) extends MicroserviceCalEvent
object AssetCreated {
  implicit val format0: Format[Asset] = Json.format[Asset]
  implicit val format: Format[AssetCreated] = Json.format
}

case class AssetUpdated(asset: Asset) extends MicroserviceCalEvent
object AssetUpdated {
  implicit val format0: Format[Asset] = Json.format[Asset]
  implicit val format: Format[AssetUpdated] = Json.format
}

case class AssetDeleted(assetId: Int) extends MicroserviceCalEvent
object AssetDeleted { implicit val format: Format[AssetDeleted] = Json.format }

/**
 * Commands
 */
case class AssetsList() extends MicroserviceCalCommand[String]

case class AssetCreate(asset: Asset) extends MicroserviceCalCommand[Done]
object AssetCreate {
  implicit val format0: Format[Asset] = Json.format[Asset]
  implicit val format: Format[AssetCreate] = Json.format
}

case class AssetUpdate(asset: Asset) extends MicroserviceCalCommand[Done]
object AssetUpdate {
  implicit val format0: Format[Asset] = Json.format[Asset]
  implicit val format: Format[AssetUpdate] = Json.format
}

case class AssetDelete(asset: Asset) extends MicroserviceCalCommand[Done]
object AssetDelete {
  implicit val format0: Format[Asset] = Json.format[Asset]
  implicit val format: Format[AssetDelete] = Json.format
}
