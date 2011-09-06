// The purpose of this package is to contain a number of classes for fetcher (job source) types.
// There will probably be a factory method that iterates through all of the various
// fetcher types and spawns out a fetcher that returns all jobs to a massager.

// To be refactored and made more generic for readability.

import com.mongodb._
import com.mongodb.casbah.Imports._
import com.mongodb.casbah.commons.conversions.scala._
import com.mongodb.casbah.query

import java.net.{URLConnection, URL}
import scala.xml._



package code.redpanda.data_fetching {
   class Fetcher( val url: String, val fetch_type: String ){
     //def fetch() = {} // On a base fetcher, we get an empty list of jobs to massage
   }

  class RssFetcher( val r_url: String) extends Fetcher(r_url, "RSS"){
    def fetch():Elem =  {
      val n_url = new URL(url)
      val conn = n_url.openConnection
      XML.load(conn.getInputStream)
    }
  }

  class DiceFetcher( val sites : MongoCollection, val job_docs : MongoCollection ){

    def fetchJobs()
    {
      // Fetch all of the collection sites for our fetch group.
      val f_object = MongoDBObject("fetch_group" -> "dice")
      //sites.find(f_object).foreach { l =>
        println("Lets see what sites I got")
        for ( l <- sites.find(f_object)){
            println("ooohhh, a site of "+l("location").toString)
            if ( l("fetch_type").toString == "rss"){
              // Implement factory pattern and use it to fetch RSS feed and other based job listings
              println("Got an rss url")
              var j_link = l("url").toString
              val job_listing = (new RssFetcher(j_link)).fetch()
              println("Will parse job listings of "+job_listing.toString)
              for ( job_entry <- (job_listing\\"item")){
                  var title = (job_entry\"title").text
                  var link = (job_entry\"link").text
                  println("Getting entry of "+title+" while fetching "+link)
                  // Now fetch the URL in the site item and make that the body of the job entry
                  var j_url = new URL(link)
                // The below doesn't work.  Maybe the java classes don't like real URLs with form variables
                // and the like.  This needs to be figured out.
                  var j_body = XML.load(j_url.openConnection().getInputStream())
                  var j_save = MongoDBObject("title" -> title,
                                "link" -> link,
                                "body" -> j_body.toString());
                  job_docs.save(j_save)
              }
            }
      }
    }
  }
}
