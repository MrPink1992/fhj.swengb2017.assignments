package at.fhj.swengb.apps.battleship.model

import javafx.scene.paint.Color
import javafx.scene.shape.Rectangle

/**
  * Represents one part of a vessel or one part of the ocean.
  */
case class BattleFxCell(pos: BattlePos,
                        width: Double,
                        height: Double,
                        game: BattleShipGame,
                        log: String => Unit,
                        someVessel: Option[Vessel] = None,
                        upGameState: (Vessel, BattlePos) => Unit,
                        upClickedPos: BattlePos => Unit,
                        jukeBox: BattleShipJukeBox)
  extends Rectangle(width, height) {

  /**
    * Initialize BattleFxCell. If cell is in given list cell gets colorized immediately (already clicked)
    * If simulation mode is active, no further action is triggered
    *
    * @param clickedPos - List of all already clicked positions. Colorize this cell if given list contains it
    */
  def init(clickedPos: Seq[BattlePos]): Unit = {

    if (clickedPos.contains(pos)) {
      colorizedAfterClick()
    } else {
      init()
    }
  }

  def init(): Unit = {

    setFill(Color.DARKGRAY)



  }

  setOnMouseClicked(e => {
    colorizedAfterClick()

    playMusicAfterClick()

    upClickedPos(pos)
  })

  def colorizedAfterClick(): Unit = {
    someVessel match {
      case None =>
        log(game.player.name + ": Missed. Just hit water.")
        setFill(Color.WHITE)
      case Some(v) =>
        setFill(Color.RED)
        upGameState(v, pos)

    }
  }

  def playMusicAfterClick(): Unit = {
    someVessel match {
      case None => jukeBox.hitWater()
      case Some(v) => jukeBox.hitShip()
    }
  }

}
