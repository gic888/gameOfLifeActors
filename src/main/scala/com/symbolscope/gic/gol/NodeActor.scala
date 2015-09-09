package com.symbolscope.gic.gol

import akka.actor.{ActorRef, Actor}
import scala.collection.mutable.{HashMap => MutMap}
import scala.util.Random

/**
 * node actor
 */
class NodeActor(val i: Int, val j: Int, val size: (Int, Int), val output: ActorRef) extends Actor {
  val neighborStates = MutMap[(Int, Int), Boolean]()
  val random = new Random()
  var state = random.nextBoolean()
  val neighbors: Seq[(Int, Int)] = for {
    dx <- -1 to 1
    dy <- -1 to 1
    if dx != 0 || dy != 0
    x = i + dx
    y = i + dy
    if x > 0
    if x < size._1
    if y > 0
    if y < size._2
  } yield (x, y)

  
  def announce() {
    val msg = State(i, j, state)
    output.tell(msg, self)
    for (coordinates <- neighbors) {
      val path = Paths.nodePath(coordinates._1, coordinates._2)
      context.actorSelection(s"../$path").tell(msg, self)
    }
  }

  def setState(newState: Boolean): Unit = {
    if (newState != state) {
      state = newState
      announce()
    }
  }

  def checkState(): Unit = {
    if (neighborStates.size < neighbors.size) {
      return
    }
    val count = neighborStates.values.count(x => x)
    if (count == 3) {
      setState(true)
    } else if (count < 2 || count > 3) {
      setState(false)
    }
  }

  def receive = {
    case Randomize =>
      setState(random.nextBoolean());
    case Anounce =>
      announce()
    case State(x, y, s) =>
      neighborStates((x, y)) = s
      checkState()
  }

}
