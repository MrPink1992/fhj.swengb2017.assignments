package at.fhj.swengb.apps.battleship

import java.text.SimpleDateFormat
import java.util.Date

import at.fhj.swengb.apps.battleship.BattleShipProtobuf.BattleShipPlayRound.{Position, Vessel, VesselOrientation}
import at.fhj.swengb.apps.battleship.BattleShipProtobuf.HighScore
import at.fhj.swengb.apps.battleship.model._

import scala.collection.JavaConverters._

object BattleShipProtocol {

  def convert(protoHighScore: HighScore): Seq[BattleShipGamePlayRound] = {
    val highScore: Seq[BattleShipGamePlayRound] = protoHighScore.getPlayedPlayRoundsList.asScala.map(e => convert(e))
    highScore
  }

  def convert(protoPlayGround: BattleShipProtobuf.BattleShipPlayRound): BattleShipGamePlayRound = {

    val battleFieldGames: Seq[BattleShipGame] = protoPlayGround.getBattlefieldsGamesList.asScala.map(e => convert(e)).toList


    val gameName: String = protoPlayGround.getGameName

    val startDate: Date = new SimpleDateFormat("yyyy/MM/dd").parse(protoPlayGround.getStartdate)

    val playRound = BattleShipGamePlayRound(gameName,
      battleFieldGames, startDate)

    if (protoPlayGround.hasWinner) {
      playRound.setWinner(convert(protoPlayGround.getWinner))
    }

    playRound
  }

  def convert(protoGame: BattleShipProtobuf.BattleShipPlayRound.BattleFieldGame): BattleShipGame = {

    val fleet = Fleet(protoGame.getVesselsList.asScala.map(e => convert(e)).toSet)

    val field: BattleField = BattleField(protoGame.getFieldWidth,
      protoGame.getFieldHeight, fleet)


    val game: BattleShipGame = BattleShipGame(convert(protoGame.getPlayer),
      field,
      x => x.toDouble,
      x => x.toDouble,
      x => (),
      x => (),
      null)

    game.clickedPositions = protoGame.getClickedPositionsList.asScala.map(e => convert(e)).toList

    game
  }

  def convert(vessel: Vessel): at.fhj.swengb.apps.battleship.model.Vessel = {

    val name: NonEmptyString = NonEmptyString(vessel.getName)
    val startPos: BattlePos =
      BattlePos(vessel.getStartPos.getX, vessel.getStartPos.getY)
    val size = vessel.getSize
    val direction: Direction = vessel.getOrientation match {
      case VesselOrientation.Horizontal => Horizontal
      case VesselOrientation.Vertical => Vertical
      case _ => ???
    }

    at.fhj.swengb.apps.battleship.model.Vessel(name, startPos, direction, size)
  }


  private def convert(position: Position): BattlePos = {
    BattlePos(position.getX, position.getY)
  }

  private def convert(player: BattleShipProtobuf.BattleShipPlayRound.Player): Player = {
    Player(player.getName, player.getCssStyleClass)
  }

  def convert(highScore: Seq[BattleShipGamePlayRound]): BattleShipProtobuf.HighScore = {

    val protoHighScoreEntries: Seq[BattleShipProtobuf.BattleShipPlayRound] = highScore.map(e => convert(e))

    val protoHighScore: HighScore.Builder = protoHighScoreEntries.foldLeft(HighScore.newBuilder())((acc, e) => acc.addPlayedPlayRounds(e))
    protoHighScore.build()
  }

  def convert(g: BattleShipGamePlayRound): BattleShipProtobuf.BattleShipPlayRound = {
    val protoPlayRound = BattleShipProtobuf.BattleShipPlayRound
      .newBuilder()
      .setGameName(g.name)


    val protoGames = g.games.map(e => convert(e))
    protoGames.foreach(e => protoPlayRound.addBattlefieldsGames(e))

    protoPlayRound.setStartdate(new SimpleDateFormat("yyyy/MM/dd").format(g.startDate))

    if (g.getWinner.isDefined) {
      protoPlayRound.setWinner(convert(g.getWinner.get))
    }

    protoPlayRound.build()
  }

  def convert(game: BattleShipGame): BattleShipProtobuf.BattleShipPlayRound.BattleFieldGame = {
    val protoGame = BattleShipProtobuf.BattleShipPlayRound.BattleFieldGame.newBuilder()

    protoGame.setPlayer(convert(game.player))
    protoGame.setFieldWidth(game.battleField.width)
    protoGame.setFieldHeight(game.battleField.height)

    val protoFleet: Set[Vessel] = game.battleField.fleet.vessels.map(e => convert(e))
    protoFleet.foreach(e => protoGame.addVessels(e))

    val protoClickedPos: List[BattleShipProtobuf.BattleShipPlayRound.Position] = game.clickedPositions.map(e => convert(e))
    protoClickedPos.foreach(e => protoGame.addClickedPositions(e))

    protoGame.build()
  }


  def convert(vessel: at.fhj.swengb.apps.battleship.model.Vessel): Vessel = {

    val vesselOrientation = {
      vessel.direction match {
        case Horizontal => VesselOrientation.Horizontal;
        case Vertical => VesselOrientation.Vertical;
        case _ => ???
      }
    }

    Vessel
      .newBuilder()
      .setName(vessel.name.value)
      .setSize(vessel.size)
      .setOrientation(vesselOrientation)
      .setStartPos(
        Position
          .newBuilder()
          .setX(vessel.startPos.x)
          .setY(vessel.startPos.y)
          .build())
      .build()
  }


  private def convert(battlePos: BattlePos): Position = {
    Position
      .newBuilder()
      .setX(battlePos.x)
      .setY(battlePos.y)
      .build()
  }

  private def convert(player: Player): BattleShipProtobuf.BattleShipPlayRound.Player = {
    BattleShipProtobuf.BattleShipPlayRound.Player.newBuilder()
      .setName(player.name)
      .setCssStyleClass(player.cssStyleClass)
      .build()
  }
}
