package com.datatroniq.calendar.asset.impl
import java.time.LocalDateTime
import akka.Done
import com.lightbend.lagom.scaladsl.persistence.{AggregateEvent, AggregateEventTag, PersistentEntity}
import com.lightbend.lagom.scaladsl.persistence.PersistentEntity.ReplyType
import com.lightbend.lagom.scaladsl.playjson.{JsonSerializer, JsonSerializerRegistry}
import play.api.libs.json.{Format, Json}
import scala.collection.immutable.Seq
import com.datatroniq.calendar.asset.api._


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
object MicroserviceCalState {implicit val format: Format[MicroserviceCalState] = Json.format}

sealed trait MicroserviceCalEvent extends AggregateEvent[MicroserviceCalEvent] {
  def aggregateTag = MicroserviceCalEvent.Tag
}

object MicroserviceCalEvent {
  val Tag = AggregateEventTag[MicroserviceCalEvent]
}

case class AssetMessageChanged(message: String) extends MicroserviceCalEvent
object AssetMessageChanged {implicit val format: Format[AssetMessageChanged] = Json.format}
/// Events
case class AssetCreated(asset: Asset) extends MicroserviceCalEvent
object AssetCreated {   implicit val format0: Format[Asset] = Json.format[Asset]
   implicit val format: Format[AssetCreated] = Json.format}

case class AssetUpdated(asset: Asset) extends MicroserviceCalEvent
object AssetUpdated {    implicit val format0: Format[Asset] = Json.format[Asset]
   implicit val format: Format[AssetUpdated] = Json.format}

case class AssetDeleted(assetId: Int) extends MicroserviceCalEvent
object AssetDeleted {implicit val format: Format[AssetDeleted] = Json.format}

case class AssetEntryCreated(entry: Entry) extends MicroserviceCalEvent
object AssetEntryCreated {  implicit val format4: Format[Entry] = Json.format[Entry]
   implicit val format: Format[AssetEntryCreated] = Json.format}

case class AssetEntryUpdated(entry: Entry) extends MicroserviceCalEvent
object AssetEntryUpdated {  implicit val format4: Format[Entry] = Json.format[Entry]
   implicit val format: Format[AssetEntryUpdated] = Json.format}

case class AssetEntryDeleted(entryId: Int) extends MicroserviceCalEvent
object AssetEntryDeleted {implicit val format: Format[AssetEntryDeleted] = Json.format}

// Commands
sealed trait MicroserviceCalCommand[R] extends ReplyType[R]
case class UseAssetMessage(message: String) extends MicroserviceCalCommand[Done]
object UseAssetMessage {implicit val format: Format[UseAssetMessage] = Json.format}
case class Hello(name: String) extends MicroserviceCalCommand[String]
object Hello {implicit val format: Format[Hello] = Json.format}

case class AssetCreate(asset: Asset) extends MicroserviceCalCommand[String]
object AssetCreate {
  implicit val format0: Format[Asset] = Json.format[Asset]
  implicit val format: Format[AssetCreate] = Json.format}

case class AssetUpdate(asset: Asset) extends MicroserviceCalCommand[String]
object AssetUpdate {
  implicit val format0: Format[Asset] = Json.format[Asset]
implicit val format: Format[AssetUpdate] = Json.format}

case class AssetDelete(asset: Asset) extends MicroserviceCalCommand[String]
object AssetDelete {
  implicit val format0: Format[Asset] = Json.format[Asset]
  implicit val format: Format[AssetDelete] = Json.format}

case class AssetEntryCreate(entry: Entry) extends MicroserviceCalCommand[String]
object AssetEntryCreate { implicit val format4: Format[Entry] = Json.format[Entry]
  implicit val format: Format[AssetEntryCreate] = Json.format}

case class AssetEntryUpdate(entry: Entry) extends MicroserviceCalCommand[String]
object AssetEntryUpdate { implicit val format4: Format[Entry] = Json.format[Entry]
  implicit val format: Format[AssetEntryUpdate] = Json.format}

case class AssetEntryDelete(entry: Entry) extends MicroserviceCalCommand[String]
object AssetEntryDelete { implicit val format4: Format[Entry] = Json.format[Entry]
  implicit val format: Format[AssetEntryDelete] = Json.format}


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
    JsonSerializer[AssetEntryDelete]


  )
}
