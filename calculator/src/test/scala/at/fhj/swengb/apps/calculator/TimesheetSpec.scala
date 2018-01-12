package at.fhj.swengb.apps.calculator

import org.scalatest.WordSpecLike
import java.nio.file.Files.readAllLines
import java.nio.file.{Files, Path, Paths}

import scala.collection.JavaConverters._

// import scala.util.{Failure, Success, Try}


/**
  * A specification for a Timesheet.
  */


class TimesheetSpec extends WordSpecLike {


  val expectedContent =
    """== Time expenditure: Calculator assignment
      |
      |[cols="1,1,4", options="header"]
      |.Time expenditure
      ||===
      || Date
      || Hours
      || Description
      |
      || 29.11.17
      || 1
      || Review of this and that
      |
      || 30.11.17
      || 5
      || implemented css
      |
      || 11.07.17
      || 2
      || fixed bugs
      |
      ||===""".stripMargin

  /**
  def f(alleZeilenDieserDatei: Seq[String]): String = {
    // alleZeilenDieserDatei.mkString("\n")
    // alleZeilenDieserDatei.foldLeft("")((acc, e) => acc + e + "\n")

  } **/

  "timesheet-calculator " should{
    "not be the same like content" in{
      
      // we need code for reading the file

      val p: Path = Paths.get("/Users/macs/workspace/fhj.swengb2017.assignments/calculator/timesheet-calculator.adoc")

      val alleZeilenDieserDatei: Seq[String] = Files.readAllLines(p).asScala

      // alleZeilenDieserDatei foreach println

      // we need code to compare the file



      assert(alleZeilenDieserDatei.mkString("\n") == expectedContent)
    }

  }




}
