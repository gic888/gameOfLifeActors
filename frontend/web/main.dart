// Copyright (c) 2015, <your name>. All rights reserved. Use of this source code
// is governed by a BSD-style license that can be found in the LICENSE file.
import 'dart:html';
import 'dart:async';
import 'dart:convert';

WebSocket ws;

log(String msg) {
  var output = querySelector('#log');
  var text = msg;
  output.text = text;
}

void drawResult(int i, int j, bool on) {
  log(i.toString());

}

void initWebSocket([int retrySeconds = 2]) {
  var reconnectScheduled = false;
  var jsonDecoder = new JsonDecoder();

  log("Connecting to websocket");
  ws = new WebSocket('ws://localhost:9000');

  void scheduleReconnect() {
    if (!reconnectScheduled) {
      new Timer(new Duration(milliseconds: 1000 * retrySeconds), () => initWebSocket(retrySeconds * 2));
    }
    reconnectScheduled = true;
  }

  ws.onOpen.listen((e) {
    log('Connected');
  });

  ws.onClose.listen((e) {
    log('Websocket closed, retrying in $retrySeconds seconds');
    scheduleReconnect();
  });

  ws.onError.listen((e) {
    log("Error connecting to ws");
    scheduleReconnect();
  });

  ws.onMessage.listen((MessageEvent e) {
    log('Received message: ${e.data}');
    var o = jsonDecoder.convert(e.data);
    drawResult(o.x, o.y, o.state);
  });
}

void main() {
  initWebSocket();
}