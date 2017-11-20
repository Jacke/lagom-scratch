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

case class AvailabilityGetFromTo(asset_id: Int, from: org.joda.time.DateTime, to: org.joda.time.DateTime) extends MicroserviceCalCommand[AssetAvailabilityWrapper]
case class AvailabilityGet(asset_id: Int) extends MicroserviceCalCommand[AssetAvailabilityWrapper]
object AvailabilityGet {
  implicit val format: Format[AvailabilityGet] = Json.format
}
