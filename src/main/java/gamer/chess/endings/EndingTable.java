package gamer.chess.endings;

import gamer.chess.ChessMove;
import gamer.chess.ChessState;
import gamer.chess.Pieces;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class EndingTable {
  private int n;
  private EndingTable previous;
  private EndingTable dual;
  private byte[] table;

  EndingTable(int npieces, EndingTable previous) {
    n = npieces;
    this.previous = previous;
  }

  void setDual(EndingTable dualTable) {
    dual = dualTable;
  }

  private EndingValue get(int idx) {
    return EndingValue.fromByte(table[idx]);
  }

  EndingValue get(ChessState state) {
    return get(encode(state));
  }

  long length() {
    long t = 1;
    for (int i = 64; i > 64 - n; i--) {
      t *= i;
    }

    t /= 2;

    for (int i = 10 + n - 3; i > 9; i--) {
      t *= i;
    }

    for (int i = 2; i <= n - 2; i++) {
      t /= i;
    }

    return t;
  }

  void generate() {
    table = new byte[(int)length()];

    Map<EndingStatus, Long> count = new HashMap<>();
    count.put(EndingStatus.UNKNOWN, 0L);
    count.put(EndingStatus.WIN, 0L);
    count.put(EndingStatus.LOSS, 0L);
    count.put(EndingStatus.DRAW, 0L);
    count.put(EndingStatus.ILLEGAL, 0L);
    for (int i = 0; i < table.length; i++) {
      EndingValue evalue = EndingValue.fromState(decode(i));
      table[i] = evalue.asByte();
      count.put(evalue.status, count.get(evalue.status) + 1);
    }

    for (EndingStatus status : count.keySet()) {
      System.out.format("%s %d\n", status, count.get(status));
    }

    int moves = 0;
    long win = 1, loss = 0, draw = 0;
    while (win + loss + draw > 0 && moves < 126) {
      win = 0;
      loss = 0;
      draw = 0;
      moves++;
      EndingStatus bestNext = EndingStatus.WIN;
      for (int i = 0; i < table.length; i++) {
        EndingValue evalue = EndingValue.fromByte(table[i]);
        if (evalue.status != EndingStatus.UNKNOWN)
          continue;

        ChessState state = decode(i);
        for (ChessMove move : state.getMoves()) {
          ChessState next = state.clone();
          next.play(move);

          EndingStatus status;
          int moveTo = move.to;
          if (Pieces.isEmpty(state.get(moveTo))) {
            if (n == 3) {
              status = EndingStatus.DRAW;
            } else {
              status = this.previous.get(next).status;
            }
          } else {
            status = dual.get(next).status;
          }
          assert status != EndingStatus.ILLEGAL;

          if (status.worseThan(bestNext)) {
            bestNext = status;
          }
        }

        switch (bestNext) {
          case WIN:
            table[i] = EndingValue.of(EndingStatus.LOSS, moves).asByte();
            loss++;
            break;

          case LOSS:
            table[i] = EndingValue.of(EndingStatus.WIN, moves).asByte();
            win++;
            break;

          case DRAW:
            table[i] = EndingValue.of(EndingStatus.DRAW).asByte();
            draw++;
            break;

          case UNKNOWN:
          case ILLEGAL:
            throw new RuntimeException();
        }
      }

      System.out.format("Move #%d: win %d, loss %d, draw %d\n",
                         moves, win, loss, draw);
    }

    draw = 0;
    for (int i = 0; i < table.length; i++) {
      EndingValue evalue = EndingValue.fromByte(table[i]);
      if (evalue.status == EndingStatus.UNKNOWN) {
        table[i] = EndingValue.of(EndingStatus.DRAW).asByte();
        draw++;
      }
    }

    System.out.format("Draws from unknown: %d\n", draw);
  }

  private int encode(ChessState state) {
    return 0;
  }

  private ChessState decode(int idx) {
    return new ChessState();
  }

  enum EndingStatus {
    UNKNOWN,
    WIN,
    LOSS,
    DRAW,
    ILLEGAL;

    boolean worseThan(EndingStatus other) {
      assert this != ILLEGAL;
      assert other != ILLEGAL;
      switch (this) {
        case UNKNOWN:
          return other == EndingStatus.LOSS || other == EndingStatus.DRAW;

        case WIN:
          return other == EndingStatus.UNKNOWN ||
                 other == EndingStatus.LOSS ||
                 other == EndingStatus.DRAW;

        case LOSS:
          return false;

        case DRAW:
          return other == EndingStatus.LOSS;

        case ILLEGAL:
          throw new RuntimeException();
      }

      throw new RuntimeException("Can't happen");
    }
  }

  static class EndingValue {
    private static final List<EndingValue> instances;

    static {
      instances = new ArrayList<>(256);
      instances.add(new EndingValue(EndingStatus.UNKNOWN, 0));
      for (int i = 1; i < 127; i++) {
        instances.add(new EndingValue(EndingStatus.WIN, i));
      }
      instances.add(new EndingValue(EndingStatus.ILLEGAL, 0));
      for (int i = 0; i < 127; i++) {
        instances.add(new EndingValue(EndingStatus.LOSS, i));
      }
      instances.add(new EndingValue(EndingStatus.DRAW, 0));

      for (int i = -128; i < 128; i++) {
        assert fromByte((byte)i).asByte() == i;
      }
    }

    final EndingStatus status;
    final int moves;
    final byte byteValue;

    private EndingValue(EndingStatus status, int moves) {
      this.status = status;
      this.moves = moves;
      this.byteValue = toByte(status, moves);
    }

    static public EndingValue of(EndingStatus status) {
      assert status != EndingStatus.WIN;
      assert status != EndingStatus.LOSS;
      return fromByte(toByte(status, 0));
    }

    static public EndingValue of(EndingStatus status, int moves) {
      return fromByte(toByte(status, moves));
    }

    static public EndingValue fromByte(byte i) {
      return instances.get(i + 128);
    }

    static public EndingValue fromState(ChessState state) {
      switch (state.getPayoff(0)) {
        case 1:
          if (state.getPlayerBool()) {
            return of(EndingStatus.ILLEGAL);
          } else {
            return of(EndingStatus.LOSS, 0);
          }

        case -1:
          if (!state.getPlayerBool()) {
            return of(EndingStatus.ILLEGAL);
          } else {
            return of(EndingStatus.LOSS, 0);
          }

        case 0:
          return of(EndingStatus.DRAW);

        default:
          return of(EndingStatus.UNKNOWN);
      }
    }

    private static byte toByte(EndingStatus status, int moves) {
      switch (status) {
        case UNKNOWN:
          return -128;

        case WIN:
          assert moves > 0;
          assert moves < 127;
          return (byte) (moves - 128);

        case LOSS:
          assert moves >= 0;
          assert moves < 127;
          return (byte) moves;

        case DRAW:
          return 127;

        case ILLEGAL:
          return -1;
      }

      throw new RuntimeException("Can't happen");
    }

    public byte asByte() {
      return byteValue;
    }
  }
}
