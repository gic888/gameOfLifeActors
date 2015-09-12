// Copyright (c) 2015, <your name>. All rights reserved. Use of this source code
// is governed by a BSD-style license that can be found in the LICENSE file.
import 'dart:html';
import 'dart:async';
import 'dart:convert';

typedef void Receive(Map<String, dynamic> message);
typedef void Logger(String);

class GameDisplay {
  CanvasElement canvas;
  int dotSize;
  int maxVal;
  int width;
  CanvasRenderingContext2D context;

  GameDisplay(this.canvas) {
    dotSize = 10;
    maxVal = 3;
    width = canvas.width;
    context = canvas.context2D;
  }

  void drawResult(Map<String, dynamic> message) {
    int i = message["x"];
    int j = message["y"];
    maxVal = i > maxVal ? i : maxVal;
    maxVal = j > maxVal ? j : maxVal;
    dotSize = (width / maxVal).round();
    bool state = message["state"];
    if (state) {
      context.setFillColorRgb(0, 0, 0);
    } else {
      context.setFillColorRgb(200, 200, 200);
    }
    context.fillRect((i -1)*dotSize, (j-1)*dotSize, dotSize, dotSize);
  }
}


class MessageReceiver {
  bool reconnecting;
  JsonDecoder jsonDecoder;
  WebSocket ws;
  Receive receiver;
  Logger logger;
  int retryTime;
  String address;

  MessageReceiver(this.receiver, this.address, this.logger) {
    jsonDecoder = new JsonDecoder();
    retryTime = 4000; //milliseconds between connection attempts
    logger("Connecting to websocket at $address");
    connect();
  }

  void connect() {
    reconnecting = false;
    ws = new WebSocket(address);

    void reconnect() {
      if (!reconnecting) {
        new Timer(new Duration(milliseconds: retryTime), () => connect());
      }
      reconnecting = true;
    }

    ws.onOpen.listen((e) {
      logger("connected");
    });

    ws.onClose.listen((e) {
      logger('Websocket closed, retrying in $retryTime ms');
      reconnect();
    });

    ws.onError.listen((e) {
      logger("Error connecting to ws");
      reconnect();
    });

    ws.onMessage.listen((MessageEvent e) {
      logger('Received message: ${e.data}');
      Map<String, dynamic> o = jsonDecoder.convert(e.data);
      receiver(o);
    });
  }
}

void main() {
  void log(String msg) {
    var output = querySelector('#log');
    var text = msg;
    output.text = text;
  }
  var inAddy= 'ws://localhost:9000';
  var outAddy = 'ws://localhost:9001';

  var game = new GameDisplay(querySelector("#draw"));
  new MessageReceiver(game.drawResult, inAddy, log);
}

