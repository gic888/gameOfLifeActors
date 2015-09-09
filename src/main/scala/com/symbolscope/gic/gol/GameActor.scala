package com.symbolscope.gic.gol

import akka.actor.{Props, ActorRef, Actor}

/**
 * main controller for the game
 */
class GameActor(output: ActorRef, width: Int, height: Int) extends Actor {

  override def preStart() {
    for (i <- 1 to width; j <- 1 to height) {
      context.actorOf(Props(classOf[NodeActor], i, j, (width, height), output), Paths.nodePath(i, j))
    }
    context.children foreach(_.tell(Anounce, self))
  }

  def receive = {
    case x => unhandled(x)
  }
}
