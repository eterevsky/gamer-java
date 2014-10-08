package gamer.chess;

import static gamer.chess.Chess.CELLS;
import static gamer.chess.Chess.SIZE;
import static gamer.chess.Util.a2i;
import static gamer.chess.Util.i2a;
import static gamer.chess.Util.i2col;
import static gamer.chess.Util.i2row;

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
  private static final byte BLACK = 0;

  private static final byte WHITE_SHORT_CASTLING = 1;
  private static final byte WHITE_LONG_CASTLING = 2;
  private static final byte BLACK_SHORT_CASTLING = 4;
  private static final byte BLACK_LONG_CASTLING = 8;

  private static final int MOVES_WITHOUT_CAPTURE = 150;
  
  private static final byte[] INITIAL_BOARD = Util.hexStringToByteArray(
      "1211000000000102" +
      "1311000000000103" +
      "1411000000000104" +
      "1511000000000105" +
      "1611000000000106" +
      "1411000000000104" +
      "1311000000000103" +
      "1211000000000102");

  private final GameStatus status;
  private final byte[] board;
  private final byte castlings;
  private final byte enPassant;  // -1 if no en passant pawn
  private final int movesSinceCapture;

  private final List<ChessMove> moves;

  ChessState() {
    status = GameStatus.FIRST_PLAYER;
    board = INITIAL_BOARD;
    castlings = WHITE_LONG_CASTLING | WHITE_SHORT_CASTLING |
                BLACK_LONG_CASTLING | BLACK_SHORT_CASTLING;
    enPassant = -1;
    movesSinceCapture = 0;

    moves = generateMoves();
  }

  private ChessState(ChessState prev, ChessMove move) {
    boolean player = !prev.status.getPlayer();
    board = applyMove(prev.board, move);

    byte piece = prev.getPiece(move.from);

    castlings = newCastlings(prev.castlings, move, player, piece);
    
    if (piece == PAWN && Math.abs(move.from - move.to) == 2) {
      enPassant = move.to;
    } else {
      enPassant = -1;
    }

    if (piece == PAWN ||
        prev.getPiece(move.to) != EMPTY ||
        prev.enPassant > 0 && board[prev.enPassant] == EMPTY) {
      movesSinceCapture = 0;
    } else {
      movesSinceCapture = prev.movesSinceCapture + 1;
    }

    moves = generateMoves(player);

    if (moves.size() > 0 && movesSinceCapture < MOVES_WITHOUT_CAPTURE) {
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

  private byte getPiece(int cell) {
    return board[cell] & PIECE_MASK;
  }

  private byte newCastles(byte prevCastles, byte[] board) {
    byte castlings = prevCastles;
    if (board[a2i("a1")] != WHITE | ROOK)
      castlings &= ~WHITE_LONG_CASTLING;

    if (board[a2i("a8")] != BLACK | ROOK)
      castlings &= ~BLACK_LONG_CASTLING;
      
    if (board[a2i("h1")] != WHITE | ROOK)
      castlings &= ~WHITE_SHORT_CASTLING;
     
    if (board[a2i("h8")] != BLACK | ROOK)
      castlings &= ~BLACK_SHORT_CASTLING;

    if (board[a2i("e1")] != WHITE | KING)
      castlings &= ~WHITE_LONG_CASTLING & ~WHITE_SHORT_CASTLING;
      
    if (board[a2i("e8")] != BLACK | KING)
      castlings &= ~BLACK_LONG_CASTLING & ~BLACK_SHORT_CASTLING;
      
    return castlings;
  }

  // Apply move to the board, updating states of castlings/en passant
  private byte[] applyMove(byte[] prevBoard, ChessMove move) {
    byte[] board = prevBoard.clone();
    byte piece = board[move.from] & PIECE_MASK;
    boolean player = status.getPlayer();
    
    switch (piece) {
      case PAWN:
        int rowTo = i2row(move.to);
        if (Math.abs(move.from - move.to) > 2 && board[move.to] == EMPTY) {
          applyCaptureEnPassant(board, move);          
        }
        if (rowTo == 1 || rowTo == 8) {
          applyPromotion(       
        } else {
          applySimpleMove(board, move);
        }
        break;          
    
      case ROOK:
      case KNIGHT:
      case BISHOP:
      case QUEEN:
        applySimpleMove(board, move);
        break;
       
      case KING:
        if (Math.abs(move.from - move.to) > 12) {
          // Castles.
        } else {
          applySimpleMove(board, move);
        }
      
    }
  }

  private List<ChessMove> generateMoves() {
    return new List<>();
  }

  // true if check to (not by) player
  private boolean isCheck(byte[] board, boolean player) {
    return false;
  }
}
