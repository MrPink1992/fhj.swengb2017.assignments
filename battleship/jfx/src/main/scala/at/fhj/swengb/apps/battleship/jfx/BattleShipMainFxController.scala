package at.fhj.swengb.apps.battleship.jfx

import java.net.URL
import java.util.{Optional, ResourceBundle}
import javafx.fxml.{FXML, Initializable}
import javafx.scene.Scene
import javafx.scene.control.Button

import at.fhj.swengb.apps.battleship.model.{BattleShipJukeBox, FleetConfig}

class BattleShipMainFxController extends Initializable {

  override def initialize(location: URL, resources: ResourceBundle): Unit = {
  }


  @FXML def onStartGame(): Unit = { BattleShipFxApp.showScene(BattleShipFxApp.getGameScene, BattleShipFxApp.getRootStage) }

  @FXML def onShowHighscore(): Unit = { BattleShipFxApp.showScene(BattleShipFxApp.getHighscoreScene, BattleShipFxApp.getRootStage) }

  @FXML def onShowCredits(): Unit = { BattleShipFxApp.showScene(BattleShipFxApp.getCreditsScene, BattleShipFxApp.getRootStage) }
}
