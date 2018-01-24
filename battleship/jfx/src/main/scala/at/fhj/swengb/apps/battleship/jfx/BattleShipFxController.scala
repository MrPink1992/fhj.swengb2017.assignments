package at.fhj.swengb.apps.battleship.jfx

import java.io.File
import java.net.URL
import java.nio.file.{Files, Paths}
import java.util.ResourceBundle
import javafx.fxml.{FXML, Initializable}
import javafx.scene.control._
import javafx.scene.layout.{BorderPane, GridPane, VBox}
import javafx.scene.paint.Color
import javafx.stage.FileChooser
import javafx.stage.FileChooser.ExtensionFilter

import at.fhj.swengb.apps.battleship.model._
import at.fhj.swengb.apps.battleship.{BattleShipProtobuf, BattleShipProtocol}

class BattleShipFxController extends Initializable {
  private var gamePlayround: BattleShipGamePlayRound = _

  @FXML private var gameBackground: BorderPane = _
  @FXML private var battleGroundGridPane: GridPane = _
  @FXML private var clickHistorySlider: Slider = _
  @FXML private var lbHeader: Label = _
  @FXML private var btSaveGame: Button = _
  @FXML private var lbPlayerName: Label = _
  @FXML private var lbStatisticHeader: Label = _
  @FXML private var lbPlayerHeader: Label = _
  @FXML private var shipStatisticBox: VBox = _
  @FXML private var log: TextArea = _

  @FXML def newGame(): Unit = {

    def createPlayer(name: String, cssStyle: String): Player = {
      if (name.isEmpty) Player("Noname", cssStyle)
      else Player(name, cssStyle)
    }

    BattleShipFxDialogHandler().initMultiPlayer(1) match {
      case (null, null) =>
      case (player1, bfPlayer1) =>
        BattleShipFxDialogHandler().initMultiPlayer(2) match {
          case (null, null) =>
          case (player2, bfPlayer2) =>
            val playGround = BattleShipGamePlayRound(
              player1,
              bfPlayer1,
              player2,
              bfPlayer2,
              getCellWidth,
              getCellHeight,
              appendLog,
              updateGUIAfterAction,
              BattleShipFxApp.getBattleShipJukeBox)
            initPlayRound(playGround)
        }
    }
  }


  @FXML def saveGame(): Unit = {
    try {
      val fileChooser = new FileChooser
      fileChooser.setTitle("Choose location to save game!")
      fileChooser.getExtensionFilters.add(new ExtensionFilter("Protobuf binary files", "*.bin"))

      val chosenFile: File = fileChooser.showSaveDialog(null)

      if (chosenFile != null) {
        val protoBattleShipGame = BattleShipProtocol.convert(gamePlayround)
        protoBattleShipGame.writeTo(Files.newOutputStream(Paths.get(chosenFile.getAbsolutePath)))
        appendLog("Saved Game in " + chosenFile.getAbsolutePath)

      }
    }
    catch {
      case e: Exception => appendLog("Error occurred during save state! " + e.getMessage)
    }
  }

  private def appendLog(message: String): Unit = log.appendText(message + "\n")

  @FXML def loadGame(): Unit = {
    try {
      val fileChooser = new FileChooser
      fileChooser.setTitle("Choose location to load game!")
      fileChooser.getExtensionFilters.add(new ExtensionFilter("Protobuf binary files", "*.bin"))

      val chosenFile: File = fileChooser.showOpenDialog(null)

      if (chosenFile != null) {
        this.log.setText("Loaded game from " + chosenFile.getAbsolutePath)
        val battleShipGamePlayRound: BattleShipGamePlayRound = BattleShipGamePlayRound(
          chosenFile,
          BattleShipProtobuf.BattleShipPlayRound.parseFrom,
          getCellWidth,
          getCellHeight,
          appendLog,
          updateGUIAfterAction,
          BattleShipFxApp.getBattleShipJukeBox,
          x => x)

        initPlayRound(battleShipGamePlayRound)
      }
    }
    catch {
      case e: Exception => appendLog("Error occurred during load game! " + e.getMessage)
    }
  }

  @FXML def returnToMain(): Unit = BattleShipFxApp.showScene(BattleShipFxApp.getWelcomeScene, BattleShipFxApp.getRootStage)

  @FXML def onSliderChanged(): Unit = {
    var simModeActive: Boolean = true

    val simClickPos: List[BattlePos] =
      this.gamePlayround.currentBattleShipGame.clickedPositions.takeRight(clickHistorySlider.getValue.toInt).reverse

    this.battleGroundGridPane.getChildren.clear()

    this.gamePlayround.currentBattleShipGame.getCells.foreach(cell => {
      this.battleGroundGridPane.add(cell, cell.pos.x, cell.pos.y)
      cell.init(simClickPos)
      cell.setDisable(simModeActive)
    })

    updateShipStatistic(this.gamePlayround.currentBattleShipGame)
  }

  private def updateShipStatistic(game: BattleShipGame): Unit = {
    this.shipStatisticBox.getChildren.clear()

    game.battleField.fleet.vessels.foreach(vessel => {
      val label: Label = new Label(vessel.name.value)

      if (game.sunkShips.contains(vessel)) label.setTextFill(Color.RED)
      else label.setTextFill(Color.GREEN)

      this.shipStatisticBox.getChildren.add(label)
    })
  }

  override def initialize(url: URL, rb: ResourceBundle): Unit = {
    this.btSaveGame.setDisable(true)
    this.battleGroundGridPane.setVisible(false)
    this.clickHistorySlider.setVisible(false)
    this.lbStatisticHeader.setVisible(false)
    this.lbPlayerHeader.setVisible(false)
    this.log.setVisible(false)
  }


  def initPlayRound(newPlayRound: BattleShipGamePlayRound): Unit = {
    this.gamePlayround = newPlayRound
    this.lbHeader.setText(gamePlayround.name)

    this.btSaveGame.setDisable(false)
    this.battleGroundGridPane.setVisible(true)
    this.lbStatisticHeader.setVisible(true)
    this.lbPlayerHeader.setVisible(true)
    this.log.setVisible(true)

    this.gameBackground.getStyleClass.remove("bg_playerA")
    this.gameBackground.getStyleClass.remove("bg_playerB")
    this.gameBackground.getStyleClass.add("bg_game")


    val gameToInitialize: BattleShipGame = this.gamePlayround.getBattleShipGameWithShorterClicks

    switchGameGridField(gameToInitialize)

    if (this.gamePlayround.games.lengthCompare(1).equals(0)) {
      this.clickHistorySlider.setMax(gameToInitialize.clickedPositions.size)
      this.clickHistorySlider.setValue(gameToInitialize.clickedPositions.size)
      this.clickHistorySlider.setVisible(true)
    }
    else this.clickHistorySlider.setVisible(false)

  }


  def updateGUIAfterAction(battleShipGame: BattleShipGame): Unit = {
    clickHistorySlider.setMax(battleShipGame.clickedPositions.size)
    clickHistorySlider.setValue(battleShipGame.clickedPositions.size)

    updateShipStatistic(battleShipGame)

    if (battleShipGame.isGameOver) {
      this.gamePlayround.setWinner(battleShipGame.player)

      val result = BattleShipFxDialogHandler().showGameOverDialog(this.gamePlayround)
      if (result.isPresent) {
        this.btSaveGame.setDisable(true)

        HighScore().addRoundToHighScore(this.gamePlayround)
      }
      else appendLog("ERROR: Unexpected end of game!")
    }
    else {
      if (gamePlayround.games.lengthCompare(1) > 0) {
        val otherGame: BattleShipGame = gamePlayround.getOtherBattleShipGame
        val result = BattleShipFxDialogHandler().showPlayerChangeDialog(otherGame.player)
        if (result)
          switchGameGridField(otherGame)
      }
    }
  }

  private def getCellHeight(y: Int): Double = this.battleGroundGridPane.getRowConstraints.get(y).getPrefHeight

  private def getCellWidth(x: Int): Double = this.battleGroundGridPane.getColumnConstraints.get(x).getPrefWidth

  private def switchGameGridField(gameToLoad: BattleShipGame): Unit = {
    this.lbPlayerName.setText(gameToLoad.player.name)
    this.battleGroundGridPane.getChildren.clear()
    gameToLoad.getCells.foreach(cell => {
      this.battleGroundGridPane.add(cell, cell.pos.x, cell.pos.y)
      cell.init(gameToLoad.clickedPositions)
    })

    this.gameBackground.getStyleClass.remove("bg_playerA")
    this.gameBackground.getStyleClass.remove("bg_playerB")
    this.gameBackground.getStyleClass.add(gameToLoad.player.cssStyleClass)

    this.gamePlayround.currentBattleShipGame = gameToLoad

    updateShipStatistic(gameToLoad)
  }
}
