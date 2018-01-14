package at.fhj.swengb.apps.battleship.jfx

import java.security.acl.Group
import javafx.application.Application
import javafx.fxml.FXMLLoader
import javafx.scene.{Parent, Scene}
import javafx.stage.Stage
import javafx.scene.media.Media
import javafx.scene.media.MediaPlayer

import at.fhj.swengb.apps.battleship.AlertBox

import scala.compat.Platform
import scala.util.{Failure, Success, Try}

object BattleShipFxApp {

  def main(args: Array[String]): Unit = {
    Application.launch(classOf[BattleShipFxApp], args: _*)
  }

}


class BattleShipFxApp extends Application {

  /*doing the media player, mp3 in folder*/

  val triedRoot = Try(FXMLLoader.load[Parent](getClass.getResource("at/fhj/swengb/apps/battleship/jfx/startupScreen.fxml")))









  val media = new Media("at/fhj/swengb/apps/battleship/jfx/Dead.mp3")
  val mediaPlayer = new MediaPlayer(media)
  mediaPlayer.setCycleCount(7)


 // AlertBox.display("Battleship","Bist du bereit für den ultimativen Kampf?");


  import javafx.geometry.Rectangle2D
  import javafx.stage.Screen



  /* doing media player done*/

  override def start(stage: Stage) = {
    triedRoot match {
      case Success(root) =>
        stage.setScene(new Scene(root))
        stage.show()
        stage.setTitle("Es wird Zeit!")
        stage.setResizable(false)//setting title of window to some meaningful message!. //
        mediaPlayer.play()
      case Failure(e) => e.printStackTrace()
    }
  }






}