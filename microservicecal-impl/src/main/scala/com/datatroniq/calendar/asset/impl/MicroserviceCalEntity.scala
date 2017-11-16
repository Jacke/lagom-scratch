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

class MicroserviceCalEntity extends PersistentEntity {
  override type Command = MicroserviceCalCommand[_]
  override type Event = MicroserviceCalEvent
  override type State = MicroserviceCalState

  override def initialState: MicroserviceCalState =
    MicroserviceCalState("Event", LocalDateTime.now.toString)
  override def behavior: Behavior = {
    case MicroserviceCalState(message, _) =>
      Actions()
        .onCommand[UseAssetMessage, Done] {
          case (UseAssetMessage(newMessage), ctx, state) =>
            ctx.thenPersist(
              AssetMessageChanged(newMessage)
            ) { _ =>
              ctx.reply(Done)
            }
        }
        .onCommand[AssetCreate, Done] {
          case (AssetCreate(asset), ctx, state) =>
            ctx.thenPersist(
              AssetCreate(asset)
            ) { _ =>
              ctx.reply(Done)
            }
        }
        .onCommand[AssetUpdate, Done] {
          case (AssetUpdate(asset), ctx, state) =>
            ctx.thenPersist(
              AssetUpdate(asset)
            ) { _ =>
              ctx.reply(Done)
            }
        }
        .onCommand[AssetDelete, Done] {
          case (AssetDelete(asset), ctx, state) =>
            ctx.thenPersist(
              AssetDelete(asset)
            ) { _ =>
              ctx.reply(Done)
            }
        }
        .onCommand[AssetEntryCreate, Done] {
          case (AssetEntryCreate(entry), ctx, state) =>
            ctx.thenPersist(
              AssetEntryCreate(entry)
            ) { _ =>
              ctx.reply(Done)
            }
        }
        .onCommand[AssetEntryUpdate, Done] {
          case (AssetEntryUpdate(entry), ctx, state) =>
            ctx.thenPersist(
              AssetEntryUpdate(entry)
            ) { _ =>
              ctx.reply(Done)
            }
        }
        .onCommand[AssetEntryDelete, Done] {
          case (AssetEntryDelete(entry), ctx, state) =>
            ctx.thenPersist(
              AssetEntryDelete(entry)
            ) { _ =>
              ctx.reply(Done)
            }
        }
        .onReadOnlyCommand[Hello, String] {
          case (Hello(name), ctx, state) =>
            ctx.reply(
              Json
                .toJson(Map("name" -> Json.toJson(name),
                            "state" -> Json.toJson(state)))
                .toString)
        }
        .onReadOnlyCommand[Hello, String] {
          case (AssetEntries(assetId), ctx, state) =>
                        ctx.reply(
                          Json.toJson(Map("state" -> Json.toJson(state))))
        }
        .onReadOnlyCommand[Hello, String] {
          case (AssetsList(), ctx, state) =>
                        ctx.reply(
                          Json.toJson(Map("state" -> Json.toJson(state))))
        }

        .onEvent {
          case (AssetMessageChanged(newMessage), state) =>
            MicroserviceCalState(newMessage, LocalDateTime.now().toString)
          case (AssetCreated(asset), state) => 
             MicroserviceCalState(asset, LocalDateTime.now().toString)
          case (AssetUpdated(asset), state) => 
             MicroserviceCalState(asset, LocalDateTime.now().toString)
          case (AssetDeleted(asset), state) => 
             MicroserviceCalState(asset, LocalDateTime.now().toString)
          case (AssetEntryCreated(entry), state) => 
             MicroserviceCalState(entry, LocalDateTime.now().toString)
          case (AssetEntryUpdated(entry), state) => 
             MicroserviceCalState(entry, LocalDateTime.now().toString)
          case (AssetEntryDeleted(entry), state) => 
             MicroserviceCalState(entry, LocalDateTime.now().toString)
        }
  }
}

case class MicroserviceCalState(message: String, timestamp: String)
object MicroserviceCalState {
  implicit val format: Format[MicroserviceCalState] = Json.format
}

case class AssetMessageChanged(message: String) extends MicroserviceCalEvent
object AssetMessageChanged {
  implicit val format: Format[AssetMessageChanged] = Json.format
}
// Commands
case class UseAssetMessage(message: String) extends MicroserviceCalCommand[Done]
object UseAssetMessage {
  implicit val format: Format[UseAssetMessage] = Json.format
}
case class Hello(name: String) extends MicroserviceCalCommand[String]
object Hello { implicit val format: Format[Hello] = Json.format }



object MicroserviceCalSerializerRegistry extends JsonSerializerRegistry {
  override def serializers: Seq[JsonSerializer[_]] = Seq(
    JsonSerializer[UseAssetMessage],
    JsonSerializer[Hello],
    JsonSerializer[AssetMessageChanged],
    JsonSerializer[MicroserviceCalState],
    JsonSerializer[AssetCreated],
    JsonSerializer[AssetUpdated],
    JsonSerializer[AssetDeleted],
    JsonSerializer[AssetEntryCreated],
    JsonSerializer[AssetEntryUpdated],
    JsonSerializer[AssetEntryDeleted],
    JsonSerializer[AssetCreate],
    JsonSerializer[AssetUpdate],
    JsonSerializer[AssetDelete],
    JsonSerializer[AssetEntryCreate],
    JsonSerializer[AssetEntryUpdate],
    JsonSerializer[AssetEntryDelete],
    JsonSerializer[AssetEntries],
    JsonSerializer[AssetsList]

  )
}
