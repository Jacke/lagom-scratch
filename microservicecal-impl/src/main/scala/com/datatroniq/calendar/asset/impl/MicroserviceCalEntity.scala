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

class MicroserviceCalEntity extends PersistentEntity {
  override type Command = MicroserviceCalCommand[_]
  override type Event = MicroserviceCalEvent
  override type State = MicroserviceCalState

  override def initialState: MicroserviceCalState =
    MicroserviceCalState("Event", List(), List(), List(), LocalDateTime.now.toString)
  override def behavior: Behavior = {
    case MicroserviceCalState(message, assets, entries, entry_exceptions, _) =>
      Actions()
        .onCommand[AssetCreate, Asset] {
          case (AssetCreate(asset), ctx, state) => {
            println("asset")
            println(asset)
            ctx.thenPersist(
              AssetCreated(asset)
            ) { createdAsset =>
              println(createdAsset)
              ctx.reply(createdAsset.asset)
            }
          }
        }
        .onCommand[AssetUpdate, Asset] {
          case (AssetUpdate(id, asset), ctx, state) =>
            ctx.thenPersist(
              AssetUpdated(asset)
            ) { updatedAsset =>
              ctx.reply(updatedAsset.asset)
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
        .onCommand[AssetEntryExceptionCreate, EntryException] {
          case (AssetEntryExceptionCreate(entryException), ctx, state) =>
            ctx.thenPersist(
              AssetEntryExceptionCreated(entryException)
            ) { _ =>
              ctx.reply(entryException)
            }
        }
        .onCommand[AssetEntryExceptionUpdate, EntryException] {
          case (AssetEntryExceptionUpdate(entryException), ctx, state) =>
            ctx.thenPersist(
              AssetEntryExceptionUpdated(entryException)
            ) { _ =>
              ctx.reply(entryException)
            }
        }
      
        .onCommand[AssetEntryExceptionDelete, Int] {
          case (AssetEntryExceptionDelete(id), ctx, state) =>
            ctx.thenPersist(
              AssetEntryExceptionDeleted(id)
            ) { _ =>
              ctx.reply(id)
            }
        }


        .onReadOnlyCommand[AssetEntries, List[Entry]] {
          case (AssetEntries(assetId), ctx, state) =>
                        ctx.reply(state.entries.filter(e => e.asset_id == assetId))
        }
        .onReadOnlyCommand[AssetsList, List[Asset]] {
          case (AssetsList(), ctx, state) =>
                        ctx.reply(state.assets)
        }
        .onReadOnlyCommand[AssetGet, Asset] {
          case (AssetGet(id), ctx, state) => {
            state.assets.find(a => a.id == id) match {
              case Some(asset) => ctx.reply(asset)
              case _ => ctx.invalidCommand("Not found")
            }
          }
        }
        .onReadOnlyCommand[AssetEntryExceptions, List[EntryException]] {
          case (AssetEntryExceptions(entry_id), ctx, state) => {
            state.entry_exceptions.filter(ee => ee.entry_id == entry_id)
          }
        }

        .onEvent {
          // Assets
          case (AssetUpdated(assetUpdated), state) => 
             MicroserviceCalState(state.message, (assetUpdated :: state.assets.filter(a => a.id != assetUpdated.id)), state.entries, state.entry_exceptions, LocalDateTime.now().toString)
          case (AssetDeleted(assetRemovedId), state) => 
             state.copy(state.message, state.assets.filter(a => a.id != assetRemovedId), state.entries, state.entry_exceptions, LocalDateTime.now().toString)
          case (AssetCreatedDb(assetCreated), state) => {
            println(assetCreated)
            state
          }
          // Entries
          case (AssetEntryCreated(newEntry), state) => 
             MicroserviceCalState(state.message, state.assets, (newEntry :: state.entries), state.entry_exceptions, LocalDateTime.now().toString)
          case (AssetEntryUpdated(entryUpdated), state) => 
             MicroserviceCalState(state.message, state.assets, (entryUpdated :: state.entries.filter(a => a.id != entryUpdated.id)), state.entry_exceptions, LocalDateTime.now().toString)
          case (AssetEntryDeleted(entryRemovedId), state) => 
             MicroserviceCalState(state.message, state.assets, state.entries.filter(a => a.id != entryRemovedId),state.entry_exceptions,  LocalDateTime.now().toString)
          // Entry exceptions          
          case (AssetEntryExceptionCreated(newEntryException), state) =>
             MicroserviceCalState(state.message, state.assets, state.entries, (newEntryException :: state.entry_exceptions), LocalDateTime.now().toString)            
          case (AssetEntryExceptionUpdated(updatedEntryException), state) =>
             MicroserviceCalState(state.message, state.assets, state.entries, (updatedEntryException :: state.entry_exceptions.filter(a => a.id != updatedEntryException.id)), LocalDateTime.now().toString)                          
          case (AssetEntryExceptionDeleted(entryExceptionId), state) => 
             MicroserviceCalState(state.message, state.assets, state.entries, state.entry_exceptions.filter(a => a.id != entryExceptionId), LocalDateTime.now().toString)            
        }
  }
}

case class MicroserviceCalState(message: String, 
                                assets:List[Asset], 
                                entries: List[Entry], 
                                entry_exceptions: List[EntryException], 
                                timestamp: String)
object MicroserviceCalState {
  implicit val format0: Format[Asset] = Json.format[Asset]
  implicit val format4: Format[Entry] = Json.format[Entry]
  implicit val format8: Format[EntryException] = Json.format[EntryException]
  implicit val format: Format[MicroserviceCalState] = Json.format
}



object MicroserviceCalSerializerRegistry extends JsonSerializerRegistry {
  override def serializers: Seq[JsonSerializer[_]] = Seq(
    JsonSerializer[MicroserviceCalState],
    JsonSerializer[AssetCreated],
    JsonSerializer[AssetUpdated],
    JsonSerializer[AssetDeleted],
    JsonSerializer[AssetEntryCreated],
    JsonSerializer[AssetEntryUpdated],
    JsonSerializer[AssetEntryDeleted],
    JsonSerializer[AssetEntryExceptionCreate],
    JsonSerializer[AssetEntryExceptionUpdated],
    JsonSerializer[AssetEntryExceptionDelete],
    JsonSerializer[AssetEntryExceptions],
    JsonSerializer[AssetCreate],
    JsonSerializer[AssetUpdate],
    JsonSerializer[AssetDelete],
    JsonSerializer[AssetEntryCreate],
    JsonSerializer[AssetEntryUpdate],
    JsonSerializer[AssetEntryDelete],
    JsonSerializer[AssetEntries]

  )
}
