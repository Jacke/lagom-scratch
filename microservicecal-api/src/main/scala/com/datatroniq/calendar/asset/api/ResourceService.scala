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
import org.joda.time.Minutes
object AssetService {
  val TOPIC_NAME = "Assets"
}

case class EntryException(id: Option[Int] = None,
                          entry_id: Int,
                          startDateUtc: DateTime,
                          endDateUtc: DateTime)

trait MicroserviceCalService extends Service {
  import com.datatroniq.calendar.utils.Formats._

  def getAllAssets(): ServiceCall[NotUsed, List[Asset]]
  def getAsset(assetId: Int): ServiceCall[NotUsed, Asset]
  def createAsset(): ServiceCall[Asset, Asset]
  def updateAsset(id: Int): ServiceCall[Asset, Asset]
  def deleteAsset(id: Int): ServiceCall[NotUsed, Int]

  def getEntries(assetId: Int): ServiceCall[NotUsed, List[Entry]]
  def createAssetEntry(id: Int): ServiceCall[Entry, Entry]
  def updateAssetEntry(id: Int): ServiceCall[Entry, Entry]
  def deleteAssetEntry(id: Int): ServiceCall[NotUsed, Int]

  def entryExceptionCreate(): ServiceCall[EntryException, EntryException]
  def entryExceptionUpdate(id: Int): ServiceCall[EntryException, EntryException]
  def getEntryExceptionsByEntry(
      entry_id: Int): ServiceCall[NotUsed, List[EntryException]]
  def deleteEntryException(entry_id: Int): ServiceCall[EntryException, Int]

  implicit val format5: Format[EntryException] = Json.format[EntryException]

  def assetAvailability(
      assetId: Int): ServiceCall[NotUsed, AssetAvailabilityWrapper]

  def assetAvailabilityFromTo(assetId: Int, from: String, to: String): ServiceCall[NotUsed, AssetAvailabilityWrapper]

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
        restCall(Method.PUT, "/api/asset/entry/:id", updateAssetEntry _),
        restCall(Method.DELETE, "/api/asset/entry/:id", deleteAssetEntry _),

        restCall(Method.GET, "/api/entry/:entry_id/exception", getEntryExceptionsByEntry _),
        restCall(Method.POST, "/api/entry/exception", entryExceptionCreate _),        
        restCall(Method.DELETE, "/api/entry/:entry_id/exception", deleteEntryException _),

        restCall(Method.GET, "/api/asset/:assetId/availabilities", assetAvailability _),
        restCall(Method.GET, "/api/asset/:assetId/availabilities/:from/:to", assetAvailabilityFromTo _)

      )
      .withAutoAcl(true)
    // @formatter:on
  }
}
