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
// FOR TEST removeme
import play.api.libs.json.{Format, Json}
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




///////
//          Primary commands
////
// Query the Read-Side Database

  override def getAllAssets() = ServiceCall { request =>
    val test: String = "test"
    val ref = persistentEntityRegistry.refFor[MicroserviceCalEntity](test)
    ref.ask(AssetsList()) // List[Asset]]
    persistToDb(action: DBIO[_])
    //db.run(repository.selectAssets() )
  }
  override def getAsset(assetId: Int) = ServiceCall { request =>
    val test: String = "test"
    val ref = persistentEntityRegistry.refFor[MicroserviceCalEntity](test)
    ref.ask(AssetGet(assetId)).map { r =>
      Json.toJson(r).toString
    }
    //db.run( repository.selectAsset(assetId) )
  }

  override def getEntries(assetId: Int) = ServiceCall { request =>
    val test: String = "test"
    val ref = persistentEntityRegistry.refFor[MicroserviceCalEntity](test)
    ref.ask(AssetEntries(assetId)) // List[Entry]]
    persistToDb(action: DBIO[_])
    //db.run (repository.selectEntryByAsset(assetId) )
  }


//  Update the Read-Side
  override def createAsset() = ServiceCall { request =>
    val test: String = "test"
    val ref = persistentEntityRegistry.refFor[MicroserviceCalEntity](test)
    //    db.run( repository.assetCreate(Asset(2, "bookstore")) )
      db.run(repository.assetCreate(request)).flatMap { db_result =>
        ref.ask(AssetCreate(db_result)).map { r =>
          r
      }
    }
  }

  override def updateAsset(id: Int) = ServiceCall { request =>
    val test: String = "test"
    val ref = persistentEntityRegistry.refFor[MicroserviceCalEntity](test)
    ref.ask(AssetUpdate(id, request)) // Asset]
    persistToDb(action: DBIO[_])
    //db.run( repository.assetUpdate(id, Asset(2, "bookstore")) )
  }
  override def deleteAsset(id: Int) = ServiceCall { request =>
    val test: String = "test"
    val ref = persistentEntityRegistry.refFor[MicroserviceCalEntity](test)
    ref.ask(AssetDelete(id)) // Asset]
    persistToDb(action: DBIO[_])
    //db.run( repository.assetRemove(id) )
  }



  override def createAssetEntry(id: Int) = ServiceCall { request =>
    val test: String = "test"
    val ref = persistentEntityRegistry.refFor[MicroserviceCalEntity](test)
    //db.run(
    //  repository.entryCreate(Entry(1, 2, "test entry", org.joda.time.DateTime.now(), org.joda.time.DateTime.now())))
    db.run(repository.entryCreate(request)).flatMap { db_result =>
        ref.ask(AssetEntryCreate(db_result)).map { r =>
          r
      }
    }
  }
  override def updateAssetEntry(assetId: Int, id: Int) = ServiceCall {
    request =>
      val test: String = "test"
      val ref = persistentEntityRegistry.refFor[MicroserviceCalEntity](test)
      ref.ask(AssetEntryUpdate(request))
      persistToDb(action: DBIO[_])
      //db.run(
      //  repository.entryUpdate(id, Entry(1, 2, "test entry", org.joda.time.DateTime.now(), org.joda.time.DateTime.now())))

  }
  override def deleteAssetEntry(assetId: Int, id: Int) = ServiceCall {
    request =>
      val test: String = "test"
      val ref = persistentEntityRegistry.refFor[MicroserviceCalEntity](test)
      //db.run( repository.entryRemove(id) )
      ref.ask(AssetEntryDelete(id))
      persistToDb(action: DBIO[_])

  }

// 1.2 The company wants to know when the store was open
  override def assetAvailability(assetId: Int) = ServiceCall { request =>
    val test: String = "test"
    val ref = persistentEntityRegistry.refFor[MicroserviceCalEntity](test)
    //ref.ask(Hello(test)) // AssetAvailabilityWrapper
    Future(
      AssetAvailabilityWrapper(1, List(Availability(org.joda.time.DateTime.now(), org.joda.time.DateTime.now()), 
        Availability(org.joda.time.DateTime.now(), org.joda.time.DateTime.now()))))
  }



  private def persistToDb(action: DBIO[_]) = {
    if (true) {
      db.run(action)
    }
  }
}
