import com.mongodb.casbah.Imports._
import com.mongodb.casbah.commons.conversions.scala._
import com.mongodb.casbah.query

import java.net.{URLConnection, URL}
import scala.xml._
import Stream._


object redpanda{

  def main(args: Array[String]) {
  println("Hey")
  startMongo
  }
  

}

case class Persistance {}

sealed trait MongoDBPersistence extends Persistence {
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



trait Engine{}
// Sources to pull data from.  dice, twitter, etc...maybe some sort of web crawler???
/*
 *
  
*/
class Source {

  def fetchJob(uri: String ): Stream(Any) = {
    // lets see what my quirky mind puts together
    // long term I don't know if there's any min/max amount of data that can be collected in 
    // one swoop.  Is a stream bad idea?

    // set uri
    // visit location and grab xml seq
    val url = new URL(uri)
    val connection = url.openConnection
    XML.load(connection.getInputStream)
  }
}


/*
class LocationFeed < ActiveRecord::Base
class FetchFeed < ActiveRecord::Base
end

class Job < ActiveRecord::Base
end

  def fetch_job_feed( )

    ActiveRecord::Base.establish_connection(
      :adapter => "mysql2",
      :host => "localhost", 
      :database => "dice_job_management"
      )
      
    locations = LocationFeed.find(:all)
    locations.each do | loc |
      url = URI.parse(loc.url)
      req = Net::HTTP::Get.new(url.path)
      res = Net::HTTP.start(url.host,url.port) { | http| http.request(req) }
      feed_hash = Zlib::crc32(res.body)
      if FetchFeed.exists?(:fetch_hash => feed_hash)
        job_feed = FetchFeed.find_by_id(loc.id)
      else
        job_feed = FetchFeed.new
        job_feed.loc_id = loc.id
      end
        job_feed.fetch_hash = feed_hash
        job_feed.fetch_text = res.body
        job_feed.fetch_time = Time.now
        job_feed.save
        populate_job_data(res.body, job_feed.id)
    end
  end
  
  def populate_job_data( xml_body, feed_id )
    job_listing = XmlSimple.xml_in(xml_body)
    if job_listing['channel'][0].has_key?('item') 
      job_listing['channel'][0]['item'].each do | j | 
      link = j['link'][0]
      if !Job.exists?(:job_url => link)      
        job_entry = Job.new
        job_entry.fetch_id = feed_id
        job_entry.job_description = j['title']
        job_entry.job_url = link
# now fetch the html entry of the real job description
        url = URI.parse(link)
        req = Net::HTTP::Get.new(url.path)

        res = Net::HTTP.start(url.host,url.port) { | http| http.request(req) }
        job_entry.job_hash = Zlib::crc32(res.body)
        job_entry.job_text = res.body
        job_entry.job_fetch_time = Time.now
        job_entry.save
      end
    end
    end
  end
  
fetch_job_feed()
*/
