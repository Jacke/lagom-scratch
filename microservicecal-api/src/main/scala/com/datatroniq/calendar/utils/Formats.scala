package com.datatroniq.calendar.utils
import play.api.libs.json.{Format, Json}
import org.joda.time.DateTime
import com.datatroniq.calendar.asset.api._
import play.api.libs.json.JodaReads._
import play.api.libs.json.JodaWrites._

object Formats {
  implicit val format: Format[Asset] = Json.format[Asset]
  implicit val format2: Format[Availability] = Json.format[Availability]
  implicit val format3: Format[AssetAvailabilityWrapper] =
    Json.format[AssetAvailabilityWrapper]
  implicit val format4: Format[Entry] = Json.format[Entry]
}