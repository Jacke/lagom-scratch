package com.datatroniq.calendar.asset.impl
import java.time.LocalDateTime
import akka.Done
import com.lightbend.lagom.scaladsl.persistence.{AggregateEvent, AggregateEventTag, PersistentEntity}
import com.lightbend.lagom.scaladsl.persistence.PersistentEntity.ReplyType
import com.lightbend.lagom.scaladsl.playjson.{JsonSerializer, JsonSerializerRegistry}
import play.api.libs.json.{Format, Json}
import scala.collection.immutable.Seq


class MicroserviceCalEntity extends PersistentEntity {
  override type Command = MicroserviceCalCommand[_]
  override type Event = MicroserviceCalEvent
  override type State = MicroserviceCalState

  override def initialState: MicroserviceCalState = MicroserviceCalState("Event", LocalDateTime.now.toString)
  override def behavior: Behavior = {
    case MicroserviceCalState(message, _) => Actions().onCommand[UseAssetMessage, Done] {
      case (UseAssetMessage(newMessage), ctx, state) =>
        ctx.thenPersist(
          AssetMessageChanged(newMessage)
        ) { _ =>
          ctx.reply(Done)
        }
    }.onReadOnlyCommand[Hello, String] {
      case (Hello(name), ctx, state) =>
        ctx.reply(Json.toJson(Map("name" -> Json.toJson(name), "state" -> Json.toJson(state) )).toString)
    }.onEvent {
      case (AssetMessageChanged(newMessage), state) =>
        MicroserviceCalState(newMessage, LocalDateTime.now().toString)
    }
  }
}

case class MicroserviceCalState(message: String, timestamp: String)
object MicroserviceCalState {
  implicit val format: Format[MicroserviceCalState] = Json.format
}

sealed trait MicroserviceCalEvent extends AggregateEvent[MicroserviceCalEvent] {
  def aggregateTag = MicroserviceCalEvent.Tag
}

object MicroserviceCalEvent {
  val Tag = AggregateEventTag[MicroserviceCalEvent]
}

case class AssetMessageChanged(message: String) extends MicroserviceCalEvent

case class AssetCreated() extends MicroserviceCalEvent
case class AssetUpdated() extends MicroserviceCalEvent
case class AssetDeleted() extends MicroserviceCalEvent
/// EVENTS
case class AssetEntryCreated() extends MicroserviceCalEvent
case class AssetEntryUpdated() extends MicroserviceCalEvent
case class AssetEntryDeleted() extends MicroserviceCalEvent


object AssetMessageChanged {
  implicit val format: Format[AssetMessageChanged] = Json.format
}

sealed trait MicroserviceCalCommand[R] extends ReplyType[R]
case class UseAssetMessage(message: String) extends MicroserviceCalCommand[Done]
object UseAssetMessage {
  implicit val format: Format[UseAssetMessage] = Json.format
}
case class Hello(name: String) extends MicroserviceCalCommand[String]
object Hello {
  implicit val format: Format[Hello] = Json.format
}
object MicroserviceCalSerializerRegistry extends JsonSerializerRegistry {
  override def serializers: Seq[JsonSerializer[_]] = Seq(
    JsonSerializer[UseAssetMessage],
    JsonSerializer[Hello],
    JsonSerializer[AssetMessageChanged],
    JsonSerializer[MicroserviceCalState]
  )
}
