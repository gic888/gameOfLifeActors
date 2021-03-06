package com.symbolscope.gic.gol

import io.netty.channel.Channel

/**
 * Message enums
 */
trait Message

case object Randomize extends Message

case object Connect extends Message

case object Hello extends Message

case class Announce(toAll: Boolean) extends Message
case class SetState(alive: Boolean) extends Message
case class State(i: Int, j: Int, alive: Boolean) extends Message
case class DearJohn(i: Int, j: Int) extends Message
case class RegisterChannel(channel: Channel) extends Message

