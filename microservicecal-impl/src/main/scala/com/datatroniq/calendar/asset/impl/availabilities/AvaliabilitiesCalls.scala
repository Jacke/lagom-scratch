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

trait AvaliabilitiesCalls extends MicroserviceCalService {
  val persistentEntityRegistry: PersistentEntityRegistry
  val readSide: ReadSide
  val db: Database
  val profile: JdbcProfile
  val repository: MicroserviceCalEntityRepository

  override def assetAvailability(assetId: Int) = ServiceCall { request =>
    val ref = persistentEntityRegistry.refFor[MicroserviceCalEntity](
      AssetService.TOPIC_NAME)
    Future(
      AssetAvailabilityWrapper(
        1,
        List(Availability(org.joda.time.DateTime.now(),
                          org.joda.time.DateTime.now()),
             Availability(org.joda.time.DateTime.now(),
                          org.joda.time.DateTime.now()))
      ))
  }

  override def allAvailabilities() = ServiceCall { request =>
    val ref = persistentEntityRegistry.refFor[MicroserviceCalEntity](
      AssetService.TOPIC_NAME)

    Future(
      List(AssetAvailabilityWrapper(
        1,
        List(Availability(org.joda.time.DateTime.now(),
                          org.joda.time.DateTime.now()),
             Availability(org.joda.time.DateTime.now(),
                          org.joda.time.DateTime.now())
      ))))
  }

}
