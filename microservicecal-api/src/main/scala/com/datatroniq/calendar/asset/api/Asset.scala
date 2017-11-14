package com.datatroniq.calendar.asset.api
import play.api.libs.json.{Format, Json}

case class Asset(id:Int, name: String)

object Asset {
  implicit val format: Format[Asset] = Json.format[Asset]
}