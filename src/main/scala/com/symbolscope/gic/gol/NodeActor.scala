package com.symbolscope.gic.gol

import akka.actor.{Kill, Actor, ActorRef, ActorSelection}

import scala.collection.mutable.{HashMap => MutMap, HashSet => MutSet}
import scala.concurrent.duration._

/**
 * node actor
 */
class NodeActor(val i: Int, val j: Int, val output: ActorRef) extends Actor {
  val neighborStates = MutMap[String, Boolean]()
  val neighborRefs = MutSet[ActorRef]()
  var state = false
  var tick: FiniteDuration = FiniteDuration(Gol.tick, MILLISECONDS)
  implicit val dispatch = context.system.dispatcher
  context.system.scheduler.scheduleOnce(tick, self, Connect)
  context.system.scheduler.schedule(2 * tick, tick, self, Announce(false))
  System.out.println(s"$i, $j up and running")

  override def preStart(): Unit = {
    allPossibleNeighbors().foreach(_.tell(Hello, self))
  }

  def allPossibleNeighbors(): Seq[ActorSelection] = {
    for {
      dx <- -1 to 1
      dy <- -1 to 1
      if dx != 0 || dy != 0
      path = Paths.nodePath(i + dx, j + dy)
    } yield context.actorSelection(s"../$path")

  }

  def announce(toAll: Boolean) {
    //state = Random.nextBoolean()
    val msg = State(i, j, state)
    output.tell(msg, self)
    if (toAll) {
      allPossibleNeighbors().foreach(_.tell(msg, self))
    } else {
      neighborRefs.foreach(_.tell(msg, self))
    }
  }

  def checkState(): Unit = {
    if (neighborStates.size == neighborRefs.size) {
      val count = neighborStates.values.count(x => x)
      if (count < 2) {
        state = false
      } else if (count == 2) {
        state = state
      } else if (count == 3) {
        state = true
      } else if (count > 3) {
        state = false
      }
      neighborStates.clear()
    }
  }

  def receive = {
    case Connect =>
      allPossibleNeighbors().foreach(_.tell(Hello, self))
    case Hello =>
      neighborRefs.add(sender())
    case SetState(s) =>
      state = s
    case Announce(toAll) =>
      announce(toAll)
    case State(x, y, s) =>
      neighborRefs.add(sender())
      neighborStates(sender().path.name) = s
      checkState()
    case DearJohn(_, _) =>
      freakOutAndDie()
  }

  def freakOutAndDie(): Unit = {
    allPossibleNeighbors().foreach(_.tell("I hate you all", self))
    output.tell("I hate you all", self)
    System.out.println("Life sucks. I quit. -- " + self.path.name)
    self.tell(Kill, self)
    throw new Exception("Life sucks")
  }

}
