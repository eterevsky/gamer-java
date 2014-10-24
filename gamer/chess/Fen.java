package gamer.chess;

import static gamer.chess.Pieces.EMPTY;
import static gamer.chess.Pieces.PAWN;
import static gamer.chess.Pieces.ROOK;
import static gamer.chess.Pieces.KNIGHT;
import static gamer.chess.Pieces.BISHOP;
import static gamer.chess.Pieces.QUEEN;
import static gamer.chess.Pieces.KING;
import static gamer.chess.Pieces.WHITE;
import static gamer.chess.Pieces.BLACK;

class Fen {
  private static class Parser {
    private String fen;
    private int idx;

    Parser(String fen) {
      this.fen = fen;
      idx = 0;
    }

    StateBuilder parse() {
      StateBuilder builder = new StateBuilder();

      int cell = 0;
      for (char c : getComponent().toCharArray()) {
        switch (c) {
          case 'P': builder.set(tp(cell++), WHITE | PAWN); break;
          case 'R': builder.set(tp(cell++), WHITE | ROOK); break;
          case 'N': builder.set(tp(cell++), WHITE | KNIGHT); break;
          case 'B': builder.set(tp(cell++), WHITE | BISHOP); break;
          case 'Q': builder.set(tp(cell++), WHITE | QUEEN); break;
          case 'K': builder.set(tp(cell++), WHITE | KING); break;
          case 'p': builder.set(tp(cell++), BLACK | PAWN); break;
          case 'r': builder.set(tp(cell++), BLACK | ROOK); break;
          case 'n': builder.set(tp(cell++), BLACK | KNIGHT); break;
          case 'b': builder.set(tp(cell++), BLACK | BISHOP); break;
          case 'q': builder.set(tp(cell++), BLACK | QUEEN); break;
          case 'k': builder.set(tp(cell++), BLACK | KING); break;

          case '1': case '2': case '3': case '4':
          case '5': case '6': case '7': case '8':
            for (int i = 0; i < c - '0'; i++)
              builder.set(tp(cell++), EMPTY);
            break;

          case '/':
            assert cell % 8 == 0;
            break;

          default:
            throw new RuntimeException("Can't parse: " + fen);
        }
      }

      switch (getComponent()) {
        case "w": builder.setPlayer(true); break;
        case "b": builder.setPlayer(false); break;
        default:
          throw new RuntimeException("Can't parse: " + fen);
      }

      byte castlings = 0;
      for (char c : getComponent().toCharArray()) {
        switch (c) {
          case 'K': castlings |= State.WHITE_SHORT_CASTLING; break;
          case 'Q': castlings |= State.WHITE_LONG_CASTLING; break;
          case 'k': castlings |= State.BLACK_SHORT_CASTLING; break;
          case 'q': castlings |= State.BLACK_LONG_CASTLING; break;

          case '-': assert castlings == 0; break;

          default:
            throw new RuntimeException("Can't parse: " + fen);
        }
      }
      builder.setCastlings(castlings);

      String enPassant = getComponent();
      if (!enPassant.equals("-"))
        builder.setEnPassant(Board.a2i(enPassant));

      String movesWithoutCapture = getComponent();
      if (!movesWithoutCapture.equals(""))
        builder.setMovesSinceCapture(Integer.parseInt(movesWithoutCapture));

      String totalMoves = getComponent();
      if (!totalMoves.equals(""))
        builder.setMovesCount(Integer.parseInt(totalMoves) * 2 +
                              (builder.getPlayer() ? 0 : 1));

      return builder;
    }

    static private int tp(int cell) {
      int row = 8 - cell / 8;
      int col = cell % 8 + 1;
      return Board.cr2i(col, row);
    }

    private String getComponent() {
      while (idx < fen.length() && Character.isWhitespace(fen.charAt(idx))) {
        idx++;
      }

      int start = idx;
      while (idx < fen.length() && !Character.isWhitespace(fen.charAt(idx))) {
        idx++;
      }

      return fen.substring(start, idx);
    }
  }

  static StateBuilder parse(String fen) {
    Parser parser = new Parser(fen);
    return parser.parse();
  }
}