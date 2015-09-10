package com.symbolscope.gic.gol

/**
 * Message enums
 */
trait Message

case object Randomize extends Message

case object Connect extends Message

case object Hello extends Message

case class Anounce(toAll: Boolean) extends Message
case class SetState(alive: Boolean) extends Message
case class State(i: Int, j: Int, alive: Boolean) extends Message

