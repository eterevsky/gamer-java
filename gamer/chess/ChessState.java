package gamer.chess;

import gamer.def.GameException;
import gamer.def.GameState;
import gamer.def.GameStatus;
import gamer.def.Move;

import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Random;

public final class ChessState implements GameState<Chess>, State {
  private final GameStatus status;
  private final byte[] boardBytes;
  private final byte castlings;
  private final int enPassant;  // -1 if no en passant pawn,
                                // otherwise the passed empty square
  private final int movesSinceCapture;
  private final int movesCount;

  private final List<ChessMove> moves;

  ChessState(StateBuilder builder) {
    status = builder.status();
    boardBytes = builder.getBoard().toBytes().clone();
    castlings = builder.getCastlings();
    enPassant = builder.getEnPassant();
    movesSinceCapture = builder.getMovesSinceCapture();
    movesCount = builder.getMovesCount();
    moves = builder.disownMoves();
  }

  ChessState() {
    this(new StateBuilder());
  }

  static ChessState fromFen(String fen) {
    return new ChessState(Fen.parse(fen));
  }

  // State implementation

  // @Override
  public Board getBoard() {
    return new Board(boardBytes.clone());
  }

  // @Override
  public byte getCastlings() {
    return castlings;
  }

  // @Override
  public int getEnPassant() {
    return enPassant;
  }

  // @Override
  public int getMovesSinceCapture() {
    return movesSinceCapture;
  }

  // @Override
  public int getMovesCount() {
    return movesCount;
  }

  // GameState<> implementation

  // @Override
  public boolean isTerminal() {
    return status.isTerminal();
  }

  // @Override
  public GameStatus status() {
    return status;
  }

  // @Override
  public final List<ChessMove> getMoves() {
    return moves;
  }

  // @Override
  public ChessMove getRandomMove(Random random) {
    return moves.get(random.nextInt(moves.size()));
  }

  // @Override
  public ChessState play(Move<Chess> moveInt) {
    ChessMove move = (ChessMove) moveInt;

    if (!moves.contains(move)) {
      throw new GameException("Illegal move");
    }

    StateBuilder builder = new StateBuilder(this);
    builder.applyMove(move);
    return new ChessState(builder);
  }

  // @Override
  public ChessState play(String moveStr) {
    ChessMove move = AlgebraicNotation.parse(this, moveStr);
    return play(move);
  }

  // @Override
  public String moveToString(Move<Chess> move) {
    return AlgebraicNotation.moveToString(this, (ChessMove) move);
  }

  // @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    for (int row = 8; row >= 1; row--) {
      for (int col = 1; col <= 8; col++) {
        builder.append(" " + Pieces.piece2a(boardBytes[Board.cr2i(col, row)]));
      }
      builder.append("\n");
    }
    return builder.toString();
  }

  // GameState implementation ends here.

  String moveToStringWithNumber(ChessMove move) {
    StringBuilder builder = new StringBuilder();
    int moves = movesCount / 2 + 1;
    builder.append(moves);
    builder.append(". ");

    if (status == GameStatus.SECOND_PLAYER)
      builder.append("... ");

    builder.append(moveToString(move));
    return builder.toString();
  }

  Iterable<Integer> iterate(final byte piece, final boolean player) {
    return new Iterable<Integer>() {
      private final byte pieceWithColor = Pieces.withColor(piece, player);

      public Iterator<Integer> iterator() {
        return new Iterator<Integer>() {
          private int idx = 0;

          private void findNext() {
            while (idx < 64 && boardBytes[idx] != pieceWithColor) {
              idx++;
            }
          }

          public boolean hasNext() {
            findNext();
            return idx < 64;
          }

          public Integer next() {
            if (!hasNext())
              throw new NoSuchElementException();
            return idx++;
          }

          public void remove() {
            throw new UnsupportedOperationException();
          }
        };
      }
    };
  }
}
