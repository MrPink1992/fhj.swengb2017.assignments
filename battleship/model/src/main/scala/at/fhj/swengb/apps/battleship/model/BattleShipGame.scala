package at.fhj.swengb.apps.battleship.model

/**
  * Contains all information about a battleship game.
  *
  * @param player        => Player which plays this game
  * @param battleField   => Represebts battlefiled of this gmae
  * @param getCellWidth  => Size of Cell
  * @param getCellHeight => Size of Cell
  * @param log           => Function where to write the log
  * @param updateGUI     => Function to update GUI after Click action (e.g. update Slider for history)
  */
case class BattleShipGame(player: Player,
                          battleField: BattleField,
                          getCellWidth: Int => Double,
                          getCellHeight: Int => Double,
                          log: String => Unit,
                          updateGUI: BattleShipGame => Unit,
                          jukeBox: BattleShipJukeBox) {

  //We don't ever change cells, they should be initialized only once.
  private val cells: Seq[BattleFxCell] = for {
    x <- 0 until battleField.width
    y <- 0 until battleField.height
    pos = BattlePos(x, y)
  } yield {
    BattleFxCell(BattlePos(x, y),
      getCellWidth(x),
      getCellHeight(y),
      this,
      log,
      battleField.fleet.findByPos(pos),
      updateGameState,
      updateClickedPositions,
      jukeBox)
  }
  /**
    * remembers which vessel was hit at which position
    * starts with the empty map, meaning that no vessel was hit yet.
    *
    **/
  var hits: Map[Vessel, Set[BattlePos]] = Map()
  /*
  * contains all vessels which are destroyed
  */
  var sunkShips: Set[Vessel] = Set()
  /*
  * Contains all already clicked positions.
  */
  var clickedPositions: List[BattlePos] = List()
  var isGameOver: Boolean = false

  def getCells: Seq[BattleFxCell] = cells

  /**
    * Adds a new Position to clicked set
    *
    * @param pos Position which was clicked and is requested to add to list
    */
  def updateClickedPositions(pos: BattlePos): Unit = {
    clickedPositions = pos :: clickedPositions
    updateGUI(this)
  }

  /**
    * Called function after click and hit a vessel.
    *
    * @param vessel - hit vessel
    * @param pos    - position which was hit
    */
  def updateGameState(vessel: Vessel, pos: BattlePos): Unit = {
    log(player.name + ": Vessel " + vessel.name.value + " was hit at position " + pos)

    if (hits.contains(vessel)) {
      val oldPos: Set[BattlePos] = hits(vessel)
      hits = hits.updated(vessel, oldPos + pos)

      if (oldPos.contains(pos)) {
        log(player.name + ": Field was clicked 2 times. WTF?")
      }

      if (vessel.occupiedPos == hits(vessel)) {
        log(s"Ship ${vessel.name.value} was COMPLETELY DESTROYED!")
        sunkShips = sunkShips + vessel

        if (battleField.fleet.vessels == sunkShips) {
          log(player.name + ": GAME OVER!")
          isGameOver = true
        }
      }

    } else {

      hits = hits.updated(vessel, Set(pos))
    }

  }

}
