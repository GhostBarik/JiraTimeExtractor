package webapp

import org.scalajs.jquery.JQuery

import scalatags.JsDom.all._

/**
  * Auxilary methods
  */
object Utils {

  /**
    * simple factory for creating html tables from list of [[String]], using `scalatags` library
    *
    * @param header text values for table heading columns
    * @param content text values for each cell in table (2-dimensional list - [rows, cols])
    *
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

  /**
    * typed representation of parsed json object
    */
  case class TimeRecord(id: Int, startDate: String, issueKey: String, userId: String, duration: Int)

  /**
    * exception, that is thrown in the case where json structure doesn't conform to the JIRA output format
    */
  case class JsonStructureException() extends Exception

  /**
    * Implicit wrapper, extending JQuery object by new utility method -> getValue()
    * This method simplifies extracting text value from some html elements, such as <textarea/>
    *
    * @param jqueryObject top-level object ($) from JQuery library
    */
  implicit class JQueryExtension(jqueryObject: JQuery) {
    def getValue: String = jqueryObject.`val`().asInstanceOf[String]
  }
}
