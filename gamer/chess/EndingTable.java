package gamer.chess;

import gamer.def.GameStatus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class EndingTable {
  private int n;
  private EndingTable previous;
  private byte[] table;

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
          return other == LOSS || other == DRAW;

        case WIN:
          return other == UNKNOWN || other == LOSS || other == DRAW;

        case LOSS:
          return false;

        case DRAW:
          return other == LOSS;
      }

      throw new RuntimeException("Can't happen");
    }
  }

  static class EndingValue {
    final EndingStatus status;
    final int moves;
    final byte byteValue;
    private static final List<EndingValue> instances;

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
      switch (state.status()) {
        case WIN:
          if (state.getPlayer()) {
            return of(EndingStatus.ILLEGAL);
          } else {
            return of(EndingStatus.LOSS, 0);
          }

        case LOSS:
          if (!state.getPlayer()) {
            return of(EndingStatus.ILLEGAL);
          } else {
            return of(EndingStatus.LOSS, 0);
          }

        case DRAW:
          return of(EndingStatus.DRAW);

        default:
          return of(EndingStatus.UNKNOWN);
      }
    }

    public byte asByte() {
      return byteValue;
    }

    private EndingValue(EndingStatus status, int moves) {
      this.status = status;
      this.moves = moves;
      this.byteValue = toByte(status, moves);
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

    static {
      instances = new ArrayList<EndingValue>(256);
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
  }

  EndingTable(int npieces, EndingTable previous) {
    n = npieces;
    this.previous = previous;
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
          ChessState next = state.play(move);

          EndingStatus status;
          if (state.isCapture(move)) {
            if (n == 3) {
              status = EndingStatus.DRAW;
            } else {
              status = this.previous.get(next).status;
            }
          } else {
            int inext = encode(next);
            EndingValue nextValue = EndingValue.fromByte(table[inext]);
            status = nextValue.status;
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

  static private ThreadLocal<List<Integer>> pieceCount = null;

  private int encode(ChessState state) {
    List<Integer> pieceCount = this.pieceCount.get();
    if (pieceCount == null) {
      piececCount = new ArrayList<>();
      for (int i = 0; i < 16; i++)
        pieceCount.add(0);
      this.pieceCount.set(pieceCount);
    }

    for (int i = 0; i < 16; i++)
      pieceCount.set(i, 0);

  }

  private ChessState decode(int idx) {
  }
}
