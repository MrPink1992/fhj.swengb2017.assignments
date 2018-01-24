package at.fhj.swengb.apps.battleship.model

import javafx.scene.media.{Media, MediaPlayer}

case class BattleShipJukeBox(backgroundMusic: Media, shipHitMedia: Media, waterHitMedia: Media) {

  private val backgroundMusicPlayer: MediaPlayer = {
    val player: MediaPlayer = new MediaPlayer(backgroundMusic)
    player.setCycleCount(MediaPlayer.INDEFINITE)

    player
  }
  private var backgroundMute: Boolean = false
  private var soundeffectMute: Boolean = false

  def playMusic(background: Boolean, effect: Boolean): Unit = {
    backgroundMute = !background
    soundeffectMute = !effect
    playBackgroundMusic()
  }

  def isTotalMute: Boolean = backgroundMute && soundeffectMute

  def setTotalMute(state: Boolean): Unit = {
    backgroundMute = state
    soundeffectMute = state

    playBackgroundMusic()
  }


  def hitShip(): Unit = {
    if (!soundeffectMute) {
      backgroundMusicPlayer.stop()
      initMediaPlayer(shipHitMedia).play()
      playBackgroundMusic()
    }
  }

  def playBackgroundMusic(): Unit = {
    if (!backgroundMute)
      backgroundMusicPlayer.play()
    else
      backgroundMusicPlayer.pause()
  }

  private def initMediaPlayer(media: Media): MediaPlayer = {
    val player: MediaPlayer = new MediaPlayer(media)
    player
  }

  def hitWater(): Unit = {
    if (!soundeffectMute) {
      backgroundMusicPlayer.stop()
      initMediaPlayer(waterHitMedia).play()
      playBackgroundMusic()
    }
  }

}

