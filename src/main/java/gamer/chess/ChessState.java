package gamer.chess;

import gamer.def.GameException;
import gamer.def.Position;
import gamer.util.GameStatusInt;

import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Random;

public final class ChessState
    implements Position<ChessState, ChessMove>, State<ChessState> {
  private final byte[] boardBytes;
  private final byte castlings;
  private final int status;
  private final int enPassant;  // -1 if no en passant pawn,
                                // otherwise the passed empty square
  private final int movesSinceCapture;
  private final int movesCount;

  private final List<ChessMove> moves;

  public ChessState(StateBuilder builder) {
    int statusTemp = GameStatusInt.init();
    statusTemp = GameStatusInt.setPlayerBool(
        statusTemp, builder.getPlayerBool());
    if (builder.isTerminal()) {
      statusTemp = GameStatusInt.setPayoff(statusTemp, builder.getPayoff(0));
    }
    status = statusTemp;
    boardBytes = builder.getBoard().toBytes().clone();
    castlings = builder.getCastlings();
    enPassant = builder.getEnPassant();
    movesSinceCapture = builder.getMovesSinceCapture();
    movesCount = builder.getMovesCount();
    moves = builder.disownMoves();
  }

  public static ChessState fromFen(String fen) {
    return new ChessState(Fen.parse(fen));
  }

  public String asFen() {
    return Fen.toFen(this);
  }
	
	public StateBuilder toMutable() {
		return toBuilder();
	}

  // State implementation

  @Override
  public Board getBoard() {
    return new Board(boardBytes.clone());
  }

  @Override
  public byte getCastlings() {
    return castlings;
  }

  @Override
  public int getEnPassant() {
    return enPassant;
  }

  @Override
  public int getMovesSinceCapture() {
    return movesSinceCapture;
  }

  @Override
  public int getMovesCount() {
    return movesCount;
  }

  @Override
  public boolean getPlayerBool() {
    return GameStatusInt.getPlayerBool(status);
  }

  // Position<> implementation

  @Override
  public int getPlayer() {
    return getPlayerBool() ? 0 : 1;
  }

  @Override
  public boolean isTerminal() {
    return GameStatusInt.isTerminal(status);
  }

  @Override
  public int getPayoff(int player) {
    return GameStatusInt.getPayoff(status, player);
  }

  @Override
  public final List<ChessMove> getMoves() {
    return moves;
  }

  @Override
  public ChessMove getRandomMove(Random random) {
    return moves.get(random.nextInt(moves.size()));
  }

  @Override
  public ChessState play(ChessMove move) {
    if (!moves.contains(move)) {
      throw new GameException("Illegal move");
    }

    StateBuilder builder = this.toBuilder();
    builder.apply(move);
    return new ChessState(builder);
  }

  public ChessState play(String moveStr) {
    return play(parseMove(moveStr));
  }

  @Override
  public ChessMove parseMove(String moveStr) {
    return AlgebraicNotation.parse(this, moveStr);
  }

  @Override
  public String moveToString(ChessMove move) {
    return AlgebraicNotation.moveToString(this, move);
  }

  @Override
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

  StateBuilder toBuilder() {
    return new StateBuilder(this);
  }

  public boolean isCapture(ChessMove move) {
    return boardBytes[move.to] != Pieces.EMPTY;
  }

  String moveToStringWithNumber(ChessMove move) {
    StringBuilder builder = new StringBuilder();
    int moves = movesCount / 2 + 1;
    builder.append(moves);
    builder.append(". ");

    if (!GameStatusInt.getPlayerBool(status))
      builder.append("... ");

    builder.append(moveToString(move));
    return builder.toString();
  }

  Iterable<Integer> iterate(final byte piece, final boolean player) {
    return new Iterable<Integer>() {
      private final byte pieceWithColor = Pieces.withColor(piece, player);

      @Override
      public Iterator<Integer> iterator() {
        return new Iterator<Integer>() {
          private int idx = 0;

          private void findNext() {
            while (idx < 64 && boardBytes[idx] != pieceWithColor) {
              idx++;
            }
          }

          @Override
          public boolean hasNext() {
            findNext();
            return idx < 64;
          }

          @Override
          public Integer next() {
            if (!hasNext())
              throw new NoSuchElementException();
            return idx++;
          }

          @Override
          public void remove() {
            throw new UnsupportedOperationException();
          }
        };
      }
    };
  }
}
