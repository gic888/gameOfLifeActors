package com.symbolscope.gic.gol;

import akka.actor.UntypedActor;
import scala.concurrent.duration.FiniteDuration;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * a child of the OutputActor, which prints the game state
 */
public class PrintingActor extends UntypedActor {
    Integer width;
    Integer[][] board;

    public PrintingActor() {
        width = Gol.size();

        board = new Integer[width][width];
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < width; j++) {
                board[i][j] = 0;
            }
        }
        FiniteDuration tick = new FiniteDuration(Gol.tick(), TimeUnit.MILLISECONDS);
        //getContext().system().scheduler().schedule(tick, tick, self(), "PRINT BOARD"
        //        , getContext().system().dispatcher(), self());
    }

    @Override
    public void onReceive(Object message) throws Exception {
        if (message instanceof String) {
            String s = (String) message;
            if (s.equals("PRINT BOARD")) {
                System.out.println(boardAsString());
            } else {
                System.out.println("PRINTER: " + (String) message);
            }
        } else if (message instanceof Integer[] && ((Integer[]) message).length == 3) {
            Integer[] vals = (Integer[]) message;
            try {
                board[vals[0]-1][vals[1]-1] = vals[2];
            } catch (ArrayIndexOutOfBoundsException e) {
                System.out.println(String.format(
                        "Warning: point %d, %d set on board of size %d", vals[0], vals[1], width));
            }
        }
    }

    protected String boardAsString() {
        List<String> rows = new ArrayList<>();
        for (int i = 0; i < width; i++) {
            List<String> row = new ArrayList<>();
            for (int j = 0; j < width; j++) {
                row.add(board[i][j] == 1 ? "X" : " ");
            }
            rows.add(String.join("", row));
        }
        return String.join("\n", rows);
    }
}
