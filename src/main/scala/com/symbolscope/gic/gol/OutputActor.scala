package com.symbolscope.gic.gol

import akka.actor.Actor
import akka.event.Logging

/**
 * Communication into the Wide World from the system
 */
class OutputActor extends Actor {
  val logger = Logging(context.system, this)
  def receive = {
    case State(i, j, alive) =>
      logger.info(s"$i $j -> $alive")
      publish(Map("x" -> i, "y" -> j, "state" -> alive))
  }

  def publish(m: Map[String, Any]): Unit = {
    val json = Json.mapToJson(m)
    logger.info(json)

  }

}
