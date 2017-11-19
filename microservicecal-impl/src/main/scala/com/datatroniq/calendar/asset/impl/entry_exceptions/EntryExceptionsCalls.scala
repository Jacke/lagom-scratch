package com.datatroniq.calendar.asset.impl

import com.datatroniq.calendar.asset.api
import com.datatroniq.calendar.asset.api._
import com.lightbend.lagom.scaladsl.api.ServiceCall
import com.lightbend.lagom.scaladsl.api.broker.Topic
import com.lightbend.lagom.scaladsl.broker.TopicProducer
import com.datatroniq.calendar.asset.impl.repository._
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

trait EntryExceptionsCalls extends MicroserviceCalService {
  val persistentEntityRegistry: PersistentEntityRegistry
  val readSide: ReadSide
  val db: Database
  val profile: JdbcProfile
  val repository: MicroserviceCalEntityRepository

  override def getEntryExceptionsByEntry(entry_id: Int) = ServiceCall {
    request =>
      val ref = persistentEntityRegistry.refFor[MicroserviceCalEntity](
        AssetService.TOPIC_NAME)
      ref.ask(GetAssetEntryExceptions(entry_id)).flatMap { _ =>
        db.run(repository.getEntryExceptionsByEntry(entry_id)).map(_.toList)
      }
  }
  override def entryExceptionCreate() = ServiceCall { request =>
    val ref = persistentEntityRegistry.refFor[MicroserviceCalEntity](
      AssetService.TOPIC_NAME)
    ref.ask(AssetEntryExceptionCreate(request)).flatMap { _ =>
      db.run(repository.entryExceptionCreate(request))
    }
  }
  override def entryExceptionUpdate(id: Int) = ServiceCall { request =>
    val ref = persistentEntityRegistry.refFor[MicroserviceCalEntity](
      AssetService.TOPIC_NAME)
    ref.ask(AssetEntryExceptionUpdate(request)).flatMap { _ =>
      db.run(repository.entryExceptionUpdate(id, request))
    }
  }
  def deleteEntryException(entry_id: Int) = ServiceCall { request =>
    val ref = persistentEntityRegistry.refFor[MicroserviceCalEntity](
      AssetService.TOPIC_NAME)
    ref.ask(AssetEntryExceptionDelete(entry_id)).flatMap { _ =>
      db.run(repository.removeEntryException(entry_id))
    }
  }

}
