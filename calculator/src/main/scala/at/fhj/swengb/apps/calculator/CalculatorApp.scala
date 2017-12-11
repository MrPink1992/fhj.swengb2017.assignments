package at.fhj.swengb.apps.calculator

import java.net.URL
import java.util.ResourceBundle
import javafx.application.Application
import javafx.beans.property.{ObjectProperty, SimpleObjectProperty}
import javafx.fxml.{FXML, FXMLLoader, Initializable}
import javafx.scene.control.TextField
import javafx.scene.{Parent, Scene}
import javafx.stage.Stage

import scala.util.{Failure, Success}
import scala.util.control.NonFatal

object CalculatorApp {

  def main(args: Array[String]): Unit = {
    Application.launch(classOf[CalculatorFX], args: _*)
  }
}

class CalculatorFX extends javafx.application.Application {

  val fxml = "/at/fhj/swengb/apps/calculator/calculator.fxml"
  val css = "/at/fhj/swengb/apps/calculator/calculator.css"

  def mkFxmlLoader(fxml: String): FXMLLoader = {
    new FXMLLoader(getClass.getResource(fxml))
  }

  override def start(stage: Stage): Unit =
    try {
      stage.setTitle("Calculator")
      setSkin(stage, fxml, css)
      stage.show()
      stage.setMinWidth(stage.getWidth)
      stage.setMinHeight(stage.getHeight)
    } catch {
      case NonFatal(e) => e.printStackTrace()
    }

  def setSkin(stage: Stage, fxml: String, css: String): Boolean = {
    val scene = new Scene(mkFxmlLoader(fxml).load[Parent]())
    stage.setScene(scene)
    stage.getScene.getStylesheets.clear()
    stage.getScene.getStylesheets.add(css)
  }

}

class CalculatorFxController extends Initializable {

  val calculatorProperty: ObjectProperty[RpnCalculator] = new SimpleObjectProperty[RpnCalculator](RpnCalculator())

  def getCalculator() : RpnCalculator = calculatorProperty.get()

  def setCalculator(rpnCalculator : RpnCalculator) : Unit = calculatorProperty.set(rpnCalculator)

  @FXML var numberTextField : TextField = _

  override def initialize(location: URL, resources: ResourceBundle) = {

  }

  def sgn(): Unit = {
    getCalculator().push(Op(numberTextField.getText)) match {
      case Success(c) => setCalculator(c); numberTextField.setText(c.peek().asInstanceOf[Val].value.toString)
      case Failure(e) => setCalculator(RpnCalculator()); numberTextField.setText("Error") // show warning / error
    }
    getCalculator().stack foreach println
  }

  def btn0Clk(): Unit = setNumber("0")

  def btn1Clk(): Unit  = setNumber("1")

  def btn2Clk(): Unit  = setNumber("2")

  def btn3Clk(): Unit  = setNumber("3")

  def btn4Clk(): Unit  = setNumber("4")

  def btn5Clk(): Unit  = setNumber("5")

  def btn6Clk(): Unit  = setNumber("6")

  def btn7Clk(): Unit  = setNumber("7")

  def btn8Clk(): Unit  = setNumber("8")

  def btn9Clk(): Unit = setNumber("9")

  def btnMulClk(): Unit = setOp("*")

  def btnDivClk(): Unit = setOp("/")

  def btnPlsClk(): Unit = setOp("+")

  def btnMinClk(): Unit = setOp("-")

  def btnPtnClk(): Unit = {
    if (numberTextField.getText != "Error" && !numberTextField.getText.contains(".")) {
      numberTextField.setText(numberTextField.getText + ".")
    }
  }

  def btnSgnClk(): Unit = {
    if (numberTextField.getText != "Error"
      && numberTextField.getText != "0"
      && numberTextField.getText != "*"
      && numberTextField.getText != "/"
      && numberTextField.getText != "-"
      && numberTextField.getText != "+") {
      val dbl = numberTextField.getText.toDouble * -1
      numberTextField.setText(dbl.toString)
    }
  }

  def btnCClk(): Unit = {
    if (numberTextField.getText == "0") {
      setCalculator(RpnCalculator())
    } else {
      numberTextField.setText("0")
    }
  }

  private def setOp(op: String): Unit = {
    if (numberTextField.getText != "Error") {
      numberTextField.setText(op)
    }
  }

  private def setNumber(s: String): Unit = {
    if (numberTextField.getText == "Error"
      || numberTextField.getText == "0"
      || numberTextField.getText == "*"
      || numberTextField.getText == "/"
      || numberTextField.getText == "-"
      || numberTextField.getText == "+") {
      numberTextField.setText(s)
    } else {
      numberTextField.setText(numberTextField.getText + s)
    }
  }

}