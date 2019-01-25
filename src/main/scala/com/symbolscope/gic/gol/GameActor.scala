package com.symbolscope.gic.gol

import java.util.Date

import akka.actor._
import akka.actor.SupervisorStrategy._
import scala.concurrent.duration._

import scala.util.Random

/**
 * main controller for the game
 */
class GameActor(output: ActorRef, input: ActorRef, width: Int, height: Int) extends Actor {
  Random.setSeed(new Date().getTime)

  override def preStart() {
    for (i <- 1 to width; j <- 1 to height) {
      context.actorOf(Props(classOf[NodeActor], i, j, output), Paths.nodePath(i, j))
    }
    context.children foreach (_.tell(SetState(Random.nextBoolean()), self))
    input.tell(Connect, self)
  }

  def receive = {
    case Randomize =>
      context.children.foreach(_.tell(SetState(Random.nextBoolean()), self))
    case ow: DearJohn =>
      context.actorSelection(Paths.nodePath(ow.i, ow.j)).tell(ow, sender())
    case x => unhandled(x)
  }

  override val supervisorStrategy: SupervisorStrategy =
    OneForOneStrategy(maxNrOfRetries = 100, withinTimeRange = new FiniteDuration(1, MINUTES)) {
      case _ =>
        Restart
    }
}
