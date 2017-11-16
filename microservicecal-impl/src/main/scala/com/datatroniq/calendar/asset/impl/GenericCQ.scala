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

trait MicroserviceCalEvent extends AggregateEvent[MicroserviceCalEvent] {
  def aggregateTag = MicroserviceCalEvent.Tag
}
object MicroserviceCalEvent {
  val Tag = AggregateEventTag[MicroserviceCalEvent]
}
trait MicroserviceCalCommand[R] extends ReplyType[R]
