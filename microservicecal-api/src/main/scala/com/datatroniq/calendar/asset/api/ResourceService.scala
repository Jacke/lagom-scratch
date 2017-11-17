package com.datatroniq.calendar.asset.api

import akka.{Done, NotUsed}
import com.lightbend.lagom.scaladsl.api.broker.Topic
import com.lightbend.lagom.scaladsl.api.broker.kafka.{
  KafkaProperties,
  PartitionKeyStrategy
}
import com.lightbend.lagom.scaladsl.api.{Service, ServiceCall}
import play.api.libs.json.{Format, Json}
import play.api.libs.json.JodaReads._
import play.api.libs.json.JodaWrites._
import com.lightbend.lagom.scaladsl.api.transport.Method
import org.joda.time.DateTime
object AssetService {
  val TOPIC_NAME = "Assets"
}
case class Asset(id: Option[Int] = None, name: String)
case class Availability(from: DateTime, end: DateTime)
case class AssetAvailabilityWrapper(assetId: Int, availability: List[Availability])
/*
Event

Column Name Type
EventId Unique id
name String
from StartDateUtc  DateTime
end EndDateUtc  DateTime
IsAllDay  Boolean
Duration  Integer
IsRecurring Boolean
RecurrencePattern String
*/

case class Entry(id: Option[Int] = None, asset_id: Int, name: String, startDateUtc: DateTime, endDateUtc: DateTime, 
  duration: Int, isAllDay: Boolean = false, 
  isRecuring: Boolean = false, recurrencePattern: String = "")

/*
EventException

Column Name Type
EventExceptionId  Unique id
EventID (FK)  Unique id
ExceptionDateUtc  DateTime
*/
case class EntryException(id: Option[Int] = None, entry_id: Int, startDateUtc: DateTime, endDateUtc: DateTime)


trait AssetService extends Service {
  import com.datatroniq.calendar.utils.Formats._
// 1.1 The employee manages the calendar for his book store
  def getAllAssets(): ServiceCall[NotUsed, List[Asset]]
  def getAsset(assetId: Int): ServiceCall[NotUsed, Asset]
  def createAsset(): ServiceCall[Asset, Asset]
  def updateAsset(id: Int): ServiceCall[Asset, Asset]
  def deleteAsset(id: Int): ServiceCall[NotUsed, Int]

  def getEntries(assetId: Int): ServiceCall[NotUsed, List[Entry]]
  def createAssetEntry(id: Int): ServiceCall[Entry, Entry]
  def updateAssetEntry(assetId: Int, id: Int): ServiceCall[Entry, Entry]
  def deleteAssetEntry(assetId: Int, id: Int): ServiceCall[NotUsed, Int]

  def entryExceptionCreate(): ServiceCall[EntryException, EntryException]
  def getEntryExceptionsByEntry(entry_id: Int): ServiceCall[NotUsed, List[EntryException]]
  def deleteEntryException(entry_id: Int): ServiceCall[EntryException, Int]
//  def EntryException(entry_id: Int): ServiceCall[NotUsed, Int]

  implicit val format5: Format[EntryException] = Json.format[EntryException]


// 1.2 The company wants to know when the store was open
  def assetAvailability(assetId: Int): ServiceCall[NotUsed, AssetAvailabilityWrapper]

  
  override final def descriptor = {
    import Service._
    // @formatter:off
    named("Asset")
      .withCalls(
        restCall(Method.GET, "/api/asset/:id", getAsset _),
        restCall(Method.GET, "/api/assets",    getAllAssets _),
        restCall(Method.GET, "/api/asset/:id/entries",    getEntries _),
        restCall(Method.POST, "/api/asset", createAsset _),
        restCall(Method.PUT, "/api/asset/:id", updateAsset _),
        restCall(Method.DELETE, "/api/asset/:id", deleteAsset _),
        restCall(Method.POST, "/api/asset/:id/entry", createAssetEntry _),
        restCall(Method.PUT, "/api/asset/:assetId/entry/:id", updateAssetEntry _),
        restCall(Method.POST, "/api/entry/exception", entryExceptionCreate _),        
        restCall(Method.GET, "/api/entry/:entry_id/exception", getEntryExceptionsByEntry _),
        restCall(Method.DELETE, "/api/entry/:entry_id/exception", deleteEntryException _),

        restCall(Method.DELETE, "/api/asset/:assetId/entry/:id", deleteAssetEntry _),
        restCall(Method.GET, "/api/asset/:id/availabilities", assetAvailability _)
      )
      .withAutoAcl(true)
    // @formatter:on
  }
}

