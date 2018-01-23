package at.fhj.swengb.apps.battleship.jfx

import java.net.URL
import java.text.SimpleDateFormat
import java.util.{Date, ResourceBundle}
import javafx.beans.property.{SimpleIntegerProperty, SimpleStringProperty}
import javafx.beans.value.ObservableValue
import javafx.collections.{FXCollections, ObservableList}
import javafx.fxml.{FXML, Initializable}
import javafx.scene.control.{Button, SelectionMode, TableColumn, TableView}
import javafx.util.Callback

import at.fhj.swengb.apps.battleship.model.{BattleShipGamePlayRound, HighScore}

class BattleShipHighscoreFxController extends Initializable {

  @FXML private var Highscoretable: TableView[HighScoreEntry] = _
  @FXML private var btReplay: Button = _
  @FXML private var colDate: TableColumn[HighScoreEntry, String] = _
  @FXML private var colWinner: TableColumn[HighScoreEntry, String] = _
  @FXML private var colGameName: TableColumn[HighScoreEntry, String] = _
  @FXML private var colClickAmount: TableColumn[HighScoreEntry, Int] = _


  @FXML def onReplayGame(): Unit = {
    val selectedEntry: HighScoreEntry = Highscoretable.getSelectionModel.getSelectedItem

    if (selectedEntry != null) {
      val selectedBattleShipPlayRound = selectedEntry.getBattleShipPlayRound
      BattleShipFxDialogHandler().showHigschoreGameDialog(selectedBattleShipPlayRound)
    }
  }


  @FXML def resetHighscore(): Unit = {
    val deleteHighscore: Boolean = BattleShipFxDialogHandler().askResetHighscoreDialog()
    if (deleteHighscore) {
      HighScore().clearHighscore(); initData()
    }
  }

  private def initData(): Unit = {
    val tableData: ObservableList[HighScoreEntry] = convertToObservableList(HighScore().getSortedHighScore)
    this.Highscoretable.setItems(tableData)
  }

  private def convertToObservableList(highScore: Seq[BattleShipGamePlayRound]): ObservableList[HighScoreEntry] = {
    val tableData: ObservableList[HighScoreEntry] = FXCollections.observableArrayList()
    highScore.foreach(highScore => tableData.add(HighScoreEntry(highScore)))

    tableData
  }

  @FXML def returnToMain(): Unit = BattleShipFxApp.showScene(BattleShipFxApp.getWelcomeScene, BattleShipFxApp.getRootStage)

  override def initialize(location: URL, resources: ResourceBundle): Unit = {
    initTableViewColumn[String](colDate, _.formatedDate)
    initTableViewColumn[String](colWinner, _.winnerName)
    initTableViewColumn[String](colGameName, _.playroundName)
    initTableViewColumn[Int](colClickAmount, _.clickAmount)

    this.Highscoretable.getSelectionModel.setSelectionMode(SelectionMode.SINGLE)
    this.Highscoretable.getSelectionModel.setCellSelectionEnabled(false)
    this.Highscoretable.setFixedCellSize(25)

    initData()
  }

  def initTableViewColumn[T]: (TableColumn[HighScoreEntry, T], (HighScoreEntry) => Any) => Unit = initTableViewColumnCellValueFactory[HighScoreEntry, T]

  def initTableViewColumnCellValueFactory[HighScoreEntry, T](column: TableColumn[HighScoreEntry, T], function: HighScoreEntry => Any): Unit = {
    column.setCellValueFactory(createCellValueFactory(cell => function(cell.getValue).asInstanceOf[ObservableValue[T]]))
  }

  private def createCellValueFactory[HighScoreEntry, T](fn: TableColumn.CellDataFeatures[HighScoreEntry, T] => ObservableValue[T]):
  Callback[TableColumn.CellDataFeatures[HighScoreEntry, T], ObservableValue[T]] = {
    (cdf: TableColumn.CellDataFeatures[HighScoreEntry, T]) => fn(cdf)
  }

}

class HighScoreEntry {
  val formatedDate: SimpleStringProperty = new SimpleStringProperty()
  val winnerName: SimpleStringProperty = new SimpleStringProperty()
  val playroundName: SimpleStringProperty = new SimpleStringProperty()
  val clickAmount: SimpleIntegerProperty = new SimpleIntegerProperty()

  private var playRound: BattleShipGamePlayRound = _

  def setDate(date: Date): Unit = formatedDate.set(new SimpleDateFormat("yyyy/MM/dd").format(date))

  def setWinner(name: String): Unit = winnerName.set(name)

  def setPlayroundName(name: String): Unit = playroundName.set(name)

  def setClickAmount(clicks: Int): Unit = clickAmount.set(clicks)

  def setPlayRound(roundToReplay: BattleShipGamePlayRound): Unit = playRound = roundToReplay

  def getBattleShipPlayRound: BattleShipGamePlayRound = playRound
}

object HighScoreEntry {
  def apply(playRound: BattleShipGamePlayRound): HighScoreEntry = {
    val hsEntry = new HighScoreEntry
    hsEntry.setDate(playRound.startDate)
    hsEntry.setWinner(playRound.getWinnerName)
    hsEntry.setPlayroundName(playRound.name)
    hsEntry.setClickAmount(playRound.getTotalAmountOfMoves)
    hsEntry.setPlayRound(playRound)

    hsEntry
  }
}
