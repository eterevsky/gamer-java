package gamer.chess;

import static gamer.def.GameStatus.WIN;
import static gamer.def.GameStatus.LOSS;
import static gamer.def.GameStatus.DRAW;

class EndingTable {
  private int n;
  private EndingTable previous;
  // 0       - unknown
  // 1-127   - win in N half-moves
  // 128-254 - lost in N-128 moves
  // 255     - draw
  private byte[] table;

  EndingTable(int npieces, EndingTable previous) {
    n = npieces;
    this.previous = previous;
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

  ChessState decode(long idx) {
    return new ChessState();
  }

  void generate() {
    table = new byte[length()];

    for (long i = 0; i < table.length; i++) {
      ChessState state = decode(i);
      switch (state.status()) {
        case WIN:  // Illegal position.
          table[i] = 1;
          break;

        case LOSS:
          table[i] = 128;
          break;

        case DRAW:
          table[i] = 255;
          break;

        default:
          table[i] = 0;
      }
    }

    int move = 0;
    while (true) {
      move++;
    }
  }
}
