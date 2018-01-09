package at.fhj.swengb.apps.battleship.jfx


import java.net.URL
import java.nio.file.{Files, Paths}
import java.util.ResourceBundle
import javafx.fxml.{FXML, FXMLLoader, Initializable}
import javafx.scene.{Camera, Cursor, Parent, Scene}
import javafx.scene.control.{Button, Slider, TextArea}
import javafx.scene.layout.GridPane
import javafx.stage.Stage
import java.awt.event.ActionEvent

import at.fhj.swengb.apps.battleship.BattleShipProtocol.convert
import at.fhj.swengb.apps.battleship.BattleShipProtobuf
import at.fhj.swengb.apps.battleship.model.{BattleField, BattleShipGame, Fleet, FleetConfig}
import at.fhj.swengb.apps.battleship.model.{BattleField, BattleShipGame, Fleet, FleetConfig}
import com.sun.javafx.sg.prism.NGNode

import scala.util.{Failure, Success, Try}

class WelcomeScreenController extends Initializable{

  override def initialize(url: URL, rb: ResourceBundle): Unit = initGame()

  private def initGame(): Unit = {
    println("hello")
  }














  @FXML def loadnewscene : Unit = {
   val n = new Stage()
    val o = (FXMLLoader.load[Parent](getClass.getResource("/at/fhj/swengb/apps/battleship/jfx/battleshipfx.fxml")))
    val s = new Scene(o)


    n.setResizable(false)
    n.setScene(s)
    n.show()





    println("Fuck off, hardcore! ")


  }






}
