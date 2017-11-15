package com.datatroniq.calendar.asset.api

import akka.{Done, NotUsed}
import com.lightbend.lagom.scaladsl.api.broker.Topic
import com.lightbend.lagom.scaladsl.api.broker.kafka.{KafkaProperties, PartitionKeyStrategy}
import com.lightbend.lagom.scaladsl.api.{Service, ServiceCall}
import play.api.libs.json.{Format, Json}
import com.lightbend.lagom.scaladsl.api.transport.Method
import org.joda.time.DateTime
object AssetService  {
  val TOPIC_NAME = "Assets"
}
case class Asset(id:Int, name: String)
case class Availability(from: Int, end: Int)
case class AssetAvailabilityWrapper(assetId: Int, availability: List[Availability])
case class Entry(id:Int, asset_id: Int, name: String, from: Int, end: Int)


trait AssetService extends Service {
  implicit val format: Format[Asset] = Json.format[Asset]
  implicit val format2: Format[Availability] = Json.format[Availability]
  implicit val format3: Format[AssetAvailabilityWrapper] = Json.format[AssetAvailabilityWrapper]
  implicit val format4: Format[Entry] = Json.format[Entry]

  def hello(id: String): ServiceCall[NotUsed, String]
// 1.1 The employee manages the calendar for his book store
  def getAllAssets(): ServiceCall[NotUsed, List[Asset]]
  def getAsset(assetId: Int): ServiceCall[NotUsed, Asset]
  def createAsset(): ServiceCall[NotUsed, Asset]
  def updateAsset(id: Int): ServiceCall[NotUsed, Asset]
  def deleteAsset(id: Int): ServiceCall[NotUsed, Int]

  def getEntries(assetId: Int): ServiceCall[NotUsed, List[Entry]]
  def createAssetEntry(id: Int): ServiceCall[NotUsed, Entry]
  def updateAssetEntry(assetId:Int, id: Int): ServiceCall[NotUsed, Entry]
  def deleteAssetEntry(assetId:Int, id: Int): ServiceCall[NotUsed, Int]


// 1.2 The company wants to know when the store was open
  def assetAvailability(assetId: Int): ServiceCall[NotUsed, AssetAvailabilityWrapper]

  def useGreeting(id: String): ServiceCall[AssetMessage, Done]
  def greetingsTopic(): Topic[AssetMessageChanged]

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
        restCall(Method.DELETE, "/api/asset/:assetId/entry/:id", deleteAssetEntry _),
        restCall(Method.GET, "/api/asset/:id/availabilities", assetAvailability _)
      )
      .withTopics(
        topic(AssetService.TOPIC_NAME, greetingsTopic _)
          .addProperty(
            KafkaProperties.partitionKeyStrategy,
            PartitionKeyStrategy[AssetMessageChanged](_.name)
          )
      )
      .withAutoAcl(true)
    // @formatter:on
  }
}

case class AssetMessage(message: String)
object AssetMessage {
  implicit val format: Format[AssetMessage] = Json.format[AssetMessage]
}
case class AssetMessageChanged(name: String, message: String)
object AssetMessageChanged {
  implicit val format: Format[AssetMessageChanged] = Json.format[AssetMessageChanged]
}

