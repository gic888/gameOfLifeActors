package com.symbolscope.gic.gol

import akka.actor.Actor
import akka.event.Logging
import org.jboss.netty.channel.group.DefaultChannelGroup

/**
 * Communication into the Wide World from the system
 */
class OutputActor extends Actor {
  val logger = Logging(context.system, this)
  val channels = new DefaultChannelGroup()
  def receive = {
    case State(i, j, alive) =>
      logger.info(s"$i $j -> $alive")
      publish(Map("x" -> i, "y" -> j, "state" -> alive))
    case RegisterChannel(channel) =>
      channels.add(channel)
  }

  def publish(m: Map[String, Any]): Unit = {
    val json = Json.mapToJson(m)
    logger.info(json)
    channels.write(json)
  }

}
