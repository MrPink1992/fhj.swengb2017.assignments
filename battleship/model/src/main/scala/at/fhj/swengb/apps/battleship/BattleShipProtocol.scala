package at.fhj.swengb.apps.battleship

import scala.collection.JavaConverters._
import at.fhj.swengb.apps.battleship.model._

object BattleShipProtocol {

  def convert(g: BattleShipGame): BattleShipProtobuf.BattleShipGame = {
    val game = BattleShipProtobuf.BattleShipGame.newBuilder()
    val field = BattleShipProtobuf.Field.newBuilder()
    val ships = g.battleField.fleet.vessels.map(convert).asJava
    val steps = g.battleField.steps.map(convert).asJava

    field.setWidth(g.battleField.width)
    field.setHeight(g.battleField.height)
    field.addAllShips(ships)
    field.addAllSteps(steps)

    game.setField(field)

    game.build()
  }

  def convert(g: BattleShipProtobuf.BattleShipGame): BattleField = {
    val field = g.getField
    val ships = Fleet(field.getShipsList.asScala.map(convert).toSet)
    val steps = field.getStepsList.asScala.map(convert).toList
    val battleField = BattleField(field.getWidth, field.getHeight, ships)
    battleField.steps = steps
    battleField
  }

  def convert(g: Vessel): BattleShipProtobuf.Ship = {
    val ship = BattleShipProtobuf.Ship.newBuilder()

    0 until g.size foreach { n =>
      val cell = BattleShipProtobuf.Cell.newBuilder()
      val pos = BattleShipProtobuf.Pos.newBuilder()

      g.direction match {
        case Vertical => {
          ship.setDirection(BattleShipProtobuf.Ship.Direction.VERTICAL)
          pos.setX(g.startPos.x)
          pos.setY(g.startPos.y + n)
        }
        case Horizontal => {
          ship.setDirection(BattleShipProtobuf.Ship.Direction.HORIZONTAL)
          pos.setX(g.startPos.x + n)
          pos.setY(g.startPos.y)
        }
      }

      cell.setPos(pos)
      ship.addCells(cell)
    }

    ship.setName(g.name.value)

    ship.build()
  }

  def convert(g: BattleShipProtobuf.Ship): Vessel = {
    val startPos = convert(g.getCells(0).getPos)

    val direction = g.getDirection match {
      case BattleShipProtobuf.Ship.Direction.VERTICAL => Vertical
      case BattleShipProtobuf.Ship.Direction.HORIZONTAL => Horizontal
    }

    Vessel(NonEmptyString(g.getName), startPos, direction, g.getCellsCount)
  }

  def convert(g: BattlePos): BattleShipProtobuf.Pos = {
    val pos = BattleShipProtobuf.Pos.newBuilder()

    pos.setX(g.x)
    pos.setY(g.y)

    pos.build()
  }

  def convert(g: BattleShipProtobuf.Pos): BattlePos = BattlePos(g.getX, g.getY)

}
