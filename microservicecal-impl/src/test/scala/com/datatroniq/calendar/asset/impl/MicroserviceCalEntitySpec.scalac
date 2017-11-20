package com.datatroniq.calendar.asset.impl

import akka.actor.ActorSystem
import akka.testkit.TestKit
import com.lightbend.lagom.scaladsl.testkit.PersistentEntityTestDriver
import com.lightbend.lagom.scaladsl.playjson.JsonSerializerRegistry
import org.scalatest.{BeforeAndAfterAll, Matchers, WordSpec}

class MicroserviceCalEntitySpec
    extends WordSpec
    with Matchers
    with BeforeAndAfterAll {

  private val system = ActorSystem("MicroserviceCalEntitySpec",
                                   JsonSerializerRegistry.actorSystemSetupFor(
                                     MicroserviceCalSerializerRegistry))

  override protected def afterAll(): Unit = {
    TestKit.shutdownActorSystem(system)
  }

  private def withTestDriver(
      block: PersistentEntityTestDriver[MicroserviceCalCommand[_],
                                        MicroserviceCalEvent,
                                        MicroserviceCalState] => Unit): Unit = {
    val driver = new PersistentEntityTestDriver(system,
                                                new MicroserviceCalEntity,
                                                "microservice-cal-1")
    block(driver)
    driver.getAllIssues should have size 0
  }

  "MicroserviceCal  entity" should {

    "say hello by default" in withTestDriver { driver =>
      val outcome = driver.run(Hello("Taxicab"))
      outcome.replies.toString should include("Taxicab")
    }

    "allow updating the greeting message" in withTestDriver { driver =>
      val outcome1 = driver.run(UseAssetMessage("Hi"))
      outcome1.events should contain only AssetMessageChanged("Hi")
      val outcome2 = driver.run(Hello("Taxicab"))
      outcome2.replies.toString should include("Taxicab")
    }

  }
}
