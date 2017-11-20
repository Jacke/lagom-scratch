package com.datatroniq.calendar.asset.impl

import com.datatroniq.calendar.asset.api
import com.datatroniq.calendar.asset.api._
import com.lightbend.lagom.scaladsl.api.ServiceCall
import com.datatroniq.calendar.asset.impl.repository._
import com.lightbend.lagom.scaladsl.api.broker.Topic
import com.lightbend.lagom.scaladsl.broker.TopicProducer
import com.lightbend.lagom.scaladsl.persistence.{
  EventStreamElement,
  PersistentEntityRegistry
}
import org.joda.time.DateTime
import com.lightbend.lagom.scaladsl.api.{Service, ServiceCall}
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import com.lightbend.lagom.scaladsl.api.{Service, ServiceCall}
import slick.jdbc.JdbcBackend.Database
import akka.Done
import com.lightbend.lagom.scaladsl.persistence.ReadSide
import com.lightbend.lagom.scaladsl.persistence.AggregateEventTag
import com.lightbend.lagom.scaladsl.persistence.ReadSideProcessor
import com.lightbend.lagom.scaladsl.persistence.jdbc.JdbcReadSide
import com.lightbend.lagom.scaladsl.persistence.EventStreamElement
import slick.dbio.DBIO
import scala.concurrent.ExecutionContext
import akka.persistence.query.Offset
import com.lightbend.lagom.scaladsl.persistence.slick.SlickReadSide
import _root_.slick.driver.JdbcProfile
import com.datatroniq.calendar.utils.Formats._
import com.datatroniq.calendar.utils.AvailabilitySplitter
import org.joda.time._
import org.joda.time.format._

trait AvaliabilitiesCalls extends MicroserviceCalService {
  val persistentEntityRegistry: PersistentEntityRegistry
  val readSide: ReadSide
  val db: Database
  val profile: JdbcProfile
  val repository: MicroserviceCalEntityRepository

  override def assetAvailability(assetId: Int) = ServiceCall { request =>
    val ref = persistentEntityRegistry.refFor[MicroserviceCalEntity](
      AssetService.TOPIC_NAME)

    ref.ask(AssetEntries(assetId)).flatMap { _ =>
      db.run(repository.selectEntryByAsset(assetId)).map(_.toList).flatMap { entries => 
      db.run(repository.getEntriesException(entries.map(_.id.get).toList)).map(_.toList).map { entry_exceptions =>
        val availabilities = AvailabilitySplitter.split(entries, entry_exceptions)
        AssetAvailabilityWrapper(
          assetId,
          availabilities
        )
        }
      }
    }
  }

  override def assetAvailabilityFromTo(assetId: Int, from: String, to: String) = ServiceCall { request =>
    val ref = persistentEntityRegistry.refFor[MicroserviceCalEntity](
      AssetService.TOPIC_NAME)
    val pattern = DateTimeFormat.forPattern("MM/dd/yyyy HH:mm:ss")
    val fromDateTime = DateTime.parse(from, pattern)
    val toDateTime = DateTime.parse(to, pattern)
    val interval = new org.joda.time.Interval(fromDateTime, toDateTime)

ref.ask(AssetEntries(assetId)).flatMap { _ =>
      db.run(repository.selectEntryByAsset(assetId)).map(_.toList).flatMap { allEntries => 
      val entries = allEntries.filter { entry => 
        interval.contains(new Interval(entry.startDateUtc, entry.endDateUtc))
      }
      db.run(repository.getEntriesException(entries.map(_.id.get).toList)).map(_.toList).map { entry_exceptions =>
        val availabilities = AvailabilitySplitter.split(entries, entry_exceptions)
          AssetAvailabilityWrapper(
            assetId,
            availabilities
          )
        }
      }
    } 
  }

}
