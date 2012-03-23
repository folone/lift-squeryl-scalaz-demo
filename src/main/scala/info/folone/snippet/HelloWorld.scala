package info.folone
package snippet

import scala.xml.{ NodeSeq, Text }
import net.liftweb.util._
import net.liftweb.common._
import java.util.Date
import lib._
import Helpers._
import model._

class HelloWorld {
  lazy val date: Box[Date] = DependencyFactory.inject[Date] // inject the date

  // replace the contents of the element with id "time" with the date
  def howdy =
    "#userdata *" #> (User.currentUser match {
        case Full(_) ⇒
          <img src="http://spl.smugmug.com/Humor/Lambdacats/typeerror/960526360_mnbFT-O-2.jpg" />
        case _       ⇒ <span>{ date.map(_.toString) }</span>
  })

  /*
   lazy val date: Date = DependencyFactory.time.vend // create the date via factory

   def howdy = "#time *" #> date.toString
   */
}

