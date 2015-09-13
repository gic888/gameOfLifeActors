package com.symbolscope.gic.gol

import java.util.logging.{Handler, Level, Logger}

import akka.actor.{ActorSystem, Props}
import com.typesafe.config.ConfigFactory
import io.netty.channel.Channel
import io.netty.handler.logging.LogLevel
import org.mashupbots.socko.routes.{Routes, WebSocketHandshake}
import org.mashupbots.socko.webserver.{WebServer, WebServerConfig}


object Gol {
  val size = 60;
  val tick = 2000;

  def main(args: Array[String]) {
    val config = ConfigFactory.parseString(
      """
      akka.event-handlers = ["akka.event.slf4j.Slf4jEventHandler"]
      akka.log-dead-letters=off
      akka.loglevel=WARNING
      """)
    implicit val system = ActorSystem.create("gameOfLife", config)
    val output = system.actorOf(Props[OutputActor], "output")
    system.actorOf(Props[InputActor], "input")
    system.actorOf(Props(classOf[GameActor], output, size, size), "game")
    val webServer = new WebServer(WebServerConfig("gol", "localhost", 9000), Routes {
      case WebSocketHandshake(wsHandshake) =>
        wsHandshake.authorize()
        var chan: Channel = wsHandshake.context.channel()
        output ! RegisterChannel(chan)
      case _ =>
        throw new UnsupportedOperationException("Huh?")
    }, system)
    webServer.start()
  }
}