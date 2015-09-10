package com.symbolscope.gic.gol

import akka.actor.{ActorSystem, Props}
import com.typesafe.config.ConfigFactory


object Gol {
  def main(args: Array[String]) {
    val config = ConfigFactory.parseString("akka.log-dead-letters=off")
    val system = ActorSystem.create("gameOfLife", config)
    val output = system.actorOf(Props[OutputActor], "output")
    system.actorOf(Props[InputActor], "input")
    system.actorOf(Props(classOf[GameActor], output, 50, 50), "game")
  }
}