package at.fhj.swengb.apps.battleship.jfx

import java.net.URL
import java.util.ResourceBundle
import javafx.beans.property.SimpleStringProperty
import javafx.collections.{FXCollections, ObservableList}
import javafx.fxml.{FXML, Initializable}
import javafx.scene.control._
import javafx.scene.layout.GridPane
import javafx.stage.Stage

import at.fhj.swengb.apps.battleship.model.{BattleShip, _}

class BattleShipMutliplayerEditFxController extends Initializable {

  private val usedFleetConfig: FleetConfig = BattleShipFxApp.getUsedFleetConfig
  var initPlayerNr: Int = 1
  var closedRegularly: Boolean = false
  @FXML private var lbHeader: Label = _
  @FXML private var tfPlayerName: TextField = _
  @FXML private var lvFleet: ListView[VesselListViewEntry] = _
  @FXML private var cbOrientation: ComboBox[Direction] = _
  @FXML private var battleGroundGridPane: GridPane = _
  @FXML private var btInitialize: Button = _
  private var gameField: BattleField = BattleField.placeRandomly(BattleField(10, 10, Fleet.Empty))
  private var player: Player = _

  def getResult: Option[(Player, BattleField)] = Option(player, gameField)

  @FXML def onInitialize(): Unit = {
    closedRegularly = true
    val stage = btInitialize.getScene.getWindow.asInstanceOf[Stage]
    stage.close()
  }

  @FXML def onSetRandomFleet(): Unit = {
    gameField = BattleField.placeRandomly(BattleField(10, 10, Fleet(usedFleetConfig)))
    refreshGameField(gameField)
    refreshFleetList(gameField.fleet.vessels)
    checkEnableInitButton()
  }

  @FXML def onClearFleet(): Unit = {
    gameField = BattleField(10, 10, Fleet(Set[Vessel]()))
    refreshGameField(gameField)
    refreshFleetList(gameField.fleet.vessels)

    checkEnableInitButton()
  }

  @FXML def onPlayerNameEntered(): Unit = {
    if (tfPlayerName.getText().nonEmpty) {
      player = Player(tfPlayerName.getText(), getCssStyle(initPlayerNr))
    } else {
      player = null
    }
    checkEnableInitButton()
  }

  private def getCssStyle(playerNr: Int): String = playerNr match {
    case 1 => "bg_playerA"
    case 2 => "bg_playerB"
    case _ => "bg_game"
  }

  private def checkEnableInitButton(): Unit = {
    if (player != null && gameField != null &&
      tfPlayerName.getText().nonEmpty && lvFleet.getItems.size() == 0) {
      btInitialize.setDisable(false)
    } else
      btInitialize.setDisable(true)
  }

  @FXML def onShipSelected(): Unit = {
    if (lvFleet.getSelectionModel.getSelectedItem != null) {

      val ship: Vessel = lvFleet.getSelectionModel.getSelectedItem.getVessel
      val direction: Direction = cbOrientation.getSelectionModel.getSelectedItem

      if (ship != null && direction != null) {
        disableAllButons(false)
      }
    }
  }


  private def disableAllButons(disable: Boolean): Unit = {

    battleGroundGridPane.getChildren.forEach(e => {
      val cell: EditorFxCell = e.asInstanceOf[EditorFxCell]
      cell.someVessel match {
        case Some(v) =>
          cell.setDisable(true)
        case None => cell.setDisable(disable)
      }
    })
  }

  override def initialize(location: URL, resources: ResourceBundle): Unit = {
    lbHeader.setText("Setup " + initPlayerNr)

    lvFleet.getSelectionModel.setSelectionMode(SelectionMode.SINGLE)
    refreshGameField(gameField)
    refreshFleetList(gameField.fleet.vessels)
    initDirectionComboBox()

    checkEnableInitButton()
  }

  private def getCellHeight(y: Int): Double = battleGroundGridPane.getRowConstraints.get(y).getPrefHeight

  private def getCellWidth(x: Int): Double = battleGroundGridPane.getColumnConstraints.get(x).getPrefWidth

  private def initDirectionComboBox(): Unit = {
    val cbData: ObservableList[Direction] = FXCollections.observableArrayList()
    cbData.add(Horizontal)
    cbData.add(Vertical)

    cbOrientation.setItems(cbData)
    cbOrientation.getSelectionModel.selectFirst()
  }

  private def updateEditorAfterClick(pos: BattlePos): Unit = {

    var selVessel: Vessel = lvFleet.getSelectionModel.getSelectedItem.getVessel
    val selDirection: Direction = cbOrientation.getSelectionModel.getSelectedItem

    val shipName: String = selVessel.name.value
    if (selVessel != null && selDirection != null) {
      val newVessel: Vessel = selVessel.size match {
        case 2 => new Submarine(shipName, pos, selDirection)
        case 3 => new Destroyer(shipName, pos, selDirection)
        case 4 => new Cruiser(shipName, pos, selDirection)
        case 5 => new BattleShip(shipName, pos, selDirection)
        case _ => ???
      }

      val illegalePos: Set[BattlePos] = newVessel.occupiedPos.diff(gameField.availablePos)
      if (illegalePos.nonEmpty) {}
      else
      {
        val newShips: Seq[Vessel] = newVessel +: gameField.fleet.vessels.toSeq
        gameField = BattleField(10, 10, new Fleet(newShips.toSet))
      }
    }

    refreshGameField(gameField)
    refreshFleetList(gameField.fleet.vessels)
    disableAllButons(true)
    checkEnableInitButton()
  }


  private def refreshGameField(field: BattleField): Unit = {
    battleGroundGridPane.getChildren.clear()

    val cells: Seq[EditorFxCell] = for {
      x <- 0 until field.width
      y <- 0 until field.height
      pos = BattlePos(x, y)
    } yield {
      EditorFxCell(BattlePos(x, y),
        getCellWidth(x),
        getCellHeight(y),
        field.fleet.findByPos(pos),
        updateEditorAfterClick)
    }
    for (c <- cells) {
      battleGroundGridPane.add(c, c.pos.x, c.pos.y)
      c.init()
    }

    disableAllButons(true)
  }


  private def refreshFleetList(alreadySet: Set[Vessel]): Unit = {
    var aFullStandardFleet: Set[Vessel] = Fleet(usedFleetConfig).vessels

    val alreadySetShips: Set[NonEmptyString] = alreadySet.map(e => e.name)
    aFullStandardFleet = aFullStandardFleet.filter(e => !alreadySetShips.contains(e.name))
    val lvData: ObservableList[VesselListViewEntry] = FXCollections.observableArrayList()
    aFullStandardFleet.toList.foreach(e => lvData.add(VesselListViewEntry(e)))
    lvFleet.setItems(lvData)
  }


  private class VesselListViewEntry {
    val name: SimpleStringProperty = new SimpleStringProperty()
    private var ship: Vessel = _

    def setName(name: String): Unit = this.name.set(name)

    def getVessel: Vessel = ship

    def setVessel(vessel: Vessel): Unit = {
      ship = vessel
    }

    override def toString: String = name.get
  }

  private object VesselListViewEntry {
    def apply(vessel: Vessel): VesselListViewEntry = {
      val entry = new VesselListViewEntry
      entry.setName(vessel.name.value)
      entry.setVessel(vessel)
      entry
    }
  }

}



