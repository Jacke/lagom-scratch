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

class MicroserviceCalEntity extends PersistentEntity {
  override type Command = MicroserviceCalCommand[_]
  override type Event = MicroserviceCalEvent
  override type State = MicroserviceCalState

  override def initialState: MicroserviceCalState =
    MicroserviceCalState("Event", List(), List(), LocalDateTime.now.toString)
  override def behavior: Behavior = {
    case MicroserviceCalState(message, assets, entries, _) =>
      Actions()
        .onCommand[UseAssetMessage, Done] {
          case (UseAssetMessage(newMessage), ctx, state) =>
            ctx.thenPersist(
              AssetMessageChanged(newMessage)
            ) { _ =>
              ctx.reply(Done)
            }
        }
        .onCommand[AssetCreate, Asset] {
          case (AssetCreate(asset), ctx, state) =>
            ctx.thenPersist(
              AssetCreated(asset)
            ) { _ =>
              ctx.reply(asset)
            }
        }
        .onCommand[AssetUpdate, Asset] {
          case (AssetUpdate(id, asset), ctx, state) =>
            ctx.thenPersist(
              AssetUpdated(asset)
            ) { _ =>
              ctx.reply(asset)
            }
        }
        .onCommand[AssetDelete, Int] {
          case (AssetDelete(id), ctx, state) =>
            ctx.thenPersist(
              AssetDeleted(id)
            ) { _ =>
              ctx.reply(id)
            }
        }
        .onCommand[AssetEntryCreate, Entry] {
          case (AssetEntryCreate(entry), ctx, state) =>
            ctx.thenPersist(
              AssetEntryCreated(entry)
            ) { _ =>
              ctx.reply(entry)
            }
        }
        .onCommand[AssetEntryUpdate, Entry] {
          case (AssetEntryUpdate(entry), ctx, state) =>
            ctx.thenPersist(
              AssetEntryUpdated(entry)
            ) { _ =>
              ctx.reply(entry)
            }
        }
        .onCommand[AssetEntryDelete, Int] {
          case (AssetEntryDelete(id), ctx, state) =>
            ctx.thenPersist(
              AssetEntryDeleted(id)
            ) { _ =>
              ctx.reply(id)
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

        .onReadOnlyCommand[AssetEntries, List[Entry]] {
          case (AssetEntries(assetId), ctx, state) =>
                        ctx.reply(state.entries.filter(e => e.asset_id == assetId))
        }
        .onReadOnlyCommand[AssetsList, List[Asset]] {
          case (AssetsList(), ctx, state) =>
                        ctx.reply(state.assets)
        }
        .onReadOnlyCommand[AssetGet, Option[Asset]] {
          case (AssetGet(id), ctx, state) =>
                ctx.reply(state.assets.find(a => a.id == id))
        }

        .onEvent {
          case (AssetMessageChanged(newMessage), state) =>
            MicroserviceCalState(newMessage, state.assets, state.entries, LocalDateTime.now().toString)
          // Assets
          case (AssetCreated(newAsset), state) => 
             MicroserviceCalState(state.message, (newAsset :: state.assets), state.entries, LocalDateTime.now().toString)
          case (AssetUpdated(assetUpdated), state) => 
             MicroserviceCalState(state.message, (assetUpdated :: state.assets.filter(a => a.id != assetUpdated.id)), state.entries, LocalDateTime.now().toString)
          case (AssetDeleted(assetRemovedId), state) => 
             state.copy(state.message, state.assets.filter(a => a.id != assetRemovedId), state.entries, LocalDateTime.now().toString)
          // Entries
          case (AssetEntryCreated(newEntry), state) => 
             MicroserviceCalState(state.message, state.assets, (newEntry :: state.entries), LocalDateTime.now().toString)
          case (AssetEntryUpdated(entryUpdated), state) => 
             MicroserviceCalState(state.message, state.assets, (entryUpdated :: state.entries.filter(a => a.id != entryUpdated.id)), LocalDateTime.now().toString)
          case (AssetEntryDeleted(entryRemovedId), state) => 
             MicroserviceCalState(state.message, state.assets, state.entries.filter(a => a.id != entryRemovedId), LocalDateTime.now().toString)
        }
  }
}

case class MicroserviceCalState(message: String, assets:List[Asset], entries: List[Entry], timestamp: String)
object MicroserviceCalState {
  implicit val format0: Format[Asset] = Json.format[Asset]
  implicit val format4: Format[Entry] = Json.format[Entry]
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
    JsonSerializer[AssetEntries]

  )
}
