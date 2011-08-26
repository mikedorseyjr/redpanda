import com.mongodb.casbah.Imports._
import com.mongodb.casbah.commons.conversions.scala._
import com.mongodb.casbah.query


object redpanda{

  def main(args: Array[String]) {
  println("Hey")
  startMongo
  }

  def startMongo = {
    val mongoConn = MongoConnection()
    val db = mongoConn("forest")
    db.dropDatabase()
    println("Mongo Started")
    val mongo = db("events")

    val eventBuilder = MongoDBObject.newBuilder
    eventBuilder += "name" -> "Jamal Burgess"
    eventBuilder += "orientation" -> "Anew"

    val mongoEvent = eventBuilder.result
    println("Mongo Events: %s".format(mongoEvent))
    mongo.insert(mongoEvent)
  }


}

case class Persistance {}
sealed trait MongoDBPersistence {}
trait Engine{}
// Sources to pull data from.  dice, twitter, etc...maybe some sort of web crawler???
/*
 *
  
*/
class Source {}

