## A Web Game of Life using Actors and Websockets

This is a simple project with an server running a Akka actor system, which plays Conway's Game of Life. Each node in the game is represented by an actor. These nodes broadcast state changes. A server exposes the message stream to web clients via a websocket. A separate socket allows the clients to send in messages to reset the game, or to destroy one of the actors.

The purpose of the demo is to show full stack async communication with an actor system, and self healing of a coupled actor system when units fail. 