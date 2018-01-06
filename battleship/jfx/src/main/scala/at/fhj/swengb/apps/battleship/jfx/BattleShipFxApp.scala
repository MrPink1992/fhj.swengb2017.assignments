package at.fhj.swengb.apps.battleship.jfx

import java.security.acl.Group
import javafx.application.Application
import javafx.fxml.FXMLLoader
import javafx.scene.{Parent, Scene}
import javafx.stage.Stage
import javafx.scene.media.Media
import javafx.scene.media.MediaPlayer

import at.fhj.swengb.apps.battleship.AlertBox

import scala.util.{Failure, Success, Try}

object BattleShipFxApp {

  def main(args: Array[String]): Unit = {
    Application.launch(classOf[BattleShipFxApp], args: _*)
  }

}


class BattleShipFxApp extends Application {

  /*doing the media player, mp3 in folder*/

  val triedRoot = Try(FXMLLoader.load[Parent](getClass.getResource("/at/fhj/swengb/apps/battleship/jfx/battleshipfx.fxml")))

  import javafx.animation.FadeTransition
  import javafx.scene.paint.Color
  import javafx.util.Duration

  val rect = new Nothing(100, 40, 100, 100)
  (50)
  rect.setArcWidth(50)
  rect.setFill(Color.VIOLET)

  val ft = new FadeTransition(Duration.millis(3000), rect)
  ft.setFromValue(1.0)
  ft.setToValue(0.3)
  ft.setCycleCount(4)
  ft.setAutoReverse(true)






  val media = new Media("file:///C:/workspace/fhj.swengb2017.assignments/battleship/jfx/src/main/resources/at/fhj/swengb/apps/battleship/jfx/Dead_Combo_-_01_-_Povo_Que_Cas_Descalo.mp3")
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
        stage.setTitle("Es wird Zeit!")   //setting title of window to some meaningful message!. //
        mediaPlayer.play()
      case Failure(e) => e.printStackTrace()
    }
  }






}