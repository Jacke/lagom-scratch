package com.datatroniq.calendar.asset.impl

import com.datatroniq.calendar.asset.api
import com.datatroniq.calendar.asset.api._
import com.lightbend.lagom.scaladsl.api.ServiceCall
import com.lightbend.lagom.scaladsl.api.broker.Topic
import com.lightbend.lagom.scaladsl.broker.TopicProducer
import com.lightbend.lagom.scaladsl.persistence.{EventStreamElement, PersistentEntityRegistry}
import org.joda.time.DateTime
import com.lightbend.lagom.scaladsl.api.{Service, ServiceCall}
import slick.jdbc.JdbcBackend.Database
// FOR TEST removeme
import play.api.libs.json.{Format, Json}
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

class MicroserviceCalServiceImpl(persistentEntityRegistry: PersistentEntityRegistry
  //repository: MicroserviceCalEntityRepository, 
  //db: Database
) extends AssetService {
///////
//          Test commands
////
  override def hello(id: String) = ServiceCall { _ =>
    val ref = persistentEntityRegistry.refFor[MicroserviceCalEntity](id)
    ref.ask(Hello(id))
  }
  override def useGreeting(id: String) = ServiceCall { request =>
    val ref = persistentEntityRegistry.refFor[MicroserviceCalEntity](id)
    ref.ask(UseAssetMessage(request.message))
  }
  override def greetingsTopic(): Topic[api.AssetMessageChanged] =
    TopicProducer.singleStreamWithOffset {
      fromOffset =>
        persistentEntityRegistry.eventStream(MicroserviceCalEvent.Tag, fromOffset)
          .map(ev => (convertEvent(ev), ev.offset))
    }
///////
//          Primary commands
////
  override def getAllAssets() = ServiceCall { request =>
    val test:String = "test"
    val ref = persistentEntityRegistry.refFor[MicroserviceCalEntity](test)
    //ref.ask(Hello(test)) // List[Asset]]
    Future( List(Asset(1, "cab"), Asset(2, "bookstore")) )
  }
  override def getAsset(assetId: Int) = ServiceCall { request =>
    val test:String = "test"
    val ref = persistentEntityRegistry.refFor[MicroserviceCalEntity](test)
    //ref.ask(Hello(test)) // Asset]
     Future( Asset(2, "bookstore") )
  }
  override def createAsset() = ServiceCall { request =>
    val test:String = "test"
    val ref = persistentEntityRegistry.refFor[MicroserviceCalEntity](test)
    //ref.ask(Hello(test))
     Future( Asset(2, "bookstore") )
  }

  override def updateAsset(id: Int) = ServiceCall { request =>
    val test:String = "test"
    val ref = persistentEntityRegistry.refFor[MicroserviceCalEntity](test)
    //ref.ask(Hello(test)) // Asset]
     Future( Asset(2, "bookstore") )
  }
  override def deleteAsset(id: Int) = ServiceCall { request =>
    val test:String = "test"
    val ref = persistentEntityRegistry.refFor[MicroserviceCalEntity](test)
    //ref.ask(Hello(test)) // Asset]
    Future(1)
  }

  override def getEntries(assetId: Int) = ServiceCall { request =>
    val test:String = "test"
    val ref = persistentEntityRegistry.refFor[MicroserviceCalEntity](test)
    //ref.ask(Hello(test)) // List[Entry]]
   Future( List(Entry(1, 2, "test entry", 1, 1))
  )
  }

  override def createAssetEntry(id: Int) = ServiceCall { request =>
    val test:String = "test"
    val ref = persistentEntityRegistry.refFor[MicroserviceCalEntity](test)
     Future( Entry(1, 2, "test entry", 1, 1) )
  }
  override def updateAssetEntry(assetId:Int, id: Int) = ServiceCall { request =>
    val test:String = "test"
    val ref = persistentEntityRegistry.refFor[MicroserviceCalEntity](test)
     Future( Entry(1, 2, "test entry", 1, 1) )
  }
  override def deleteAssetEntry(assetId:Int, id: Int) = ServiceCall { request =>
    val test:String = "test"
    val ref = persistentEntityRegistry.refFor[MicroserviceCalEntity](test)
    Future(1)
  }

// 1.2 The company wants to know when the store was open
  override def assetAvailability(assetId: Int) = ServiceCall { request =>
    val test:String = "test"
    val ref = persistentEntityRegistry.refFor[MicroserviceCalEntity](test)
    //ref.ask(Hello(test)) // AssetAvailabilityWrapper
    Future(AssetAvailabilityWrapper(1, List(Availability(4, 2 ),Availability(4, 2 ))) ) }

  private def convertEvent(helloEvent: EventStreamElement[MicroserviceCalEvent]): api.AssetMessageChanged = {
    helloEvent.event match {
      case AssetMessageChanged(msg) => api.AssetMessageChanged(helloEvent.entityId, msg)
    }
  }
}
