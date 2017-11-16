package com.datatroniq.calendar.asset.impl

import com.lightbend.lagom.scaladsl.server.LocalServiceLocator
import com.lightbend.lagom.scaladsl.testkit.ServiceTest
import org.scalatest.{AsyncWordSpec, BeforeAndAfterAll, Matchers}
import com.datatroniq.calendar.asset.api._

class MicroserviceCalServiceSpec
    extends AsyncWordSpec
    with Matchers
    with BeforeAndAfterAll {

  private val server = ServiceTest.startServer(
    ServiceTest.defaultSetup
      .withCassandra(false)
  ) { ctx =>
    new MicroserviceCalApplication(ctx) with LocalServiceLocator
  }

  val assetService = server.serviceClient.implement[AssetService]

  override protected def afterAll() = server.stop()

  "MicroserviceCal Service" should {

    "say hello" in {
      assetService.getAllAssets().invoke().map { answer =>
        answer.head.name should ===("cab")
      }
    }

    "allow responding with a custom message" in {
      for {
        _ <- assetService.getAllAssets().invoke()
        answer <- assetService.getAllAssets().invoke()
      } yield {
        answer.last.name should ===("bookstore")
      }
    }
  }
}
