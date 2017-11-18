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

  def assetUpdate(id: Int, assetToUpdate: Asset): DBIO[Asset] = {
    val q: Query[Assets, Asset, Seq] = assets.filter(_.id === id)
    for {
      select <- q.result
      updated <- select.headOption match {
        case Some(asset) =>
          q.update(assetToUpdate.copy(id = Some(id) ))
        case None =>
          assets += Asset(assetToUpdate.id, assetToUpdate.name)
      }
    } yield assetToUpdate
  }

  def assetRemove(id: Int): DBIO[Int] = assets.filter(_.id === id).delete

  def selectAssets(): DBIO[Seq[Asset]] = assets.result
  
  def selectAsset(id: Int): DBIO[Option[Asset]] = assets.filter(_.id === id).result.headOption
  

//case class Entry(id: Option[Int] = None, asset_id: Int, name: String, startDateUtc: DateTime, endDateUtc: DateTime, 
//  duration: Int, isAllDay: Boolean = false, 
//  isRecuring: Boolean = false, recurrencePattern: String = "")

  class Entries(tag: Tag) extends Table[Entry](tag, "entries") {
    def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
    def asset_id = column[Int]("asset_id")
    def name = column[String]("name")
    def startDateUtc = column[org.joda.time.DateTime]("startDateUtc")
    def endDateUtc = column[org.joda.time.DateTime]("endDateUtc")
    def duration = column[Int]("duration")
    def isAllDay = column[Boolean]("isAllDay")
    def isRecuring = column[Boolean]("isRecuring")
    def recurrencePattern = column[String]("recurrencePattern")
    def asset =
      foreignKey("ASSET_FK", asset_id, assets)(
        _.id,
        onDelete = ForeignKeyAction.Cascade)
    def * = (id.?, asset_id, name, startDateUtc, endDateUtc,duration, isAllDay, isRecuring, recurrencePattern) <> (Entry.tupled, Entry.unapply)
  }
  lazy val entries: TableQuery[Entries] = TableQuery[Entries]

//case class EntryException(id: Option[Int] = None, entry_id: Int, startDateUtc: DateTime, endDateUtc: DateTime)
  class EntryExceptions(tag: Tag) extends Table[EntryException](tag, "entry_exceptions") {
    def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
    def entry_id = column[Int]("entry_id")
    def startDateUtc = column[org.joda.time.DateTime]("startDateUtc")
    def endDateUtc = column[org.joda.time.DateTime]("endDateUtc")

    def entriesFk =
      foreignKey("ENTRY_EXECPT_FK", entry_id, entries)(
        _.id,
        onDelete = ForeignKeyAction.Cascade)
    def * = (id.?, entry_id, startDateUtc, endDateUtc) <> (EntryException.tupled, EntryException.unapply)
  }
  lazy val entry_exceptions: TableQuery[EntryExceptions] = TableQuery[EntryExceptions]


/*******
 *  Entry exceptions
 **/
  def getEntryException(id: Int): DBIO[Option[EntryException]] = entry_exceptions.filter(_.id === id).result.headOption

  def entryExceptionCreate(e: EntryException): DBIO[EntryException] =
    (entry_exceptions returning entry_exceptions) += e

  def entryExceptionUpdate(id: Int, entryExceptionToUpdate: EntryException): DBIO[EntryException] = {
    val q: Query[EntryExceptions, EntryException, Seq] = entry_exceptions.filter(_.id === id)
    for {
      select <- q.result
      updated <- select.headOption match {
        case Some(entryException) =>
          q.update(entryException.copy(id = Some(id) ))
        case None =>
          entry_exceptions += entryExceptionToUpdate.copy(id = None)
      }
    } yield entryExceptionToUpdate
  }

  def removeEntryException(id: Int): DBIO[Int] = 
    entry_exceptions.filter(_.id === id).delete

  def getEntryExceptionsByEntry(entry_id: Int): DBIO[Seq[EntryException]] = 
    entry_exceptions.filter(_.entry_id === entry_id).result
  
/*******
 *  Entries
 *****/
  def getEntry(id: Int): DBIO[Option[Entry]] = entries.filter(_.id === id).result.headOption  

  def getEntriesByAsset(asset_id: Int): DBIO[Seq[Entry]] = entries.filter(_.asset_id === asset_id).result

  def selectEntries(): DBIO[Seq[Entry]] = entries.result
  
  def selectEntry(id: Int): DBIO[Option[Entry]] = entries.filter(_.id === id).result.headOption
  
  def selectEntryByAsset(asset_id: Int): DBIO[Seq[Entry]] =
    entries.filter(_.asset_id === asset_id).result
  

  def entryCreate(e: Entry): DBIO[Entry] =
    (entries returning entries) += e
  
  def entryUpdate(id: Int, entryToUpdate: Entry): DBIO[Entry] = {
    val q: Query[Entries, Entry, Seq] = entries.filter(_.id === id)
    for {
      select <- q.result
      updated <- select.headOption match {
        case Some(entry) =>
          q.update(entryToUpdate.copy(id = Some(id) ))
        case None =>
          entries += entryToUpdate.copy(id = None)
      }
    } yield entryToUpdate
  }
  def entryRemove(id: Int): DBIO[Int] = entries.filter(_.id === id).delete


  def createAllTable: DBIO[_] = MTable.getTables.flatMap { tables =>
      if (!tables.exists(_.name.name == "assets")) {
        DBIO.seq(
          assets.schema.create,
          entries.schema.create,
          entry_exceptions.schema.create)
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
        .build()

    def aggregateTags: Set[AggregateEventTag[MicroserviceCalEvent]] =
      Set(MicroserviceCalEvent.Tag)
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
