package com.symbolscope.gic.gol

import akka.actor.{ActorRef, Actor}
import akka.event.Logging
import org.mashupbots.socko.events.WebSocketFrameEvent
import org.mashupbots.socko.routes.{WebSocketFrame, WebSocketHandshake, Routes}
import org.mashupbots.socko.webserver.{WebServerConfig, WebServer}

/**
 * communication into the system from the Wide World
 */
class InputActor extends Actor {
  var sendTo: ActorRef = _
  val logger = Logging(context.system, this)

  def receive: PartialFunction[Any, Unit] = {
    case Connect if sendTo == null =>
      sendTo = sender()
      startServer()
    case x => unhandled(x)
  }

  def startServer(): Unit = {

    val webServer = new WebServer(WebServerConfig("golInput", "localhost", 9001), Routes {
      case WebSocketHandshake(wsHandshake) =>
        wsHandshake.authorize()
      case  WebSocketFrame(wsFrame) =>
        decodeAndForward(wsFrame)
    }, context.system)
    webServer.start()
  }

  def decodeAndForward(event: WebSocketFrameEvent): Unit = {
    val text = event.readText()
    logger.info("got message" + text)
    try {
      val msg: Map[String, Any] = Json.jsonToMap(text)
      msg.get("action") match {
        case Some(s: String) => sendTo.tell(actionFor(s, msg), self)
        case _ => throw new Exception("Message doesn't specify a known 'action'")
      }
    } catch {
      case e: Exception =>
        logger.error("failed to parse and route request: " + text + " with: " + e.getMessage)
    }
  }

  def actionFor(action: String, options: Map[String, Any]): Message = {
    action match {
      case "randomize" =>
        Randomize
      case "kill" =>
        DearJohn(options("x").asInstanceOf[Int], options("y").asInstanceOf[Int])
      case _ =>
        throw new Exception("unknown action: " + action)
    }

  }

}
