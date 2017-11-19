package com.datatroniq.calendar.asset.impl

import com.datatroniq.calendar.asset.api
import com.datatroniq.calendar.asset.api._
import com.datatroniq.calendar.asset.impl.repository._
import com.lightbend.lagom.scaladsl.api.ServiceCall
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

trait AssetsCalls extends MicroserviceCalService {
  val persistentEntityRegistry: PersistentEntityRegistry
  val readSide: ReadSide
  val db: Database
  val profile: JdbcProfile
  val repository: MicroserviceCalEntityRepository

  override def getAllAssets() = ServiceCall { request =>
    val ref = persistentEntityRegistry.refFor[MicroserviceCalEntity]("test")
    ref.ask(AssetsList()).flatMap { _ =>
      db.run(repository.selectAssets()).map(_.toList)
    }
  }
  override def getAsset(asset_id: Int) = ServiceCall { request =>
    val ref = persistentEntityRegistry.refFor[MicroserviceCalEntity]("test")
    ref.ask(AssetGet(asset_id)).flatMap { r =>
      db.run(repository.selectAsset(asset_id)).map { optR =>
        optR match {
          case Some(asset) => asset
          case _           => r
        }
      }
    }
  }

//  Update the Read-Side
  override def createAsset() = ServiceCall { request =>
    val ref = persistentEntityRegistry.refFor[MicroserviceCalEntity]("test")
    db.run(repository.assetCreate(request)).flatMap { db_result =>
      ref.ask(AssetCreate(db_result))
    }
  }

  override def updateAsset(id: Int) = ServiceCall { request =>
    val ref = persistentEntityRegistry.refFor[MicroserviceCalEntity]("test")
    ref.ask(AssetUpdate(id, request)).flatMap { action =>
      db.run(repository.assetUpdate(id, request))
    }
  }
  override def deleteAsset(id: Int) = ServiceCall { request =>
    val ref = persistentEntityRegistry.refFor[MicroserviceCalEntity]("test")
    ref.ask(AssetDelete(id))
    ref.ask(AssetDelete(id)).flatMap { action =>
      db.run(repository.assetRemove(id))
    }

  }

}
