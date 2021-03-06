package gamer.chess;

class AlgebraicNotation {
  static ChessMove parse(ChessState state, String moveStr) {
    MoveComponents components = new MoveComponents(moveStr);
    ChessMove move = null;

    for (ChessMove m : state.getMoves()) {
      if (m.to == components.to &&
          Pieces.piece(state.get(m.from)) == components.piece &&
          m.promote == components.promote &&
          (components.fromCell == -1 || m.from == components.fromCell) &&
          (components.fromRow == 0 ||
           Board.i2row(m.from) == components.fromRow) &&
          (components.fromCol == 0 ||
           Board.i2col(m.from) == components.fromCol)) {
        if (move != null) {
          throw new RuntimeException(
              "Ambiguous move notation: " + moveStr +
              " (alternatives: " + move.toString() + " " + m.toString());
        }
        move = m;
      }
    }

    if (move == null)
      throw new RuntimeException("Illegal move: " + moveStr);

    return move;
  }

  static String moveToString(ChessState state, ChessMove move) {
    byte piece = Pieces.piece(state.get(move.from));
    if (piece == Pieces.KING) {
      if (move.to == move.from + 16) {
        return "O-O";
      }
      if (move.to == move.from - 16) {
        return "O-O-O";
      }
    }

    StringBuilder builder = new StringBuilder();

    if (piece != Pieces.PAWN) {
      builder.append(Pieces.PIECE_LETTER[piece]);
    } else if (state.get(move.to) != Pieces.EMPTY) {
      builder.append(Board.i2cola(move.from));
    }

    // TODO: disambiguation

    if (state.get(move.to) != Pieces.EMPTY) {
      builder.append("x");
    }

    builder.append(Board.i2a(move.to));

    if (move.promote != 0) {
      builder.append("=");
      builder.append(Pieces.PIECE_LETTER[move.promote]);
    }

    return builder.toString();
  }

  private static class MoveComponents {
    boolean shortCastling = false;
    boolean longCastling = false;
    byte piece = Pieces.EMPTY;
    int fromRow = 0;
    int fromCol = 0;
    int fromCell = -1;
    int to = 0;
    boolean take = false;
    byte promote = Pieces.EMPTY;
    boolean check;

    String str;
    int idx = 0;

    MoveComponents(String moveStr) {
      this.str = moveStr;
      parseMove();
    }

    private char current() {
      return str.charAt(idx);
    }

    private char next() {
      char c = current();
      idx++;
      return c;
    }

    private void back() {
      idx--;
    }

    private boolean end() {
      return idx >= str.length();
    }

    private void parseMove() {
      if (parseCastling())
        return;

      piece = parsePiece();
      if (piece == Pieces.EMPTY) {
        piece = Pieces.PAWN;
      }

      int cell = parseCell();

      if (cell >= 0 && (end() || current() == '=')) {
        to = cell;
      } else {
        if (cell >= 0) {
          fromCell = cell;
        } else {
          fromCol = parseCol();
          fromRow = parseRow();
        }

        if (current() == 'x') {
          take = true;
          next();
        }

        to = parseCell();
        if (to == -1) {
          throw new RuntimeException("can't parse the move: " + str);
        }
      }

      if (!end() && current() == '=') {
        next();
        promote = parsePiece();
        if (promote == Pieces.EMPTY)
          throw new RuntimeException("can't parse the move: " + str);
      }

      if (!end() && current() == '+') {
        next();
        check = true;
      }

      if (!end())
        throw new RuntimeException("can't parse the move: " + str);
    }

    private boolean parseCastling() {
      if (str.equals("O-O")) {
        shortCastling = true;
        return true;
      }

      if (str.equals("O-O-O")) {
        longCastling = true;
        return true;
      }

      return false;
    }

    private byte parsePiece() {
      switch (next()) {
        case 'K': return Pieces.KING;
        case 'Q': return Pieces.QUEEN;
        case 'R': return Pieces.ROOK;
        case 'B': return Pieces.BISHOP;
        case 'N': return Pieces.KNIGHT;

        default:
          back();
          return Pieces.EMPTY;
      }
    }

    private int parseCell() {
      int col = parseCol();
      if (col == 0)
        return -1;
      int row = parseRow();
      if (row == 0) {
        back();
        return -1;
      }
      return Board.cr2i(col, row);
    }

    private int parseCol() {
      char c = next();
      if ('a' <= c && c <= 'h')
        return c - 'a' + 1;
      back();
      return 0;
    }

    private int parseRow() {
      char c = next();
      if ('1' <= c && c <= '8')
        return c - '1' + 1;
      back();
      return 0;
    }
  }

}