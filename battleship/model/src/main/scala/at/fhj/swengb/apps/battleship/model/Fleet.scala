package at.fhj.swengb.apps.battleship.model


import scala.util.Random

object Fleet {

  val Empty = Fleet(Set[Vessel]())
  val Default: Fleet = {
    val battleships: Set[Vessel] = Set(new BattleShip("Boaty McBoatface", BattlePos(0, 0), Vertical))
    val cruisers: Set[Vessel] = Set(new Cruiser("Cruz", BattlePos(1, 0), Vertical), new Cruiser("Santa", BattlePos(2, 0), Vertical))
    val destroyers: Set[Vessel] = Set(
      new Destroyer("Graz", BattlePos(5, 5), Horizontal),
      new Destroyer("Wien", BattlePos(0, 6), Horizontal),
      new Destroyer("Linz", BattlePos(0, 7), Horizontal),
    )
    val submarines: Set[Vessel] = Set(
      new Submarine("A", BattlePos(6, 6), Horizontal),
      new Submarine("A", BattlePos(1, 6), Horizontal),
      new Submarine("A", BattlePos(3, 2), Horizontal),
      new Submarine("A", BattlePos(4, 4), Horizontal),
    )

    val fleet: Set[Vessel] = battleships ++ cruisers ++ destroyers ++ submarines
    Fleet(fleet)
  }

  def apply(battleField: BattleField): Fleet = {
    Default
  }

  def apply(fleetConfig: FleetConfig): Fleet = {
    val vessels: Set[Vessel] =
      (for {((k, v), i) <- fleetConfig.vesselMap.zipWithIndex
            a <- 0 until v} yield {
        val direction = if (Random.nextBoolean()) Horizontal else Vertical
        k match {
          case x if x == classOf[BattleShip] => new BattleShip(s"Destroyer $i $a", BattlePos(0, 0), direction)
          case x if x == classOf[Cruiser] => new Cruiser(s"Heavy Cruiser $i $a", BattlePos(0, 0), direction)
          case x if x == classOf[Destroyer] => new Destroyer(s"Corvette $i $a", BattlePos(0, 0), direction)
          case x if x == classOf[Submarine] => new Submarine(s"Submarine $i $a", BattlePos(0, 0), direction)
        }
      }).toSet


    Fleet(vessels)
  }

}

case class Fleet(vessels: Set[Vessel]) {

  def occupiedPositions: Set[BattlePos] = vessels.flatMap(v => v.occupiedPos)

  def findByPos(pos: BattlePos): Option[Vessel] = vessels.find(v => v.occupiedPos.contains(pos))

  def findByName(name: String): Option[Vessel] = vessels.find(v => v.name == NonEmptyString(name))

}