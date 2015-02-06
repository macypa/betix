package betix.scala

import betix.core.Configuration
import org.sikuli.basics.SikuliScript
import org.sikuli.script.{App, FindFailed, Pattern, Screen}
import org.slf4j.{Logger, LoggerFactory}

class Login {
  private val logger: Logger = LoggerFactory.getLogger(classOf[Login])

  def main(args: Array[String]) {
    println("Hello, world!");
  }

  def checkLogin(config: Configuration, screen: Screen) {
    App.focus(config.getConfigAsString("browser"))

    try {
      screen.find(new Pattern("img/logout.png"))
      logger.info("You're logged in.")
    }
    catch {
      case e: FindFailed => {
        SikuliScript.popup("You're NOT logged in.")
        logger.error("Not logged in!")
        tryLogin(config, screen)
      }
    }
  }

  def tryLogin(config: Configuration, screen: Screen) {
    App.focus(config.getConfigAsString(Configuration.Keys.browser.name))

    try {
      screen.find(new Pattern("img/logout.png"))
      logger.info("You're logged in.")
    }
    catch {
      case e: FindFailed => {
        SikuliScript.popup("You're NOT logged in.")
        logger.error("Not logged in!")
        System.exit(1)
      }
    }
  }
}
