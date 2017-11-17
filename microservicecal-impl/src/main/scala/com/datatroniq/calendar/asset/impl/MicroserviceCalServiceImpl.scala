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
    persistentEntityRegistry: PersistentEntityRegistry,
    readSide: ReadSide,
    slickReadSide: SlickReadSide,
    db: Database,
    val profile: JdbcProfile
) extends AssetService {

  val repository = MicroserviceCalEntityRepository(db, profile)
  readSide.register[MicroserviceCalEvent](
    MicroserviceCalEntityRepository.processor(
      slickReadSide,
      db,
      profile))

/**
 * Primary commands
 */
// Query the Read-Side Database
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
        ref.ask(AssetCreate(db_result)).map { r =>
          r
      }
    }
  }

  override def updateAsset(id: Int) = ServiceCall { request =>
    val ref = persistentEntityRegistry.refFor[MicroserviceCalEntity]("test")
    ref.ask(AssetUpdate(id, request)).flatMap { _ => 
      db.run(repository.assetUpdate(id, request))
    }
  }
  override def deleteAsset(id: Int) = ServiceCall { request =>
    val ref = persistentEntityRegistry.refFor[MicroserviceCalEntity]("test")
    ref.ask(AssetDelete(id)).flatMap { _ => 
      db.run(repository.assetRemove(id))
    }
  }

  override def entryExceptionCreate() = ServiceCall { request =>
    val ref = persistentEntityRegistry.refFor[MicroserviceCalEntity]("test")
    ref.ask(AssetEntryExceptionCreate(request)).flatMap { _ =>
      db.run(repository.entryExceptionCreate(request))
    }  
  }

  override def getEntryExceptionsByEntry(entry_id: Int) = ServiceCall { request =>
    val ref = persistentEntityRegistry.refFor[MicroserviceCalEntity]("test")
    ref.ask(AssetEntryExceptions(entry_id)).flatMap { _ =>
      db.run(repository.getEntryExceptionsByEntry(entry_id)).map(_.toList)
    }  
  }

  def deleteEntryException(entry_id: Int) = ServiceCall { request =>
    val ref = persistentEntityRegistry.refFor[MicroserviceCalEntity]("test")
    ref.ask(AssetEntryExceptionDelete(entry_id)).flatMap { _ => 
      db.run(repository.removeEntryException(entry_id))
    }
  }

  override def getEntries(asset_id: Int) = ServiceCall { request =>
    val ref = persistentEntityRegistry.refFor[MicroserviceCalEntity]("test")
    ref.ask(AssetEntries(asset_id)).flatMap { _ =>
      db.run(repository.selectEntryByAsset(asset_id)).map(_.toList)
    }  
  }

  override def createAssetEntry(id: Int) = ServiceCall { request =>
    val ref = persistentEntityRegistry.refFor[MicroserviceCalEntity]("test")
    db.run(repository.entryCreate(request)).flatMap { db_result =>
        ref.ask(AssetEntryCreate(db_result)).map { r =>
          r
      }
    }
  }

  override def updateAssetEntry(asset_id: Int, id: Int) = ServiceCall {
    request =>
      val ref = persistentEntityRegistry.refFor[MicroserviceCalEntity]("test")
      ref.ask(AssetEntryUpdate(request)).flatMap { _ =>
        db.run(repository.entryUpdate(id, request))
      }
  }
  
  override def deleteAssetEntry(asset_id: Int, id: Int) = ServiceCall {
    request =>
      val ref = persistentEntityRegistry.refFor[MicroserviceCalEntity]("test")
      ref.ask(AssetEntryDelete(id)).flatMap { _ =>
        db.run(repository.entryRemove(id))
      }
  }

// 1.2 The company wants to know when the store was open
  override def assetAvailability(asset_id: Int) = ServiceCall { request =>
    val ref = persistentEntityRegistry.refFor[MicroserviceCalEntity]("test")
    Future(
      AssetAvailabilityWrapper(1, List(Availability(org.joda.time.DateTime.now(), org.joda.time.DateTime.now()), 
        Availability(org.joda.time.DateTime.now(), org.joda.time.DateTime.now()))))
  }



  private def persistToDb[T](action: DBIO[T]):Future[T] = {
//    if (true) { // Individual persistents switch
      db.run(action)
//    } else {
//    }
  }
}
