package gamer.chess;

import static gamer.chess.Board.a2i;
import static gamer.chess.Board.cr2i;
import static gamer.chess.Board.i2a;
import static gamer.chess.Board.i2col;
import static gamer.chess.Board.i2row;
import static gamer.chess.Pieces.EMPTY;
import static gamer.chess.Pieces.PAWN;
import static gamer.chess.Pieces.ROOK;
import static gamer.chess.Pieces.QUEEN;
import static gamer.chess.Pieces.KING;
import static gamer.chess.Pieces.WHITE;
import static gamer.chess.Pieces.BLACK;
import static gamer.chess.Pieces.isWhite;
import static gamer.chess.Pieces.isBlack;
import static gamer.chess.Pieces.color;
import static gamer.chess.Pieces.piece2a;

import gamer.def.GameException;
import gamer.def.GameState;
import gamer.def.GameStatus;
import gamer.def.Move;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public final class ChessState implements GameState<Chess> {
  private static final byte WHITE_SHORT_CASTLING = 1;
  private static final byte WHITE_LONG_CASTLING = 2;
  private static final byte BLACK_SHORT_CASTLING = 4;
  private static final byte BLACK_LONG_CASTLING = 8;

  private static final int MOVES_WITHOUT_CAPTURE = 150;

  private static final byte[] INITIAL_BOARD = Util.hexStringToByteArray(
      "020100000000090a" +
      "030100000000090b" +
      "040100000000090c" +
      "050100000000090d" +
      "060100000000090e" +
      "040100000000090c" +
      "030100000000090b" +
      "020100000000090a");

  private final GameStatus status;
  private final byte[] board;
  private final byte castlings;
  private final int enPassant;  // -1 if no en passant pawn,
                                // otherwise the passed empty square
  private final int movesSinceCapture;

  private final List<ChessMove> moves;

  ChessState() {
    status = GameStatus.FIRST_PLAYER;
    board = INITIAL_BOARD;
    castlings = WHITE_LONG_CASTLING | WHITE_SHORT_CASTLING |
                BLACK_LONG_CASTLING | BLACK_SHORT_CASTLING;
    enPassant = -1;
    movesSinceCapture = 0;

    moves = generateMoves(true);
  }

  private ChessState(ChessState prev, ChessMove move) {
    boolean player = !prev.status.getPlayer();
    board = applyMove(prev.board, prev.enPassant, move);

    castlings = newCastlings(prev.castlings, board);
    byte piece = prev.getPiece(move.from);

    if (piece == PAWN && player && move.to - move.from == 2) {
      enPassant = move.from + 1;
    } else if (piece == PAWN && !player && move.from - move.to == 2) {
      enPassant = move.to + 1;
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

  public boolean isTerminal() {
    return status.isTerminal();
  }

  public ChessState play(Move<Chess> moveInt) {
    ChessMove move = (ChessMove) moveInt;
    if (!moves.contains(move)) {
      throw new GameException("Illegal move");
    }

    return new ChessState(this, move);
  }

  public final List<ChessMove> getMoves() {
    return moves;
  }

  public ChessMove getRandomMove(Random random) {
    return moves.get(random.nextInt(moves.size()));
  }

  private byte getPiece(int cell) {
    return (byte) (board[cell] & Pieces.PIECE_MASK);
  }

  private byte newCastlings(byte prevCastlings, byte[] board) {
    byte castlings = prevCastlings;
    if (board[a2i("a1")] != (WHITE | ROOK))
      castlings &= ~WHITE_LONG_CASTLING;

    if (board[a2i("a8")] != (BLACK | ROOK))
      castlings &= ~BLACK_LONG_CASTLING;

    if (board[a2i("h1")] != (WHITE | ROOK))
      castlings &= ~WHITE_SHORT_CASTLING;

    if (board[a2i("h8")] != (BLACK | ROOK))
      castlings &= ~BLACK_SHORT_CASTLING;

    if (board[a2i("e1")] != (WHITE | KING))
      castlings &= ~WHITE_LONG_CASTLING & ~WHITE_SHORT_CASTLING;

    if (board[a2i("e8")] != (BLACK | KING))
      castlings &= ~BLACK_LONG_CASTLING & ~BLACK_SHORT_CASTLING;

    return castlings;
  }

  // Apply move to the board, updating states of castlings/en passant
  private byte[] applyMove(byte[] prevBoard, int prevEnPassant, ChessMove move) {
    byte[] board = prevBoard.clone();
    byte piece = (byte) (board[move.from] & Pieces.PIECE_MASK);

    switch (piece) {
      case PAWN:
        applyPawnMove(board, prevEnPassant, move);
        break;

      case KING:
        if (Math.abs(move.from - move.to) > 12) {
          applyCastling(board, move);
        } else {
          applySimpleMove(board, move);
        }
        break;

      default:
        applySimpleMove(board, move);
        break;
    }

    return board;
  }

  private void applySimpleMove(byte[] board, ChessMove move) {
    board[move.to] = board[move.from];
    board[move.from] = EMPTY;
  }

  private void applyPawnMove(byte[] board, int prevEnPassant, ChessMove move) {
    board[move.to] = board[move.from];
    board[move.from] = EMPTY;

    if ((board[move.to] & WHITE) != 0) {
      if (move.to == enPassant) {
        board[move.to - 1] = EMPTY;
      } else if (i2row(move.to) == 8) {
        board[move.to] = (byte) (move.promote & WHITE);
      }
    } else {
      if (move.to == prevEnPassant) {
        board[move.to + 1] = EMPTY;
      } else if (i2row(move.to) == 1) {
        board[move.to] = (byte) (move.promote & BLACK);
      }
    }
  }

  private void applyCastling(byte[] board, ChessMove move) {
    board[move.to] = board[move.from];
    board[move.from] = EMPTY;
    if (move.to > move.from) {
      board[move.from + 8] = board[move.from + 24];
      board[move.from + 24] = EMPTY;
    } else {
      board[move.from - 8] = board[move.from - 32];
      board[move.from - 32] = EMPTY;
    }
  }

  private List<ChessMove> generateMoves(boolean player) {
    List<ChessMove> moves = new ArrayList<>();

    for (int cell = 0; cell < 64; cell++) {
      if (board[cell] == EMPTY ||
          Pieces.color(board[cell]) != player)
        continue;

      switch (Pieces.piece(board[cell])) {
        case PAWN:
          addPawnMoves(moves, cell, player);
          break;
      }
    }

    return moves;
  }

  private void addPawnMoves(List<ChessMove> moves, int cell, boolean player) {
    int row = i2row(cell);
    int col = i2col(cell);

    if (player) {

      if (row < 7) {
        if (board[cell + 1] == EMPTY) {
          addIfValid(moves, ChessMove.of(cell, cell + 1));
          if (row == 2 && board[cell + 2] == EMPTY)
            addIfValid(moves, ChessMove.of(cell, cell + 2));
        }
        if (col != 1 && isBlack(board[cell - 7]))
          addIfValid(moves, ChessMove.of(cell, cell - 7));
        if (col != 8 && isBlack(board[cell + 9]))
          addIfValid(moves, ChessMove.of(cell, cell + 9));
      } else {
        for (byte promote = ROOK; promote <= QUEEN; promote++) {
          if (board[cell + 1] == EMPTY)
            addIfValid(moves, ChessMove.of(cell, cell + 1, promote));
          if (col != 1 && isBlack(board[cell - 7]))
            addIfValid(moves, ChessMove.of(cell, cell - 7, promote));
          if (col != 8 && isBlack(board[cell + 9]))
            addIfValid(moves, ChessMove.of(cell, cell + 9, promote));
        }
      }

    } else {

      if (row > 2) {
        if (board[cell - 1] == EMPTY) {
          addIfValid(moves, ChessMove.of(cell, cell - 1));
          if (row == 7 && board[cell - 2] == EMPTY)
            addIfValid(moves, ChessMove.of(cell, cell - 2));
        }
        if (col != 1 && isBlack(board[cell - 9]))
          addIfValid(moves, ChessMove.of(cell, cell - 9));
        if (col != 8 && isBlack(board[cell + 7]))
          addIfValid(moves, ChessMove.of(cell, cell + 7));
      } else {
        for (byte promote = ROOK; promote <= QUEEN; promote++) {
          if (board[cell - 1] == EMPTY)
            addIfValid(moves, ChessMove.of(cell, cell - 1, promote));
          if (col != 1 && isBlack(board[cell - 9]))
            addIfValid(moves, ChessMove.of(cell, cell - 9, promote));
          if (col != 8 && isBlack(board[cell + 7]))
            addIfValid(moves, ChessMove.of(cell, cell + 7, promote));
        }
      }

    }
  }

  private void addIfValid(List<ChessMove> moves, ChessMove move) {
    boolean player = isWhite(board[move.from]);
    int undoCell1 = -1, undoCell2 = -1, undoCell3 = -1, undoCell4 = -1;
    byte undoPiece1 = EMPTY, undoPiece2 = EMPTY, undoPiece3 = EMPTY,
         undoPiece4 = EMPTY;

    undoCell1  = move.from;
    undoCell2  = move.to;
    undoPiece1 = board[undoCell1];
    undoPiece2 = board[undoCell2];

    if (move.to == enPassant &&
        getPiece(move.from) == PAWN &&
        Math.abs(move.from - move.to) > 2) {
      undoCell3 = move.to + (player ? -1 : +1);
      undoPiece3 = board[undoCell3];
    }

    if (i2col(move.from) == 5 &&
        getPiece(move.from) == KING) {
      if (i2col(move.to) == 7) {
        undoCell3 = move.from + 8;
        undoCell4 = move.to + 8;
        undoPiece3 = board[undoCell3];
        undoPiece4 = board[undoCell4];
      } else if (i2col(move.to) == 3) {
        undoCell3 = move.from - 8;
        undoCell4 = move.to - 16;
        undoPiece3 = board[undoCell3];
        undoPiece4 = board[undoCell4];
      }
    }

    applyMove(board, enPassant, move);
    if (!isCheck(board, player)) {
      moves.add(move);
    }

    board[undoCell1] = undoPiece1;
    board[undoCell2] = undoPiece2;
    if (undoCell3 >= 0) {
      board[undoCell3] = undoPiece3;
      if (undoCell4 >= 0) {
        board[undoCell4] = undoPiece4;
      }
    }
  }

  // true if check to (not by) player
  private boolean isCheck(byte[] board, boolean player) {
    return false;
  }

  public String moveToString(ChessMove move) {
    byte piece = getPiece(move.from);
    if (piece == KING) {
      if (move.to == move.from + 16) {
        return "O-O";
      }
      if (move.to == move.from - 16) {
        return "O-O-O";
      }
    }
  }

  public String toString() {
    StringBuilder builder = new StringBuilder();
    for (int row = 8; row >= 1; row--) {
      for (int col = 1; col <= 8; col++) {
        builder.append(" " + piece2a(board[cr2i(col, row)]));
      }
      builder.append("\n");
    }
    return builder.toString();
  }
}
