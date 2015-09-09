package com.symbolscope.gic.gol

/**
 * Message enums
 */
trait Message

case object Randomize extends Message
case object Anounce extends Message
case class Set(alive: Boolean) extends Message
case class State(i: Int, j: Int, alive: Boolean) extends Message

