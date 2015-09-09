package com.symbolscope.gic.gol

import akka.actor.{Props, ActorSystem}


object Gol {
  def main(args: Array[String]) {
    val system = ActorSystem.create("gameOfLife")
    val output = system.actorOf(Props[OutputActor], "output")
    system.actorOf(Props[InputActor], "input")
    system.actorOf(Props(classOf[GameActor], output, 50, 50), "game")
  }
}