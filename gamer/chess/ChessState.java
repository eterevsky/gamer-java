package gamer.chess;

import static gamer.chess.Pieces.EMPTY;
import static gamer.chess.Pieces.PAWN;
import static gamer.chess.Pieces.ROOK;
import static gamer.chess.Pieces.QUEEN;
import static gamer.chess.Pieces.KING;
import static gamer.chess.Pieces.WHITE;
import static gamer.chess.Pieces.BLACK;
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

  private final GameStatus status;
  private final Board board;
  private final byte castlings;
  private final int enPassant;  // -1 if no en passant pawn,
                                // otherwise the passed empty square
  private final int movesSinceCapture;

  private final List<ChessMove> moves;

  ChessState() {
    status = GameStatus.FIRST_PLAYER;
    MutableBoard board = MutableBoard.INITIAL_BOARD;
    castlings = WHITE_LONG_CASTLING | WHITE_SHORT_CASTLING |
                BLACK_LONG_CASTLING | BLACK_SHORT_CASTLING;
    enPassant = -1;
    movesSinceCapture = 0;

    moves = generateMoves(board, true);
    this.board = board.toBoard();
  }

  private ChessState(ChessState prev, ChessMove move) {
    boolean player = !prev.status.getPlayer();

    MutableBoard board = prev.board.mutableClone();
    applyMove(board, prev.enPassant, move);

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
        prev.enPassant > 0 && board.isEmpty(prev.enPassant)) {
      movesSinceCapture = 0;
    } else {
      movesSinceCapture = prev.movesSinceCapture + 1;
    }

    moves = generateMoves(board, player);
    this.board = board.toBoard();

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
    return board.getPiece(cell);
  }

  private byte newCastlings(byte prevCastlings, MutableBoard board) {
    byte castlings = prevCastlings;
    if (board.get("a1") != (WHITE | ROOK))
      castlings &= ~WHITE_LONG_CASTLING;

    if (board.get("a8") != (BLACK | ROOK))
      castlings &= ~BLACK_LONG_CASTLING;

    if (board.get("h1") != (WHITE | ROOK))
      castlings &= ~WHITE_SHORT_CASTLING;

    if (board.get("h8") != (BLACK | ROOK))
      castlings &= ~BLACK_SHORT_CASTLING;

    if (board.get("e1") != (WHITE | KING))
      castlings &= ~WHITE_LONG_CASTLING & ~WHITE_SHORT_CASTLING;

    if (board.get("e8") != (BLACK | KING))
      castlings &= ~BLACK_LONG_CASTLING & ~BLACK_SHORT_CASTLING;

    return castlings;
  }

  // Apply move to the board, updating states of castlings/en passant
  private void applyMove(
      MutableBoard board, int prevEnPassant, ChessMove move) {
    byte piece = board.getPiece(move.from);

    switch (piece) {
      case PAWN:
        applyPawnMove(board, prevEnPassant, move);
        break;

      case KING:
        if (Math.abs(move.from - move.to) > 12) {
          applyCastling(board, move);
        } else {
          board.move(move.from, move.to);
        }
        break;

      default:
        board.move(move.from, move.to);
        break;
    }
  }

  private void applyPawnMove(
      MutableBoard board, int prevEnPassant, ChessMove move) {
    boolean player = board.color(move.from);
    board.move(move.from, move.to);

    if (player) {
      if (move.to == prevEnPassant) {
        board.set(move.to - 1, EMPTY);
      } else if (Board.i2row(move.to) == 8) {
        board.set(move.to, Pieces.white(move.promote));
      }
    } else {
      if (move.to == prevEnPassant) {
        board.set(move.to + 1, EMPTY);
      } else if (Board.i2row(move.to) == 1) {
        board.set(move.to, Pieces.black(move.promote));
      }
    }
  }

  private void applyCastling(MutableBoard board, ChessMove move) {
    board.move(move.from, move.to);

    if (move.to > move.from) {
      board.move(move.from + 24, move.from + 8);
    } else {
      board.move(move.from - 32, move.from - 8);
    }
  }

  private List<ChessMove> generateMoves(MutableBoard board, boolean player) {
    List<ChessMove> moves = new ArrayList<>();

    for (int cell = 0; cell < 64; cell++) {
      if (board.isEmpty(cell) || board.color(cell) != player)
        continue;

      switch (board.getPiece(cell)) {
        case PAWN:
          addPawnMoves(board, moves, cell, player);
          break;

        default:
          throw new UnsupportedOperationException(
              "Can't generate moves for this piece!");
      }
    }

    return moves;
  }

  private void addPawnMoves(
      MutableBoard board, List<ChessMove> moves, int cell, boolean player) {
    int row = Board.i2row(cell);
    int col = Board.i2col(cell);

    if (player) {

      if (row < 7) {
        if (board.isEmpty(cell + 1)) {
          addIfValid(board, moves, ChessMove.of(cell, cell + 1));
          if (row == 2 && board.isEmpty(cell + 2))
            addIfValid(board, moves, ChessMove.of(cell, cell + 2));
        }
        if (col != 1 && board.isBlack(cell - 7))
          addIfValid(board, moves, ChessMove.of(cell, cell - 7));
        if (col != 8 && board.isBlack(cell + 9))
          addIfValid(board, moves, ChessMove.of(cell, cell + 9));
      } else {
        for (byte promote = ROOK; promote <= QUEEN; promote++) {
          if (board.isEmpty(cell + 1))
            addIfValid(board, moves, ChessMove.of(cell, cell + 1, promote));
          if (col != 1 && board.isBlack(cell - 7))
            addIfValid(board, moves, ChessMove.of(cell, cell - 7, promote));
          if (col != 8 && board.isBlack(cell + 9))
            addIfValid(board, moves, ChessMove.of(cell, cell + 9, promote));
        }
      }

    } else {

      if (row > 2) {
        if (board.isEmpty(cell - 1)) {
          addIfValid(board, moves, ChessMove.of(cell, cell - 1));
          if (row == 7 && board.isEmpty(cell - 2))
            addIfValid(board, moves, ChessMove.of(cell, cell - 2));
        }
        if (col != 1 && board.isWhite(cell - 9))
          addIfValid(board, moves, ChessMove.of(cell, cell - 9));
        if (col != 8 && board.isWhite(cell + 7))
          addIfValid(board, moves, ChessMove.of(cell, cell + 7));
      } else {
        for (byte promote = ROOK; promote <= QUEEN; promote++) {
          if (board.isEmpty(cell - 1))
            addIfValid(board, moves, ChessMove.of(cell, cell - 1, promote));
          if (col != 1 && board.isWhite(cell - 9))
            addIfValid(board, moves, ChessMove.of(cell, cell - 9, promote));
          if (col != 8 && board.isWhite(cell + 7))
            addIfValid(board, moves, ChessMove.of(cell, cell + 7, promote));
        }
      }

    }
  }

  private void addIfValid(
      MutableBoard board, List<ChessMove> moves, ChessMove move) {
    boolean player = board.isWhite(move.from);
    int undoCell1 = -1, undoCell2 = -1, undoCell3 = -1, undoCell4 = -1;
    byte undoPiece1 = EMPTY, undoPiece2 = EMPTY, undoPiece3 = EMPTY,
         undoPiece4 = EMPTY;

    undoCell1  = move.from;
    undoCell2  = move.to;
    undoPiece1 = board.get(undoCell1);
    undoPiece2 = board.get(undoCell2);

    if (move.to == enPassant &&
        board.getPiece(move.from) == PAWN &&
        Math.abs(move.from - move.to) > 2) {
      undoCell3 = move.to + (player ? -1 : +1);
      undoPiece3 = board.get(undoCell3);
    }

    if (Board.i2col(move.from) == 5 &&
        board.getPiece(move.from) == KING) {
      if (Board.i2col(move.to) == 7) {
        undoCell3 = move.from + 8;
        undoCell4 = move.to + 8;
        undoPiece3 = board.get(undoCell3);
        undoPiece4 = board.get(undoCell4);
      } else if (Board.i2col(move.to) == 3) {
        undoCell3 = move.from - 8;
        undoCell4 = move.to - 16;
        undoPiece3 = board.get(undoCell3);
        undoPiece4 = board.get(undoCell4);
      }
    }

    applyMove(board, enPassant, move);
    if (!isCheck(board, player)) {
      moves.add(move);
    }

    board.set(undoCell1, undoPiece1);
    board.set(undoCell2, undoPiece2);
    if (undoCell3 >= 0) {
      board.set(undoCell3, undoPiece3);
      if (undoCell4 >= 0) {
        board.set(undoCell4, undoPiece4);
      }
    }
  }

  // true if check to (not by) player
  private boolean isCheck(MutableBoard board, boolean player) {
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

    return null;
  }

  public String toString() {
    StringBuilder builder = new StringBuilder();
    for (int row = 8; row >= 1; row--) {
      for (int col = 1; col <= 8; col++) {
        builder.append(" " + piece2a(board.get(col, row)));
      }
      builder.append("\n");
    }
    return builder.toString();
  }
}
