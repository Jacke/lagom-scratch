package com.datatroniq.calendar.asset.impl

import com.datatroniq.calendar.asset.api
import com.datatroniq.calendar.asset.api._
import com.lightbend.lagom.scaladsl.api.ServiceCall
import com.lightbend.lagom.scaladsl.api.broker.Topic
import com.lightbend.lagom.scaladsl.broker.TopicProducer
import com.lightbend.lagom.scaladsl.persistence.{EventStreamElement, PersistentEntityRegistry}
import org.joda.time.DateTime

// FOR TEST removeme
import play.api.libs.json.{Format, Json}
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

class MicroserviceCalServiceImpl(persistentEntityRegistry: PersistentEntityRegistry) extends AssetService {
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
    testAssets
  }
  override def getAsset(assetId: Int) = ServiceCall { request =>
    val test:String = "test"
    val ref = persistentEntityRegistry.refFor[MicroserviceCalEntity](test)
    //ref.ask(Hello(test)) // Asset]
    testAsset
  }
  override def getEntries(assetId: Int) = ServiceCall { request =>
    val test:String = "test"
    val ref = persistentEntityRegistry.refFor[MicroserviceCalEntity](test)
    //ref.ask(Hello(test)) // List[Entry]]
    testEntries
  }

  override def createAsset() = ServiceCall { request =>
    val test:String = "test"
    val ref = persistentEntityRegistry.refFor[MicroserviceCalEntity](test)
    ref.ask(Hello(test))
  }
  override def createAssetEntry(id: String) = ServiceCall { request =>
    val test:String = "test"
    val ref = persistentEntityRegistry.refFor[MicroserviceCalEntity](test)
    ref.ask(Hello(test))
  }
// 1.2 The company wants to know when the store was open
  override def assetAvaliability(assetId: Int) = ServiceCall { request =>
    val test:String = "test"
    val ref = persistentEntityRegistry.refFor[MicroserviceCalEntity](test)
    //ref.ask(Hello(test)) // AssetAvaliabilityWrapper
    testAvaliability
  }
///////////

private def testAssets = 
    Future( List(Asset(1, "cab"), Asset(2, "bookstore")) )

private def testAsset = Future(
   Asset(2, "bookstore") )

private def testEntries = Future(
   List(Entry(1, 2, org.joda.time.DateTime.now(), org.joda.time.DateTime.now().plusMinutes(10)))
)

private def testAvaliability = Future(
  AssetAvaliabilityWrapper(1, 
  List(Avaliability(org.joda.time.DateTime.now(), org.joda.time.DateTime.now().plusMinutes(10)),
       Avaliability(org.joda.time.DateTime.now(), org.joda.time.DateTime.now().plusMinutes(10)))) )

  private def convertEvent(helloEvent: EventStreamElement[MicroserviceCalEvent]): api.AssetMessageChanged = {
    helloEvent.event match {
      case AssetMessageChanged(msg) => api.AssetMessageChanged(helloEvent.entityId, msg)
    }
  }
}
