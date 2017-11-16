package com.datatroniq.calendar.asset.impl

import com.lightbend.lagom.scaladsl.persistence.ReadSideProcessor.ReadSideHandler
import com.lightbend.lagom.scaladsl.persistence._
import com.lightbend.lagom.scaladsl.persistence.{
  AggregateEventTag,
  EventStreamElement,
  ReadSideProcessor
}
import com.datatroniq.calendar.asset.impl._
import com.datatroniq.calendar.asset.api._

import scala.concurrent.{ExecutionContext, Future}
import scala.concurrent.ExecutionContext.Implicits.global
import _root_.slick.jdbc.JdbcBackend.Database
import _root_.slick.driver.JdbcProfile
import _root_.slick.driver.PostgresDriver.api._
import _root_.slick.model._
import _root_.slick.jdbc.meta.MTable

import com.github.tototoshi.slick.PostgresJodaSupport._
import com.lightbend.lagom.scaladsl.persistence.slick._
import org.joda.time.DateTime

trait Tables {

  val profile: JdbcProfile
  import profile.api._
  implicit val ec: ExecutionContext
  class Assets(tag: Tag) extends Table[Asset](tag, "assets") {
    def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
    def name = column[String]("name")
    def * = (id.?, name) <> (Asset.tupled, Asset.unapply)
  }
  lazy val assets: TableQuery[Assets] = TableQuery[Assets]

  def assetCreate(a: Asset): DBIO[Asset] = (assets returning assets) += Asset(a.id, a.name)
  def assetUpdate(id: Int, assetToUpdate: Asset): DBIO[_] = {
    val q: Query[Assets, Asset, Seq] = assets.filter(_.id === id)
    for {
      select <- q.result
      updated <- select.headOption match {
        case Some(asset) =>
          q.update(assetToUpdate.copy(id = Some(id) ))
        case None =>
          assets += Asset(assetToUpdate.id, assetToUpdate.name)
      }
    } yield updated
  }
  def selectAssets() = assets.result
  def selectAsset(id: Int) = assets.filter(_.id === id).result
  def assetRemove(id: Int): DBIO[_] = assets.filter(_.id === id).delete

//case class Entry(id:Int, asset_id: Int, from: DateTime, end: DateTime)
  class Entries(tag: Tag) extends Table[Entry](tag, "entries") {
    def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
    def asset_id = column[Int]("asset_id")
    def name = column[String]("name")
    def from = column[org.joda.time.DateTime]("from")
    def end = column[org.joda.time.DateTime]("end")

    def asset =
      foreignKey("ASSET_FK", asset_id, assets)(
        _.id,
        onUpdate = ForeignKeyAction.Restrict,
        onDelete = ForeignKeyAction.Cascade)
    def * = (id.?, asset_id, name, from, end) <> (Entry.tupled, Entry.unapply)
  }
  lazy val entries: TableQuery[Entries] = TableQuery[Entries]

  def entryCreate(e: Entry): DBIO[_] =
    (entries returning entries) += Entry(e.id, e.asset_id, e.name, e.from, e.end)
  def getEntry(id: Int) = entries.filter(_.id === id)
  def getEntriesByAsset(asset_id: Int) = entries.filter(_.asset_id === asset_id)
  def selectEntries() = entries.result
  def selectEntry(id: Int) = entries.filter(_.id === id).result
  def selectEntryByAsset(asset_id: Int) =
    entries.filter(_.asset_id === asset_id).result
  def entryRemove(id: Int): DBIO[_] = entries.filter(_.id === id).delete
  def entryUpdate(id: Int, entryToUpdate: Entry): DBIO[_] = {
    val q: Query[Entries, Entry, Seq] = entries.filter(_.id === id)
    for {
      select <- q.result
      updated <- select.headOption match {
        case Some(entry) =>
          q.update(entryToUpdate.copy(id = Some(id) ))
        case None =>
          entries += Entry(entryToUpdate.id,
                           entryToUpdate.asset_id,
                           entryToUpdate.name,
                           entryToUpdate.from,
                           entryToUpdate.end)
      }
    } yield updated
  }
  def testStub[T](e: EventStreamElement[T]): DBIO[_] =
    entries.filter(_.id === 1).delete

  def createAllTable: DBIO[_] = MTable.getTables.flatMap { tables =>
      if (!tables.exists(_.name.name == "assets")) {
        DBIO.seq(assets.schema.create,
        entries.schema.create)
      } else {
        DBIO.successful(())
      }
    }.transactionally

}

object MicroserviceCalEntityRepository {
  def apply(db: Database, profile: JdbcProfile)(implicit ec: ExecutionContext) = new MicroserviceCalEntityRepository(db, profile)
  def processor(readSide: SlickReadSide, db: Database, profile: JdbcProfile) = new MicroserviceCalEntityProcessor(
      readSide,
      db,
      profile)

  class MicroserviceCalEntityProcessor(
      readSide: SlickReadSide,
      db: Database,
      val profile: JdbcProfile)(implicit val ec: ExecutionContext)
      extends ReadSideProcessor[MicroserviceCalEvent]
      with Tables {
    def buildHandler(): ReadSideHandler[MicroserviceCalEvent] =
      readSide
        .builder[MicroserviceCalEvent]("test-entity-read-side")
        .setGlobalPrepare(createAllTable)
        .setEventHandler(assetCreatedOp)
        .setEventHandler(assetUpdatedOp)
        .setEventHandler(assetDeletedOp)
        .setEventHandler(assetEntryCreatedOp)
        .setEventHandler(assetEntryUpdatedOp)
        .setEventHandler(assetEntryDeletedOp)
        .build()

    def aggregateTags: Set[AggregateEventTag[MicroserviceCalEvent]] =
      Set(MicroserviceCalEvent.Tag)

    def assetCreatedOp(event: EventStreamElement[AssetCreated]) =
      testStub(event)
    def assetUpdatedOp(event: EventStreamElement[AssetUpdated]) =
      testStub(event)
    def assetDeletedOp(event: EventStreamElement[AssetDeleted]) =
      testStub(event)
    def assetEntryCreatedOp(event: EventStreamElement[AssetEntryCreated]) =
      testStub(event)
    def assetEntryUpdatedOp(event: EventStreamElement[AssetEntryUpdated]) =
      testStub(event)
    def assetEntryDeletedOp(event: EventStreamElement[AssetEntryDeleted]) =
      testStub(event)
  }
}

class MicroserviceCalEntityRepository(db: Database, val profile: JdbcProfile)(
    implicit val ec: ExecutionContext)
    extends Tables {
  import profile.api._
  def getAppendCount(id: Int): Future[Option[Int]] = db.run {
    assets
      .filter(_.id === id)
      .map(_.id)
      .result
      .headOption
  }
}
