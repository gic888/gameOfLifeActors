package com.symbolscope.gic.gol

import akka.actor.{Actor, ActorRef, ActorSelection}

import scala.collection.mutable.{HashMap => MutMap, HashSet => MutSet}
import scala.concurrent.duration._

/**
 * node actor
 */
class NodeActor(val i: Int, val j: Int, val output: ActorRef) extends Actor {
  val neighborStates = MutMap[String, Boolean]()
  val neighborRefs = MutSet[ActorRef]()
  var state = false
  var tick: FiniteDuration = FiniteDuration(500, MILLISECONDS)
  context.system.scheduler.schedule(tick, tick, self, Anounce(false))(context.system.dispatcher)

  def allPossibleNeighbors(): Seq[ActorSelection] = {
    for {
      dx <- -1 to 1
      dy <- -1 to 1
      if dx != 0 || dy != 0
      path = Paths.nodePath(i + dx, j + dy)
    } yield context.actorSelection(s"../$path")

  }

  def announce(toAll: Boolean) {
    val msg = State(i, j, state)
    output.tell(msg, self)
    if (toAll) {
      allPossibleNeighbors().foreach(_.tell(msg, self))
    } else {
      neighborRefs.foreach(_.tell(msg, self))
    }
  }

  def setState(newState: Boolean): Unit = {
    if (newState != state) {
      state = newState
    }
  }

  def checkState(): Unit = {
    if (neighborStates.size < 3) {
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
    case Connect =>
      allPossibleNeighbors().foreach(_.tell(Hello, self))
    case Hello =>
      neighborRefs.add(sender())
    case SetState(s) =>
      setState(s)
    case Anounce(toAll) =>
      announce(toAll)
    case State(x, y, s) =>
      neighborRefs.add(sender())
      neighborStates(sender().path.name) = s
      checkState()
  }

}
