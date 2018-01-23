package at.fhj.swengb.apps.battleship.model

import java.io.{File, InputStream}
import java.nio.file.{Files, Paths}
import java.text.SimpleDateFormat
import java.util.{Calendar, Date}

import at.fhj.swengb.apps.battleship.{BattleShipProtobuf, BattleShipProtocol}

import scala.util.Random

object BattleShipGamePlayRound
{

  def apply(player1: Player,
            battlefieldPlayer1: BattleField,
            player2: Player,
            battlefieldPlayer2: BattleField,
            getCellWidth: Int => Double,
            getCellHeight: Int => Double,
            log: String => Unit,
            updateGUIAfterAction: BattleShipGame => Unit,
            jukeBox: BattleShipJukeBox): BattleShipGamePlayRound = {

    //Create Games for each player. Battlefield is field of enemy
    val gamePlayer1: BattleShipGame = createBattleShipGame(player1, battlefieldPlayer2, getCellWidth, getCellHeight, log, updateGUIAfterAction, jukeBox)
    val gamePlayer2: BattleShipGame = createBattleShipGame(player2, battlefieldPlayer1, getCellWidth, getCellHeight, log, updateGUIAfterAction, jukeBox)

    BattleShipGamePlayRound(createRandomPlayRoundName(), Seq(gamePlayer1, gamePlayer2), Calendar.getInstance.getTime)
  }

  def apply(file: File,
            parse: InputStream => BattleShipProtobuf.BattleShipPlayRound,
            getCellWidth: Int => Double,
            getCellHeight: Int => Double,
            log: String => Unit,
            updateGUIAfterAction: BattleShipGame => Unit,
            jukeBox: BattleShipJukeBox,
            unused: Int => Int): BattleShipGamePlayRound = {

    //Read Protobuf-Object and convert it to a BattleShipGamePlayRound-Instance
    val protoBattleShipGamePlayRound: BattleShipProtobuf.BattleShipPlayRound = parse(Files.newInputStream(Paths.get(file.getAbsolutePath)))
    val loadedBattleShipGamePlayRound: BattleShipGamePlayRound = BattleShipProtocol.convert(protoBattleShipGamePlayRound)

    //Initialize all games (Single/Multiplayer mode)
    val games: Seq[BattleShipGame] =
      loadedBattleShipGamePlayRound.games.map(e => createBattleShipGame(e, getCellWidth, getCellHeight, log, updateGUIAfterAction, jukeBox))

    //Create new gamePlayround-Event based on loaded Data
    BattleShipGamePlayRound(loadedBattleShipGamePlayRound.name, games, loadedBattleShipGamePlayRound.startDate)
  }

  def apply(highScorePlayround: BattleShipGamePlayRound,
            getCellWidth: Int => Double,
            getCellHeight: Int => Double,
            log: String => Unit,
            updateGUIAfterAction: BattleShipGame => Unit,
            jukeBox: BattleShipJukeBox): BattleShipGamePlayRound = {


    val games: Seq[BattleShipGame] = highScorePlayround.games.map(g => createBattleShipGame(g, getCellWidth, getCellHeight, log, updateGUIAfterAction, jukeBox))

    val newPR = BattleShipGamePlayRound(highScorePlayround.name, games, highScorePlayround.startDate)
    newPR.winner = highScorePlayround.winner
    newPR
  }
  private def createRandomPlayRoundName(): String =
  {
    val w1: Seq[String] = Seq("The", "Dreadful", "Heroic", "Destroying", "Intergalactic")
    val w2: Seq[String] = Seq("battle", "fight", "encounter", "conflict", "war", "clash")
    val w3: Seq[String] = Seq("of", "at", "in")
    val w4: Seq[String] = Seq("Mars", "Yupiter", "Neptun", "Pluto")

    val rGen: Random = new Random()

    val name: String = w1(rGen.nextInt(w1.size - 1)) + " " +
      w2(rGen.nextInt(w2.size - 1)) + " " +
      w3(rGen.nextInt(w3.size - 1)) + " " +
      w4(rGen.nextInt(w4.size - 1))

    name
  }


  private def createBattleShipGame(player: Player,
                                   getCellWidth: Int => Double,
                                   getCellHeight: Int => Double,
                                   log: String => Unit,
                                   updateGUIAfterAction: BattleShipGame => Unit,
                                   jukeBox: BattleShipJukeBox,
                                   fleetConfig: FleetConfig): BattleShipGame =
  {
    val battlefield: BattleField = BattleField(10, 10, Fleet(fleetConfig))

    BattleShipGame(player, BattleField.placeRandomly(battlefield), getCellWidth, getCellHeight, log, updateGUIAfterAction, jukeBox)
  }


  private def createBattleShipGame(player: Player,
                                   battlefield: BattleField,
                                   getCellWidth: Int => Double,
                                   getCellHeight: Int => Double,
                                   log: String => Unit,
                                   updateGUIAfterAction: BattleShipGame => Unit,
                                   jukeBox: BattleShipJukeBox): BattleShipGame =
    BattleShipGame(player, battlefield, getCellWidth, getCellHeight, log, updateGUIAfterAction, jukeBox)


  private def createBattleShipGame(loadedGame: BattleShipGame,
                                   getCellWidth: Int => Double,
                                   getCellHeight: Int => Double,
                                   log: String => Unit,
                                   updateGUIAfterAction: BattleShipGame => Unit,
                                   jukeBox: BattleShipJukeBox): BattleShipGame = {
    val newGame: BattleShipGame = BattleShipGame(loadedGame.player, loadedGame.battleField, getCellWidth, getCellHeight, log, updateGUIAfterAction, jukeBox)

    newGame.clickedPositions = loadedGame.clickedPositions
    newGame
  }
}

case class BattleShipGamePlayRound(name: String, games: Seq[BattleShipGame], startDate: Date)
{
  require(name.nonEmpty)

  require(games.nonEmpty && games.lengthCompare(2) <= 0)

  var currentBattleShipGame: BattleShipGame = games.head
  private var winner: Player = _

  //Return total amount of moves in play round
  def getTotalAmountOfMoves: Int = games.foldLeft(0)((acc, game) => acc + game.clickedPositions.size)

  def getMergedClickedPositions: Seq[BattlePos] =
  {
    if (games.lengthCompare(1) == 0) games.head.clickedPositions
    else
    {
      var clicksGame1: Seq[BattlePos] = games.head.clickedPositions
      var clicksGame2: Seq[BattlePos] = games.last.clickedPositions

      var mergedList: Seq[BattlePos] = Seq()
      var takeFromFirst: Boolean = true

      def takeHeadAndAddtoMergeList(list: Seq[BattlePos]): Seq[BattlePos] = list match
      {
        case Nil => Nil
        case head :: Nil =>
          mergedList = head +: mergedList
          Nil
        case head :: tail =>
          mergedList = head +: mergedList
          tail
      }

      while (clicksGame1.isEmpty && clicksGame2.isEmpty)
      {
        if (takeFromFirst)
        {
          clicksGame1 = takeHeadAndAddtoMergeList(clicksGame1)
          takeFromFirst = false
        }
        else
        {
          clicksGame2 = takeHeadAndAddtoMergeList(clicksGame2)
          takeFromFirst = true
        }
      }

      mergedList
    }
  }

  def getWinnerName: String =
  {
    getWinner match
    {
      case None => "???"
      case Some(w) => w.name
    }
  }

  def getWinner: Option[Player] = Option(winner)

  def setWinner(player: Player): Unit = winner = player

  def getFormatedStartDate: String = new SimpleDateFormat("yyyy/MM/dd").format(startDate)

  def getPlayRoundName: String = name


  def getBattleShipGameWithShorterClicks: BattleShipGame =
  {
    val currGameClicks: Int = currentBattleShipGame.clickedPositions.size
    val otherGameClicks: Int = getOtherBattleShipGame.clickedPositions.size

    if (currGameClicks <= otherGameClicks) currentBattleShipGame
    else getOtherBattleShipGame
  }


  def getOtherBattleShipGame: BattleShipGame =
  {
    if (games.lengthCompare(1) == 0) currentBattleShipGame
    else games.filter(p => !p.equals(currentBattleShipGame)).head
  }

}
