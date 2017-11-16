package com.datatroniq.calendar.asset.impl

import com.lightbend.lagom.scaladsl.api.ServiceLocator
import com.lightbend.lagom.scaladsl.api.ServiceLocator.NoServiceLocator
import play.api.db.HikariCPComponents
import com.lightbend.lagom.scaladsl.persistence.jdbc.JdbcPersistenceComponents
import com.lightbend.lagom.scaladsl.server._
import com.lightbend.lagom.scaladsl.devmode.LagomDevModeComponents
import play.api.libs.ws.ahc.AhcWSComponents
import com.datatroniq.calendar.asset.api.AssetService
import com.lightbend.lagom.scaladsl.broker.kafka.LagomKafkaComponents
import com.softwaremill.macwire._
import com.lightbend.lagom.scaladsl.persistence.slick._
import _root_.slick.jdbc.JdbcBackend.Database

class MicroserviceCalLoader extends LagomApplicationLoader {

  override def load(context: LagomApplicationContext): LagomApplication =
    new MicroserviceCalApplication(context) {
      override def serviceLocator: ServiceLocator = NoServiceLocator
    }

  override def loadDevMode(context: LagomApplicationContext): LagomApplication =
    new MicroserviceCalApplication(context) with LagomDevModeComponents

  override def describeService = Some(readDescriptor[AssetService])
}

abstract class MicroserviceCalApplication(context: LagomApplicationContext)
    extends LagomApplication(context)
    with SlickPersistenceComponents
    with HikariCPComponents
    with LagomKafkaComponents
    with AhcWSComponents {
  // Bind the service that this server provides
  override lazy val lagomServer =
    serverFor[AssetService](wire[MicroserviceCalServiceImpl])
  // Register the JSON serializer registry
  override lazy val jsonSerializerRegistry = MicroserviceCalSerializerRegistry
  // Register the hello-lagom persistent entity
  persistentEntityRegistry.register(wire[MicroserviceCalEntity])
}
