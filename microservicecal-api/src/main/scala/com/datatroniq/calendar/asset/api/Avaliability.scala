package com.datatroniq.calendar.asset.api
import org.joda.time.DateTime
import play.api.libs.json.{Format, Json}

case class Avaliability(from: DateTime, end: DateTime)
case class AssetAvaliabilityWrapper(assetId: Int, avaliability: List[Avaliability])


object Avaliability {
  implicit val format: Format[Avaliability] = Json.format[Avaliability]
}

object AssetAvaliabilityWrapper {
  implicit val format: Format[AssetAvaliabilityWrapper] = Json.format[AssetAvaliabilityWrapper]
}