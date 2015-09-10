package com.symbolscope.gic.gol

import java.util.Date

import akka.actor.{Actor, ActorRef, Props}

import scala.util.Random

/**
 * main controller for the game
 */
class GameActor(output: ActorRef, width: Int, height: Int) extends Actor {
  Random.setSeed(new Date().getTime)

  override def preStart() {
    for (i <- 1 to width; j <- 1 to height) {
      context.actorOf(Props(classOf[NodeActor], i, j, output), Paths.nodePath(i, j))
    }
    context.children foreach (_.tell(SetState(Random.nextBoolean()), self))
    context.children foreach (_.tell(Anounce(true), self))
  }

  def receive = {
    case x => unhandled(x)
  }
}
