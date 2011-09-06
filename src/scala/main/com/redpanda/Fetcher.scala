// The purpose of this class is to be an abstract class for all fetcher types.
// There will probably be a factory method that iterates through all of the various
// fetcher types and spawns out a fetcher that returns all jobs to the primary queue.
// For now, this just provides a generic class type.  This could easily be a trait
// but the problem is, what class is ever going to have to fetch feed data and parse
// it.
import java.net.{URLConnection, URL}
import scala.xml._

   class Fetcher( val url: String, val fetch_type: String ){
     def fetch() = List() // On a base fetcher, we get an empty list of jobs to massage
   }

  class RssFetcher( val url: String, val fetch_type: String) extends Fetcher(url, fetch_type){
    def fetch():Elem =  {
      val n_url = new URL(url)
      val conn = n_url.openConnection
      XML.load(conn.getInputStream)
    }
  }
