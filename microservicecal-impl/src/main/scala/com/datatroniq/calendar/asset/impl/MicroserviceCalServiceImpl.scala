package com.datatroniq.calendar.asset.impl

import com.datatroniq.calendar.asset.api
import com.datatroniq.calendar.asset.api._
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

class MicroserviceCalServiceImpl(
    val persistentEntityRegistry: PersistentEntityRegistry,
    val readSide: ReadSide,
    val slickReadSide: SlickReadSide,
    val db: Database,
    val profile: JdbcProfile
) extends AssetService {

  val repository = MicroserviceCalEntityRepository(db, profile)
  readSide.register[MicroserviceCalEvent](
    MicroserviceCalEntityRepository.processor(
      slickReadSide,
      db,
      profile))


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
          case _ => r
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
    ref.ask(AssetUpdate(id, request)).flatMap { action => db.run(repository.assetUpdate(id, request)) } 
  }
  override def deleteAsset(id: Int) = ServiceCall { request =>
    val ref = persistentEntityRegistry.refFor[MicroserviceCalEntity]("test")
    ref.ask(AssetDelete(id))
    ref.ask(AssetDelete(id)).flatMap { action => db.run(repository.assetRemove(id)) } 

  }



////////

  override def getEntryExceptionsByEntry(entry_id: Int) = ServiceCall { request =>
    val ref = persistentEntityRegistry.refFor[MicroserviceCalEntity](AssetService.TOPIC_NAME)
    ref.ask(GetAssetEntryExceptions(entry_id)).flatMap { _ =>
      db.run(repository.getEntryExceptionsByEntry(entry_id)).map(_.toList)
    }  
  }
  override def entryExceptionCreate() = ServiceCall { request =>
    val ref = persistentEntityRegistry.refFor[MicroserviceCalEntity](AssetService.TOPIC_NAME)
    ref.ask(AssetEntryExceptionCreate(request)).flatMap { _ =>
      db.run(repository.entryExceptionCreate(request))
    }  
  }
  override def entryExceptionUpdate(id:Int) = ServiceCall { request =>
    val ref = persistentEntityRegistry.refFor[MicroserviceCalEntity](AssetService.TOPIC_NAME)
    ref.ask(AssetEntryExceptionUpdate(request)).flatMap { _ =>
      db.run(repository.entryExceptionUpdate(id, request))
    }  
  }  
  def deleteEntryException(entry_id: Int) = ServiceCall { request =>
    val ref = persistentEntityRegistry.refFor[MicroserviceCalEntity](AssetService.TOPIC_NAME)
    ref.ask(AssetEntryExceptionDelete(entry_id)).flatMap { _ => 
      db.run(repository.removeEntryException(entry_id))
    }
  }

  override def getEntries(asset_id: Int) = ServiceCall { request =>
    val ref = persistentEntityRegistry.refFor[MicroserviceCalEntity](AssetService.TOPIC_NAME)
    ref.ask(AssetEntries(asset_id)).flatMap { _ =>
      db.run(repository.selectEntryByAsset(asset_id)).map(_.toList)
    }  
  }

  override def createAssetEntry(id: Int) = ServiceCall { request =>
    val ref = persistentEntityRegistry.refFor[MicroserviceCalEntity](AssetService.TOPIC_NAME)
    db.run(repository.entryCreate(request.durate() )).flatMap { db_result =>
        ref.ask(AssetEntryCreate(db_result)).map { r =>
          r
      }
    }
  }

  override def updateAssetEntry(id: Int) = ServiceCall { request =>
      val ref = persistentEntityRegistry.refFor[MicroserviceCalEntity](AssetService.TOPIC_NAME)
      ref.ask(AssetEntryUpdate(request.durate() )).flatMap { _ =>
        db.run(repository.entryUpdate(id, request))
      }
  }

  override def deleteAssetEntry(id: Int) = ServiceCall { request =>
      val ref = persistentEntityRegistry.refFor[MicroserviceCalEntity](AssetService.TOPIC_NAME)
      ref.ask(AssetEntryDelete(id)).flatMap { _ =>
        db.run(repository.entryRemove(id))
      }
  }

// 1.2 The company wants to know when the store was open
  override def assetAvailability(asset_id: Int) = ServiceCall { request =>
    val ref = persistentEntityRegistry.refFor[MicroserviceCalEntity](AssetService.TOPIC_NAME)
    Future(
      AssetAvailabilityWrapper(1, List(Availability(org.joda.time.DateTime.now(), org.joda.time.DateTime.now()), 
        Availability(org.joda.time.DateTime.now(), org.joda.time.DateTime.now()))))
  }

}