package com.datatroniq.calendar.asset.api

import play.api.libs.json.{Format, Json}
import play.api.libs.json.JodaReads._
import play.api.libs.json.JodaWrites._
import com.lightbend.lagom.scaladsl.api.transport.Method
import org.joda.time.DateTime
import org.joda.time.Minutes

case class Availability(from: DateTime, end: DateTime)
case class AssetAvailabilityWrapper(assetId: Int,
                                    availability: List[Availability])
