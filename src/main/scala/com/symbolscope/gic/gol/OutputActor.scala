package com.symbolscope.gic.gol

import akka.actor.{Props, Actor}
import akka.event.Logging
import io.netty.channel.Channel
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame

import scala.collection.mutable.{HashSet => MutSet}

/**
 * Communication into the Wide World from the system
 */
class OutputActor extends Actor {
  val logger = Logging(context.system, this)
  val channels = MutSet[Channel]()
  val printer = context.actorOf(Props[PrintingActor], "printer")

  def receive = {
    case State(i, j, alive) =>
      logger.info(s"$i $j -> $alive")
      publish(Map("x" -> i, "y" -> j, "state" -> alive))
      printer.tell(Array[Integer](i, j, if (alive) 1 else 0), self)
    case RegisterChannel(c) =>
      channels.add(c)
    case m =>
      printer.tell(m.toString(), self)
  }

  def publish(m: Map[String, Any]): Unit = {
    val json = Json.mapToJson(m)
    //logger.info(json)
    channels.foreach(c => c.writeAndFlush(new TextWebSocketFrame(json)))
  }

}
