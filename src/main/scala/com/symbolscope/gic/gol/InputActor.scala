package com.symbolscope.gic.gol

import akka.actor.Actor

/**
 * communication into the system from the Wide World
 */
class InputActor extends Actor {
  def receive = {
    case x => unhandled(x)
  }
}
