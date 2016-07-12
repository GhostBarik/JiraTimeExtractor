package webapp

import org.scalajs.jquery.JQuery

import scala.scalajs.js.{Date => JsDate}
import scalatags.JsDom.all._

/**
  * Auxilary methods.
  */
object Utils {

  /**
    * Typed representation of parsed json object.
    */
  case class TimeRecord(id: Int, startDate: Date, issueKey: String, userId: String, duration: Int)

  /**
    * Exception, that is thrown in the case where json structure doesn't conform to the JIRA output format.
    */
  class JsonStructureException extends Exception

  /**
    * Implicit wrapper, extending JQuery object by new utility method -> getValue().
    * This method simplifies extracting text value from some html elements, such as <textarea/>.
    *
    * @param jqueryObject top-level object ($) from JQuery library
    */
  implicit class JQueryExtension(jqueryObject: JQuery) {
    def getValue: String = jqueryObject.`val`().asInstanceOf[String]
  }

  implicit val pairOfDatesOrdering = new Ordering[(Date, Date)] {
    override def compare(x: (Date, Date), y: (Date, Date)): Int =
      if (x._1 != x._2) x._1 compareTo x._2 else y._1 compareTo y._2
  }

  /**
    * Immutable and simplified container for dates.
    */
  case class Date(day: Int, month: Int, year: Int) extends Ordered[Date] {

    // dates can be compared
    override def compare(that: Date) = {

      if (this.year != that.year) {
        this.year - that.year
      } else if (this.month != that.month) {
        this.month - that.month
      } else {
        this.day - that.day
      }
    }

    /**
      * Next properties are computed lazily ("on demand"), because of more expensive calculations.
      */

    // we can calculate ("on demand") a concrete number of the week for current date
    lazy val weekNumber = {

      // algorithm was taken from Stackoverflow:
      // => http://stackoverflow.com/questions/6117814/get-week-of-year-in-javascript-like-in-php

      // Copy date so don't modify original
      val d = toJsDate(this)
      d.setHours(0,0,0)

      // Set to nearest Thursday: current date + 4 - current day number
      // Make Sunday's day number 7
      d.setDate(d.getDate + 4 - (if (d.getDay != 0) d.getDay else 7))

      // Get first day of year
      val yearStart = new JsDate(d.getFullYear, 0, 1)

      // Calculate full weeks to nearest Thursday
      scala.math.ceil((((d.valueOf - yearStart.valueOf) / 86400000) + 1) / 7).toInt
    }

    // we can also calculate ("on demand") first and last day of the current week
    lazy val weekBounds: (Date, Date) = {

      val correctedDate = toJsDate _ andThen toImmutableDate

      def daysStepBy(step: Int => Int) =
        Stream.iterate(this.day)(step)
          .map(Date(_, this.month, this.year))
          .map(correctedDate(_))

      def calculateBoundDate(s1: Stream[Date]) =
        s1.takeWhile{_.weekNumber == this.weekNumber}

      val bounds =
        calculateBoundDate(daysStepBy(_ - 1)).reverse ++
        calculateBoundDate(daysStepBy(_ + 1))

      (bounds.head, bounds.last)
    }

    // pretty printing for date
    override def toString: String = {

      // add zeroes from left: "4" => "04", "9" => "09" etc.
      def fillZeroes(s: String) = s.toString.reverse.padTo(2, '0').reverse

      Seq(fillZeroes(day.toString), fillZeroes(month.toString), year).mkString(".")
    }
  }

  /**
    * Conversions between JsDate and Date.
    */
  def toImmutableDate(date: JsDate) = Date(date.getDate, date.getMonth+1, date.getFullYear)
  def toJsDate(date: Date): JsDate = new JsDate(date.year, date.month-1, date.day)

  /**
    * simple factory for creating html tables from list of [[String]], using `scalatags` library
    *
    * @param header text values for table heading columns
    * @param content text values for each cell in table (2-dimensional list - [rows, cols])
    * @return [[org.scalajs.dom.Element]] - root hmtl element of created table (DOM)
    */
  def createTable(header: Seq[String], content: Seq[Seq[String]]) = {

    assert(content.isEmpty || content.forall(_.length == header.length))

    table(`class` := "table",
      thead(
        tr(header.map(col =>
          td(strong(col)))
        )
      ),
      tbody(
        content.map(row =>
          tr(row.map(col =>
            td(col))
          )
        )
      )
    )
  }
}
