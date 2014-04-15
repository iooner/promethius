package backend

import play.api.Play
import play.api.Play.current

object Settings {

  val config = Play.configuration

  /**
   * How often to make polling updates
   *
   */
  val pollFrequency = config.getInt("ripley.pollingFrequency").getOrElse(5)
}
