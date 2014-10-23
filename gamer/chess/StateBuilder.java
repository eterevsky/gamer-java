package gamer.chess;

import static gamer.chess.Board.i2col;
import static gamer.chess.Board.i2row;
import static gamer.chess.Pieces.EMPTY;
import static gamer.chess.Pieces.PAWN;
import static gamer.chess.Pieces.ROOK;
import static gamer.chess.Pieces.KNIGHT;
import static gamer.chess.Pieces.BISHOP;
import static gamer.chess.Pieces.QUEEN;
import static gamer.chess.Pieces.KING;
import static gamer.chess.Pieces.WHITE;
import static gamer.chess.Pieces.BLACK;

import gamer.def.GameStatus;

import java.util.ArrayList;
import java.util.List;

class StateBuilder implements State {
  private Board board;
  private boolean player = true;
  private byte castlings = WHITE_LONG_CASTLING | WHITE_SHORT_CASTLING |
                           BLACK_LONG_CASTLING | BLACK_SHORT_CASTLING;

  private int enPassant = -1;  // -1 if no en passant pawn,
                               // otherwise the passed empty square
  private int movesSinceCapture = 0;
  private int movesCount = 0;
  private List<ChessMove> moves = null;

  private int undoCell1, undoCell2, undoCell3, undoCell4;
  private byte undoPiece1, undoPiece2, undoPiece3, undoPiece4;

  private boolean check = false;
  private int kingCellCache = -1;

  StateBuilder() {
    this.board = new Board();
  }

  StateBuilder(State state) {
    board = state.getBoard();
    player = state.status().getPlayer();
    castlings = state.getCastlings();
    enPassant = state.getEnPassant();
    movesSinceCapture = state.getMovesSinceCapture();
    movesCount = state.getMovesCount();
  }

  // @Override
  public Board getBoard() {
    return board;
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

  // @Override
  public GameStatus status() {
    if (movesSinceCapture > MOVES_WITHOUT_CAPTURE || drawByMaterial())
      return GameStatus.DRAW;

    if (moves == null)
      generateMoves();

    if (moves.size() > 0) {
      return player ? GameStatus.FIRST_PLAYER : GameStatus.SECOND_PLAYER;
    } else if (isCheck()) {
      return player ? GameStatus.LOSS : GameStatus.WIN;
    } else {
      return GameStatus.DRAW;
    }
  }

  // @Override
  public List<ChessMove> getMoves() {
    if (moves == null)
      generateMoves();

    return moves;
  }

  List<ChessMove> disownMoves() {
    if (moves == null)
      generateMoves();

    List<ChessMove> temp = moves;
    moves = null;
    return temp;
  }

  void applyMove(ChessMove move) {
    byte piece = board.getPiece(move.from);

    // We need board before the move, so this should be before applyToBoard().
    if (piece == PAWN || board.get(move.to) != EMPTY) {
      movesSinceCapture = 0;
    } else {
      movesSinceCapture++;
    }

    applyToBoard(move);

    player = !player;

    castlings = newCastlings();

    enPassant = -1;
    if (piece == PAWN) {
      if (player && move.to - move.from == 2)
        enPassant = move.from + 1;
      if (!player && move.from - move.to == 2)
        enPassant = move.to + 1;
    }

    movesCount++;

    moves = null;
  }

  private byte newCastlings() {
    byte castlings = this.castlings;
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

  private void applyToBoard(ChessMove move) {
    undoCell1 = move.from;
    undoPiece1 = board.get(move.from);
    undoCell2 = move.to;
    undoPiece2 = board.get(move.to);
    undoCell3 = -1;
    undoCell4 = -1;

    byte piece = board.getPiece(move.from);
    board.move(move.from, move.to);

    if (piece == PAWN) {
      if (move.to == enPassant) {
        int takenCell = player ? move.to - 1 : move.to + 1;
        undoCell3 = takenCell;
        undoPiece3 = board.get(undoCell3);
      } else if (i2row(move.to) == 1 || i2row(move.to) == 8) {
        board.set(move.to, Pieces.withColor(move.promote, player));
      }
    }

    if (piece == KING && Math.abs(move.from - move.to) == 16) {
      int rookFrom;
      int rookTo;
      if (move.to > move.from) {
        rookFrom = move.from + 24;
        rookTo = move.from + 8;
      } else {
        rookFrom = move.from - 32;
        rookTo = move.from - 8;
      }

      undoCell3 = rookFrom;
      undoPiece3 = board.get(rookFrom);
      undoCell4 = rookTo;
      undoPiece4 = board.get(rookTo);

      board.move(rookFrom, rookTo);
    }
  }

  private void undoApplyToBoard() {
    board.set(undoCell1, undoPiece1);
    board.set(undoCell2, undoPiece2);
    if (undoCell3 >= 0) {
      board.set(undoCell3, undoPiece3);
      if (undoCell4 >= 0) {
        board.set(undoCell4, undoPiece4);
      }
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

  private void generateMoves() {
    check = isCheck();
    moves = new ArrayList<>();

    for (int cell = 0; cell < 64; cell++) {
      if (board.isEmpty(cell) || board.color(cell) != player)
        continue;

      switch (board.getPiece(cell)) {
        case PAWN:
          addPawnMoves(cell);
          break;

        case ROOK:
          addLinearMoves(cell, ROOK_DELTA_COL, ROOK_DELTA_ROW);
          break;

        case KNIGHT:
          addSpotMoves(cell, KNIGHT_DELTA_COL, KNIGHT_DELTA_ROW);
          break;

        case BISHOP:
          addLinearMoves(cell, BISHOP_DELTA_COL, BISHOP_DELTA_ROW);
          break;

        case QUEEN:
          addLinearMoves(cell, QUEEN_DELTA_COL, QUEEN_DELTA_ROW);
          break;

        case KING:
          addSpotMoves(cell, KING_DELTA_COL, KING_DELTA_ROW);
          break;

        default:
          throw new RuntimeException("WTF is this piece?!");
      }
    }
  }

  private void addPawnMoves(int cell) {
    int row = i2row(cell);
    int col = i2col(cell);

    if (player) {

      if (row < 7) {
        if (board.isEmpty(cell + 1)) {
          addIfValid(ChessMove.of(cell, cell + 1));
          if (row == 2 && board.isEmpty(cell + 2))
            addIfValid(ChessMove.of(cell, cell + 2));
        }
        if (col != 1 && board.isBlack(cell - 7))
          addIfValid(ChessMove.of(cell, cell - 7));
        if (col != 8 && board.isBlack(cell + 9))
          addIfValid(ChessMove.of(cell, cell + 9));
      } else {
        for (byte promote = ROOK; promote <= QUEEN; promote++) {
          if (board.isEmpty(cell + 1))
            addIfValid(ChessMove.of(cell, cell + 1, promote));
          if (col != 1 && board.isBlack(cell - 7))
            addIfValid(ChessMove.of(cell, cell - 7, promote));
          if (col != 8 && board.isBlack(cell + 9))
            addIfValid(ChessMove.of(cell, cell + 9, promote));
        }
      }

    } else {

      if (row > 2) {
        if (board.isEmpty(cell - 1)) {
          addIfValid(ChessMove.of(cell, cell - 1));
          if (row == 7 && board.isEmpty(cell - 2))
            addIfValid(ChessMove.of(cell, cell - 2));
        }
        if (col != 1 && board.isWhite(cell - 9))
          addIfValid(ChessMove.of(cell, cell - 9));
        if (col != 8 && board.isWhite(cell + 7))
          addIfValid(ChessMove.of(cell, cell + 7));
      } else {
        for (byte promote = ROOK; promote <= QUEEN; promote++) {
          if (board.isEmpty(cell - 1))
            addIfValid(ChessMove.of(cell, cell - 1, promote));
          if (col != 1 && board.isWhite(cell - 9))
            addIfValid(ChessMove.of(cell, cell - 9, promote));
          if (col != 8 && board.isWhite(cell + 7))
            addIfValid(ChessMove.of(cell, cell + 7, promote));
        }
      }

    }
  }

  private void addLinearMoves(int cell, int[] colDelta, int[] rowDelta) {
    for (int idelta = 0; idelta < colDelta.length; idelta++) {
      int dc = colDelta[idelta];
      int dr = rowDelta[idelta];

      int col = i2col(cell);
      int row = i2row(cell);

      while (true) {
        col += dc;
        row += dr;

        if (col > 8 || col < 1 || row > 8 || row < 1)
          break;

        int currentCell = Board.cr2i(col, row);
        byte piece = board.get(currentCell);

        if (piece == EMPTY) {
          addIfValid(ChessMove.of(cell, currentCell));
        } else if (Pieces.color(piece) != player) {
          addIfValid(ChessMove.of(cell, currentCell));
          break;
        } else {
          break;
        }
      }
    }
  }

  private void addSpotMoves(int cell, int[] colDelta, int[] rowDelta) {
    for (int idelta = 0; idelta < colDelta.length; idelta++) {
      int col = i2col(cell) + colDelta[idelta];
      int row = i2row(cell) + rowDelta[idelta];

      if (col >= 1 && col <= 8 && row >= 1 && row <= 8 &&
          (board.isEmpty(col, row) || board.color(col, row) != player)) {
        addIfValid(ChessMove.of(cell, Board.cr2i(col, row)));
      }
    }
  }

  private int findKing() {
    byte king = Pieces.withColor(KING, player);

    if (kingCellCache < 0 || board.get(kingCellCache) != king) {
      for (kingCellCache = 0; kingCellCache < 64; kingCellCache++) {
        if (board.get(kingCellCache) == king)
          break;
      }
    }

    return kingCellCache;
  }

  private void addIfValid(ChessMove move) {
    applyToBoard(move);

    int kingCell = findKing();
    int kingCol = i2col(kingCell);
    int kingRow = i2row(kingCell);
    int fromCol = i2col(move.from);
    int fromRow = i2row(move.from);
    boolean valid;

    if (check || move.to == kingCell) {
      valid = !isCheck();
    } else if (kingRow == fromRow && kingRow != i2row(move.to)) {
      if (fromCol > kingCol) {
        valid = !checkByLinearPiece(kingCol, kingRow, ROOK, 1, 0);
      } else {
        valid = !checkByLinearPiece(kingCol, kingRow, ROOK, -1, 0);
      }
    } else if (kingCol == fromCol && kingCol != i2col(move.to)) {
      if (fromRow > kingRow) {
        valid = !checkByLinearPiece(kingCol, kingRow, ROOK, 0, 1);
      } else {
        valid = !checkByLinearPiece(kingCol, kingRow, ROOK, 0, -1);
      }
    } else if (fromCol - kingCol == fromRow - kingRow) {
      if (fromCol > kingCol) {
        valid = !checkByLinearPiece(kingCol, kingRow, BISHOP, 1, 1);
      } else {
        valid = !checkByLinearPiece(kingCol, kingRow, BISHOP, -1, -1);
      }
    } else if (fromCol - kingCol == kingRow - fromRow) {
      if (fromCol > kingCol) {
        valid = !checkByLinearPiece(kingCol, kingRow, BISHOP, 1, -1);
      } else {
        valid = !checkByLinearPiece(kingCol, kingRow, BISHOP, -1, 1);
      }
    } else {
      valid = true;
    }

    if (valid)
      moves.add(move);

    undoApplyToBoard();
  }

  private boolean checkByLinearPiece(
      int col, int row, byte attackingPiece, int cd, int rd) {
    attackingPiece = Pieces.withColor(attackingPiece, !player);
    byte queen = Pieces.withColor(QUEEN, !player);
    while (true) {
      col += cd;
      row += rd;
      if (col > 8 || col < 1 || row > 8 || row < 1)
        return false;

      byte piece = board.get(col, row);

      if (piece == attackingPiece || piece == queen)
        return true;

      if (piece != EMPTY)
        return false;
    }
  }

  // true if check to (not by) player
  private boolean isCheck() {
    int cell = findKing();
    int col = i2col(cell);
    int row = i2row(cell);

    if (player && row < 7 &&
        (col > 1 && board.get(cell - 7) == (BLACK | PAWN) ||
         col < 8 && board.get(cell + 9) == (BLACK | PAWN))) {
      return true;
    }

    if (!player && row > 2 &&
        (col > 1 && board.get(cell - 9) == (WHITE | PAWN) ||
         col < 8 && board.get(cell + 7) == (WHITE | PAWN))) {
      return true;
    }

    return checkBySpotPiece(cell, KNIGHT, KNIGHT_DELTA_COL, KNIGHT_DELTA_ROW) ||
           checkBySpotPiece(cell, KING, KING_DELTA_COL, KING_DELTA_ROW) ||
           checkByLinearPieces(
              cell, ROOK, QUEEN, ROOK_DELTA_COL, ROOK_DELTA_ROW) ||
           checkByLinearPieces(
              cell, BISHOP, QUEEN, BISHOP_DELTA_COL, BISHOP_DELTA_ROW);
  }

  private boolean checkBySpotPiece(
      int cell, byte piece, int[] colDelta, int[] rowDelta) {
    piece = Pieces.withColor(piece, !player);
    int kingCol = i2col(cell);
    int kingRow = i2row(cell);

    for (int i = 0; i < colDelta.length; i++) {
      int col = kingCol + colDelta[i];
      int row = kingRow + rowDelta[i];

      if (col >= 1 && col <= 8 && row >= 1 && row <= 8 &&
          board.get(col, row) == piece) {
        return true;
      }
    }

    return false;
  }

  private boolean checkByLinearPieces(
      int cell, byte piece1, byte piece2, int[] colDelta, int[] rowDelta) {
    piece1 = Pieces.withColor(piece1, !player);
    piece2 = Pieces.withColor(piece2, !player);
    int kingCol = i2col(cell);
    int kingRow = i2row(cell);

    for (int i = 0; i < colDelta.length; i++) {
      int col = kingCol;
      int row = kingRow;
      while (true) {
        col += colDelta[i];
        row += rowDelta[i];
        if (col > 8 || col < 1 || row > 8 || row < 1)
          break;

        byte piece = board.get(col, row);

        if (piece == piece1 || piece == piece2)
          return true;

        if (piece != EMPTY)
          break;
      }
    }

    return false;
  }

  private boolean drawByMaterial() {
    byte leftPiece = EMPTY;

    for (int cell = 0; cell < 64; cell++) {
      byte piece = board.getPiece(cell);
      if (piece == EMPTY || piece == KING)
        continue;
      if (board.color(cell) != player || leftPiece != EMPTY)
        return false;
      leftPiece = piece;
    }

    return leftPiece == KNIGHT || leftPiece == BISHOP;
  }
}