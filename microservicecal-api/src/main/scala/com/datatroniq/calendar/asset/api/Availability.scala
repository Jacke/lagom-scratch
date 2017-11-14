package com.datatroniq.calendar.asset.api
import org.joda.time.DateTime
import play.api.libs.json.{Format, Json}

case class Availability(from: DateTime, end: DateTime)
case class AssetAvailabilityWrapper(assetId: Int, availability: List[Availability])


object Availability {
  implicit val format: Format[Availability] = Json.format[Availability]
}

object AssetAvailabilityWrapper {
  implicit val format: Format[AssetAvailabilityWrapper] = Json.format[AssetAvailabilityWrapper]
}