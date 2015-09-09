package com.symbolscope.gic.gol

import akka.actor.Actor

/**
 * Communication into the Wide World from the system
 */
class OutputActor extends Actor {
  def receive = {
    case State(i, j, alive) => println(s"$i $j -> $alive")
  }

}
