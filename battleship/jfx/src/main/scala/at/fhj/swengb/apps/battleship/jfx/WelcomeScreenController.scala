package at.fhj.swengb.apps.battleship.jfx

import java.net.URL
import java.nio.file.{Files, Paths}
import java.util.ResourceBundle
import javafx.fxml.{FXML, FXMLLoader, Initializable}
import javafx.scene.{Parent, Scene}
import javafx.scene.control.{Button, Slider, TextArea}
import javafx.scene.layout.GridPane
import javafx.stage.Stage
import java.awt.event.ActionEvent

import at.fhj.swengb.apps.battleship.BattleShipProtocol.convert
import at.fhj.swengb.apps.battleship.BattleShipProtobuf
import at.fhj.swengb.apps.battleship.model.{BattleField, BattleShipGame, Fleet, FleetConfig}
import at.fhj.swengb.apps.battleship.model.{BattleField, BattleShipGame, Fleet, FleetConfig}


import scala.util.{Failure, Success, Try}

class WelcomeScreenController extends Initializable{

  override def initialize(url: URL, rb: ResourceBundle): Unit = initGame()

  private def initGame(): Unit = {
    println("hello")
  }














  @FXML def loadnewscene : Unit = {



    println("Fuck off, hardcore! ")


  }






}
