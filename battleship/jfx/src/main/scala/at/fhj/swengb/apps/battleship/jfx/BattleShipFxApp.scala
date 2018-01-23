package at.fhj.swengb.apps.battleship.jfx

import javafx.application.Preloader.StateChangeNotification
import javafx.application.{Application, Preloader}
import javafx.fxml.FXMLLoader
import javafx.scene.control.ProgressBar
import javafx.scene.image.ImageView
import javafx.scene.layout.BorderPane
import javafx.scene.media.Media
import javafx.scene.{Parent, Scene}
import javafx.stage.{Stage, StageStyle}

import at.fhj.swengb.apps.battleship.model.{BattleShipJukeBox, FleetConfig}
import com.sun.javafx.application.LauncherImpl

import scala.util.{Failure, Success, Try}

object BattleShipFxApp {
  private var rootStage: Stage = _
  private var jukeBox: BattleShipJukeBox = _
  private var usedFleetConfig: FleetConfig = FleetConfig.Standard

  private var welcomeScreen: Scene = _
  private var gameScreen: Scene = _
  private var highscoreScreen: Scene = _
  private var creditScreen: Scene = _

  def getRootStage: Stage = rootStage

  def getBattleShipJukeBox: BattleShipJukeBox = jukeBox

  def getWelcomeScene: Scene = welcomeScreen

  def getGameScene: Scene = gameScreen

  def getHighscoreScene: Scene = parseScene("/at/fhj/swengb/apps/battleship/jfx/fxml/battleshipHighscorefx.fxml")

  def getCreditsScene: Scene = parseScene("/at/fhj/swengb/apps/battleship/jfx/fxml/battleshipCreditsfx.fxml")


  private def parseScene(fxml: String): Scene = {
    val triedScene = Try(FXMLLoader.load[Parent](getClass.getResource(fxml)))
    triedScene match {
      case Success(root) =>
        val scene: Scene = new Scene(root)
        scene.getStylesheets.clear()
        scene.getStylesheets.add("/at/fhj/swengb/apps/battleship/jfx/battleshipfx.css")
        scene

      case Failure(e) => e.printStackTrace(); null
    }
  }

  def getUsedFleetConfig: FleetConfig = usedFleetConfig

  def setUsedFleetConfig(newFleetConfig: FleetConfig): Unit = usedFleetConfig = newFleetConfig

  def initializeScenes(): Unit = {
    welcomeScreen = parseScene("/at/fhj/swengb/apps/battleship/jfx/fxml/battleshipMainfx.fxml")
    gameScreen = parseScene("/at/fhj/swengb/apps/battleship/jfx/fxml/battleshipGamefx.fxml")
  }

  def showScene(scene: Scene, stage: Stage): Unit = {
    if (stage == null) System.exit(1)
    else {
      stage.setScene(scene)
      stage.show()
    }
  }


  def main(args: Array[String]): Unit = LauncherImpl.launchApplication(classOf[BattleShipFxApp], classOf[BattleShipFxSlashScreen], args)
}

class BattleShipFxApp extends Application {
  override def start(stage: Stage): Unit = {
    stage.setTitle("Battleship by Eierbären")
    stage.setResizable(false)


    BattleShipFxApp.rootStage = stage

    BattleShipFxApp.showScene(BattleShipFxApp.getWelcomeScene, stage)

    BattleShipFxApp.jukeBox.playBackgroundMusic()
  }

  override def init(): Unit = {
    val backgroundMedia: Media = new Media(getClass.getResource("/at/fhj/swengb/apps/battleship/jfx/music/Battlefield1942Sound.mp3").toExternalForm)
    val shipHitMedia: Media = new Media(getClass.getResource("/at/fhj/swengb/apps/battleship/jfx/music/explosion.mp3").toExternalForm)
    val missedMusic: Media = new Media(getClass.getResource("/at/fhj/swengb/apps/battleship/jfx/music/missed_hit.mp3").toExternalForm)

    BattleShipFxApp.jukeBox = BattleShipJukeBox(backgroundMedia, shipHitMedia, missedMusic)

    BattleShipFxApp.initializeScenes()
  }
}


class BattleShipFxSlashScreen extends Preloader {
  private var stage: Stage = _
  private var progressBar: ProgressBar = new ProgressBar

  override def start(primaryStage: Stage): Unit = {
    stage = primaryStage

    stage.initStyle(StageStyle.UNDECORATED)

    stage.setWidth(600)
    stage.setHeight(240)

    stage.setScene(createScene)
    stage.show()
  }

  private def createScene: Scene = {
    val mainPane: BorderPane = new BorderPane()

    val image: ImageView = new ImageView(getClass.getResource("/at/fhj/swengb/apps/battleship/jfx/pics/SplashScreen.png").toString)
    image.setFitWidth(600)
    image.setFitHeight(240)

    progressBar.setPrefWidth(600)

    mainPane.setCenter(image)
    mainPane.setBottom(progressBar)

    new Scene(mainPane)
  }

  override def handleProgressNotification(pn: Preloader.ProgressNotification): Unit = progressBar.setProgress(pn.getProgress)

  override def handleStateChangeNotification(evt: Preloader.StateChangeNotification): Unit = {
    if (evt.getType.equals(StateChangeNotification.Type.BEFORE_START)) stage.hide()
  }
}