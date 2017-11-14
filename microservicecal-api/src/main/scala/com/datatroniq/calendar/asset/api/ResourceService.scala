package com.datatroniq.calendar.asset.api

import akka.{Done, NotUsed}
import com.lightbend.lagom.scaladsl.api.broker.Topic
import com.lightbend.lagom.scaladsl.api.broker.kafka.{KafkaProperties, PartitionKeyStrategy}
import com.lightbend.lagom.scaladsl.api.{Service, ServiceCall}
import play.api.libs.json.{Format, Json}
import com.lightbend.lagom.scaladsl.api.transport.Method

object AssetService  {
  val TOPIC_NAME = "Assets"
}

trait AssetService extends Service {
  def hello(id: String): ServiceCall[NotUsed, String]

// 1.1 The employee manages the calendar for his book store
  def getAllAssets(): ServiceCall[NotUsed, List[Asset]]
  def getAsset(assetId: Int): ServiceCall[NotUsed, Asset]
  def getEntries(assetId: Int): ServiceCall[NotUsed, List[Entry]]

  def createAsset(): ServiceCall[NotUsed, String]
  def createAssetEntry(id: String): ServiceCall[NotUsed, String]
// 1.2 The company wants to know when the store was open
  def assetAvaliability(assetId: Int): ServiceCall[NotUsed, AssetAvaliabilityWrapper]

  def useGreeting(id: String): ServiceCall[AssetMessage, Done]
  def greetingsTopic(): Topic[AssetMessageChanged]

  override final def descriptor = {
    import Service._
    // @formatter:off
    named("Asset")
      .withCalls(
        restCall(Method.GET, "/api/asset/:id", getAsset _),
        //restCall(Method.GET, "/api/asset/:id", useGreeting _)
        restCall(Method.GET, "/api/assets",    getAllAssets _),
        restCall(Method.GET, "/api/asset/:id/entries",    getEntries _),
        //1.1 The employee manages the calendar for his book store
        restCall(Method.POST, "/api/asset", createAsset _),
        restCall(Method.POST, "/api/asset/:id/entry", createAssetEntry _),
        restCall(Method.GET, "/api/asset/:id/avaliabilities", assetAvaliability _)
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

