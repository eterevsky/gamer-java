package gamer.chess;

import static gamer.chess.Chess.CELLS;
import static gamer.chess.Chess.SIZE;

import gamer.def.GameException;
import gamer.def.GameState;
import gamer.def.GameStatus;

public final class ChessState implements GameState<Chess> {
  private static final byte PIECE_MASK = 7;
  private static final byte EMPTY = 0;
  private static final byte PAWN = 1;
  private static final byte ROOK = 2;
  private static final byte KNIGHT = 3;
  private static final byte BISHOP = 4;
  private static final byte QUEEN = 5;
  private static final byte KING = 6;
  private static final byte WHITE = 0x10;
  private static final byte EN_PASSANT = 0x20;

  private static final byte WHITE_SHORT_CASTLES = 1;
  private static final byte WHITE_LONG_CASTLES = 2;
  private static final byte BLACK_SHORT_CASTLES = 4;
  private static final byte BLACK_LONG_CASTLES = 8;

  private static final int MAX_DRY_MOVES = 150;

  private final byte[] board;
  private final int movesSinceTake;
  private final byte castles;
  private final GameStatus status;
  private final List<ChessMove> moves;

  private static byte[] hexStringToByteArray(String s) {
    int len = s.length();
    byte[] data = new byte[len / 2];
    for (int i = 0; i < len; i += 2) {
        data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                             + Character.digit(s.charAt(i+1), 16));
    }
    return data;
  }

  private static final byte[] INITIAL_BOARD = hexStringToByteArray(
      "1211000000000102" +
      "1311000000000103" +
      "1411000000000104" +
      "1511000000000105" +
      "1611000000000106" +
      "1411000000000104" +
      "1311000000000103" +
      "1211000000000102");

  ChessState() {
    board = INITIAL_BOARD;
    movesSinceTake = 0;
    castles = 15;
    status = GameStatus.FIRST_PLAYER;
    moves = generateMoves(true);
  }

  private ChessState(ChessState prev, ChessMove move) {
    boolean player = !prev.status.getPlayer();
    board = applyMove(prev.board, move);

    byte piece = prev.getPiece(move.from);
    if (piece == PAWN || prev.getPiece(move.to) != EMPTY) {
      movesSinceTake = 0;
    } else {
      movesSinceTake = prev.movesSinceTake + 1;
    }

    castles = newCastles(prev.castles, move);
    moves = generateMoves(player);

    if (moves.size() > 0 && movesSinceTake < MAX_DRY_MOVES) {
      status = player ? GameStatus.FIRST_PLAYER : GameStatus.SECOND_PLAYER;
      return;
    }

    if (isCheck(board, player) && moves.size() == 0) {
      status = player ? GameStatus.LOSS : GameStatus.WIN;
    } else {
      status = GameStatus.DRAW;
    }
  }

  public GameStatus status() {
    return status;
  }

  public ChessState play(Move<Chess> moveInt) {
    ChessMove move = (ChessMove) moveInt;
    if (!moves.contains(move)) {
      throw new GameException("Illegal move");
    }

    return new ChessState(this, move);
  }

  private boolean isEmpty(int cell) {
    return board[cell] == 0;
  }

  private boolean getColor(int cell) {
    return board[cell] & WHITE != 0;
  }

  private byte getPiece(int cell) {
    return board[cell] & PIECE_MASK;
  }

  private byte newCastles(byte prevCastles, ChessMove move) {
    byte castles = prevCastles;
    if (piece == ROOK) {
      switch (move.from) {
        case 0:  // a1
          castles &= ~WHITE_LONG_CASTLES;
          break;

        case 7:  // a8
          castles &= ~BLACK_LONG_CASTLES;
          break;

        case 56:  // h1
          castles &= ~WHITE_SHORT_CASTLES;
          break;

        case 63:  // h8
          castles &= ~BLACK_LONG_CASTLES;
          break;
      }
    }
    return castles;
  }

  // Apply move to the board, updating states of castles/en passant
  private byte[] applyMove(byte[] prevBoard, ChessMove move) {
    byte[] board = prevBoard.clone();
    byte piece = board[move.from] & PIECE_MASK;
    boolean player = 

    switch
  }

  private void generateMoves(boolean player) {
  }

  // true if check to (not by) player
  private boolean isCheck(byte[] board, boolean player) {
  }
}
