package com.datatroniq.calendar.asset.api
import play.api.libs.json.{Format, Json}

import org.joda.time.DateTime
case class Entry(id:Int, asset_id: Int, from: DateTime, end: DateTime)


object Entry {
  implicit val format: Format[Entry] = Json.format[Entry]
}