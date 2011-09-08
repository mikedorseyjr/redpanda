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
import scala.io._



package code.redpanda.data_fetching {

import code.redpanda.data_fetching.Fetcher.RssFetcher

// For now this base class defines a fetch and an xmlFetch.  I'd be much happier if I could come up with
// a base type for XML that had the XML parsing while also allowing me to return lists of some other type
// I may be using in the future.
abstract class Fetcher( val url: String, val fetch_type: String ){
     def fetch():List[String] // On a base fetcher, we get an empty list of jobs to massage
     def xmlFetch():Elem
   }

// This object is a factory object.  It is used for creating various types of fetchers.
  object Fetcher {

    // RssFetcher returns a fetcher that can return XML for parsing on fetch.
    private class RssFetcher( val r_url: String) extends Fetcher(r_url, "RSS"){

      // XML implementation that allows us to use the Scala XML classes for parsing
      override def xmlFetch():Elem =  {
        val n_url = new URL(r_url)
        val conn = n_url.openConnection
        XML.load(conn.getInputStream)
      }

      override def fetch():List[String] = { List("") } // No implementation here
    }

   // This method takes the fetch type and returns an appropriate fetcher for the type in particular.
    def createFetcher( fetch_type: String, url: String):Fetcher ={
      fetch_type match {
        case "rss" =>  new RssFetcher(url)
        case _ => sys.error("Unknown option.")
      }
    }
  }

  // This fetcher is defined for various sites we may be acquiring job data from.  It is a base class that contains
  // an implementation that all of the other site fetchers will use.  This class will probably end up abstract after
  // a while and moved into a factory method.  Don't know yet though.
  class SiteFetcher( val j_sites : MongoCollection, val j_docs : MongoCollection, val f_group: String, val f_types : List[String] ){
    val fetch_group = f_group;
    val fetch_types = f_types;

    def fetchJobs()
    {
      // Fetch all of the collection sites for our fetch group.
      val f_object = MongoDBObject("fetch_group" -> f_group)
      // Iterate through all of the sites
      for ( l <- j_sites.find(f_object)){
          //if ( l("fetch_type").toString == "rss"){
          if ( fetch_types.contains(l("fetch_type").toString)){
            // Process through the acceptable fetch types that our class uses and move all of that data as well.
            job_process(l)
          }
      }
    }

    // Empty implementation.  Every subclass will implement their own.
    def job_process( entry: MongoDBObject )
    {

    }

  }

  // This fetcher is used for processing Dice data
  class DiceFetcher( val sites : MongoCollection, val job_docs : MongoCollection ) extends SiteFetcher(sites, job_docs, "dice", List("rss")){

    // Note for functionality, we only have to define how we process a job and add it to our job_docs mongo collection.

    override def job_process( entry: MongoDBObject )
    {
      val l = entry;
      // Implement factory pattern and use it to fetch RSS feed and other based job listings
      var j_link = l("url").toString
      val fetch_type = l("fetch_type").toString
      val r_fetch = Fetcher.createFetcher(fetch_type, j_link)
      val job_listing = r_fetch.xmlFetch()
      for ( job_entry <- (job_listing\\"item")){
          var title = (job_entry\"title").text
          var link = (job_entry\"link").text
          // Now fetch the URL in the site item and make that the body of the job entry
          var j_url = new URL(link)
        // The below doesn't work.  Maybe the java classes don't like real URLs with form variables
        // and the like.  This needs to be figured out.

          var j_body = scala.io.Source.fromURL(j_url).mkString
          var j_save = MongoDBObject("title" -> title,
                        "link" -> link,
                        "body" -> j_body.toString());
          job_docs.save(j_save)
      }
    }
  }
}
