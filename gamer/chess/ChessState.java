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

import gamer.def.GameException;
import gamer.def.GameState;
import gamer.def.GameStatus;
import gamer.def.Move;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Random;

public final class ChessState implements GameState<Chess> {
  private static final byte WHITE_SHORT_CASTLING = 1;
  private static final byte WHITE_LONG_CASTLING = 2;
  private static final byte BLACK_SHORT_CASTLING = 4;
  private static final byte BLACK_LONG_CASTLING = 8;

  private static final int MOVES_WITHOUT_CAPTURE = 150;

  private final GameStatus status;
  private final byte[] boardBytes;
  private final byte castlings;
  private final int enPassant;  // -1 if no en passant pawn,
                                // otherwise the passed empty square
  private final int movesSinceCapture;
  private final int totalHalfMoves;

  private final List<ChessMove> moves;

  ChessState() {
    status = GameStatus.FIRST_PLAYER;
    Board board = new Board();
    castlings = WHITE_LONG_CASTLING | WHITE_SHORT_CASTLING |
                BLACK_LONG_CASTLING | BLACK_SHORT_CASTLING;
    enPassant = -1;
    movesSinceCapture = 0;
    totalHalfMoves = 0;

    moves = generateMoves(board, true);
    this.boardBytes = board.toBytes().clone();
  }

  private ChessState(ChessState prev, ChessMove move) {
    boolean player = !prev.status.getPlayer();
    totalHalfMoves = prev.totalHalfMoves + 1;

    Board board = new Board(prev.boardBytes.clone());
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

    if (piece == PAWN || prev.getPiece(move.to) != EMPTY) {
      movesSinceCapture = 0;
    } else {
      movesSinceCapture = prev.movesSinceCapture + 1;
    }

    moves = generateMoves(board, player);

    GameStatus byMaterial = statusByMaterial(board, player);
    if (byMaterial != null) {
      status = byMaterial;
    } else if (moves.size() > 0 && movesSinceCapture < MOVES_WITHOUT_CAPTURE) {
      status = player ? GameStatus.FIRST_PLAYER : GameStatus.SECOND_PLAYER;
    } else if (isCheck(board, player) && moves.size() == 0) {
      status = player ? GameStatus.LOSS : GameStatus.WIN;
    } else {
      status = GameStatus.DRAW;
    }

    this.boardBytes = board.toBytesDisown();
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

  public ChessState play(String moveStr) {
    ChessMove move = parseAlgebraic(moveStr);
    return play(move);
  }

  public final List<ChessMove> getMoves() {
    return moves;
  }

  public ChessMove getRandomMove(Random random) {
    return moves.get(random.nextInt(moves.size()));
  }

  byte getPiece(int cell) {
    return Pieces.piece(boardBytes[cell]);
  }

  private byte newCastlings(byte prevCastlings, Board board) {
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

  private void applyMove(
      Board board, int prevEnPassant, ChessMove move) {
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
      Board board, int prevEnPassant, ChessMove move) {
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

  private void applyCastling(Board board, ChessMove move) {
    board.move(move.from, move.to);

    if (move.to > move.from) {
      board.move(move.from + 24, move.from + 8);
    } else {
      board.move(move.from - 32, move.from - 8);
    }
  }

  private final int[] ROOK_DELTA_COL = {0, 1, 0, -1};
  private final int[] ROOK_DELTA_ROW = {1, 0, -1, 0};
  private final int[] KNIGHT_DELTA_COL = {1, 2, 2, 1, -1, -2, -2, -1};
  private final int[] KNIGHT_DELTA_ROW = {2, 1, -1, -2, -2, -1, 1, 2};
  private final int[] BISHOP_DELTA_COL = {1, 1, -1, -1};
  private final int[] BISHOP_DELTA_ROW = {1, -1, -1, 1};
  private final int[] QUEEN_DELTA_COL = {0, 1, 1, 1, 0, -1, -1, -1};
  private final int[] QUEEN_DELTA_ROW = {1, 1, 0, -1, -1, -1, 0, 1};
  private final int[] KING_DELTA_COL = {0, 1, 1, 1, 0, -1, -1, -1};
  private final int[] KING_DELTA_ROW = {1, 1, 0, -1, -1, -1, 0, 1};

  private List<ChessMove> generateMoves(Board board, boolean player) {
    List<ChessMove> moves = new ArrayList<>();

    for (int cell = 0; cell < 64; cell++) {
      if (board.isEmpty(cell) || board.color(cell) != player)
        continue;

      switch (board.getPiece(cell)) {
        case PAWN:
          addPawnMoves(board, moves, cell, player);
          break;

        case ROOK:
          addLinearMoves(
              board, moves, cell, player, ROOK_DELTA_COL, ROOK_DELTA_ROW);
          break;

        case KNIGHT:
          addSpotMoves(
              board, moves, cell, player, KNIGHT_DELTA_COL, KNIGHT_DELTA_ROW);
          break;

        case BISHOP:
          addLinearMoves(
              board, moves, cell, player, BISHOP_DELTA_COL, BISHOP_DELTA_ROW);
          break;

        case QUEEN:
          addLinearMoves(
              board, moves, cell, player, QUEEN_DELTA_COL, QUEEN_DELTA_ROW);
          break;

        case KING:
          addSpotMoves(
              board, moves, cell, player, KING_DELTA_COL, KING_DELTA_ROW);
          break;

        default:
          throw new RuntimeException("WTF is this piece?!");
      }
    }

    return moves;
  }

  private void addPawnMoves(
      Board board, List<ChessMove> moves, int cell, boolean player) {
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

  private void addLinearMoves(
      Board board,
      List<ChessMove> moves,
      int cell,
      boolean player,
      int[] colDelta,
      int[] rowDelta) {
    for (int idelta = 0; idelta < colDelta.length; idelta++) {
      int dc = colDelta[idelta];
      int dr = rowDelta[idelta];

      int col = Board.i2col(cell);
      int row = Board.i2row(cell);

      while (true) {
        col += dc;
        row += dr;

        if (col > 8 || col < 1 || row > 8 || row < 1)
          break;

        int currentCell = Board.cr2i(col, row);
        byte piece = board.get(currentCell);

        if (piece == EMPTY) {
          addIfValid(board, moves, ChessMove.of(cell, currentCell));
        } else if (Pieces.color(piece) != player) {
          addIfValid(board, moves, ChessMove.of(cell, currentCell));
          break;
        } else {
          break;
        }
      }
    }
  }

  private void addSpotMoves(
      Board board,
      List<ChessMove> moves,
      int cell,
      boolean player,
      int[] colDelta,
      int[] rowDelta) {
    for (int idelta = 0; idelta < colDelta.length; idelta++) {
      int dr = rowDelta[idelta];

      int col = Board.i2col(cell) + colDelta[idelta];
      int row = Board.i2row(cell) + rowDelta[idelta];

      if (col >= 1 && col <= 8 && row >= 1 && row <= 8 &&
          (board.isEmpty(col, row) || board.color(col, row) != player)) {
        addIfValid(board, moves, ChessMove.of(cell, Board.cr2i(col, row)));
      }
    }
  }

  private void addIfValid(
      Board board, List<ChessMove> moves, ChessMove move) {
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
  private boolean isCheck(Board board, boolean player) {
    byte king = Pieces.withColor(KING, player);
    int cell;
    for (cell = 0; cell < 64; cell++) {
      if (board.get(cell) == king)
        break;
    }

    int col = Board.i2col(cell);
    int row = Board.i2row(cell);

    if (player && row < 7 &&
        (col > 1 && board.get(col - 1, row + 1) == (BLACK | PAWN) ||
         col < 8 && board.get(col + 1, row + 1) == (BLACK | PAWN))) {
      return true;
    }

    if (!player && row > 2 &&
        (col > 1 && board.get(col - 1, row - 1) == (WHITE | PAWN) ||
         col < 8 && board.get(col + 1, row - 1) == (WHITE | PAWN))) {
      return true;
    }

    for (int i = 0; i < KNIGHT_DELTA_ROW.length; i++) {
      int knightCol = col + KNIGHT_DELTA_COL[i];
      int knightRow = row + KNIGHT_DELTA_ROW[i];

      if (knightCol >= 1 && knightCol <= 8 &&
          knightRow >= 1 && knightRow <= 8 &&
          board.getPiece(knightCol, knightRow) == KNIGHT &&
          board.color(knightCol, knightRow) != player) {
        return true;
      }
    }

    for (int i = 0; i < KING_DELTA_ROW.length; i++) {
      int kingCol = col + KING_DELTA_COL[i];
      int kingRow = row + KING_DELTA_ROW[i];

      if (kingCol >= 1 && kingCol <= 8 &&
          kingRow >= 1 && kingRow <= 8 &&
          board.getPiece(kingCol, kingRow) == KING) {
        return true;
      }
    }

    for (int i = 0; i < ROOK_DELTA_ROW.length; i++) {
      int colDelta = ROOK_DELTA_COL[i];
      int rowDelta = ROOK_DELTA_ROW[i];

      int currentCol = col;
      int currentRow = row;
      while (true) {
        currentCol += colDelta;
        currentRow += rowDelta;
        if (currentCol > 8 || currentCol < 1 ||
            currentRow > 8 || currentRow < 1) {
          break;
        }

        byte piece = board.get(currentCol, currentRow);
        if (Pieces.isWhite(piece) != player &&
            (Pieces.piece(piece) == ROOK ||
            Pieces.piece(piece) == QUEEN)) {
          return true;
        }

        if (piece != EMPTY)
          break;
      }
    }

    for (int i = 0; i < BISHOP_DELTA_ROW.length; i++) {
      int colDelta = BISHOP_DELTA_COL[i];
      int rowDelta = BISHOP_DELTA_ROW[i];

      int currentCol = col;
      int currentRow = row;
      while (true) {
        currentCol += colDelta;
        currentRow += rowDelta;
        if (currentCol > 8 || currentCol < 1 ||
            currentRow > 8 || currentRow < 1) {
          break;
        }

        byte piece = board.get(currentCol, currentRow);
        if (Pieces.color(piece) != player &&
            (Pieces.piece(piece) == BISHOP ||
            Pieces.piece(piece) == QUEEN)) {
          return true;
        }

        if (piece != EMPTY)
          break;
      }
    }

    return false;
  }

  public String moveToString(Move<Chess> moveI, boolean withMoveNumber) {
    ChessMove move = (ChessMove) moveI;

    if (!withMoveNumber) {
      return moveToString(move);
    }

    StringBuilder builder = new StringBuilder();
    int moves = totalHalfMoves / 2 + 1;
    builder.append(moves);
    builder.append(". ");

    if (status == GameStatus.SECOND_PLAYER)
      builder.append("... ");

    builder.append(moveToString(move));
    return builder.toString();
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

    StringBuilder builder = new StringBuilder();

    if (piece != PAWN) {
      builder.append(Pieces.PIECE_LETTER[piece]);
    } else if (boardBytes[move.to] != EMPTY) {
      builder.append(Board.i2cola(move.from));
    }

    // TODO: disambiguation

    if (boardBytes[move.to] != EMPTY) {
      builder.append("x");
    }

    builder.append(Board.i2a(move.to));

    if (move.promote != 0) {
      builder.append("=");
      builder.append(Pieces.PIECE_LETTER[move.promote]);
    }

    return builder.toString();
  }

  ChessMove parseAlgebraic(String moveStr) {
    return AlgebraicNotation.parse(this, moveStr);
  }

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

  private GameStatus statusByMaterial(Board board, boolean player) {
    byte leftPiece = EMPTY;

    for (int cell = 0; cell < 64; cell++) {
      byte piece = board.get(cell);
      if (piece == EMPTY || Pieces.piece(piece) == KING)
        continue;
      if (Pieces.color(piece) != player || leftPiece != EMPTY)
        return null;
      leftPiece = Pieces.piece(piece);
    }

    switch (leftPiece) {
      case EMPTY:
      case KNIGHT:
      case BISHOP:
        return GameStatus.DRAW;

      case ROOK:
      case QUEEN:
        return player ? GameStatus.WIN : GameStatus.LOSS;

      case PAWN:
        return null;

      default:
        throw new RuntimeException("can't happen");
    }
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
