package com.symbolscope.gic.gol

import akka.actor.{ActorSystem, Props}
import com.typesafe.config.ConfigFactory
import org.mashupbots.socko.routes.{Routes, WebSocketHandshake}
import org.mashupbots.socko.webserver.{WebServer, WebServerConfig}


object Gol {
  def main(args: Array[String]) {
    val config = ConfigFactory.parseString("akka.log-dead-letters=off")
    implicit val system = ActorSystem.create("gameOfLife", config)
    val output = system.actorOf(Props[OutputActor], "output")
    system.actorOf(Props[InputActor], "input")
    system.actorOf(Props(classOf[GameActor], output, 50, 50), "game")

    val webServer = new WebServer(WebServerConfig(), Routes {
      case WebSocketHandshake(wsHandshake) =>
        wsHandshake.authorize()
        output ! wsHandshake.context.channel()
      case _ =>
        throw new UnsupportedOperationException("Huh?")
    }, system)

    webServer.start()
  }
}