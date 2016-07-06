package webapp

import org.scalajs.dom
import org.scalajs.dom.document
import org.scalajs.jquery.jQuery
import upickle.Js.{Arr => JsArray, Num => JsNumber, Obj => JsObject, Str => JsString}
import upickle.{Invalid, json}
import webapp.Utils._

import scala.scalajs.js.JSApp
import scala.util.{Failure, Success, Try}

/**
  * Application object
  */
object Main extends JSApp {

  /**
    * main entry point to JS application
    */
  def main() {

    // callback for button onClick() event
    val clickHandler = (event: dom.Event) => {

      // parse input text and analyze results
      parseAndCalculate(jq("textarea").getValue) match {

        case Success(parsed) => showResult(parsed)
                                showSuccessMessage()

        case Failure(e) => showErrorMessage(e)
      }
    }

    // assign callback to button
    jq("extractButton").on("click", clickHandler)
  }

  /**
    * parse input json string
    * result is wrapped in `Try` functor to simplify exception handling
    */
  def parseAndCalculate(jsonData: String): Try[Seq[TimeRecord]] = {

    val escaped = jsonData.replaceAll("\\s+","")

    println("escaped json: " + escaped)

    // try to parse input string
    val parsed = Try(json.read(escaped))

    // in the case of success => extract data from inner array
    parsed.map{ jsonTree =>

      println("parsed json tree: " + jsonTree.toString)

      val JsArray(inner @ _*) = jsonTree
      val JsArray(data @ _*) = inner.head

      // convert parsed Json array into list of TimeRecords
      data.collect{ case obj: JsObject => extractTimeRecord(obj) }

    }.recover{ // convert exceptions

      case e: Invalid.Json => throw e // json parsing exception
      case _ => throw new JsonStructureException // incorrect json structure exception
    }
  }

  /**
    * update view by results of parsing
    */
  def showResult(results: Seq[TimeRecord]) {

    // show all tabs
    jq("tabs").fadeIn(duration = 1000)

    def sumUpHours(records: Seq[TimeRecord]) = records.map(_.duration).sum.toDouble / 3600.0
    def sumUpDays(records: Seq[TimeRecord]) = sumUpHours(records) / 8.0
    def format(d: Double) = "%1.1f".format(d)

    // calculate total time
    val totalHours = sumUpHours(results)
    val totalDays = sumUpDays(results)

    // create table of total calculated time
    val totalTime = createTable(
      List("Hours (H)", "Days (MD)"),
      List(
        List(
          "%1.1f".format(totalHours),
          "%1.1f".format(totalDays)
        )
      )
    )

    // group results by applying given selector
    def grouped(selector: TimeRecord => String) = {
      results
        .groupBy(selector)
        .map{ case (k,v) =>
          List(k,
            format(sumUpHours(v)),
            format(sumUpDays(v)))
        }.toList
    }

    // group records (by users and by issues) and create table
    val groupedByUsers = createTable(
      List("User", "Hours (H)", "Days (MD)"),
      grouped(_.userId)
    )

    val groupedByIssues = createTable(
      List("Task", "Hours (H)", "Days (MD)"),
      grouped(_.issueKey)
    )

    // insert calculated tables in DOM
    insertNewTable("menu1", totalTime.render)
    insertNewTable("menu2", groupedByUsers.render)
    insertNewTable("menu3", groupedByIssues.render)
  }

  /**
    * cached map of html elements (for speeding up JQuery selectors)
    */
  val htmlCache = List(
    "tabs", "menu1", "menu2", "menu3",
    "textarea", "errorMessage", "extractButton",
    "successMessage", "warningMessage"
  ).map(id => id -> document.getElementById(id)).toMap

  /**
    * cached version of JQuery selector (to avoid overhead of element searching in DOM)
    */
  def jq(elem: String) = jQuery(htmlCache(elem))

  /**
    * insert table inside selected html node
    */
  def insertNewTable(id: String, newTable: dom.Element) {
    jq(id).empty() // delete old table
    jq(id).append(newTable) // insert new table
  }

  /**
    * hide result tabes and show error message in DOM
    */
  def showErrorMessage(exception: Throwable) {

    // hide all tabs
    jq("tabs").hide()

    // show appropriate message depending on the exception type
    exception match {
      case _: JsonStructureException => showWarningMessage()
      case _ => showErrorMessage()
    }
  }

  /**
    * converter from JavaScript object to TimeRecord value
    */
  def extractTimeRecord(obj : JsObject): TimeRecord = {

    val JsNumber(id) = obj("id")
    val JsString(startDate) = obj("startDate")
    val JsString(issueKey) = obj("issueKey")
    val JsString(userId) = obj("userId")
    val JsNumber(duration) = obj("duration")

    TimeRecord(id.toInt, startDate, issueKey, userId, duration.toInt)
  }

  def showSuccessMessage() {

    jq("errorMessage").hide()
    jq("warningMessage").hide()
    jq("successMessage").fadeIn(duration = 1000)
  }

  def showWarningMessage() {

    jq("successMessage").hide()
    jq("errorMessage").hide()
    jq("warningMessage").fadeIn(duration = 1000)
  }

  def showErrorMessage() {

    jq("successMessage").hide()
    jq("warningMessage").hide()
    jq("errorMessage").fadeIn(duration = 1000)
  }
}
