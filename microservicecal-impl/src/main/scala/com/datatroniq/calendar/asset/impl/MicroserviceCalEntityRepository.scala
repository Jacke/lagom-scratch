package com.datatroniq.calendar.asset.impl

import com.lightbend.lagom.scaladsl.persistence.ReadSideProcessor.ReadSideHandler
import com.lightbend.lagom.scaladsl.persistence.TestEntity.Evt
import com.lightbend.lagom.scaladsl.persistence.{ AggregateEventTag, EventStreamElement, ReadSideProcessor, TestEntity }

import scala.concurrent.{ ExecutionContext, Future }
import slick.jdbc.JdbcBackend.Database
import slick.jdbc.JdbcProfile
import org.joda.time.DateTime

trait Tables {

  val profile: JdbcProfile
  import profile.api._
  implicit val ec: ExecutionContext

	//case class Asset(id:Int, name: String)
  class Assets(tag: Tag) extends Table[Asset](tag, "assets") {
    def id = column[Int]("id", O.PrimaryKey)
    def name = column[String]("name")
    def * = (id, name) <> (Asset.tupled, Asset.unapply)
  }
  lazy val assets: TableQuery[Assets] = TableQuery[Assets]
  def createTable: DBIO[_] = assets.schema.create
  def assetCreate(): DBIO[_] = assets += Asset(id, diff)
  def assetUpdate(id: Int, assetToUpdate: Asset): DBIO[_] = {
    val q: Query[Assets, Asset, Seq] = assets.filter(_.id === id)
    for {
      select <- q.result
      updated <- select.headOption match {
        case Some(asset) =>
          q.update(asset.copy(id = id))
        case None =>
          assets += Asset(id, diff)
      }
    } yield updated
  }
  def assetRemove(id: String): DBIO[_] = assets.filter(_.id === id).remove


//case class Entry(id:Int, asset_id: Int, from: DateTime, end: DateTime)
  class Entries(tag: Tag) extends Table[Entry](tag, "entries") {
    def id = column[Int]("id", O.PrimaryKey)
    def asset_id = column[Int]("asset_id")
    def from = column[org.joda.time.DateTime]("from")
    def end = column[org.joda.time.DateTime]("end")
    def * = (id, asset_id, from, end) <> (Entry.tupled, Entry.unapply)
  }
  lazy val entries: TableQuery[Entries] = TableQuery[Entries]
  def createTable: DBIO[_] = entries.schema.create
  def entryCreate(): DBIO[_] = entries += Entry(id, diff)
  def getEntry(id: Int) = entries.filter(_.id === id)
  def getEntriesByAsset(asset_id: Int) = entries.filter(_.entries === entries)

  def entryUpdate(id: Int, entryToUpdate: Entry): DBIO[_] = {
    val q: Query[Entries, Entry, Seq] = entries.filter(_.id === id)
    for {
      select <- q.result
      updated <- select.headOption match {
        case Some(entry) =>
          q.update(entryToUpdate.copy(id = id))
        case None =>
          entries += Entry(id, diff)
      }
    } yield updated
  }
  def entryRemove(id: String): DBIO[_] = entries.filter(_.id === id).remove



}

object MicroserviceCalEntityRepository {

  class MicroserviceCalEntityProcessor(readSide: SlickReadSide, db: Database, val profile: JdbcProfile)(implicit val ec: ExecutionContext)
    extends ReadSideProcessor[MicroserviceCalEntity.Evt]
    with Tables {

    def buildHandler(): ReadSideHandler[MicroserviceCalEntity.Evt] = readSide
      .builder[MicroserviceCalEntity.Evt]("test-entity-read-side")
      .setGlobalPrepare(createTable)
      .setEventHandler(updateCount)
      .build()

    def aggregateTags: Set[AggregateEventTag[Evt]] = MicroserviceCalEntity.Evt.aggregateEventShards.allTags

    def updateCount(event: EventStreamElement[MicroserviceCalEntity.Appended]) = countUpdate(event.entityId, 1)
  }
}

class MicroserviceCalEntityRepository(db: Database, val profile: JdbcProfile)(implicit val ec: ExecutionContext)
  extends Tables {

  import profile.api._

  def getAppendCount(id: String): Future[Long] = db.run {
    testCounts.filter(_.id === id)
      .map(_.count)
      .result
      .headOption
      .map(_.getOrElse(0l))
  }
}