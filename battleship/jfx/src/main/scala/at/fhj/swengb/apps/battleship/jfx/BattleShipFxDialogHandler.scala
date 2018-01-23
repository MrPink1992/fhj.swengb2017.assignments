package at.fhj.swengb.apps.battleship.jfx

import java.util.Optional
import javafx.fxml.FXMLLoader
import javafx.scene.control.Alert.AlertType
import javafx.scene.control._
import javafx.scene.{Parent, Scene}
import javafx.stage.{Stage, StageStyle}

import at.fhj.swengb.apps.battleship.model._

import scala.util.{Failure, Success, Try}

case class BattleShipFxDialogHandler()
{
  def initMultiPlayer(playerNr: Int): (Player, BattleField) =
  {
    val fxml: String = "/at/fhj/swengb/apps/battleship/jfx/fxml/battleshipMutiplayerEditDialog.fxml"
    val fxmlLoader: FXMLLoader = new FXMLLoader()

    val controller: BattleShipMutliplayerEditFxController = new BattleShipMutliplayerEditFxController()
    controller.initPlayerNr = playerNr

    fxmlLoader.setController(controller)
    var triedScene = Try(fxmlLoader.load[Parent](getClass.getResource(fxml).openStream()))

    triedScene match {
      case Success(root) =>
        val dialog = new Stage
        dialog.initStyle(StageStyle.UTILITY)
        val scene: Scene = new Scene(root)
        dialog.setScene(scene)
        dialog.setResizable(false)
        dialog.getScene.getStylesheets.clear()
        val css = "/at/fhj/swengb/apps/battleship/jfx/battleshipfx.css"
        dialog.getScene.getStylesheets.add(css)
        dialog.showAndWait()

        controller.getResult match {
          case Some(result) =>
            if (controller.closedRegularly)
              result
            else
              (null, null)
          case None => (null, null)
        }

      case Failure(e) =>
        e.printStackTrace()
        (null, null)
    }
  }



  def askResetHighscoreDialog(): Boolean = {
    val alert = new Alert(AlertType.CONFIRMATION)
    alert.setTitle("Clear Highscores")
    alert.setHeaderText("Are you really sure to delete the Highscore?")
    alert.setContentText("All Entries will be deleted!")

    val result = alert.showAndWait
    result.get() match {
      case ButtonType.OK => true
      case _ => false
    }
  }


  def showPlayerChangeDialog(newPlayer: Player): Boolean = {
    val alert = new Alert(AlertType.INFORMATION)
    alert.setTitle("Multiplayer-Mode")
    alert.setHeaderText("Next player: " + newPlayer.name)
    true
  }



  def showHigschoreGameDialog(playRound: BattleShipGamePlayRound): Unit = {

    val fxml: String = "/at/fhj/swengb/apps/battleship/jfx/fxml/battleshipHighscoreGameReplayDialogfx.fxml"
    val fxmlLoader: FXMLLoader = new FXMLLoader()
    val controller: BattleShipGameReplayFxController = new BattleShipGameReplayFxController()
    controller.selectedPlayRound = playRound

    fxmlLoader.setController(controller)
    var triedScene = Try(fxmlLoader.load[Parent](getClass.getResource(fxml).openStream()))

    triedScene match {
      case Success(root) =>
        val dialog = new Stage
        dialog.initStyle(StageStyle.UTILITY)

        dialog.setTitle(playRound.name)
        val scene: Scene = new Scene(root)

        dialog.setScene(scene)
        dialog.setResizable(false)
        dialog.getScene.getStylesheets.clear()
        val css = "/at/fhj/swengb/apps/battleship/jfx/battleshipfx.css"
        dialog.getScene.getStylesheets.add(css)
        dialog.show()
      case Failure(e) => e.printStackTrace()
    }
  }


  def showGameOverDialog(gamePlayround: BattleShipGamePlayRound): Optional[ButtonType] = {
    gamePlayround.currentBattleShipGame.getCells.foreach(e => e.setDisable(true))
    gamePlayround.getWinner match {
      case None => Optional.empty()
      case Some(winner) =>
        val alert = new Alert(AlertType.INFORMATION)
        alert.setTitle("DESTROYED")
        alert.setHeaderText("DESTROYED")
        alert.setContentText("Player " + gamePlayround.getWinnerName + " won the Battle! ")
        alert.showAndWait()
    }
  }
}
