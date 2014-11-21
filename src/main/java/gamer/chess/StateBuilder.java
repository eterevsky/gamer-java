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

import gamer.def.PositionMut;

import java.util.ArrayList;
import java.util.List;

public class StateBuilder
    implements PositionMut<StateBuilder, ChessMove>, State {
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
  private int kingCell = -1;

  public StateBuilder() {
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
  public List<ChessMove> getMoves() {
    if (moves == null)
      generateMoves();

    return moves;
  }

  public void set(int cell, byte piece) {
    board.set(cell, piece);
  }

  public void set(int cell, int piece) {
    set(cell, (byte)piece);
  }

  public void setCastlings(byte castlings) {
    this.castlings = castlings;
  }

  public boolean getPlayerBool() {
    return player;
  }

  @Override
  public int getPlayer() {
    return player ? 0 : 1;
  }

  public void setPlayer(boolean player) {
    this.player = player;
  }

  public void setEnPassant(int cell) {
    enPassant = cell;
  }

  public void setMovesSinceCapture(int moves) {
    movesSinceCapture = moves;
  }

  public void setMovesCount(int moves) {
    movesCount = moves;
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
      if (!player && move.to - move.from == 2)
        enPassant = move.from + 1;
      if (player && move.from - move.to == 2)
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
        board.set(takenCell, EMPTY);
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
    findKing();
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
          if (!check)
            addCastlings();
          break;

        default:
          throw new RuntimeException("WTF is this piece?!");
      }
    }
  }

  private void addCastlings() {
    if (player) {
      if ((castlings & WHITE_SHORT_CASTLING) != 0 &&
          board.isEmpty("f1") && board.isEmpty("g1") &&
          moves.contains(ChessMove.of("e1", "f1"))) {
        assert board.get("e1") == (WHITE | KING);
        assert board.isEmpty("f1");
        assert board.isEmpty("g1");
        assert board.get("h1") == (WHITE | ROOK);
        addKingMoveIfValid(ChessMove.of("e1", "g1"));
      }
      if ((castlings & WHITE_LONG_CASTLING) != 0 &&
          board.isEmpty("b1") && board.isEmpty("c1") && board.isEmpty("d1") &&
          moves.contains(ChessMove.of("e1", "d1"))) {
        assert board.get("e1") == (WHITE | KING);
        assert board.isEmpty("d1");
        assert board.isEmpty("c1");
        assert board.isEmpty("b1");
        assert board.get("a1") == (WHITE | ROOK);
        addKingMoveIfValid(ChessMove.of("e1", "c1"));
      }
    } else {
      if ((castlings & BLACK_SHORT_CASTLING) != 0 &&
          board.isEmpty("f8") && board.isEmpty("g8") &&
          moves.contains(ChessMove.of("e8", "f8"))) {
        assert board.get("e8") == (BLACK | KING);
        assert board.isEmpty("f8");
        assert board.isEmpty("g8");
        assert board.get("h8") == (BLACK | ROOK);
        addKingMoveIfValid(ChessMove.of("e8", "g8"));
      }
      if ((castlings & BLACK_LONG_CASTLING) != 0 &&
          board.isEmpty("b8") && board.isEmpty("c8") && board.isEmpty("d8") &&
          moves.contains(ChessMove.of("e8", "d8"))) {
        assert board.get("e8") == (BLACK | KING);
        assert board.isEmpty("d8");
        assert board.isEmpty("c8");
        assert board.isEmpty("b8");
        assert board.get("a8") == (BLACK | ROOK);
        addKingMoveIfValid(ChessMove.of("e8", "c8"));
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
        if (col != 1 && (board.isBlack(cell - 7) || cell - 7 == enPassant)) {
          addIfValid(ChessMove.of(cell, cell - 7));
        }
        if (col != 8 && (board.isBlack(cell + 9) || cell + 9 == enPassant)) {
          addIfValid(ChessMove.of(cell, cell + 9));
        }
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
        if (col != 1 && (board.isWhite(cell - 9) || cell - 9 == enPassant))
          addIfValid(ChessMove.of(cell, cell - 9));
        if (col != 8 && (board.isWhite(cell + 7) || cell + 7 == enPassant))
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
    boolean isKing = (board.getPiece(cell) == KING);

    for (int idelta = 0; idelta < colDelta.length; idelta++) {
      int col = i2col(cell) + colDelta[idelta];
      int row = i2row(cell) + rowDelta[idelta];

      if (col >= 1 && col <= 8 && row >= 1 && row <= 8 &&
          (board.isEmpty(col, row) || board.color(col, row) != player)) {
        ChessMove move = ChessMove.of(cell, Board.cr2i(col, row));
        if (isKing) {
          addKingMoveIfValid(move);
        } else {
          addIfValid(move);
        }
      }
    }
  }

  private void findKing() {
    byte king = Pieces.withColor(KING, player);

    if (kingCell < 0 || board.get(kingCell) != king) {
      for (kingCell = 0; kingCell < 64; kingCell++) {
        if (board.get(kingCell) == king)
          break;
      }
    }
  }

  private void addKingMoveIfValid(ChessMove move) {
    assert board.getPiece(move.from) == KING;
    applyToBoard(move);

    if (!isCheck(move.to))
      moves.add(move);

    undoApplyToBoard();
  }

  private void addIfValid(ChessMove move) {
    assert board.getPiece(move.from) != KING;

    if (check || move.to == enPassant) {
      applyToBoard(move);
      if (!isCheck(kingCell))
        moves.add(move);
      undoApplyToBoard();
      return;
    }

    int kingCol = i2col(kingCell);
    int kingRow = i2row(kingCell);
    int fromCol = i2col(move.from);
    int fromRow = i2row(move.from);
    boolean valid = true;

    if (kingRow == fromRow && kingRow != i2row(move.to)) {
      if (fromCol > kingCol) {
        valid = !checkByLinearPiece(move, kingCol, kingRow, ROOK, 1, 0);
      } else {
        valid = !checkByLinearPiece(move, kingCol, kingRow, ROOK, -1, 0);
      }
    } else if (kingCol == fromCol && kingCol != i2col(move.to)) {
      if (fromRow > kingRow) {
        valid = !checkByLinearPiece(move, kingCol, kingRow, ROOK, 0, 1);
      } else {
        valid = !checkByLinearPiece(move, kingCol, kingRow, ROOK, 0, -1);
      }
    } else if (fromCol - kingCol == fromRow - kingRow) {
      if (fromCol > kingCol) {
        valid = !checkByLinearPiece(move, kingCol, kingRow, BISHOP, 1, 1);
      } else {
        valid = !checkByLinearPiece(move, kingCol, kingRow, BISHOP, -1, -1);
      }
    } else if (fromCol - kingCol == kingRow - fromRow) {
      if (fromCol > kingCol) {
        valid = !checkByLinearPiece(move, kingCol, kingRow, BISHOP, 1, -1);
      } else {
        valid = !checkByLinearPiece(move, kingCol, kingRow, BISHOP, -1, 1);
      }
    }

    if (valid)
      moves.add(move);
  }

  private boolean checkByLinearPiece(
      ChessMove move, int col, int row, byte attackingPiece, int cd, int rd) {
    applyToBoard(move);

    attackingPiece = Pieces.withColor(attackingPiece, !player);
    byte queen = Pieces.withColor(QUEEN, !player);
    while (true) {
      col += cd;
      row += rd;
      if (col > 8 || col < 1 || row > 8 || row < 1)
        break;

      byte piece = board.get(col, row);

      if (piece == attackingPiece || piece == queen) {
        undoApplyToBoard();
        return true;
      }

      if (piece != EMPTY)
        break;
    }

    undoApplyToBoard();
    return false;
  }

  // true if check to (not by) player
  private boolean isCheck() {
    return isCheck(kingCell);
  }

  private boolean isCheck(int cell) {
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
