package com.datatroniq.calendar.asset.impl

import com.lightbend.lagom.scaladsl.persistence.ReadSideProcessor.ReadSideHandler
import com.lightbend.lagom.scaladsl.persistence._
import com.lightbend.lagom.scaladsl.persistence.{ AggregateEventTag, EventStreamElement, ReadSideProcessor }
import com.datatroniq.calendar.asset.impl._
import com.datatroniq.calendar.asset.api._

import scala.concurrent.{ ExecutionContext, Future }
import _root_.slick.jdbc.JdbcBackend.Database
import _root_.slick.driver.JdbcProfile
import _root_.slick.driver.PostgresDriver.api._
import scala.concurrent.ExecutionContext.Implicits.global

//import com.github.tototoshi.slick.PostgresJodaSupport._
import com.lightbend.lagom.scaladsl.persistence.slick._
import org.joda.time.DateTime

class MicroserviceCalEntityRepository {
class Assets(tag: Tag) extends Table[Asset](tag, "assets") {
    def id = column[Int]("id", O.PrimaryKey)
    def name = column[String]("name")
    def * = (id, name) <> (Asset.tupled, Asset.unapply)
  }
  lazy val assets: TableQuery[Assets] = TableQuery[Assets]
  def createTableAssets: DBIO[_] = assets.schema.create
  def assetCreate(a: Asset): DBIO[_] = assets += Asset(a.id, a.name)
  def assetUpdate(id: Int, assetToUpdate: Asset): DBIO[_] = {
    val q: Query[Assets, Asset, Seq] = assets.filter(_.id === id)
    for {
      select <- q.result
      updated <- select.headOption match {
        case Some(asset) =>
          q.update(assetToUpdate.copy(id = id))
        case None =>
          assets += Asset(assetToUpdate.id, assetToUpdate.name)
      }
    } yield updated
  }
  def selectAssets = assets.result
  def selectAsset(id: Int) = assets.filter(_.id === id).result
  def assetRemove(id: Int): DBIO[_] = assets.filter(_.id === id).delete


//case class Entry(id:Int, asset_id: Int, from: DateTime, end: DateTime)
  class Entries(tag: Tag) extends Table[Entry](tag, "entries") {
    def id = column[Int]("id", O.PrimaryKey)
    def asset_id = column[Int]("asset_id")
    def name = column[String]("name")
    def from = column[Int]("from")
    def end = column[Int]("end")

    def asset = foreignKey("ASSET_FK", asset_id, assets)(_.id, onUpdate=ForeignKeyAction.Restrict, onDelete=ForeignKeyAction.Cascade)
    def * = (id, asset_id, name, from, end) <> (Entry.tupled, Entry.unapply)
  }
  lazy val entries: TableQuery[Entries] = TableQuery[Entries]
  def createTableEntries: DBIO[_] = entries.schema.create
  def entryCreate(e: Entry): DBIO[_] = entries += Entry(e.id, e.asset_id, e.name, e.from, e.end)
  def getEntry(id: Int) = entries.filter(_.id === id)
  def getEntriesByAsset(asset_id: Int) = entries.filter(_.asset_id === asset_id)
  def selectEntries = entries.result
  def selectEntry(id: Int) = entries.filter(_.id === id).result
  def selectEntryByAsset(asset_id: Int) = entries.filter(_.asset_id === asset_id).result
  def entryRemove(id: Int): DBIO[_] = entries.filter(_.id === id).delete
  def entryUpdate(id: Int, entryToUpdate: Entry): DBIO[_] = {
    val q: Query[Entries, Entry, Seq] = entries.filter(_.id === id)
    for {
      select <- q.result
      updated <- select.headOption match {
        case Some(entry) =>
          q.update(entryToUpdate.copy(id = id))
        case None =>
          entries += Entry(entryToUpdate.id, 
          	               entryToUpdate.asset_id, 
			           	   entryToUpdate.name, 
				           entryToUpdate.from, 
                           entryToUpdate.end)
      }
    } yield updated
  }
  def testStub[T](e: EventStreamElement[T]): DBIO[_] = entries.filter(_.id === 1).delete

}