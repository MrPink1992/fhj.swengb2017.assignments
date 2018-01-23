package at.fhj.swengb.apps.battleship.jfx

import java.net.URL
import java.util.ResourceBundle
import javafx.fxml.{FXML, Initializable}

class BattleShipCreditsFxController extends Initializable
{

  override def initialize(location: URL, resources: ResourceBundle): Unit =
  {
  }

  @FXML def returnToMain(): Unit = BattleShipFxApp.showScene(BattleShipFxApp.getWelcomeScene, BattleShipFxApp.getRootStage)
}
