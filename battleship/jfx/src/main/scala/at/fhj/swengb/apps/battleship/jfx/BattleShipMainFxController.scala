package at.fhj.swengb.apps.battleship.jfx

import java.net.URL
import java.util.ResourceBundle
import javafx.fxml.{FXML, Initializable}

class BattleShipMainFxController extends Initializable {

  override def initialize(location: URL, resources: ResourceBundle): Unit = {
  }


  @FXML def onStartGame(): Unit = {
    BattleShipFxApp.showScene(BattleShipFxApp.getGameScene, BattleShipFxApp.getRootStage)
  }

  @FXML def onShowHighscore(): Unit = {
    BattleShipFxApp.showScene(BattleShipFxApp.getHighscoreScene, BattleShipFxApp.getRootStage)
  }

  @FXML def onShowCredits(): Unit = {
    BattleShipFxApp.showScene(BattleShipFxApp.getCreditsScene, BattleShipFxApp.getRootStage)
  }
}
