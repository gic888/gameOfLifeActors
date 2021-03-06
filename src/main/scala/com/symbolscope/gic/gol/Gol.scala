package com.symbolscope.gic.gol

import akka.actor.{ActorSystem, Props}
import com.typesafe.config.ConfigFactory
import io.netty.channel.Channel
import org.mashupbots.socko.routes.{Routes, WebSocketHandshake}
import org.mashupbots.socko.webserver.{WebServer, WebServerConfig}


object Gol {
  val size = 60
  val tick = 1000

  def main(args: Array[String]) {
    val config = ConfigFactory.parseString(
      """
      akka.event-handlers = ["akka.event.slf4j.Slf4jEventHandler"]
      akka.log-dead-letters=off
      akka.loglevel=WARNING
      """)
    implicit val system = ActorSystem.create("gameOfLife", config)
    val output = system.actorOf(Props[OutputActor], "output")

    val input = system.actorOf(Props[InputActor], "input")
    system.actorOf(Props(classOf[GameActor], output, input, size, size), "game")

    val webServer = new WebServer(WebServerConfig("golOutput", "localhost", 9000), Routes {
      case WebSocketHandshake(wsHandshake) =>
        wsHandshake.authorize()
        val chan: Channel = wsHandshake.context.channel()
        output ! RegisterChannel(chan)
      case _ =>
        throw new UnsupportedOperationException("Huh?")
    }, system)
    webServer.start()
  }
}