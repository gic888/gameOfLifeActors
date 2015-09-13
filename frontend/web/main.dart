// Copyright (c) 2015, <your name>. All rights reserved. Use of this source code
// is governed by a BSD-style license that can be found in the LICENSE file.
import 'dart:html';
import 'dart:async';
import 'dart:math';
import 'dart:convert';

typedef void Receive(Map<String, dynamic> message);
typedef void Logger(String);

class GameDisplay {
  CanvasElement canvas;
  int maxVal;
  int width;
  int dotSize;
  Random rand = new Random(1000);
  CanvasRenderingContext2D context;

  GameDisplay(this.canvas) {
    maxVal = 1;
    dotSize = 10;
    width = canvas.width;
    context = canvas.context2D;
  }

  void drawResult(Map<String, dynamic> message) {
    int i = message["x"];
    int j = message["y"];
    if (i > maxVal) {
      maxVal = i;
    }
    if (j > maxVal) {
      maxVal = j;
    }
    bool state = message["state"];
    state ? context.setFillColorRgb(0, 0, 0) : context.setFillColorRgb(220, 220, 220);
    context.fillRect((i - 1) * dotSize, (j - 1) * dotSize, dotSize, dotSize);
  }

  Point randomPoint() {
    return new Point(rand.nextInt(maxVal), rand.nextInt(maxVal));
  }

  void highlight(Point p) {
    context.setFillColorRgb(255, 0, 0);
    context.fillRect((p.x - 1) * dotSize, (p.y - 1) * dotSize, 3 * dotSize, 3 * dotSize);
  }
}


class Messaging {
  bool reconnecting;
  JsonDecoder jsonDecoder;
  JsonEncoder jsonEncoder;
  WebSocket ws;
  Receive receiver;
  Logger logger;
  int retryTime;
  String address;

  Messaging(this.receiver, this.address, this.logger) {
    jsonDecoder = new JsonDecoder();
    jsonEncoder = new JsonEncoder();
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
      //logger('Received message: ${e.data}');
      Map<String, dynamic> o = jsonDecoder.convert(e.data);
      receiver(o);
    });
  }

  void send(Map<String, dynamic> message) {
    ws.sendString(jsonEncoder.convert(message));
  }
}


class MessageSender {

}

void main() {
  void log(String msg) {
    var output = querySelector('#log');
    var text = msg;
    output.text = text;
  }
  void discard(Map<String, dynamic> message) {};

  var inAddy= 'ws://localhost:9000';
  var outAddy = 'ws://localhost:9001';

  var game = new GameDisplay(querySelector("#draw"));
  new Messaging(game.drawResult, inAddy, log);
  var sender = new Messaging(discard, outAddy, log);

  void randomize(Event e) {
    log("sending randomize");
    sender.send({"action": "randomize"});

  }

  void kill(Event e) {
    var p = game.randomPoint();
    log("sending kill ${p.x}, ${p.y}");
    sender.send({"action": "kill", "x": p.x + 1  , "y": p.y + 1});
    game.highlight(p);

  }

  querySelector('#randomize').onClick.listen(randomize);
  querySelector('#kill').onClick.listen(kill);


}

